package amd64;

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
			 + "movq " + a.getRegister() + ", (" + this.getRegister() + ")";// Dereference exp

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
	
}
