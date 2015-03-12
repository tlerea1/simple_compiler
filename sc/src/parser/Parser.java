package parser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import parser.ast.Write;
import parser.symbolTable.Array;
import parser.symbolTable.Constant;
import parser.symbolTable.Entry;
import parser.symbolTable.Integer;
import parser.symbolTable.Record;
import parser.symbolTable.Scope;
import parser.symbolTable.Type;
import parser.symbolTable.Variable;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenType;
import visitor.GraphicalVisitor;
import visitor.PlainVisitor;
import visitor.Visitor;

/**
 * The SC Parser class.
 * Meant to take tokens from the Scanner parse.
 * Throws ParserExceptions on errors. 
 * @author tuvialerea
 *
 */
public class Parser {
	
	private Scanner scan;
	private Observer obs;
	private List<Token> tokens;
	private int position;
	private Scope current;
	private visitor.Visitor STVisit;
	private visitor.ASTVisitor astVisit;
	private Integer singletonInt;
	private Instruction ast;
	
	/**
	 * Creates a new instance of the parser object.
	 * @param scan The scanner to get tokens from.
	 * @param graphical whether or not you want a 
	 */
	public Parser(Scanner scan, boolean graphical) {
		this.scan = scan;
		this.tokens = this.scan.all();
		this.position = 0;
		if (graphical) {
			this.obs = new GraphicalObserver();
			this.STVisit = new GraphicalVisitor();
			// TODO: add AST graphical
		} else {
			this.obs = new BasicObserver();
			this.STVisit = new PlainVisitor();
			this.astVisit = new visitor.PlainASTVisitor();
		}
		Scope universe = new Scope(null);
		this.singletonInt = new Integer();
		universe.insert("INTEGER", this.singletonInt);
		this.current = new Scope(universe);
	}
	
	/**
	 * Parses the given program. 
	 * The toString can then be called to give a String representation of the CST
	 */
	public void parse() {
		this.program();
	}
	
	/**
	 * Confirms that the next token has text == item.
	 * If not, a ParserException will be thrown.
	 * @param item the text to look for.
	 */
	private void hardMatch(String item) {
		Token next = this.next();
		this.obs.add(next);
		if (! next.getText().equals(item)) {
			error("Expected " + item, next.getStart());
		}
	}
	
	/**
	 * Confirms that the next token is an Identifier.
	 * If not, a ParserException is thrown.
	 * @return The identifier
	 */
	private String matchIdent() {
		Token next = this.next();
		this.obs.add(next);
		if (next.getType() != TokenType.IDENTIFIER) {
			error("Failed to find identifier", next.getStart());
		}
		return next.getText();
	}
	
	/**
	 * Confirms the Program non-terminal.
	 */
	private void program() {
		String startingIdentifier;
		this.obs.add("Program");
		this.obs.decend();
		this.hardMatch("PROGRAM");
		startingIdentifier = this.matchIdent();
		this.hardMatch(";");
		this.declarations();
		if (this.peak().getText().equals("BEGIN")) {
			this.hardMatch("BEGIN");
			this.ast = this.instructions();
		}
		this.hardMatch("END");
		if (! this.matchIdent().equals(startingIdentifier)) {
			throw new ParserException("PROGRAM identifier does not match terminating identifier");
		}
		this.hardMatch(".");
		Token next = this.next();
		if (next.getType() != TokenType.EOF) {
			error("Expected EOF", next.getStart());
		}
		
	}
	
	/**
	 * Function to add new Constant to the current scope.
	 * @param identifier the identifier
	 * @param value the integer value
	 */
	private void addConstant(String identifier, int value) {
		if (! this.current.local(identifier)) { // Check for already defined
			this.current.insert(identifier, new Constant(value, this.singletonInt));
		} else {
			throw new ParserException("Identifier <" + identifier + "> is already defined in current scope");
		}
	}
	
	/**
	 * Function to add new Variable to the current scope.
	 * @param identifier the identifier
	 * @param type the type
	 */
	private void addVariable(String identifier, Type type) {
		if (! this.current.local(identifier)) { // Check for already defined
			this.current.insert(identifier, new Variable(type));
		} else {
			throw new ParserException("Identifier <" + identifier + "> is already defined in current scope");
		}
	}
	
	/**
	 * Function to add Type to the current scope.
	 * @param identifier the identifier
	 * @param type the type
	 */
	private void addType(String identifier, Type type) {
		if (! this.current.local(identifier)) { // Check for already defined
			this.current.insert(identifier, type);
		} else {
			throw new ParserException("Identifer <" + identifier + "> is already defined in current scope");
		}
	}
	
	/**
	 * Confirms the Declarations Non-terminal.
	 */
	private void declarations() {
		this.obs.add("Declarations");
		this.obs.decend();
		Token next;
		while (this.peak().getText().equals("VAR") 
				|| this.peak().getText().equals("CONST") 
				|| this.peak().getText().equals("TYPE")) {
			next = this.peak();
			if (next.getText().equals("CONST")) {
				constDecl();
			} else if (next.getText().equals("VAR")) {
				varDecl();
			} else if (next.getText().equals("TYPE")) {
				typeDecl();
			}
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the ConstDecl Non-terminal.
	 */
	private void constDecl() {
		this.obs.add("ConstDecl");
		this.obs.decend();
		Token next = this.next();
		this.obs.add(next);
		while (this.peak().getType() == TokenType.IDENTIFIER) {
			String identifier = this.matchIdent();
			this.hardMatch("=");
			Expression exp = this.expression();
			if (exp instanceof Number) {
				int value = ((Number) exp).getNum().getValue();
				this.hardMatch(";");
				addConstant(identifier, value);
			} else {
				throw new ParserException("constDecl: expression does not fold to number");
			}
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the VarDecl Non-terminal.
	 */
	private void varDecl() {
		this.obs.add("VarDecl");
		this.obs.decend();
		this.obs.add(this.next());
		while (this.peak().getType() == TokenType.IDENTIFIER) {
			Collection<String> identifiers = this.identifierList();
			this.hardMatch(":");
			Type t = this.type();
			this.hardMatch(";");
			for (String s : identifiers) {
				addVariable(s, t);
			}
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the TypeDecl Non-terminal.
	 */
	private void typeDecl() {
		this.obs.add("TypeDecl");
		this.obs.decend();
		this.obs.add(this.next());
		while (this.peak().getType() == TokenType.IDENTIFIER) {
			String identifier = this.matchIdent();
			this.hardMatch("=");
			Type t = this.type();
			this.hardMatch(";");
			addType(identifier, t);
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the Type Non-terminal.
	 */
	private Type type() {
		this.obs.add("Type");
		this.obs.decend();
		Token next = this.peak();
		Type toReturn = null;
		if (next.getType() == TokenType.IDENTIFIER) {
			int start = next.getStart();
			String ident = matchIdent();
			Entry temp = this.current.find(ident);
			if (temp instanceof Type) { // Found the type!
				toReturn = (Type) temp;
			} else if (temp == null) { // Found no entry
				error("Type error identifer <" + ident + "> not yet defined", start);
			} else { // Found a non-type
				error("Type error identifier <" + ident + "> not Type", start);
			}
		} else if (next.getText().equals("ARRAY")) {
			this.hardMatch("ARRAY");
			Expression exp = this.expression();
			if (exp instanceof Number) {
				int len = ((Number) exp).getNum().getValue();
				this.hardMatch("OF");
				Type t = this.type();
				toReturn = new Array(len, t);
			} else {
				throw new ParserException("arrayDecl: length does not evaluate to number");
			}
		} else if (next.getText().equals("RECORD")) {
			this.hardMatch("RECORD");
			Scope scope = new Scope(this.current);
			this.current = scope;
			while (! this.peak().getText().equals("END")) {
				Collection<String> identifiers = this.identifierList();
				this.hardMatch(":");
				Type t = this.type();
				this.hardMatch(";");
				for (String s : identifiers) {
					addVariable(s, t);
				}
			}
			this.hardMatch("END");
			this.current = scope.getOuter();
			scope.setOuter(null); // Detach from program scope
			toReturn = new Record(scope);
		} else {
			error("Type error, expected (identifier | \"ARRAY\" | \"RECORD\"", next.getStart());
		}
		this.obs.accend();
		return toReturn;
	}
	
	private boolean isArithmeticable(Expression e) {
		if (e instanceof Number) {
			return true;
		} else if (e instanceof Location) {
			Type t = ((Location) e).getType();
			return t instanceof Integer;
		} else if (e instanceof Binary) {
			return true;
		}
		return false;
	}
	
	/**
	 * Confirms the Expression Non-terminal.
	 */
	private Expression expression() {
		this.obs.add("Expression");
		this.obs.decend();
		Expression toReturn;
		if (this.peak().getText().equals("+") || this.peak().getText().equals("-")) {
			Expression right = this.term();
			if (isArithmeticable(right)) {
				toReturn = new Binary(new Number(new Constant(1, this.singletonInt)), right, this.peak().getText());
			} else {
				throw new ParserException("expression: using arithmetic on non-integers");
			}
			this.obs.add(this.next());
		} else {
			toReturn = this.term();
		}
		while (this.peak().getText().equals("+") || this.peak().getText().equals("-")) {
			String op = this.peak().getText();
			this.obs.add(this.next());
			Expression right = this.term();
			if (isArithmeticable(toReturn) && isArithmeticable(right)) {
				toReturn = new Binary(toReturn, right, op);
			} else {
				throw new ParserException("expression: using arithmetic on non-integers");
			}
		}
		this.obs.accend();
		return toReturn.fold();
	}
	
	/**
	 * Confirms the Term Non-terminal.
	 */
	private Expression term() {
		this.obs.add("Term");
		this.obs.decend();
		Expression toReturn = this.factor();
		while (this.peak().getText().equals("*") || this.peak().getText().equals("DIV") || this.peak().getText().equals("MOD")) {
			String op = this.peak().getText();
			this.obs.add(this.next());
			Expression right = this.factor();
			if (isArithmeticable(toReturn) && isArithmeticable(right)) {
				toReturn = new parser.ast.Binary(toReturn, right, op);
			} else {
				throw new ParserException("term: using arithmetic on non-integers");
			}
		}
		this.obs.accend();
		return toReturn;
	}
	
	/**
	 * Confirms the Factor Non-terminal.
	 */
	private Expression factor() {
		this.obs.add("Factor");
		this.obs.decend();
		Token peak = this.peak();
		Expression exp = null;
		if (peak.getType() == TokenType.NUMBER) {
			exp = new Number(new Constant(java.lang.Integer.parseInt(peak.getText()), this.singletonInt));
			this.obs.add(this.next());
		} else if (peak.getText().equals("(")) {
			this.hardMatch("(");
			exp = this.expression();
			this.hardMatch(")");
		} else if (peak.getType() == TokenType.IDENTIFIER) {
			exp = this.designator();
		} else {
			error("Factor error: " + peak.getText(), peak.getStart());
		}
		this.obs.accend();
		return exp;
	}
	
	/**
	 * Confirms the Instructions Non-terminal.
	 */
	private Instruction instructions() {
		this.obs.add("Instructions");
		this.obs.decend();
		Instruction toReturn = this.instruction();
		Instruction current = toReturn;
		while (this.peak().getText().equals(";")) {
			this.hardMatch(";");
			current.setNext(this.instruction());
			current = current.getNext();
		}
		this.obs.accend();
		return toReturn;
	}
	
	/**
	 * Confirms the Instruction Non-terminal.
	 */
	private Instruction instruction() {
		Instruction toReturn = null;
		this.obs.add("Instruction");
		this.obs.decend();
		if (this.peak().getType() == TokenType.IDENTIFIER) {
			toReturn = this.assign();
		} else if (this.peak().getText().equals("IF")) {
			toReturn = this.if1();
		} else if (this.peak().getText().equals("REPEAT")) {
			toReturn = this.repeat();
		} else if (this.peak().getText().equals("WHILE")) {
			toReturn = this.while1();
		} else if (this.peak().getText().equals("READ")) {
			toReturn = this.read();
		} else if (this.peak().getText().equals("WRITE")) {
			toReturn = this.write();
		} else {
			error("Illegal Instruction", this.peak().getStart());
		}
		this.obs.accend();
		return toReturn;
	}
	
	/**
	 * Confirms the Assign Non-terminal.
	 */
	private Assign assign() {

		this.obs.add("Assign");
		this.obs.decend();
		Expression exp = this.designator();
		if (exp instanceof Location) {
			this.hardMatch(":=");
			Expression e = this.expression();
			this.obs.accend();
			if (e instanceof Location) {
				Type t = ((Location) e).getType();
				if (((Location) exp).getType() != t) { // Do not refer to the same type
					throw new ParserException("Assignment between two different types");
				}
			} else { // Binary or Number ie INTEGER
 				if (! (((Location) exp).getType() instanceof Integer)) {
 					throw new ParserException("Assigning Integer to non-integer");
 				}
			}
			return new Assign(((Location) exp), e);
		} else {
			throw new ParserException("assign: assigning to constant");
		}
	}
	
	/**
	 * Confirms the If Non-terminal.
	 */
	private If if1() {
		this.obs.add("If");
		this.obs.decend();
		this.hardMatch("IF");
		Condition c = this.condition();
		this.hardMatch("THEN");
		Instruction ifTrue = this.instructions();
		Instruction ifFalse = null;
		if (this.peak().getText().equals("ELSE")) {
			this.hardMatch("ELSE");
			ifFalse = this.instructions();
		}
		this.hardMatch("END");
		this.obs.accend();
		return new If(c, ifTrue, ifFalse);
	}
	
	/**
	 * Confirms the Condition Non-terminal.
	 */
	private Condition condition() {
		this.obs.add("Condition");
		this.obs.decend();
		Expression left = this.expression();
		String next = this.peak().getText();
		if (next.equals("=") || next.equals("#") 
				|| next.equals("<") || next.equals(">") 
				|| next.equals("<=") || next.equals(">=")) {
			this.obs.add(this.next());
		} else {
			error("Operator expected in condition", this.peak().getStart());
		}
		Expression right = this.expression();
		this.obs.accend();
		if (isArithmeticable(left) && isArithmeticable(right)) {
			return new Condition(left, right, next);
		} else {
			throw new ParserException("condition: comparing non-integers");
		}
	}
	
	/**
	 * Confirms the Repeat Non-terminal.
	 */
	private Repeat repeat() {
		this.obs.add("Repeat");
		this.obs.decend();
		this.hardMatch("REPEAT");
		Instruction i = this.instructions();
		this.hardMatch("UNTIL");
		Condition c = this.condition();
		this.hardMatch("END");
		this.obs.accend();
		return new Repeat(c, i);
	}
	
	/**
	 * Confirms the While Non-terminal.
	 */
	private Repeat while1() {
		this.obs.add("While");
		this.obs.decend();
		this.hardMatch("WHILE");
		Condition c = this.condition();
		this.hardMatch("DO");
		Instruction i = this.instructions();
		this.hardMatch("END");
		this.obs.accend();
		return new Repeat(c.getOpposite(), i);
	}
	
	/**
	 * Confirms the Read Non-terminal.
	 */
	private Read read() {
		this.obs.add("Read");
		this.obs.decend();
		this.hardMatch("READ");
		Expression exp = this.designator();
		this.obs.accend();
		if (exp instanceof Location) {
			return new Read((Location) exp);
		} else {
			throw new ParserException("read: reading to non-variable");
		}
	}
	
	/**
	 * Confirms the Write Non-terminal.
	 */
	private Write write() {
		this.obs.add("Write");
		this.obs.decend();
		this.hardMatch("WRITE");
		Expression e = this.expression();
		this.obs.accend();
		if (isArithmeticable(e)) {
			return new Write(e);
		} else {
			throw new ParserException("write: writing non-integer");
		}
	}
	
	/**
	 * Confirms the Designator Non-terminal.
	 */
	private Expression designator() {
		this.obs.add("Designator");
		this.obs.decend();
		String ident = this.matchIdent();
		Entry e = this.current.find(ident);
		parser.symbolTable.Variable var;
		Expression toReturn;
		if (e instanceof parser.symbolTable.Variable) {
			var = (parser.symbolTable.Variable) e;
			parser.ast.Variable v = new parser.ast.Variable(var);
			toReturn = this.selector(v);
		} else if (e instanceof Constant) { 
			this.selector(null);
			Constant c = ((Constant) e);
			toReturn = new Number(c);
		} else {
			throw new ParserException("Designator found refernce to type identifier");
		}

		this.obs.accend();
		return toReturn;
	}
	
	/**
	 * Confirms the Selector Non-terminal.
	 */
	private Location selector(parser.ast.Variable var) {
		this.obs.add("Selector");
		this.obs.decend();
		Location current = var;
		while (this.peak().getText().equals(".") || this.peak().getText().equals("[")) {
			if (this.peak().getText().equals("[")) {
				if (var == null) {
					throw new ParserException("selector: indexing constant");
				}
				this.hardMatch("[");
				Collection<parser.ast.Number> indecies = this.expressionList();
				this.hardMatch("]");
				for (parser.ast.Number i : indecies) {
					Index inx = new Index(current, i);
					inx.getType(); // Forces error checking
					current = inx;
				}
			} else if (this.peak().getText().equals(".")) {
				if (var == null) {
					throw new ParserException("selector: accessing field of constant");
				}
				this.hardMatch(".");
				String ident = this.matchIdent();
				Field f = checkRecord(current, ident);
				current = f;
			}
		}
		this.obs.accend();
		return current;
	}
	
//	private void checkArrayDimensions(Location currentVar, int num) {
//		if (currentVar instanceof parser.ast.Variable) {
//			parser.ast.Variable var = (parser.ast.Variable) currentVar;
//			Type current = var.getVar().getType();
//			for (int i=0;i<num;i++) {
//				if (current instanceof Array) {
//					current = ((Array) current).getElemType();
//				} else {
//					throw new ParserException("Indexing non-array");
//				}
//			}
//		} else {
//			throw new ParserException("Indexing non-variable");
//		}
//	}
	
	private Field checkRecord(Location loc, String ident) {
		Type t = loc.getType();
		
		if (t instanceof Record) {
			Entry e = ((Record) t).getScope().find(ident);
			if (e != null && e instanceof parser.symbolTable.Variable) {
				return new Field(loc, new parser.ast.Variable((parser.symbolTable.Variable) e));
			} else {
				throw new ParserException("Selector: Could not find field of given Record");
			}
		} else {
			throw new ParserException("Selector: accessing field of non-Record");
		}
		
	}
	
	/**
	 * Confirms the ExpressionList Non-terminal.
	 */
	private Collection<parser.ast.Number> expressionList() {
		this.obs.add("ExpressionList");
		this.obs.decend();
		Collection<Number> expressions = new ArrayList<Number>();
		Expression toAdd = this.expression();
		if (toAdd instanceof Number) {
			expressions.add((Number)toAdd);
		} else {
			throw new ParserException("expressionList: adding non-number");
		}
		while (this.peak().getText().equals(",")) {
			this.hardMatch(",");
			toAdd = this.expression();
			if (toAdd instanceof Number) {
				expressions.add((Number)toAdd);
			} else {
				throw new ParserException("expressionList: adding non-number");
			}
		}
		this.obs.accend();
		return expressions;
	}
	
	/**
	 * Confirms the IdentifierList Non-terminal.
	 */
	private Collection<String> identifierList() {
		this.obs.add("IdentifierList");
		this.obs.decend();
		Collection<String> identifiers = new ArrayList<String>();
		identifiers.add(this.matchIdent());
		while (this.peak().getText().equals(",")) {
			this.hardMatch(",");
			identifiers.add(this.matchIdent());
		}
		this.obs.accend();
		return identifiers;
	}
	
	/**
	 * Gets the next token in the token stream.
	 * Advances the stream.
	 * @return the next token.
	 */
	private Token next() {
		return this.tokens.get(this.position++);
	}
	
	/**
	 * Gets the next token in the stream, without advancing the stream.
	 * @return the next token.
	 */
	private Token peak() {
		return this.tokens.get(this.position);
	}
	
	/**
	 * Returns the observer's string representation. 
	 * @return the string representation
	 */
	public String getParseTree() {
		return this.obs.toString();
	}
	
	public String getSymbolTable() {
		this.current.accept(this.STVisit);
		return this.STVisit.toString();
	}
	
	public String getAST() {
		if (this.ast != null) {
			this.ast.accept(this.astVisit);
		}
		return this.astVisit.toString();
	}
	
	/**
	 * Throws a ParserException.
	 * @param msg The error message
	 * @param place the character place the error occurred. 
	 */
	private void error(String msg, int place) {
		throw new ParserException(msg + " at: " + place);
	}
}