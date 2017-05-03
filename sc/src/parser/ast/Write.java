package parser.ast;

import amd64.Item;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

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

	@Override
	public Item accept(CodeGenVisitor v) {
		return v.visit(this);
	}
}
