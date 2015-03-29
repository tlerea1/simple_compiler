package parser.ast;

import parser.ParserException;
import parser.symbolTable.Constant;
import parser.symbolTable.Integer;
import parser.symbolTable.Type;
import visitor.ASTVisitor;

public class Binary extends Expression {
	private String operator;
	private Expression left;
	private Expression right;
	
	public Binary(Expression left, Expression right, String op) {
		this.setOperator(op);
		this.setLeft(left);
		this.setRight(right);
	}
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		if (!operator.equals("+") && !operator.equals("-") 
				&& !operator.equals("*") && !operator.equals("DIV")
				&& !operator.equals("MOD")) {
			throw new ParserException("Making binary with invalid operator");
		}
		this.operator = operator;
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
	
	public Expression fold() {
		this.left = this.left.fold();
		this.right = this.right.fold();

		if (this.left instanceof Number
				&& this.right instanceof Number) {
			Type t = ((Number) this.left).getNum().getType();
			if (t instanceof Integer) {
				int left = ((Number) this.left).getNum().getValue();
				int right = ((Number) this.right).getNum().getValue();
				switch (this.operator) {
					case "+":
						return new Number(new Constant(left+right, t));
					case "-":
						return new Number(new Constant(left - right, t));
					case "*":
						return new Number(new Constant(left * right, t));
					case "DIV":
						if (right == 0) {
							throw new ParserException("Division by 0 is not allowed");
						}
						return new Number(new Constant(left / right, t));
					case "MOD":
						if (right == 0) {
							throw new ParserException("Mod by 0 is not allowed");
						}
						return new Number(new Constant(left % right, t));
					default:
						throw new ParserException("binary fold: Invalid opperator");
				}
			} else {
				return this; // Can only fold INTEGER CONSTs
			}
		} else {
			return this;
		}
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
}
