package parser.ast;

import amd64.Item;
import parser.symbolTable.Bool;
import parser.symbolTable.Constant;
import parser.symbolTable.Type;
import util.Singleton;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public class Number extends Expression {
	private Constant num;
	
	public Number(Constant num) {
		this.num = num;
	}

	public Constant getNum() {
		return num;
	}

	public void setNum(Constant num) {
		this.num = num;
	}

	@Override
	public Expression fold() {
		return this;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}

	@Override
	public Type getType() {
		return num.getType();
	}

	@Override
	public Expression getOpposite() {
		if (this.getType() instanceof Bool) {
			return new Number(new Constant(-1 * (this.num.getValue()-1), Singleton.getBool()));
		} else {
			throw new RuntimeException("Cannot invert non-boolean");
		}
	}

	@Override
	public Item accept(CodeGenVisitor v) {
		return v.visit(this);
	}
}
