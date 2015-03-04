package scanner;

/**
 * Class to represent a token. Holds starting and ending location of token.
 * Also stores the source text that makes up the token.
 * @author tuvialerea
 *
 */
public class Token {

	private TokenType type;
	private int val; // Value of a number token
	private String text; // Source code that makes the token
	private int start; // Starting character in source
	private int end; // Ending character in source
	
	/**
	 * Default constructor, makes a blank token.
	 */
	public Token() {
		this.type = TokenType.EMPTY;
	}
	
	/**
	 * Token Constructor
	 * @param type Type of the token
	 * @param value Integer value of a number token.
	 * @param text the Source code text
	 */
	public Token(TokenType type, int value, String text) { 
		this.type = type;
		this.val = value;
		this.text = text;
	}
	
	/**
	 * Gets the token's type
	 * @return The type of the token
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * Sets the token's type.
	 * @param type the type to set to.
	 */
	public void setType(TokenType type) {
		this.type = type;
	}

	/**
	 * Gets the value of the token (only used for number tokens).
	 * @return the value of the token
	 */
	public int getVal() {
		return val;
	}

	/**
	 * Sets the value of the token.
	 * @param val the value to set to.
	 */
	public void setVal(int val) {
		this.val = val;
	}

	/**
	 * Gets the source's text for the token.
	 * @return the token's text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the token's text.
	 * @param text the text to set to.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the end character of the token.
	 * @return the end character.
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Sets the end character of the token.
	 * @param end the value to set the end to.
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * Gets the start character of the token.
	 * @return the start value.
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Sets the start character value of the token.
	 * @param start the value to set to.
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	/**
	 * Returns a string representation of the token. 
	 */
	public String toString() {
		String s = "";
		switch (this.type) {
			case NUMBER:
				s += "integer";
				break;
			case IDENTIFIER:
				s += "identifier";
				break;
			case KEYWORD:
				s += this.text;
				break;
			case SYMBOL:
				s += this.text;
				break;
			case EOF:
				s += "eof";
				break;
			default:
				throw new RuntimeException("error: Token: Printing default case token!");
		}
		if (this.type == TokenType.IDENTIFIER || this.type == TokenType.NUMBER) {
			s += "<" + this.text + ">";
		}
		s += "@(" + this.start + ", " + this.end + ")";
		return s;
	}
}
