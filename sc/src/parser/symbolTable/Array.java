package parser.symbolTable;

import amd64.CodeGen;
import interpreter.environment.ArrayBox;
import interpreter.environment.Box;
import util.Singleton;
import visitor.Visitor;

/**
 * Class to represent an ARRYA type in SIMPLE.
 * @author tuvialerea
 *
 */
public class Array extends Type {
	private int length;
	private Type elemType;
	private int currentLength;
	
	/**
	 * Array constructor.
	 * @param len the length of the array
	 * @param type the type of the elements of the array
	 */
	public Array(int len, Type type) {
		this.length = len;
		this.elemType = type;
		this.currentLength = len;
	}

	/**
	 * Length getter.
	 * @return the length of the array
	 */
	public int getLength() {
		return currentLength;
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
	
	public Box getBox() {
		return new ArrayBox(this.length, this.elemType);
	}
	
	public int size() {
		return this.length * this.elemType.size() + CodeGen.SIZEOF_INT; // Extra length field
	}
	
	public boolean equals(Object o) {
		if (o instanceof Array) {
			Array other = (Array) o;
			if (this.length == -1 || other.length == -1) {
				return this.elemType.equals(other.elemType);
			} else {
				return super.equals(o);
			}
		}
		return false;
	}
	
	public int hashCode() {
		return super.hashCode();
	}
	
	public void setCurrentLength(int len) {
		this.currentLength = len;
	}
}
