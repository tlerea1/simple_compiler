
public class TypeEntry extends Entry {
	private Type type;
	
	public TypeEntry(Type type) {
		this.setType(type);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public String toString() {
		return "Type:" + this.type;
	}
}
