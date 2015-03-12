package parser.ast;

import parser.symbolTable.Type;
import visitor.ASTVisitor;

public abstract class Location extends Expression {
	public abstract Type getType();
	
	public Expression fold() {
		return this;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
