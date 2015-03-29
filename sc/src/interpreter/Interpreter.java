package interpreter;

import interpreter.environment.ArrayBox;
import interpreter.environment.Box;
import interpreter.environment.Environment;
import interpreter.environment.IntegerBox;
import interpreter.environment.RecordBox;

import java.util.Scanner;

import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Condition;
import parser.ast.Expression;
import parser.ast.Field;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Number;
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Variable;
import parser.ast.Write;
import parser.symbolTable.Constant;
import parser.symbolTable.Scope;
import util.Singleton;

public class Interpreter {
	private Instruction ast;
	private Scope st;
	private Environment environment;
	private Scanner stdin;
	
	public Interpreter(Instruction ast, Scope st) {
		this.ast = ast;
		this.st = st;
		this.environment = st.getEnvironment();
		stdin = new Scanner(System.in);
	}
	
	public void Interpret() {
		this.Interpret(this.ast);
	}
	
	private void Interpret(Instruction current) {
		while (current != null) {
			if (current instanceof Assign) {
				this.Interpret((Assign) current);
			} else if (current instanceof If) {
				this.Interpret((If) current);
			} else if (current instanceof Repeat) {
				this.Interpret((Repeat) current);
			} else if (current instanceof Read) {
				this.Interpret((Read) current);
			} else if (current instanceof Write) {
				this.Interpret((Write) current);
			} else {
				throw new InterpreterException("interpreter: Found unknown instruction type");
			}
			current = current.getNext();
		}
	}
	
	private void Interpret(Assign inst) {
		Box left = evaluate(inst.getLoc());
		Expression right = evaluate(inst.getExp());
		if (right instanceof Location) {
			Box rightBox = evaluate((Location) right);
			if (left.getType() == rightBox.getType()) {
				if (left instanceof IntegerBox) {
					((IntegerBox) left).setValue(((IntegerBox)rightBox).getValue());
				} else if (left instanceof ArrayBox) {
					for (int i=0;i<((ArrayBox) left).length();i++) {
						((ArrayBox) left).set(i, ((ArrayBox) rightBox).get(i).clone());
					}
				} else if (left instanceof RecordBox) {
					((RecordBox) left).setEnvironment(((RecordBox) rightBox).getEnvironment().clone());
				} else {
					throw new InterpreterException("unknown box type");
				}
			}
		} else if (right instanceof Number){
			if (left instanceof IntegerBox) {
				((IntegerBox) left).setValue(((Number) right).getNum().getValue());
			} // Assigning to non integer checked by parser
		} else {
			throw new InterpreterException("unknown expression type, assign");
		}
	}
	
	private void Interpret(If inst) {
		if (evaluate(inst.getCondition())) {
			Interpret(inst.getIfTrue());
		} else {
			Interpret(inst.getIfFalse());
		}
	}
	
	private void Interpret(Repeat inst) {
		do {
			Interpret(inst.getInstructions());
		} while (evaluate(inst.getCondition().getOpposite()));
	}
	
	private void Interpret(Read inst) {
		IntegerBox box = (IntegerBox) evaluate(inst.getLoc());
		if (this.stdin.hasNextInt()) {
			box.setValue(this.stdin.nextInt());
		} else {
			box.setValue(-1); // End of input
		}
	}
	
	private void Interpret(Write inst) {
		Expression exp = evaluate(inst.getExp());
		if (exp instanceof Location) {
			Box box = evaluate((Location) exp);
			System.out.println(((IntegerBox) box).getValue());
		} else {
			System.out.println(((Number) exp).getNum().getValue());
		}
	}
	
	private Box evaluate(Location loc) {
		if (loc instanceof Field) {
			return evaluate((Field) loc);
		} else if (loc instanceof Index) {
			return evaluate((Index) loc);
		} else if (loc instanceof Variable) {
			return evaluate((Variable) loc);
		} else {
			throw new InterpreterException("Unknown location type");
		}
	}
	
	private Box evaluate(Field loc) {
		Box location = evaluate(loc.getLoc());
		if (location instanceof RecordBox) {
			RecordBox box = (RecordBox) location;
			return box.get(loc.getVar().getIdentifer());
		} else {
			throw new InterpreterException("Box not a record");
		}
	}
	
	private Box evaluate(Index loc) {
		Box location = evaluate(loc.getLoc());
		if (location instanceof ArrayBox) {
			ArrayBox box = (ArrayBox) location;
			return box.get(((Number) evaluate(loc.getExp())).getNum().getValue());
		} else {
			throw new InterpreterException("Box not an array");
		}
	}
	
	private Box evaluate(Variable loc) {
		return this.environment.get(loc.getIdentifer()); 
	}
	
	private Expression evaluate(Expression exp) {
		if (exp instanceof Location) {
			Box box = evaluate((Location) exp);
			if (box instanceof IntegerBox) {
				return new Number(new Constant(((IntegerBox) box).getValue(), Singleton.getInteger()));
			} else {
				return exp;
			}
		} else if (exp instanceof Binary) {
			return new Number(new Constant(evaluate((Binary) exp), Singleton.getInteger()));
		} else if (exp instanceof Number) {
			return exp;
		} else {
			throw new InterpreterException("unknown Expression type");
		}
	}
	
	private int evaluate(Binary exp) {
		int left = ((Number) evaluate(exp.getLeft())).getNum().getValue();
		int right = ((Number) evaluate(exp.getRight())).getNum().getValue();
		
		switch(exp.getOperator()) {
			case "+":
				return left + right;
			case "-":
				return left - right;
			case "*":
				return left * right;
			case "DIV":
				if (right != 0) {
					return left / right;
				} else {
					throw new InterpreterException("RuntimeError: division by zero");
				}
			case "MOD":
				if (right != 0) {
					return left % right;
				} else {
					throw new InterpreterException("RuntimeError: modulo by zero");
				}
			default:
				throw new InterpreterException("unknown operator in binary");
		}
	}
	
	private boolean evaluate(Condition con) {
		int left = ((Number) evaluate(con.getLeft())).getNum().getValue();
		int right = ((Number) evaluate(con.getRight())).getNum().getValue();
		
		switch (con.getOperator()) {
			case "=":
				return left == right;
			case ">":
				return left > right;
			case "<":
				return left < right;
			case "#":
				return left != right;
			case ">=":
				return left >= right;
			case "<=":
				return left <= right;
			default:
				throw new InterpreterException("unknown condition operator");
		}
	}
}
