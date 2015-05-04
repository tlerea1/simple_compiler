package interpreter.environment;

import interpreter.InterpreterException;
import parser.symbolTable.Type;

/**
 * Class to hold the memory for an Array during interpretation of a SIMPLE program.
 * @author tuvialerea
 *
 */
public class ArrayBox extends Box {
	private int length;
	private Box[] data;
	private Type type;
	
	/**
	 * ArrayBox constructor.
	 * @param length length of the array
	 * @param elem the ST type object for the elements
	 */
	public ArrayBox(int length, Type elem) {
		this.length = length;
		this.type = elem;
		if (length != -1) { // If Array if generically sized
			this.data = new Box[this.length];
			for (int i=0;i<this.length;i++) {
				this.data[i] = this.type.getBox();
			}
		}
	}
	
	/**
	 * Gets the index'th element of the array
	 * @param index the index of the array
	 * @return the element at the given index.
	 */
	public Box get(int index) {
		try {
			return this.data[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InterpreterException("RuntimeError: Array Index out of bounds");
		}
	}
	
	/**
	 * Gets the length of the array.
	 * @return the length of the array
	 */
	public int length() {
		return this.length;
	}
	
	/**
	 * Sets the index'th element of the array to the given element.
	 * @param index the index of the array to set
	 * @param b the Box to set that element to
	 */
	public void set(int index, Box b) {
		try {
			this.data[index] = b;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InterpreterException("RuntimeError: Array Index out of bounds");
		}
	}
	
	public ArrayBox clone() {
		ArrayBox toReturn = new ArrayBox(this.length, this.type);
		toReturn.data = this.data.clone();
		return toReturn;
	}
	
	public Type getType() {
		return this.type;
	}
}
