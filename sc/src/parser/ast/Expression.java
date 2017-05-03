package parser.ast;

import amd64.Item;
import parser.symbolTable.Type;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public abstract class Expression extends Node {
	public abstract Expression fold();
	public abstract Type getType();
	public abstract Expression getOpposite();
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
	
}
