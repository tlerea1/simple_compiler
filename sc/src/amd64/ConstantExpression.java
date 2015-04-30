package amd64;

public class ConstantExpression extends Item {
	private int value;
	
	public ConstantExpression(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
