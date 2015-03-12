package parser.ast;

public class Assign extends Instruction {
	private Location loc;
	private Expression exp;
	
	public Assign(Location location, Expression expression) {
		this.loc = location;
		this.exp = expression;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}
}
