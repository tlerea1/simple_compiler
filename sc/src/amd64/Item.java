package amd64;

import java.io.PrintStream;

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
		
	// Function for finding the Memory that represents indexing mem by this
	public abstract Memory index(ConstantOffset mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes);
	// Function for finding the Memory that represents indexing mem by this
	public abstract Memory index(Address mem, RegisterAllocator ra, PrintStream out, int elemSizeInBytes);
}
