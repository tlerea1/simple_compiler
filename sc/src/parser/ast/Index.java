package parser.ast;

import amd64.Memory;
import parser.ParserException;
import parser.symbolTable.Array;
import parser.symbolTable.Type;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public class Index extends Location {
	private Location loc;
	private Expression exp;
	
	public Index(Location loc, Expression exp) {
		this.loc = loc;
		this.exp = exp;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}
	
	public Type getType() {
		Type t = loc.getType();
		if (t instanceof Array) {
			return ((Array) t).getElemType();
		} else {
			throw new ParserException("Indexing non-array");
		}
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}

	@Override
	public Memory accept(CodeGenVisitor v) {
		return v.visit(this);
	}
}
