package amd64;

public abstract class Item {
	
	public abstract String toString();
	public abstract void free(RegisterAllocator ra);
	
	public abstract String moveAsPointer(String register);
	
	public String moveAsValue(String register) {
		return "movq " + this + ", " + register;
	}
	
	// Functions to move To a Memory Item. If the Item has a register it 
	// will be freed before return!
	public abstract String moveTo(Memory mem, RegisterAllocator ra);
}
