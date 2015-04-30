package parser.symbolTable;

import visitor.ASTVisitor;

public class Field extends Variable {
	public Field(Type type) {
		super(type);
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
