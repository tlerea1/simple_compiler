package parser.ast;

import parser.ParserException;
import parser.symbolTable.Constant;
import parser.symbolTable.Type;
import util.Singleton;

public class RelBinary extends Binary {
	
	public RelBinary(Expression left, Expression right, String op) {
		super(left, right, op);
	}
	
	public Expression fold() {
		this.left = this.left.fold();
		this.right = this.right.fold();

		if (this.left instanceof Number
				&& this.right instanceof Number) {
			int left = ((Number) this.left).getNum().getValue();
			int right = ((Number) this.right).getNum().getValue();
			switch (this.operator) {
				case "=":
					return new Number(new Constant(boolToInt(left == right), Singleton.getBool()));
				case "#":
					return new Number(new Constant(boolToInt(left != right), Singleton.getBool()));
				case "<":
					return new Number(new Constant(boolToInt(left < right), Singleton.getBool()));
				case ">":
					return new Number(new Constant(boolToInt(left > right), Singleton.getBool()));
				case "<=":
					return new Number(new Constant(boolToInt(left <= right), Singleton.getBool()));
				case ">=":
					return new Number(new Constant(boolToInt(left >= right), Singleton.getBool()));
				default:
					throw new ParserException("RelBinary fold: Invalid opperator");
			}

		} else {
			return this;
		}
	}
	
	private int boolToInt(boolean b) {
		if (b) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public Type getType() {
		return Singleton.getBool();
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
	
	public Expression getOpposite() {
		return new RelBinary(this.left, this.right, this.opposite(this.operator));
	}
}
