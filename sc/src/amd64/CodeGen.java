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
	private int offset = -4;
	private PrintStream out;
	
	private final int SIZEOF_INT = 8;
	
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
	
	private void genFunctions() {
		this.out.println(".globl main");
		this.out.println("array_out_of_bounds:");
		this.out.println("leaq printf_arg(%rip), %rdi");
		this.out.println("leaq array_str(%rip), %rsi");
		this.out.println("movq $0, %rax");
		this.out.println("call printf");
		this.out.println("movq $1, %rdi");
		this.out.println("call exit\n\n");
		
		this.out.println("main:\n");
		this.out.println("movq %rsp, %rbp");
		
	}
	
	private void text() {
		this.out.println("retq\n\n\n");
		this.out.println(".text");
		this.out.println("array_str:");
		this.out.println(".string \"error: Array index out of bounds!\\n\"");
		this.out.println("printf_arg:");
		this.out.println(".string \"%s\"");
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
		var.setLocation(this.offset);
		var.getType().accept(this); // Initiallizes to zero
	}

	@Override
	public void visit(Integer i) {
		this.out.println("movl $0, " + this.offset + "(%rbp)");
		this.offset -= i.size() * SIZEOF_INT;
	}

	@Override
	public void visit(Array ra) {
		this.out.println("movl $" + ra.getLength() + ", " + this.offset + "(%rbp)");
		this.offset -= SIZEOF_INT; // storing the length of the array
		for (int i=0;i<ra.getLength();i++) {
			ra.getElemType().accept(this);
		}
	}

	@Override
	public void visit(Record r) {
		r.getScope().accept(this);
	}

	@Override
	public void visit(Scope s) {
		for (Map.Entry<String, Entry> e : s.getEntries()) {
			if (e.getValue() instanceof Variable) {
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
		a.getExp().accept(this);
		a.getLoc().accept(this);
		this.out.println("pop %rax"); // location address
		this.out.println("pop %rbx"); // expression
		this.out.println("movq %rbx, (%rax)");
		if (a.getNext() != null) {
			a.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(If a) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Repeat r) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Read r) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Write w) {
		w.getExp().accept(this);
		this.out.println("pop %rax");
		this.out.println("movq %rax, %rsi");
		this.out.println("leaq printf_arg(%rip), %rdi");
		this.out.println("movq $0, %rax");
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

		if (left == 0) {
			this.out.println("pop %rax"); // left
		} else {
			this.out.println("pop %rcx");
			this.out.println("movq (%rcx), %rax");
		}
		if (right == 0) {
			this.out.println("pop %rbx"); // right
		} else { // needs dereferencing
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
				this.out.println("cltd");
				this.out.println("idivq %rbx");
				break;
			case "MOD":
				this.out.println("cltd");
				this.out.println("idivq %rbx");
				this.out.println("%rdx, %rax");
				break;
			case "*":
				this.out.println("imulq %rbx, %rax");
				break;
		}
		this.out.println("push %rax");
		return 0;
	}

	@Override
	public int visit(Number n) {
		this.out.println("push $" + n.getNum().getValue());
		return 0;
	}

	@Override
	public int visit(Location l) {
		return 0;
	}

	@Override
	public int visit(parser.ast.Variable v) {
		this.out.println("movq $" + v.getVar().getLocation() + ", %rax");
		this.out.println("add %rbp, %rax");
		this.out.println("push %rax");
		return 1;
	}

	@Override
	public int visit(Index i) {
		i.getExp().accept(this);
		i.getLoc().accept(this);
		this.out.println("pop %rcx");
		this.out.println("subq $4, %rcx"); // rcx holds length
		this.out.println("pop %rax");
		this.out.println("cmp $rax, (%rcx)");
		this.out.println("bge array_out_of_bounds");
		int size = ((Array) i.getLoc().getType()).getElemType().size();
		size *= 4;
		this.out.println("imulq $" + size + ", %rax");
		this.out.println("subq %rax, %rcx");
		this.out.println("push %rcx");
		return 1;
	}

	@Override
	public int visit(Field f) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int visit(Condition c) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private void allocate() {
		
	}

}
