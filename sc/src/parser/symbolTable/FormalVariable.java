package parser.symbolTable;

import visitor.ASTVisitor;

public class FormalVariable extends Variable {

	public FormalVariable(Type type) {
		super(type);
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
	
}
