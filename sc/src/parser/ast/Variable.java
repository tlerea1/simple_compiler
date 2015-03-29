package parser.ast;

import parser.symbolTable.Type;
import visitor.ASTVisitor;

public class Variable extends Location {
	
	private String identifer;
	private parser.symbolTable.Variable var;
	
	public Variable(String ident, parser.symbolTable.Variable var) {
		this.identifer = ident;
		this.setVar(var);
	}

	public String getIdentifer() {
		return identifer;
	}

	public void setIdentifer(String identifer) {
		this.identifer = identifer;
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
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
