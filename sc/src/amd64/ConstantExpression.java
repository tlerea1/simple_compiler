package amd64;

import java.io.PrintStream;

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

	@Override
	public Memory index(ConstantOffset mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		if (this.getValue() < 0) {
			throw new AMD64Exception("Index out of bounds exception");
		}
		return mem.offset(this.getValue() * elemSizeInBytes);
	}

	@Override
	public Memory index(Address mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes) {
		if (this.getValue() < 0) {
			throw new AMD64Exception("Index out of bounds exception");
		}
		return mem.offset(this.getValue() * elemSizeInBytes);
	}
	
}
