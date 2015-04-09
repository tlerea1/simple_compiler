package amd64;

import java.io.PrintStream;
import java.util.Map;

import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Condition;
import parser.ast.Expression;
import parser.ast.Field;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Node;
import parser.ast.Number;
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Write;
import parser.symbolTable.Array;
import parser.symbolTable.Constant;
import parser.symbolTable.Entry;
import parser.symbolTable.Integer;
import parser.symbolTable.Record;
import parser.symbolTable.Scope;
import parser.symbolTable.Type;
import parser.symbolTable.Variable;
import visitor.ASTVisitor;

public class CodeGen implements ASTVisitor {

	private Instruction ast;
	private Scope st;
	private int offset = -8;
	private PrintStream out;
	private int currentLabel = 0;
	
	public static final int SIZEOF_INT = 8;
	
	public CodeGen(Instruction ast, Scope st, PrintStream stream) {
		this.ast = ast;
		this.st = st;
		this.out = stream;
	}
	
	public void generateAMD64() {
		this.genFunctions();
		this.st.accept(this);
		this.ast.accept(this);
		this.text();
	}
	/*
	 * Function to output the start of main, includes stack allocation and memset
	 */
	private void genFunctions() {
		this.out.println(".text");

		this.out.println(".globl main");

		
		this.out.println("main:\n");
		this.out.println("push %rbp");
		this.out.println("movq %rsp, %rbp");
		this.out.println("subq $" + this.st.size() + ", %rsp");
		this.out.println("movl $" + this.st.size() + ", %edx");
		this.out.println("movl $0, %esi");
		this.out.println("movq %rsp, %rdi");
		this.out.println("call memset");
		
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
	}
	
	@Override
	public void visit(Entry e) {
		return;
	}

	@Override
	public void visit(Constant constant) {
		return;
	}

	@Override
	public void visit(Variable var) {
		var.setLocation(this.offset); // Set offset from rbp
		var.getType().accept(this); 
	}

	@Override
	public void visit(Integer i) {
		this.offset -= i.size(); // Move offset by sizeof INT
	}

	@Override
	public void visit(Array ra) {
		this.out.println("movq $" + ra.getLength() + ", " + this.offset + "(%rbp)"); // Save length
		this.offset -= SIZEOF_INT; // storing the length of the array
		for (int i=0;i<ra.getLength();i++) {
			ra.getElemType().accept(this);
		}
	}

	@Override
	public void visit(Record r) {
		int currentOff = 0;
		r.getScope().accept(this); // Accept all vars in scope
		for (Map.Entry<String, Entry> e : r.getScope().getEntries()) {
			if (e.getValue() instanceof Variable) { // Set offset from record start
				((Variable) e.getValue()).setLocation(currentOff);
				currentOff += ((Variable) e.getValue()).size();
			}
		}
	}

	@Override
	public void visit(Scope s) {
		for (Map.Entry<String, Entry> e : s.getEntries()) {
			if (e.getValue() instanceof Variable) { // Accept all variables
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
			this.out.println("sub $" + (size-SIZEOF_INT) + ", %rax");
			this.out.println("sub $" + (size-SIZEOF_INT) + ", %rbx");
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
		a.getCondition().accept(this); // cmp done here
		int current = ++this.currentLabel;
		switch (a.getCondition().getOperator()) { // If false jmp to either end or else segment
			case "=":
				this.out.println("jne L" + current);
				break;
			case "#":
				this.out.println("je L" + current);
				break;
			case ">":
				this.out.println("jle L" + current);
				break;
			case "<":
				this.out.println("jge L" + current);
				break;
			case ">=":
				this.out.println("jl L" + current);
				break;
			case "<=":
				this.out.println("jg L" + current);
				break;
		}
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
		r.getCondition().accept(this); // Do cmp
		switch (r.getCondition().getOperator()) { // If not true, repeat
			case "=":
				this.out.println("jne L" + current);
				break;
			case "#":
				this.out.println("je L" + current);
				break;
			case ">":
				this.out.println("jle L" + current);
				break;
			case "<":
				this.out.println("jge L" + current);
				break;
			case ">=":
				this.out.println("jl L" + current);
				break;
			case "<=":
				this.out.println("jg L" + current);
				break;
		}
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

	@Override
	public int visit(parser.ast.Variable v) { // Push address
		this.out.println("movq $" + v.getVar().getLocation() + ", %rax");
		this.out.println("add %rbp, %rax");
		this.out.println("push %rax");
		return 1;
	}

	@Override
	public int visit(Index i) { // Pushes adjusted address
		int exp = i.getExp().accept(this);
		i.getLoc().accept(this);
		this.out.println("pop %rcx"); // rcx holds ref -> length
		if (exp == 0) { // If number
			this.out.println("pop %rax");
		} else { // If location
			this.out.println("pop %rbx");
			this.out.println("movq (%rbx), %rax");
		}
		this.out.println("cmpq (%rcx), %rax"); // Bounds check, unsigned to check both
		this.out.println("jae array_out_of_bounds");
		int size = ((Array) i.getLoc().getType()).getElemType().size();
		this.out.println("imulq $" + size + ", %rax"); // Skip sizeof element
		this.out.println("subq %rax, %rcx");
		this.out.println("subq $" + SIZEOF_INT + ", %rcx"); // Skip length field
		this.out.println("push %rcx");
		return 1;
	}

	@Override
	public int visit(Field f) { // Pushes adjusted address
		int offset = f.getVar().getVar().getLocation();
		f.getLoc().accept(this);
		this.out.println("pop %rax");
		this.out.println("subq $" + offset + ", %rax"); // Get adjusted address
		this.out.println("push %rax");
		return 1;
	}

	@Override
	public int visit(Condition c) { // does a cmp left, right
		int left = c.getLeft().accept(this);
		int right = c.getRight().accept(this);
		this.out.println("pop %rax"); // right
		if (right != 0) {
			this.out.println("movq (%rax), %rax");
		}
		this.out.println("pop %rbx"); // left
		if (left != 0) {
			this.out.println("movq (%rbx), %rbx");
		}
		this.out.println("cmpq %rax, %rbx");
		return 0;
	}
	

}
