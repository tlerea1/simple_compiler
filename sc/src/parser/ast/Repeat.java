package parser.ast;

import visitor.ASTVisitor;

public class Repeat extends Instruction {
	private Expression condition;
	private Instruction instructions;
	
	public Repeat(Expression con, Instruction instruction) {
		this.condition = con;
		this.instructions = instruction;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public Instruction getInstructions() {
		return instructions;
	}

	public void setInstructions(Instruction instructions) {
		this.instructions = instructions;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
