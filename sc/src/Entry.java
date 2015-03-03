
public abstract class Entry {
	
	public void accept(Visitor v) {
		v.visit(this);
	}
}
