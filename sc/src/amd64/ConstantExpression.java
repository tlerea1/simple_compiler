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
	
	public String toString() {
		return "$" + getValue();
	}

	@Override
	public void free(RegisterAllocator ra) {
		// No need to free anything
		return;
	}

	@Override
	public String moveAsPointer(String register) {
		// Cant move as pointer
		return "";
	}

	@Override
	public String moveTo(Memory mem, RegisterAllocator ra) {
		return mem.takeFrom(this, ra);
	}
	
}
