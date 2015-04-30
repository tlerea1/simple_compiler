package amd64;

public class Address extends Item {
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
}
