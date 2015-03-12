package parser.ast;

import parser.ParserException;

public class Condition extends Node {
	private Expression left;
	private Expression right;
	private String operator;
	
	public Condition(Expression left, Expression right, String op) {
		this.left = left;
		this.right = right;
		this.setOperator(op);
	}

	@Override
	public String toString() {
		return "Condition [left=" + left + ", right=" + right + ", operator="
				+ operator + "]";
	}

	public Expression getLeft() {
		return left;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	public Expression getRight() {
		return right;
	}

	public void setRight(Expression right) {
		this.right = right;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		if (!operator.equals("=") && !operator.equals("#") 
				&& !operator.equals("<") && !operator.equals(">") 
				&& !operator.equals("<=") && !operator.equals(">=")) {
			throw new ParserException("Making IF with invalid operator");
		}
		this.operator = operator;
	}
	
	private String opposite(String opperator) {
		String op = "";
		switch(opperator) {
			case "=":
				op = "#";
				break;
			case "#":
				op = "=";
				break;
			case ">":
				op = "<=";
				break;
			case "<":
				op = ">=";
				break;
			case ">=":
				op = "<";
				break;
			case "<=":
				op = ">";
				break;
		}
		return op;
	}
	
	public Condition getOpposite() {
		return new Condition(this.left, this.right, this.opposite(this.operator));
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}
}
