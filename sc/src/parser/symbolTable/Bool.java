package parser.symbolTable;

import interpreter.environment.Box;
import interpreter.environment.IntegerBox;
import visitor.CodeGenVisitor;
import visitor.Visitor;
import amd64.CodeGen;
import amd64.Item;

public class Bool extends Type {
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public Box getBox() {
		return new IntegerBox(0);
	}
	
	public int size() {
		return CodeGen.SIZEOF_INT;
	}

	@Override
	public Item accept(CodeGenVisitor v) {
		// TODO Auto-generated method stub
		return v.visit(this);
	}
}
