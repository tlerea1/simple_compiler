package parser.ast;

import parser.symbolTable.Type;
import visitor.ASTVisitor;

public abstract class Location extends Expression {
	
	/**
	 * Function to return the resulting Type of this location.
	 * @return the Type
	 */
	public abstract Type getType();
	
	public Expression fold() {
		return this;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
