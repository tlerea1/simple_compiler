package interpreter;

import interpreter.environment.ArrayBox;
import interpreter.environment.Box;
import interpreter.environment.Environment;
import interpreter.environment.IntegerBox;
import interpreter.environment.RecordBox;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Expression;
import parser.ast.Field;
import parser.ast.FunctionCall;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Number;
import parser.ast.ProcedureCall;
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Variable;
import parser.ast.Write;
import parser.symbolTable.Array;
import parser.symbolTable.Constant;
import parser.symbolTable.Scope;
import parser.symbolTable.procedures.Procedure;
import util.Singleton;

/**
 * Class for interpreting SIMPLE programs.
 * @author tuvialerea
 *
 */
public class Interpreter {
	private Instruction ast;
	private Scope st;
	private Environment environment;
	private Scanner stdin;
	
	/**
	 * Interpreter Constructor.
	 * @param ast the Abstract Syntax Tree from the parser.
	 * @param st the Symbol Table from the parser.
	 */
	public Interpreter(Instruction ast, Scope st) {
		this.ast = ast;
		this.st = st;
		this.environment = st.getEnvironment();
		stdin = new Scanner(System.in);
	}
	
	/**
	 * Runts the interpreter.
	 */
	public void Interpret() {
		this.Interpret(this.ast);
	}
	
	private void Interpret(Instruction current) { // Runs the correct interpret function
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
			} else if (current instanceof ProcedureCall) {
				this.Interpret((ProcedureCall) current);
			} else {
				throw new InterpreterException("interpreter: Found unknown instruction type");
			}
			current = current.getNext();
		}
	}
	
	private void Interpret(Assign inst) {
		Box left = evaluate(inst.getLoc());
		Expression right = evaluate(inst.getExp());
		if (right instanceof Location) { // assignment of array or record
			Box rightBox = evaluate((Location) right);
			if (left.getType() == rightBox.getType()) { // Correct type assignment
				if (left instanceof IntegerBox) {
					((IntegerBox) left).setValue(((IntegerBox)rightBox).getValue());
				} else if (left instanceof ArrayBox) { // copy all elements
					for (int i=0;i<((ArrayBox) left).length();i++) {
						((ArrayBox) left).set(i, ((ArrayBox) rightBox).get(i).clone());
					}
				} else if (left instanceof RecordBox) { // copy environment
					((RecordBox) left).setEnvironment(((RecordBox) rightBox).getEnvironment().clone());
				} else {
					throw new InterpreterException("unknown box type");
				}
			}
		} else if (right instanceof Number){ // assignment to an integer
			if (left instanceof IntegerBox) {
				((IntegerBox) left).setValue(((Number) right).getNum().getValue());
			} // Assigning to non integer checked by parser
		} else {
			throw new InterpreterException("unknown expression type, assign");
		}
	}
	
	private void Interpret(If inst) { // run if
		if (intToBool(((Number) evaluate(inst.getCondition())).getNum().getValue())) {
			Interpret(inst.getIfTrue());
		} else {
			Interpret(inst.getIfFalse());
		}
	}
	
	private void Interpret(Repeat inst) { // run repeat
		do {
			Interpret(inst.getInstructions());
		} while (intToBool(((Number) evaluate(inst.getCondition().getOpposite())).getNum().getValue()));
	}
	
	private void Interpret(Read inst) { // Run read
		IntegerBox box = (IntegerBox) evaluate(inst.getLoc());
		if (inst.getLoc().getType().equals(Singleton.getChar())) {
			if (this.stdin.hasNext(Pattern.compile(".{1}"))) {
				String s = this.stdin.next(Pattern.compile(".{1}"));
				if (s.length() != 1) {
					throw new InterpreterException("Read more than one char");
				}
				box.setValue(s.charAt(0));
			} else {
				box.setValue(-1);
			}
		} else {
			if (this.stdin.hasNextInt()) {
				box.setValue(this.stdin.nextInt());
			} else {
				box.setValue(-1); // End of input
			}
		}
	}
	
	private void Interpret(Write inst) {
		Expression exp = evaluate(inst.getExp());
		if (exp.getType().equals(Singleton.getChar())) {
			if (exp instanceof Location) { // If writing a variable
				Box box = evaluate((Location) exp);
				System.out.println((char) ((IntegerBox) box).getValue()); // Must be integer. Leads me to believe this code is not needed
			} else { // If writing number
				System.out.println((char) ((Number) exp).getNum().getValue());
			}
		} else {
			if (exp instanceof Location) { // If writing a variable
				Box box = evaluate((Location) exp);
				System.out.println(((IntegerBox) box).getValue()); // Must be integer. Leads me to believe this code is not needed
			} else { // If writing number
				System.out.println(((Number) exp).getNum().getValue());
			}
		}
	}
	

	
	private void fillEnvironment(Procedure p, Environment e, List<Expression> actuals) {
		for (int i=0;i<actuals.size();i++) {
			if (actuals.get(i).getType() instanceof Array) {
				((Array) ((parser.symbolTable.Variable) p.getScope()
						.find(p.getFormals().get(i).getIdent())).getType())
						.setCurrentLength(((Array) actuals.get(i).getType()).getLength());
			}
			if (Singleton.isValueType(p.getFormals().get(i).getType())) {
				IntegerBox b = (IntegerBox) e.get(p.getFormals().get(i).getIdent()); // Right now all value types are integer boxs
				b.setValue((((Number) this.evaluate(actuals.get(i))).getNum().getValue()));
			} else {
				e.put(p.getFormals().get(i).getIdent(), this.evaluate((Location) actuals.get(i)));
			}
		}
	}
	
	private void Interpret(ProcedureCall proc) {
		Procedure p = ((Procedure) this.st.find(proc.getFunction()));
		Environment e = p.getEnvironment();
		this.fillEnvironment(p,e, proc.getActuals());
		e.setOuter(this.environment);
		this.environment = e;
		this.Interpret(p.getBody());
		this.environment = this.environment.getOuter();
		
	}
	
	private Box evaluate(Location loc) { // Runs correct evaluate function for locations
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
		Box location = evaluate(loc.getLoc()); // Post order
		if (location instanceof RecordBox) {
			RecordBox box = (RecordBox) location;
			return box.get(loc.getVar().getIdentifer());
		} else {
			throw new InterpreterException("Box not a record");
		}
	}
	
	private Box evaluate(Index loc) {
		Box location = evaluate(loc.getLoc()); // Post-order
		if (location instanceof ArrayBox) {
			ArrayBox box = (ArrayBox) location;
			return box.get(((Number) evaluate(loc.getExp())).getNum().getValue());
		} else {
			throw new InterpreterException("Box not an array");
		}
	}
	
	private Box evaluate(Variable loc) {
		return this.environment.get(loc.getIdentifer()); // Gets the variable from the program environement
	}
	
	private Expression evaluate(Expression exp) { // Function mainly removes binaries through getting variable values
		if (exp instanceof Location) { // Can either be an integer, array, or record type. If integer
									   // dereferences and returns number node, otherwise returns the location
			Box box = evaluate((Location) exp);
			if (box instanceof IntegerBox) {
				return new Number(new Constant(((IntegerBox) box).getValue(), exp.getType()));
			} else {
				return exp;
			}
		} else if (exp instanceof Binary) { // evaluates the binary
			return new Number(new Constant(evaluate((Binary) exp), exp.getType()));
		} else if (exp instanceof Number) { // returns the number
			return exp;
		} else if (exp instanceof FunctionCall ) {
			return evaluate((FunctionCall) exp);
		
		} else {
			throw new InterpreterException("unknown Expression type");
		}
	}
	
	private int evaluate(Binary exp) {
		int left = ((Number) evaluate(exp.getLeft())).getNum().getValue(); // pre-enforced by parser context conditions
		switch (exp.getOperator()) { // Short-circuit evaluation
			case "AND":
				if (left == 0) {
					return 0;
				}
				break;
			case "OR":
				if (left == 1) {
					return 1;
				}
				break;
		}
		int right = ((Number) evaluate(exp.getRight())).getNum().getValue();//Must be evaluatable to numbers
		
		switch(exp.getOperator()) { // Do operation return result
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
			case "AND":
				return left & right;
			case "OR":
				return left | right;
			case "NOT":
				return -1*(right-1);
			case "=":
				return boolToInt(left == right);
			case ">":
				return boolToInt(left > right);
			case "<":
				return boolToInt(left < right);
			case "#":
				return boolToInt(left != right);
			case ">=":
				return boolToInt(left >= right);
			case "<=":
				return boolToInt(left <= right);
			
			default:
				throw new InterpreterException("unknown operator in binary");
		}
	}
	
	private Expression evaluate(FunctionCall func) {
		Environment e = func.getProcedure().getEnvironment();
		this.fillEnvironment(func.getProcedure(),e, func.getActuals());
		e.setOuter(this.environment);
		this.environment = e;
		this.Interpret(func.getProcedure().getBody());
		Expression exp = this.evaluate(func.getProcedure().getRet());
		this.environment = this.environment.getOuter();
		return exp;
	}
	
	private int boolToInt(boolean b) {
		if (b) {
			return 1;
		} else {
			return 0;
		}
	}
	
	private boolean intToBool(int i) {
		return i == 1;
	}
}
