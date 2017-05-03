package parser.symbolTable;

import amd64.Item;
import visitor.CodeGenVisitor;
import visitor.Visitor;


public abstract class Entry {
	
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public abstract Item accept(CodeGenVisitor v);
}
