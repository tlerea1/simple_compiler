package parser.ast;

import amd64.Item;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

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
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}

	@Override
	public Item accept(CodeGenVisitor v) {
		return v.visit(this);
	}
}
