package parser.symbolTable;

import visitor.Visitor;


public abstract class Entry {
	
	public void accept(Visitor v) {
		v.visit(this);
	}
}
