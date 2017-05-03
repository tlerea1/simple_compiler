package amd64;

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
		// TODO Auto-generated method stub
		return null;
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
}
