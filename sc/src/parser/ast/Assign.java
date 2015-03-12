package parser.ast;

import visitor.ASTVisitor;

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
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
