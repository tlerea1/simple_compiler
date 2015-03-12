package parser.ast;

import parser.ParserException;
import parser.symbolTable.Record;
import parser.symbolTable.Type;
import visitor.ASTVisitor;

public class Field extends Location {
	private Location loc;
	private Variable var;
	
	public Field(Location loc, Variable var) {
		this.loc = loc;
		this.var = var;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public Variable getVar() {
		return var;
	}

	public void setVar(Variable var) {
		this.var = var;
	}

	@Override
	public Type getType() {
		Type t = loc.getType();
		if (t instanceof Record) {
			return var.getType();
		} else {
			throw new ParserException("Field type non-Record");
		}
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
