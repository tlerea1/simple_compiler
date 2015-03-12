package parser.ast;

import parser.symbolTable.Type;

public class Variable extends Location {
	private parser.symbolTable.Variable var;
	
	public Variable(parser.symbolTable.Variable var) {
		this.setVar(var);
	}

	public parser.symbolTable.Variable getVar() {
		return var;
	}

	public void setVar(parser.symbolTable.Variable var) {
		this.var = var;
	}

	@Override
	public Type getType() {
		return var.getType();
	}
}
