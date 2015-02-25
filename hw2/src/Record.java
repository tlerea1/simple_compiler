
public class Record extends Type {
	private Scope scope;
	
	public Record(Scope scope) {
		this.scope = scope;
	}
	
	public String toString() {
		return "RECORD";
	}
}
