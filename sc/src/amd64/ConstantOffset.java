package amd64;

import java.io.PrintStream;

public class ConstantOffset extends Memory {
	private int offset;
	private String register;
	
	public ConstantOffset(int offset, String register) {
		this.offset = offset;
		this.register = register;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}
	
	public String toString() {
		return getOffset() + "(" + getRegister() + ")";
	}

	@Override
	public void free(RegisterAllocator ra) {
		if (! getRegister().equals("%r15") && ! getRegister().equals("%rbp")) {
			ra.push(getRegister());
			setRegister(null);
		}
	}

	@Override
	public String moveAsPointer(String register) {
		return "leaq " + this + ", " + register;
	}

	@Override
	public String takeFrom(ConstantOffset c, RegisterAllocator ra) {
		String reg = ra.pop();
		ra.push(reg); // Just need to get a register that is free right now
		return "leaq " + c + ", " + reg + '\n'
			 + "movq (" + reg + "), " + reg + '\n'
			 + "movq " + reg + ", " + this;
	}

	@Override
	public String takeFrom(Address a, RegisterAllocator ra) {
		ra.push(a.getRegister());
		return "movq " + a + ", " + a.getRegister() + '\n' // Dereference exp
			 + "movq " + a.getRegister() + ", " + this;
	}

	@Override
	public String takeFrom(ExpressionValue e, RegisterAllocator ra) {
		ra.push(e.getRegister());
		return "movq " + e.getRegister() + ", " + this;
	}

	@Override
	public String takeFrom(ConstantExpression c, RegisterAllocator ra) {
		return "movq " + c + ", " + this;
	}

	@Override
	public String moveTo(Memory mem, RegisterAllocator ra) {
		return mem.takeFrom(this, ra);
	}

	@Override
	public ConstantOffset offset(int offset) {
		return new ConstantOffset(this.getOffset() + offset, this.getRegister());
	}

	@Override
	public Memory indexBy(Item expression, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		return expression.index(this, ra, out, elemSizeInBytes);
	}

	@Override
	public Memory index(ConstantOffset mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		String newReg1 = ra.pop();
		out.println("movq " + this + ", " + newReg1); // newReg1 holds index
		out.println("sal $" + (int) (Math.log(elemSizeInBytes) / Math.log(2)) + ", " + newReg1); // Shift to jump elements in array
		String newReg2 = ra.pop();
		out.println("leaq " + mem + ", " + newReg2); // newReg2 holds address of location
		out.println("addq " + newReg1 + ", " + newReg2); // add offset to original
		ra.push(newReg1); // Free Temp register
		return new Address(newReg2); // Return address after applied indexing
	}

	@Override
	public Memory index(Address mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		String newReg = ra.pop();
		out.println("movq " + this + ", " + newReg); // newReg holds index
		out.println("sal $" + (int) (Math.log(elemSizeInBytes) / Math.log(2)) + ", " + newReg); // Shift to jump elements in array
		out.println("addq " + newReg + ", " + mem.getRegister()); // add offset to original
		ra.push(newReg); // Free index register
		return mem;
	}
}
