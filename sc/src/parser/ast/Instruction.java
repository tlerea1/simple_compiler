package parser.ast;

import visitor.ASTVisitor;

public abstract class Instruction extends Node {
	private Instruction next;

	public Instruction getNext() {
		return next;
	}

	public void setNext(Instruction next) {
		this.next = next;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
	
}
