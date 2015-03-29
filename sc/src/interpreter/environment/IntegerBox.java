package interpreter.environment;

import parser.symbolTable.Type;
import util.Singleton;

/**
 * Class to hold the memory for an Integer
 * @author tuvialerea
 *
 */
public class IntegerBox extends Box {
	private int value;
	
	/**
	 * IntegerBox constructor.
	 * @param val the value of the integer
	 */
	public IntegerBox(int val) {
		this.value = val;
	}

	/**
	 * Gets the value of the Box.
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the value of the Box.
	 * @param value the value to set to
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	public IntegerBox clone() {
		return new IntegerBox(this.value);
	}
	
	public Type getType() {
		return Singleton.getInteger();
	}
}
