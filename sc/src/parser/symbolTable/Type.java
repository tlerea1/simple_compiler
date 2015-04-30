package parser.symbolTable;

import interpreter.environment.Box;


public abstract class Type extends Entry {
	/**
	 * Function to get the initialized box for the type.
	 * @return the box.
	 */
	public abstract Box getBox();
	/**
	 * Gets the size in number of bytes for target architecture (uses CodeGen.SIZEOF_INT)
	 * @return number of bytes
	 */
	public abstract int size();

}
