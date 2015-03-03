
public class Array extends Type {
	private int length;
	private Type elemType;
	
	public Array(int len, Type type) {
		this.length = len;
		this.elemType = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Type getElemType() {
		return elemType;
	}

	public void setElemType(Type elemType) {
		this.elemType = elemType;
	}
	
	public String toString() {
		return "ARRAY " + this.length + " OF " + this.elemType;
	}

	public void accept(Visitor v) {
		v.visit(this);
	}
}
