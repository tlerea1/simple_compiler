package parser.ast;

import amd64.Item;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public abstract class Node {
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 * @return returns the visitor's return value
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
	
	public abstract Item accept(CodeGenVisitor v);
}
