package parser.ast;

import visitor.ASTVisitor;

public class If extends Instruction {
	private Expression condition;
	private Instruction ifTrue;
	private Instruction ifFalse;
	
	public If(Expression con, Instruction ifTrue) {
		this.condition = con;
		this.ifTrue = ifTrue;
		this.ifFalse = null;
	}
	
	public If(Expression con, Instruction ifTrue, Instruction ifFalse) {
		this.condition = con;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public Instruction getIfTrue() {
		return ifTrue;
	}

	public void setIfTrue(Instruction ifTrue) {
		this.ifTrue = ifTrue;
	}

	public Instruction getIfFalse() {
		return ifFalse;
	}

	public void setIfFalse(Instruction ifFalse) {
		this.ifFalse = ifFalse;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
