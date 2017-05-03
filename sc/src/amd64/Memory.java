package amd64;

import java.io.PrintStream;

public abstract class Memory extends Item {

	
	public abstract String takeFrom(ConstantOffset c, RegisterAllocator ra);
	public abstract String takeFrom(Address a, RegisterAllocator ra);
	public abstract String takeFrom(ExpressionValue e, RegisterAllocator ra);
	public abstract String takeFrom(ConstantExpression c, RegisterAllocator ra);
	
	public abstract ConstantOffset offset(int offset);
	
	// Returns the Memory Item that represents indexing this Memory by the given expression
	public abstract Memory indexBy(Item expression, RegisterAllocator ra, PrintStream out, int elemSizeInBytes);
}
