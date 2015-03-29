package parser.ast;

import visitor.ASTVisitor;

public abstract class Expression extends Node {
	public abstract Expression fold();
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
