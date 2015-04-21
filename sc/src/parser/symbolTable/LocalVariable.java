package parser.symbolTable;

import visitor.Visitor;

public class LocalVariable extends Variable {

	public LocalVariable(Type type) {
		super(type);
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}

}
