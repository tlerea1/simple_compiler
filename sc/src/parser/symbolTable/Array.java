package parser.symbolTable;

/**
 * Class to represent an ARRYA type in SIMPLE.
 * @author tuvialerea
 *
 */
public class Array extends Type {
	private int length;
	private Type elemType;
	
	/**
	 * Array constructor.
	 * @param len the length of the array
	 * @param type the type of the elements of the array
	 */
	public Array(int len, Type type) {
		this.length = len;
		this.elemType = type;
	}

	/**
	 * Length getter.
	 * @return the length of the array
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Element type getter.
	 * @return the type of the elements of the array
	 */
	public Type getElemType() {
		return elemType;
	}
	
	/**
	 * Array toString.
	 * @return a debugging string representation of the array
	 */
	public String toString() {
		return "ARRAY " + this.length + " OF " + this.elemType;
	}

	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}
}
