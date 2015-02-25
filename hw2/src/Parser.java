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
	private SymbolTable table;
	
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
		} else {
			this.obs = new BasicObserver();
		}
		Scope universe = new Scope(null);
		universe.insert("INTEGER", new TypeEntry(new Integer()));
		this.table = new SymbolTable(universe);
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
	 */
	private void matchIdent() {
		Token next = this.next();
		this.obs.add(next);
		if (next.getType() != TokenType.IDENTIFIER) {
			error("Failed to find identifier", next.getStart());
		}
	}
	
	/**
	 * Confirms the Program non-terminal.
	 */
	private void program() {
		this.obs.add("Program");
		this.obs.decend();
		this.hardMatch("PROGRAM");
		this.matchIdent();
		this.hardMatch(";");
		this.declarations();
		if (this.peak().getText().equals("BEGIN")) {
			this.hardMatch("BEGIN");
			this.instructions();
		}
		this.hardMatch("END");
		this.matchIdent();
		this.hardMatch(".");
		Token next = this.next();
		if (next.getType() != TokenType.EOF) {
			error("Expected EOF", next.getStart());
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
			this.matchIdent();
			this.hardMatch("=");
			this.expression();
			this.hardMatch(";");
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
			this.identifierList();
			this.hardMatch(":");
			this.type();
			this.hardMatch(";");
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
			this.matchIdent();
			this.hardMatch("=");
			this.type();
			this.hardMatch(";");
		}
		this.obs.accend();
	}
	
	/**
	 * Confirms the Type Non-terminal.
	 */
	private void type() {
		this.obs.add("Type");
		this.obs.decend();
		Token next = this.peak();
		if (next.getType() == TokenType.IDENTIFIER) {
			this.obs.add(this.next());
		} else if (next.getText().equals("ARRAY")) {
			this.hardMatch("ARRAY");
			this.expression();
			this.hardMatch("OF");
			this.type();
		} else if (next.getText().equals("RECORD")) {
			this.hardMatch("RECORD");
			while (! this.peak().getText().equals("END")) {
				this.identifierList();
				this.hardMatch(":");
				this.type();
				this.hardMatch(";");
			}
			this.hardMatch("END");
		} else {
			error("Type error, expected (identifier | \"ARRAY\" | \"RECORD\"", next.getStart());
		}
		this.obs.accend();
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
	private void identifierList() {
		this.obs.add("IdentifierList");
		this.obs.decend();
		this.matchIdent();
		while (this.peak().getText().equals(",")) {
			this.hardMatch(",");
			this.matchIdent();
		}
		this.obs.accend();
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
	public String toString() {
		return this.obs.toString();
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
