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
}
