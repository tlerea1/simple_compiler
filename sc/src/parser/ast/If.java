package parser.ast;

import visitor.ASTVisitor;

public class If extends Instruction {
	private Condition condition;
	private Instruction ifTrue;
	private Instruction ifFalse;
	
	public If(Condition con, Instruction ifTrue) {
		this.condition = con;
		this.ifTrue = ifTrue;
		this.ifFalse = null;
	}
	
	public If(Condition con, Instruction ifTrue, Instruction ifFalse) {
		this.condition = con;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
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
	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
