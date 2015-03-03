
public class Record extends Type {
	private Scope scope;
	
	public Record(Scope scope) {
		this.scope = scope;
	}
	
	public String toString() {
		return "RECORD";
	}
	
	public Scope getScope() {
		return this.scope;
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}
}
