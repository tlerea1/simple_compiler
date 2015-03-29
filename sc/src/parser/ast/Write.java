package parser.ast;

import visitor.ASTVisitor;

public class Write extends Instruction {
	private Expression exp;
	
	public Write(Expression exp) {
		this.exp = exp;
	}

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
