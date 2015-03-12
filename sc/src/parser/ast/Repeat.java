package parser.ast;

public class Repeat extends Instruction {
	private Condition condition;
	private Instruction instructions;
	
	public Repeat(Condition con, Instruction instruction) {
		this.condition = con;
		this.instructions = instruction;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
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
	public void accept(Visitor v) {
		v.visit(this);
	}
}
