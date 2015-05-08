package parser.symbolTable;

import amd64.CodeGen;
import visitor.Visitor;

/**
 * Class to represent a VAR in SIMPLE.
 * @author tuvialerea
 *
 */
public class Variable extends Entry {
	private Type type;
	private int location; // Offset from base pointer
	
	
	/**
	 * Variable constructor.
	 * @param type the type of the variable
	 */
	public Variable(Type type) {
		this.setType(type);
	}

	/**
	 * Type getter.
	 * @return the type of the variable
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Type setter.
	 * @param type the type to set to
	 */
	public void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * Variable toString.
	 * @return a debugging string representation
	 */
	public String toString() {
		return "Variable:" + this.type;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public int size() {
		return this.type.size();
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}
}
