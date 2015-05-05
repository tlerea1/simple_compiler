package parser.symbolTable;

import interpreter.environment.Box;
import interpreter.environment.IntegerBox;
import amd64.CodeGen;

public class Char extends Type {
	
	public Char() {
		
	}

	@Override
	public Box getBox() {
		return new IntegerBox(0);
	}

	@Override
	public int size() {
		return CodeGen.SIZEOF_INT;
	}
}
