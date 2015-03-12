package parser.ast;

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
}
