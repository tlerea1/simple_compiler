package parser.symbolTable;

import interpreter.environment.Box;


public abstract class Type extends Entry {
	/**
	 * Function to get the initialized box for the type.
	 * @return the box.
	 */
	public abstract Box getBox();
	/**
	 * Gets the size in multiples of sizeof(INTEGER) for target architecture 
	 * @return number of times of sizeof(int)
	 */
	public abstract int size();

}
