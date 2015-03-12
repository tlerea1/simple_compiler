package parser.ast;

import parser.symbolTable.Type;

public abstract class Location extends Expression {
	public abstract Type getType();
	
	public Expression fold() {
		return this;
	}
}
