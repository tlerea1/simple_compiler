package parser.symbolTable;

import interpreter.environment.Box;


public abstract class Type extends Entry {
	public abstract Box getBox();
}
