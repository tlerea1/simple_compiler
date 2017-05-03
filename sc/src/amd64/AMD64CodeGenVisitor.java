package amd64;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Expression;
import parser.ast.FunctionCall;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Node;
import parser.ast.Number;
import parser.ast.ProcedureCall;
import parser.ast.Read;
import parser.ast.RelBinary;
import parser.ast.Repeat;
import parser.ast.Write;
import parser.symbolTable.Array;
import parser.symbolTable.Bool;
import parser.symbolTable.Char;
import parser.symbolTable.Constant;
import parser.symbolTable.Entry;
import parser.symbolTable.Field;
import parser.symbolTable.FormalVariable;
import parser.symbolTable.Integer;
import parser.symbolTable.LocalVariable;
import parser.symbolTable.Record;
import parser.symbolTable.Scope;
import parser.symbolTable.SimpleString;
import parser.symbolTable.Variable;
import parser.symbolTable.procedures.Len;
import parser.symbolTable.procedures.Procedure;
import util.Singleton;
import visitor.CodeGenVisitor;

public class AMD64CodeGenVisitor implements CodeGenVisitor {

	private PrintStream out;
	private RegisterAllocator registers;
	private int currentLabel;
	
	public AMD64CodeGenVisitor(PrintStream out) {
		this.out = out;
		this.registers = new RegisterAllocator(out);
	}
	
	@Override
	public Item visit(Constant v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Array v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Bool v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Char v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Integer v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Record v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(SimpleString v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConstantOffset visit(Variable var) {
		return new ConstantOffset(var.getLocation(), "%r15");
	}

	@Override
	public Memory visit(FormalVariable var) {
		if (! Singleton.isValueType(var.getType())) {
			// Non-value type parameters are passed by reference.
			String reg = this.registers.pop(); // TODO: Spill
			this.out.println("movq " + var.getLocation() + "(%rbp), " + reg);
			return new Address(reg);
		} else {
			// Value types are just offsets from the base pointer
			return new ConstantOffset(var.getLocation(), "%rbp");
		}
	}

	@Override
	public ConstantOffset visit(LocalVariable var) {
		return new ConstantOffset(var.getLocation(), "%rbp");
	}

	@Override
	public Item visit(Procedure p) {
		this.out.println("push %rbp"); // Save bp
		this.out.println("movq %rsp, %rbp");
		this.out.println("subq $" + p.size() + ", %rsp");
		this.out.println("movl $" + p.size() + ", %edx");
		this.out.println("movl $0, %esi");
		this.out.println("movq %rsp, %rdi");
		this.out.println("call memset"); // Allocate and zero space for locals
		
		if (p != null) {
			p.getBody().accept(this);
		}
		
		// TODO: Work with returns
		if (p.getRet() != null) {
			Item ret = p.getRet().accept(this);// Generate return code
			this.registers.use("%rax", null); // Cause whatever is in %rax to be moved out
			this.out.println("movq " + ret + ", %rax");
			ret.free(this.registers);
		}
		this.out.println("movq %rbp, %rsp"); // Deallocate stack space
		this.out.println("pop %rbp"); // Return bp
		this.out.println("retq\n\n"); // Ret
		return null;
	}

	@Override
	public Item visit(parser.symbolTable.procedures.Char v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Len v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Binary v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(RelBinary v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(FunctionCall v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Memory visit(parser.ast.Field f) {
		Memory location = f.getLoc().accept(this);
		int offset = f.getVar().getVar().getLocation();
		
		// Gets a new ConstantOffset that is just offset from the 
		// original location
		return location.offset(offset);
	}

	@Override
	public Memory visit(Index i) {
		Memory location = i.getLoc().accept(this);
		Item index = i.getExp().accept(this);
		
		return location.indexBy(index, this.registers, this.out, ((Array) i.getLoc().getType()).getElemType().size());
	}

	@Override
	public Memory visit(parser.ast.Variable v) {
		return v.getVar().accept(this);
	}

	@Override
	public Item visit(Number n) {
		return new ConstantExpression(n.getNum().getValue());
	}

	@Override
	public Item visit(Assign a) {
		Memory location = a.getLoc().accept(this);
		Item exp = a.getExp().accept(this);
		
		if (! Singleton.isValueType(a.getExp().getType())) {
			// TODO: Assignment by memcpy
			this.out.println(exp.moveAsPointer("%rsi"));
			this.out.println(location.moveAsPointer("%rdi"));
			
			this.registers.use("%rdx", null);
			this.out.println("movq $" + a.getExp().getType().size() + ", %rdx");
			List<String> registers = this.pushRegisters();
			this.out.println("call memcpy");
			this.popRegisters(registers);
			location.free(this.registers);
			exp.free(this.registers);
			return null;
		}
		
		// Should free exp register
		this.out.println(exp.moveTo(location, this.registers));
		// Should free location register assuming its not rbp or r15
		location.free(this.registers);
		return null;
	}

	@Override
	public Item visit(If a) {
		Item exp = a.getCondition().accept(this); // cmp done here
		int current = ++this.currentLabel;
		if (exp instanceof ConstantExpression) { // If condition is constant we can determine the outcome statically
			if (((ConstantExpression) exp).getValue() != 1) { // If not true
				if (a.getIfFalse() != null) { // If have false instructions
					a.getIfFalse().accept(this);
				}
			} else { // If true
				a.getIfTrue().accept(this); // Do true instructions
			}
			return null;
		}
		
		this.out.println("cmp $1, " + exp); // compare the outcome to 1
		exp.free(this.registers);
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
		return null;
	}

	@Override
	public Item visit(ProcedureCall v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Read v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Repeat r) {
		int current = ++this.currentLabel;
		this.out.println("L" + current + ":"); // label to jmp to
		r.getInstructions().accept(this); // Accept instructions 
		Item exp = this.visit(r.getCondition()); // Do cmp
		if (exp instanceof ConstantExpression) {
			if (((ConstantExpression) exp).getValue() != 1) {
				this.out.println("jmp L" + current);
			}
		} else {
			this.out.println("cmpq $1, " + exp);
			this.out.println("jne L" + current); // If not true, repeat
		}
		exp.free(this.registers);
		return null;
	}

	@Override
	public Item visit(Write v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Scope s) {
		for (Map.Entry<String, Entry> e : s.getEntries()) {
			if (e.getValue() instanceof Procedure) { // Accept all variables
				this.out.println(e.getKey() + ":");
				e.getValue().accept(this);
			}
		}
		return null;
	}

	@Override
	public Item visit(Entry v) {
		throw new RuntimeException("Should Never enter visit function for Entry");
	}

	@Override
	public Item visit(Node v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item visit(Expression v) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * Function pushes all inUse registers on the stack.
	 * Marks them as free
	 */
	private List<String> pushRegisters() {
		this.registers.use("%rax", null);
		this.registers.use("%rdx", null);
		List<String> reg = this.registers.inUse();
		for (int i=0;i<reg.size();i++) {
			this.out.println("push " + reg.get(i));
			this.registers.push(reg.get(i));
		}
		return reg;
	}
	
	/*
	 * Pulls all registers pushed by pushRegisters
	 * Marks them as in use.
	 */
	private void popRegisters(List<String> reg) {
		for (int i=reg.size() - 1;i>=0;i--) {
			this.out.println("pop " + reg.get(i));
			this.registers.pop(reg.get(i));
		}
	}

	@Override
	public Memory visit(Location v) {
		throw new RuntimeException("Should Never enter visit function for Location");	}

}
