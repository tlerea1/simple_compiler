
public class Constant extends Entry {
	private Type type;
	private int value;
	
	public Constant(int val, Type type) {
		this.setValue(val);
		this.setType(type);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public String toString() {
		return "Constant:" + this.value;
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}
}
