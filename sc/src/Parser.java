import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	private Visitor visit;
	
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
			this.visit = new GraphicalVisitor();
		} else {
			this.obs = new BasicObserver();
			this.visit = new PlainVisitor();
		}
		Scope universe = new Scope(null);
		universe.insert("INTEGER", new Integer());
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
			this.instructions();
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
		Entry integer = this.current.find("INTEGER");
		if (integer instanceof Integer) { // Check for declaration of INTEGER
			Constant temp = new Constant(value, (Integer) this.current.find("INTEGER"));

			if (! this.current.local(identifier)) { // Check for already defined
				this.current.insert(identifier, temp);
			} else {
				throw new ParserException("Identifier <" + identifier + "> is already defined in current scope");
			}
		} else {
			throw new ParserException("INTEGER is not defined as a type Integer");
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
			this.expression();
			int value = 5; // TODO: should be the return of expression
			this.hardMatch(";");
			addConstant(identifier, value);
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
			this.expression();
			int len = 5; // TODO: should be return from expression
			this.hardMatch("OF");
			Type t = this.type();
			toReturn = new Array(len, t);
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
	
	/**
	 * Confirms the Expression Non-terminal.
	 */
	private void expression() {
		this.obs.add("Expression");
		this.obs.decend();
		if (this.peak().getText().equals("+") || this.peak().getText().equals("-")) {
			this.obs.add(this.next());
		}
		this.term();
		while (this.peak().getText().equals("+") || this.peak().getText().equals("-")) {
			this.obs.add(this.next());
			this.term();
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the Term Non-terminal.
	 */
	private void term() {
		this.obs.add("Term");
		this.obs.decend();
		this.factor();
		while (this.peak().getText().equals("*") || this.peak().getText().equals("DIV") || this.peak().getText().equals("MOD")) {
			this.obs.add(this.next());
			this.factor();
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the Factor Non-terminal.
	 */
	private void factor() {
		this.obs.add("Factor");
		this.obs.decend();
		Token peak = this.peak();
		if (peak.getType() == TokenType.NUMBER) {
			this.obs.add(this.next());
		} else if (peak.getText().equals("(")) {
			this.hardMatch("(");
			this.expression();
			this.hardMatch(")");
		} else if (peak.getType() == TokenType.IDENTIFIER) {
			this.designator();
		} else {
			error("Factor error", peak.getStart());
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the Instructions Non-terminal.
	 */
	private void instructions() {
		this.obs.add("Instructions");
		this.obs.decend();
		this.instruction();
		while (this.peak().getText().equals(";")) {
			this.hardMatch(";");
			this.instruction();
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the Instruction Non-terminal.
	 */
	private void instruction() {
		this.obs.add("Instruction");
		this.obs.decend();
		if (this.peak().getType() == TokenType.IDENTIFIER) {
			this.assign();
		} else if (this.peak().getText().equals("IF")) {
			this.if1();
		} else if (this.peak().getText().equals("REPEAT")) {
			this.repeat();
		} else if (this.peak().getText().equals("WHILE")) {
			this.while1();
		} else if (this.peak().getText().equals("READ")) {
			this.read();
		} else if (this.peak().getText().equals("WRITE")) {
			this.write();
		} else {
			error("Illegal Instruction", this.peak().getStart());
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the Assign Non-terminal.
	 */
	private void assign() {
		this.obs.add("Assign");
		this.obs.decend();
		this.designator();
		this.hardMatch(":=");
		this.expression();
		this.obs.accend();
	}
	
	/**
	 * Confirms the If Non-terminal.
	 */
	private void if1() {
		this.obs.add("If");
		this.obs.decend();
		this.hardMatch("IF");
		this.condition();
		this.hardMatch("THEN");
		this.instructions();
		if (this.peak().getText().equals("ELSE")) {
			this.hardMatch("ELSE");
			this.instructions();
		}
		this.hardMatch("END");
		this.obs.accend();
	}
	
	/**
	 * Confirms the Condition Non-terminal.
	 */
	private void condition() {
		this.obs.add("Condition");
		this.obs.decend();
		this.expression();
		String next = this.peak().getText();
		if (next.equals("=") || next.equals("#") 
				|| next.equals("<") || next.equals(">") 
				|| next.equals("<=") || next.equals(">=")) {
			this.obs.add(this.next());
		} else {
			error("Operator expected in condition", this.peak().getStart());
		}
		this.expression();
		this.obs.accend();
	}
	
	/**
	 * Confirms the Repeat Non-terminal.
	 */
	private void repeat() {
		this.obs.add("Repeat");
		this.obs.decend();
		this.hardMatch("REPEAT");
		this.instructions();
		this.hardMatch("UNTIL");
		this.condition();
		this.hardMatch("END");
		this.obs.accend();
	}
	
	/**
	 * Confirms the While Non-terminal.
	 */
	private void while1() {
		this.obs.add("While");
		this.obs.decend();
		this.hardMatch("WHILE");
		this.condition();
		this.hardMatch("DO");
		this.instructions();
		this.hardMatch("END");
		this.obs.accend();
	}
	
	/**
	 * Confirms the Read Non-terminal.
	 */
	private void read() {
		this.obs.add("Read");
		this.obs.decend();
		this.hardMatch("READ");
		this.designator();
		this.obs.accend();
	}
	
	/**
	 * Confirms the Write Non-terminal.
	 */
	private void write() {
		this.obs.add("Write");
		this.obs.decend();
		this.hardMatch("WRITE");
		this.expression();
		this.obs.accend();
	}
	
	/**
	 * Confirms the Designator Non-terminal.
	 */
	private void designator() {
		this.obs.add("Designator");
		this.obs.decend();
		this.matchIdent();
		this.selector();
		this.obs.accend();
	}
	
	/**
	 * Confirms the Selector Non-terminal.
	 */
	private void selector() {
		this.obs.add("Selector");
		this.obs.decend();
		while (this.peak().getText().equals(".") || this.peak().getText().equals("[")) {
			if (this.peak().getText().equals("[")) {
				this.hardMatch("[");
				this.expressionList();
				this.hardMatch("]");
			} else if (this.peak().getText().equals(".")) {
				this.hardMatch(".");
				this.matchIdent();
			}
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the ExpressionList Non-terminal.
	 */
	private void expressionList() {
		this.obs.add("ExpressionList");
		this.obs.decend();
		this.expression();
		while (this.peak().getText().equals(",")) {
			this.hardMatch(",");
			this.expression();
		}
		this.obs.accend();
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
		this.current.accept(this.visit);
		return this.visit.toString();
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
