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
}
