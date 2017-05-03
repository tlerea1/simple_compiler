package amd64;

import java.io.PrintStream;

public class ExpressionValue extends Item {
	private String register;
	
	public ExpressionValue(String register) {
		this.setRegister(register);
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}
	
	public String toString() {
		return getRegister();
	}
	
	public void free(RegisterAllocator ra) {
		// Free the register holding the value
		ra.push(getRegister());
		setRegister(null);
	}

	@Override
	public String moveAsPointer(String register) {
		// Cannot be a pointer
		return "";
	}

	@Override
	public String moveTo(Memory mem, RegisterAllocator ra) {
		return mem.takeFrom(this, ra);
	}

	@Override
	public Memory index(ConstantOffset mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		out.println("sal $" + (int) (Math.log(elemSizeInBytes) / Math.log(2)) + ", " + this.getRegister()); // Shift to jump elements in array
		String newReg = ra.pop();
		out.println("leaq " + mem + ", " + newReg);
		out.println("addq " + newReg + ", " + this.getRegister()); // add offset to original
		ra.push(newReg); // Free index register
		return new Address(this.getRegister()); // Return address after applied indexing
	}

	@Override
	public Memory index(Address mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		out.println("sal $" + (int) (Math.log(elemSizeInBytes) / Math.log(2)) + ", " + this); // Shift to jump elements in array
		out.println("addq " + this + ", " + mem.getRegister()); // add offset to original
		this.free(ra);
		return mem; // Return adjusted address
	}
	
}
