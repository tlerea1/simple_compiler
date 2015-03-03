
public class Variable extends Entry {
	private Type type;
	
	public Variable(Type type) {
		this.setType(type);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public String toString() {
		return "Variable:" + this.type;
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}
}
