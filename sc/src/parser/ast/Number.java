package parser.ast;

import parser.symbolTable.Constant;

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
	public void accept(Visitor v) {
		v.visit(this);
	}
}
