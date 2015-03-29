package parser.ast;

import visitor.ASTVisitor;

public abstract class Node {
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 * @return returns the visitor's return value
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
