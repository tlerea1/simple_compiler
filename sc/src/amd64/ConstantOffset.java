package amd64;

public class ConstantOffset extends Item {
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
}
