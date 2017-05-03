package parser.symbolTable;

import interpreter.environment.Box;
import interpreter.environment.IntegerBox;
import visitor.CodeGenVisitor;
import amd64.CodeGen;
import amd64.Item;

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

	@Override
	public Item accept(CodeGenVisitor v) {
		return v.visit(this);
	}
}
