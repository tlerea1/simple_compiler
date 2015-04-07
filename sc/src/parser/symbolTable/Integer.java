package parser.symbolTable;

import interpreter.environment.Box;
import interpreter.environment.IntegerBox;
import visitor.Visitor;

/**
 * Class to represent the INTEGER type in SIMPLE. 
 * NOTE: This is a singleton class!!
 * @author tuvialerea
 *
 */
public class Integer extends Type {

	/**
	 * Integer toString.
	 * @return a debugging string representation
	 */
	public String toString() {
		return "INTEGER";
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public Box getBox() {
		return new IntegerBox(0);
	}
	
	public int size() {
		return 8;
	}
}
