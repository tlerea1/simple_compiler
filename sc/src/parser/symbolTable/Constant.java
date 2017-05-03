package parser.symbolTable;

import amd64.Item;
import visitor.CodeGenVisitor;
import visitor.Visitor;

/**
 * Class to represent a CONST in SIMPLE.
 * @author tuvialerea
 *
 */
public class Constant extends Entry {
	private Type type;
	private int value;
	
	/**
	 * Constant Constructor. 
	 * @param val the value of the constant. 
	 * @param type the type of the constant (for now always INTEGER).
	 */
	public Constant(int val, Type type) {
		this.setValue(val);
		this.setType(type);
	}

	/**
	 * Type getter.
	 * @return the type of the constant
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
	 * Value getter.
	 * @return the value of the constant
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Value setter. 
	 * @param value the value to set to.
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Constant toString.
	 * @return a debugging string representation of the constant
	 */
	public String toString() {
		return "Constant:" + this.value;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public Item accept(CodeGenVisitor v) {
		return v.visit(this);
	}
}
