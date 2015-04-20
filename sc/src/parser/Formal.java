package parser;

import parser.symbolTable.Type;

public class Formal {
	
	private String ident;
	private Type t;
	
	public Formal(String ident, Type t) {
		this.ident = ident;
		this.t = t;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public Type getType() {
		return t;
	}

	public void setType(Type t) {
		this.t = t;
	}
}
