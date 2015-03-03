
public class Integer extends Type {

	
	public String toString() {
		return "INTEGER";
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}
}
