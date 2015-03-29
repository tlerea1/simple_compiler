package interpreter.environment;

import parser.symbolTable.Type;

public abstract class Box {
	/**
	 * Produces a perfect deep clone of the instance Box.
	 * @return returns the clone
	 */
	public abstract Box clone();
	/**
	 * Gets the symbol table type object of the instance Box.
	 * @return the type object
	 */
	public abstract Type getType();
}
