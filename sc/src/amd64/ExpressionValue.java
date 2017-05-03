package amd64;

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
	
}
