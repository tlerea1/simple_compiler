package amd64;

import java.io.PrintStream;

import parser.symbolTable.Array;

public class Address extends Memory {
	private String register;
	
	public Address(String reg) {
		this.setRegister(reg);
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}
	
	public String toString() {
		return "(" + getRegister() + ")";
	}

	@Override
	public void free(RegisterAllocator ra) {
		ra.push(getRegister());
		setRegister(null);
	}

	@Override
	public String moveAsPointer(String register) {
		return "movq " + getRegister() + ", " + register;
	}

	@Override
	public String takeFrom(ConstantOffset c, RegisterAllocator ra) {
		String reg = ra.pop();
		Address a = new Address(reg); 
		return "leaq " + c + ", " + reg + '\n' // Makes reg an address
				+ this.takeFrom(a, ra); // Treat as address
	}

	@Override
	public String takeFrom(Address a, RegisterAllocator ra) {
		ra.push(a.getRegister());
		return "movq " + a + ", " + a.getRegister() + '\n'
			 + "movq " + a.getRegister() + ", " + this;// Dereference exp

	}

	@Override
	public String takeFrom(ExpressionValue e, RegisterAllocator ra) {
		ra.push(e.getRegister());
		return "movq " + e + ", (" + this.getRegister() + ")";
	}

	@Override
	public String takeFrom(ConstantExpression c, RegisterAllocator ra) {
		return "movq " + c + ", (" + this.getRegister() + ")";
	}

	@Override
	public String moveTo(Memory mem, RegisterAllocator ra) {
		return mem.takeFrom(this, ra);
	}

	@Override
	public ConstantOffset offset(int offset) {
		return new ConstantOffset(offset, this.getRegister());
	}

	@Override
	public Memory indexBy(Item expression, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		return expression.index(this, ra, out, elemSizeInBytes);
	}

	@Override
	public Memory index(ConstantOffset mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		out.println("movq " + this + ", " + this.getRegister()); // Dereference address		
		out.println("sal $" + (int) (Math.log(elemSizeInBytes) / Math.log(2)) + ", " + this.getRegister()); // Shift to jump elements in array
		String newReg = ra.pop();
		out.println("leaq " + mem + ", " + newReg); // Load address of Location to newReg
		out.println("addq " + newReg + ", " + this.getRegister()); // add offset to Location
		ra.push(newReg); // Free index register
		return this; // Return address after applied indexing
	}

	@Override
	public Memory index(Address mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		out.println("movq " + this + ", " + this.getRegister()); // Dereference index
		out.println("sal $" + (int) (Math.log(elemSizeInBytes) / Math.log(2)) + ", " + this.getRegister()); // Shift to jump elements in array
		out.println("addq " + this.getRegister() + ", " + mem.getRegister()); // add offset to original
		this.free(ra);
		return mem; // Return adjusted address
	}
	
}
