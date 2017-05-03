package parser.symbolTable;

import amd64.ConstantOffset;
import visitor.CodeGenVisitor;
import visitor.Visitor;

public class LocalVariable extends Variable {

	public LocalVariable(Type type) {
		super(type);
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public ConstantOffset accept(CodeGenVisitor v) {
		return v.visit(this);
	}

}
