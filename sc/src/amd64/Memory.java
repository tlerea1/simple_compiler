package amd64;

public abstract class Memory extends Item {

	
	public abstract String takeFrom(ConstantOffset c, RegisterAllocator ra);
	public abstract String takeFrom(Address a, RegisterAllocator ra);
	public abstract String takeFrom(ExpressionValue e, RegisterAllocator ra);
	public abstract String takeFrom(ConstantExpression c, RegisterAllocator ra);
}
