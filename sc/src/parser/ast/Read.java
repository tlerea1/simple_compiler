package parser.ast;

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
	public void accept(Visitor v) {
		v.visit(this);
	}
}
