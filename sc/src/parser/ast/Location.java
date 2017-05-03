package parser.ast;

import amd64.Memory;
import parser.symbolTable.Bool;
import parser.symbolTable.Constant;
import parser.symbolTable.Type;
import util.Singleton;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public abstract class Location extends Expression {
	
	/**
	 * Function to return the resulting Type of this location.
	 * @return the Type
	 */
	public abstract Type getType();
	
	public Expression fold() {
		return this;
	}
	
	public Expression getOpposite() {
		if (this.getType() instanceof Bool) {
			return new Binary(new Number(new Constant(1, Singleton.getBool())), this, "NOT");
		} else {
			throw new RuntimeException("Cannot negate non-bool");
		}
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
	
	public abstract Memory accept(CodeGenVisitor v);
	
}
