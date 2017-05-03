package parser.symbolTable;

import amd64.Memory;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public class FormalVariable extends Variable {

	public FormalVariable(Type type) {
		super(type);
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
	
	public Memory accept(CodeGenVisitor v) {
		return v.visit(this);
	}
	
}
