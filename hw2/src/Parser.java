import java.util.List;


public class Parser {
	
	private Scanner scan;
	private Observer obs;
	private List<Token> tokens;
	private int position;
	
	public Parser(Scanner scan, boolean graphical) {
		this.scan = scan;
		this.tokens = this.scan.all();
		this.position = 0;
		if (graphical) {
			
		} else {
			this.obs = new BasicObserver();
		}
	}
	
	public void parse() {
		
	}
	
	private void hardMatch(String item) {
		if (! match(item)) {
			throw new ParserException("Expected " + item);
		}
	}
	
	private boolean match(String item) {
		Token next = this.next();
		this.obs.add(next);
		return next.getText().equals(item);
	}
	
	private void matchIdent() {
		Token next = this.next();
		this.obs.add(next);
		if (next.getType() != TokenType.IDENTIFIER) {
			throw new ParserException("Failed to find identifier");
		}
	}
	
	private void program() {
		this.obs.add("PROGRAM");
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
		this.hardMatch("EOF");
		
	}
	
	private void declarations() {
		this.obs.add("Declarations");
		this.obs.decend();
		Token next;
		while (this.peak().getText() == "VAR" 
				|| this.peak().getText() == "CONST" 
				|| this.peak().getText() == "TYPE") {
			next = this.peak();
			if (next.getText() == "CONST") {
				constDecl();
			} else if (next.getText() == "VAR") {
				varDecl();
			} else if (next.getText() == "TYPE") {
				typeDecl();
			}
		}
		this.obs.accend();
	}
	
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
	
	private void type() {
		this.obs.add("Type");
		this.obs.decend();
		Token next = this.next();
		if (next.getType() == TokenType.IDENTIFIER) {
			this.obs.add(next);
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
			throw new ParserException("Type error, expected (identifier | \"ARRAY\" | \"RECORD\"");
		}
		this.obs.accend();
	}
	
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
	
	private void factor() {
		this.obs.add("Factor");
		this.obs.decend();
		Token peak = this.peak();
		if (peak.getType() == TokenType.NUMBER) {
			this.obs.add(peak);
		} else if (peak.getText().equals("(")) {
			this.obs.add(peak);
			this.expression();
			this.hardMatch(")");
		} else if (peak.getType() == TokenType.IDENTIFIER) {
			this.designator();
		} else {
			throw new ParserException("Factor error");
		}
		this.obs.accend();
	}
	
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
	
	private void instruction() {
		
	}
	
	private void designator() {
		this.obs.add("Designator");
		this.obs.decend();
		this.matchIdent();
		this.selector();
		this.obs.accend();
	}
	
	private void selector() {
		this.obs.add("Selector");
		this.obs.decend();
		Token next = this.peak();
		while (next.getText().equals(".") || next.getText().equals("[")) {
			if (next.getText().equals("[")) {
				this.hardMatch("[");
				this.expressionList();
				this.hardMatch("]");
			} else if (next.getText().equals(".")) {
				this.hardMatch(".");
				this.matchIdent();
			}
		}
		this.obs.accend();
	}
	
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
	
	private Token next() {
		return this.tokens.get(this.position++);
	}
	
	private Token peak() {
		return this.tokens.get(this.position);
	}
}
