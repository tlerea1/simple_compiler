package parser.symbolTable;

import amd64.CodeGen;
import visitor.ASTVisitor;

public class Field extends Variable {
	public Field(Type type) {
		super(type);
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
		
}
