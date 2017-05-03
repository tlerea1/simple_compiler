package parser.ast;

import amd64.Item;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public class Read extends Instruction {
	private Location loc;
	
	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public Read(Location loc) {
		this.loc = loc;
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
