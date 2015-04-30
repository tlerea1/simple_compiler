package amd64;

import java.io.PrintStream;
import java.util.Map;

import parser.Formal;
import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Expression;
import parser.ast.Field;
import parser.ast.FunctionCall;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Node;
import parser.ast.Number;
import parser.ast.ProcedureCall;
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Write;
import parser.symbolTable.Array;
import parser.symbolTable.Constant;
import parser.symbolTable.Entry;
import parser.symbolTable.FormalVariable;
import parser.symbolTable.Integer;
import parser.symbolTable.LocalVariable;
import parser.symbolTable.Procedure;
import parser.symbolTable.Record;
import parser.symbolTable.Scope;
import util.Singleton;
import visitor.ASTVisitor;
/**
 * AMD64 Code generator for SC.
 * @author tuvialerea
 *
 */
public class CodeGen implements ASTVisitor {

	private Instruction ast;
	private Scope st;
	private int offset = -8;
	private PrintStream out;
	private int currentLabel = 0;
	
	/**
	 * Size of INTEGER in bytes
	 */
	public static final int SIZEOF_INT = 8;
	
	/**
	 * AMD64 CodeGen Constructor.
	 * @param ast the abstract syntax tree
	 * @param st the symbol table
	 * @param stream the stream to print to
	 */
	public CodeGen(Instruction ast, Scope st, PrintStream stream) {
		this.ast = ast;
		this.st = st;
		this.out = stream;
	}
	
	/**
	 * Function to call to generate code to specified stream.
	 */
	public void generateAMD64() {
		this.out.println(".text");
		this.out.println(".globl main");
		this.st.accept(this); // Handle declarations

		this.genFunctions(); // Start of main
		if (this.ast != null) {
			this.ast.accept(this); // Program instructions
		}
		this.text(); // End of assembly
	}
	/*
	 * Function to output the start of main, includes stack allocation and memset
	 */
	private void genFunctions() {


		
		this.out.println("main:\n");
		this.out.println("push %rbp");
		this.out.println("movq %rsp, %rbp");
		this.out.println("subq $" + this.st.size() + ", %rsp");
		this.out.println("movl $" + this.st.size() + ", %edx");
		this.out.println("movl $0, %esi");
		this.out.println("movq %rsp, %rdi");
		this.out.println("call memset");
		this.out.println("movq %rbp, _globals");
		
	}
	
	/*
	 * Function for end of main / After main stuff, strings and error segments
	 */
	private void text() {
		this.out.println("movl $0, %eax");
		this.out.println("leave");
		this.out.println("retq\n\n\n");
		
		// Array out of bounds error
		this.out.println("array_out_of_bounds:");
		this.out.println("movl $printf_arg, %edi");
		this.out.println("movl $array_str, %esi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		this.out.println("movl $1, %edi");
		this.out.println("call exit\n\n");
		
		// Divide by zero error
		this.out.println("div_by_zero:");
		this.out.println("movl $printf_arg, %edi");
		this.out.println("movl $div_zero_mes, %esi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		this.out.println("movl $1, %edi");
		this.out.println("call exit\n\n");
		
		// Mod by zero error
		this.out.println("mod_by_zero:");
		this.out.println("movl $printf_arg, %edi");
		this.out.println("movl $mod_zero_mes, %esi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		this.out.println("movl $1, %edi");
		this.out.println("call exit\n\n");
		
		// Saved strings
		this.out.println(".section .rodata");
		this.out.println("array_str:");
		this.out.println(".string \"error: Array index out of bounds!\\n\"");
		this.out.println("printf_arg:");
		this.out.println(".string \"%s\"");
		this.out.println("printf_num:");
		this.out.println(".string \"%d\\n\"");
		this.out.println("scanf_num:");
		this.out.println(".string \"%d\"");
		this.out.println("div_zero_mes:");
		this.out.println(".string \"error: Division by Zero!\\n\"");
		this.out.println("mod_zero_mes:");
		this.out.println(".string \"error: Mod by Zero!\\n\"");
		this.out.println(".section .data");
		this.out.println("_globals:");
		this.out.println(".long 0");

	}
	
	@Override
	public void visit(Entry e) {
		return;
	}

	@Override
	public void visit(Constant constant) {
		return;
	}
	
	// Generate the code for the functions
	public void visit(Procedure p) {
		this.out.println("push %rbp"); // Save bp
		this.out.println("movq %rsp, %rbp");
		this.out.println("subq $" + p.size() + ", %rsp");
		this.out.println("movl $" + p.size() + ", %edx");
		this.out.println("movl $0, %esi");
		this.out.println("movq %rsp, %rdi");
		this.out.println("call memset"); // Allocate and zero space for locals
		
		if (p.getBody() != null) {
			p.getBody().accept(this); // Generate instructions
		}
		
		if (p.getRet() != null) {
			if (p.getRet().accept(this) == 0) { // Generate return code
				this.out.println("pop %rax"); // put return value in %rax
			} else {
				this.out.println("pop %rbx"); // Derefernce if return value is a location
				this.out.println("movq (%rbx), %rax");
			}
		}
		this.out.println("movq %rbp, %rsp"); // Deallocate stack space
		this.out.println("pop %rbp"); // Return bp
		this.out.println("retq\n\n"); // Ret
	}


	@Override
	public void visit(Scope s) {
		for (Map.Entry<String, Entry> e : s.getEntries()) {
			if (e.getValue() instanceof Procedure) { // Accept all variables
				this.out.println(e.getKey() + ":");
				e.getValue().accept(this);
			}
		}
	}

	@Override
	public int visit(Node n) {
		return 0;
	}

	@Override
	public int visit(Instruction i) {
		return 0;
	}

	@Override
	public int visit(Assign a) {
		int exp = a.getExp().accept(this);
		a.getLoc().accept(this);
		this.out.println("pop %rax"); // location address
		this.out.println("pop %rbx"); // expression
		if (exp == 0) { // Number
			this.out.println("movq %rbx, (%rax)");
		} else { // Location need memcpy
			int size = a.getLoc().getType().size();
	//			this.out.println("sub $" + (size-SIZEOF_INT) + ", %rax");
	//			this.out.println("sub $" + (size-SIZEOF_INT) + ", %rbx");
			this.out.println("movq %rax, %rdi");
			this.out.println("movq %rbx, %rsi");
			this.out.println("movl $" + size + ", %edx");
			this.out.println("call memcpy");
		}
		if (a.getNext() != null) {
			a.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(If a) {
		int exp = a.getCondition().accept(this); // cmp done here
		int current = ++this.currentLabel;
		if (exp == 0) {
			this.out.println("pop %rax");
		} else {
			this.out.println("pop %rbx");
			this.out.println("movq (%rbx), %rax");
		}
		this.out.println("cmp $1, %rax");
		this.out.println("jne L" + current); // Jump if false

		a.getIfTrue().accept(this);
		if (a.getIfFalse() != null) { // else segment
			int current2 = ++this.currentLabel;
			this.out.println("jmp L" + current2);
			this.out.println("L" + current + ":");
			a.getIfFalse().accept(this);
			this.out.println("L" + current2 + ":");
		} else {
			this.out.println("L" + current + ":");
		}
		if (a.getNext() != null) {
			a.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(Repeat r) {
		int current = ++this.currentLabel;
		this.out.println("L" + current + ":"); // label to jmp to
		r.getInstructions().accept(this); // Accept instructions 
		int exp = r.getCondition().accept(this); // Do cmp
		if (exp == 0) {
		this.out.println("pop %rax");
		} else {
			this.out.println("pop %rbx");
			this.out.println("movq (%rbx), %rax");
		}
		this.out.println("cmpq $1, %rax");
		this.out.println("jne L" + current); // If not true, repeat

		return 0;
	}

	@Override
	public int visit(Read r) {
		r.getLoc().accept(this);
		this.out.println("pop %rax"); // Setup scanf
		this.out.println("push %rax");
		this.out.println("leaq (%rax), %rdx");
		this.out.println("movq %rdx, %rsi");
		this.out.println("movl $scanf_num, %edi");
		this.out.println("movl $0, %eax");
		this.out.println("call __isoc99_scanf");
		this.out.println("cmpl $-1, %eax"); // If EOF, done
		// Labels for different cmps
		int notNegOne = ++this.currentLabel;
		int notZero = ++this.currentLabel;
		int done = ++this.currentLabel;
		this.out.println("jne L" + notNegOne);
		this.out.println("pop %rbx");
		this.out.println("movq $-1, (%rbx)");
		this.out.println("jmp L" + done);
		
		
		// Done
		this.out.println("L" + notNegOne + ":");
		this.out.println("cmpl $0, %eax"); // If zero, done
		this.out.println("jne L" + notZero);
		this.out.println("pop %rbx");
		this.out.println("movq $-1, (%rbx)");
		this.out.println("jmp L" + done);
		
		this.out.println("L" + notZero + ":");
		this.out.println("pop %rbx");
		this.out.println("L" + done + ":");
		if (r.getNext() != null) {
			r.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(Write w) {
		int exp = w.getExp().accept(this);
		this.out.println("pop %rax");

		if (exp == 0) { // If number
			this.out.println("movq %rax, %rsi");
		} else { // If expression
			this.out.println("movq (%rax), %rsi");
		}
		this.out.println("movl $printf_num, %edi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		if (w.getNext() != null) {
			w.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(Expression e) {
		return 0;
	}

	@Override
	public int visit(Binary b) {
		int right = b.getRight().accept(this);
		int left = b.getLeft().accept(this);

		if (left == 0) { // If number
			this.out.println("pop %rax"); // left
		} else { // If location
			this.out.println("pop %rcx");
			this.out.println("movq (%rcx), %rax");
		}
		if (right == 0) { // If number
			this.out.println("pop %rbx"); // right
		} else { // If location needs dereferencing
			this.out.println("pop %rcx");
			this.out.println("movq (%rcx), %rbx");
		}
		switch (b.getOperator()) {
			case "+":
				this.out.println("addq %rbx, %rax");
				break;
			case "-":
				this.out.println("subq %rbx, %rax");
				break;
			case "DIV":
				this.out.println("cmp $0, %rbx"); // Check div by 0
				this.out.println("je div_by_zero");
				this.out.println("cltd");
				this.out.println("idivq %rbx");
				break;
			case "MOD":
				this.out.println("cmp $0, %rbx"); // Check mod by 0
				this.out.println("je mod_by_zero");
				this.out.println("cltd");
				this.out.println("idivq %rbx");
				this.out.println("movq %rdx, %rax");
				break;
			case "*":
				this.out.println("imulq %rbx, %rax");
				break;
			case "AND":
				this.out.println("and %rbx, %rax");
				break;
			case "OR":
				this.out.println("or %rbx, %rax");
				break;
			case "NOT":
				this.out.println("sub $1, %rbx");
				this.out.println("neg %rbx");
				this.out.println("movq %rbx, %rax");
				break;
			case "=":
				this.out.println("cmpq %rax, %rbx");
				this.out.println("movq $0, %rax");
				this.out.println("movq $1, %rbx");
				this.out.println("cmove %rbx, %rax");
				break;
			case "#":
				this.out.println("cmpq %rax, %rbx");
				this.out.println("movq $0, %rax");
				this.out.println("movq $1, %rbx");
				this.out.println("cmovne %rbx, %rax");
				break;
			case ">":
				this.out.println("cmpq %rax, %rbx");
				this.out.println("movq $0, %rax");
				this.out.println("movq $1, %rbx");
				this.out.println("cmovl %rbx, %rax");
				break;
			case "<":
				this.out.println("cmpq %rax, %rbx");
				this.out.println("movq $0, %rax");
				this.out.println("movq $1, %rbx");
				this.out.println("cmovg %rbx, %rax");
				break;
			case ">=":
				this.out.println("cmpq %rax, %rbx");
				this.out.println("movq $0, %rax");
				this.out.println("movq $1, %rbx");
				this.out.println("cmovle %rbx, %rax");
				break;
			case "<=":
				this.out.println("cmpq %rax, %rbx");
				this.out.println("movq $0, %rax");
				this.out.println("movq $1, %rbx");
				this.out.println("cmovge %rbx, %rax");
				break;
			
				
		}
		this.out.println("push %rax");
		return 0;
	}

	@Override
	public int visit(Number n) { // Push number
		this.out.println("movq $" + n.getNum().getValue() + ", %rax"); 
		this.out.println("push %rax");
		return 0;
	}

	@Override
	public int visit(Location l) {
		return 0;
	}
	
	public void visit(parser.symbolTable.Variable v) { // pushes address, uses global base pointer
		this.out.println("movq $" + v.getLocation() + ", %rax");
		this.out.println("add _globals, %rax"); // Offset from main bp
		this.out.println("push %rax");
	}
	
	public void visit(LocalVariable v) {
		this.out.println("movq $" + v.getLocation() + ", %rax");
		this.out.println("add %rbp, %rax"); // Offset from current bp
		this.out.println("push %rax");
	}
	
	public void visit(FormalVariable v) {
		if (Singleton.isValueType(v.getType())) {
			this.out.println("movq $" + v.getLocation() + ", %rax");
			this.out.println("add %rbp, %rax"); // Offset from current bp
			this.out.println("push %rax");
		} else {
			this.out.println("movq $" + v.getLocation() + ", %rax");
			this.out.println("add %rbp, %rax"); // Offset from current bp
			this.out.println("push (%rax)"); // Dereference passed by reference type
		}
	}

	@Override
	public int visit(parser.ast.Variable v) { // Call current Variable visit
		parser.symbolTable.Variable var = v.getVar();
		if (var instanceof LocalVariable) {
			this.visit((LocalVariable) var);
		} else if (var instanceof FormalVariable) {
			this.visit((FormalVariable) var);
		} else {
			this.visit(var);
		}
		return 1;
	}

	@Override
	public int visit(Index i) { // Pushes adjusted address
		int exp = i.getExp().accept(this);
		i.getLoc().accept(this);
		this.out.println("pop %rcx"); // rcx holds ref to start
		if (exp == 0) { // If number
			this.out.println("pop %rax");
		} else { // If location
			this.out.println("pop %rbx");
			this.out.println("movq (%rbx), %rax");
		}
		this.out.println("cmpq $" + ((Array) i.getLoc().getType()).getLength() + ", %rax"); // Bounds check, unsigned to check both
		this.out.println("jae array_out_of_bounds");
		int size = ((Array) i.getLoc().getType()).getElemType().size();
		this.out.println("imulq $" + size + ", %rax"); // Skip sizeof element
		this.out.println("addq %rax, %rcx");
		this.out.println("push %rcx");
		return 1;
	}

	@Override
	public int visit(Field f) { // Pushes adjusted address
		int offset = f.getVar().getVar().getLocation();
		f.getLoc().accept(this);
		this.out.println("pop %rax");
		this.out.println("addq $" + offset + ", %rax"); // Get adjusted address
		this.out.println("push %rax");
		return 1;
	}


	@Override
	public int visit(ProcedureCall proc) {
		for (int i=proc.getActuals().size()-1;i>=0;i--) {
			proc.getActuals().get(i).accept(this);
			if (Singleton.isValueType(proc.getActuals().get(i).getType()) && proc.getActuals().get(i) instanceof Location) {
				this.out.println("pop %rax");
				this.out.println("push (%rax)");
			}
		} // Push all args in reverse order since first formal has lowest address
		this.out.println("call " + proc.getFunction());
		this.out.println("addq $" + proc.getActuals().size() * SIZEOF_INT + ", %rsp");
		if (proc.getNext() != null) {
			proc.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(FunctionCall func) {
		for (int i=func.getActuals().size()-1;i>=0;i--) {
			func.getActuals().get(i).accept(this);
			if (Singleton.isValueType(func.getActuals().get(i).getType()) && func.getActuals().get(i) instanceof Location) {
				this.out.println("pop %rax");
				this.out.println("push (%rax)");
			}
		} // Push all args in reverse order since first formal has lowest address
		this.out.println("call " + func.getIdent());
		this.out.println("addq $" + func.getActuals().size() * SIZEOF_INT + ", %rsp");
		this.out.println("push %rax");
		return 0;
	}

	@Override
	public void visit(Integer i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Array ra) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Record r) {
		// TODO Auto-generated method stub
		
	}

	

}
