package scanner;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The SIMPLE compiler Scanner class. Used to scan the source code.
 * Produces tokens through the two public methods. next() and all().
 * EOF token signifies end of token stream. 
 * @author tuvialerea
 *
 */
public class Scanner {
	
	private static final int BUFFER_SIZE = 1024; // Size of buffer, reads this many bytes at a time
	private HashSet<String> keywords; // Set of all SIMPLE keywords
	private HashSet<Character> symbols; // Set of all SIMPLE symbols
	private HashSet<String> twoSymbols; // Set of all two character symbols
	private byte[] file_buf; // byte array for buffer to read from file.
	private boolean nextCalled; // boolean to detect if next has been called.
	private FileInputStream input; // Input stream, can be from stdin or file
	private int buf_pos; // Position in the string buffer, pointers to next character
	private int file_position; // Character position in source code.
	private String buf; // The string buffer that is iterated through.
	
	/**
	 * Constructor to use if reading from a file.
	 * @param filename The file to read from. 
	 * @throws IOException if file is not found
	 */
	public Scanner(String filename) throws IOException {
		initKeywords();
		initSymbols();
		initTwoSymbols();
		this.file_buf = new byte[BUFFER_SIZE];
		this.buf = "";
		this.nextCalled = false;
		this.input = new FileInputStream(filename);
		this.buf_pos = 0;
		this.file_position = -1;
	}
	
	/**
	 * Constructor to use if reading from stdin.
	 * @throws IOException If IO error with stdin
	 */
	public Scanner() throws IOException {
		initKeywords();
		initSymbols();
		initTwoSymbols();
		this.file_buf = new byte[BUFFER_SIZE];
		this.buf = "";
		this.nextCalled = false;
		this.input = new FileInputStream(FileDescriptor.in);
		this.buf_pos = 0;
		this.file_position = -1;
	}
	
	/**
	 * Function to get the next token in the stream.
	 * Once the end of stream has been reached, next
	 * will continuously return tokens of type EOF
	 * @return The next token in the stream. 
	 */
	public Token next() {
		this.nextCalled = true; // next has been called.
		Token building = new Token();
		String tokenText = "";
		int c = nextChar();
		while (isWhiteSpace(c)) { // skip over leading whitespace
			c = nextChar();
		}
		while (c =='/' && this.peak() == '*') { // Starting a comment
			tokenText += (char) c;
			while(! isFullComment(tokenText)) { // loop until comment is closed
				c = nextChar();
				if (c == -1) {
					throw new ScannerException("Comment never closed");
				}
				tokenText += (char) c;
			}
			c = nextChar();
			while (isWhiteSpace(c)) { // get rid of trailing whitespace
				c = nextChar();
			}
			tokenText = "";
		}
		building.setStart(this.file_position); // start is after whitespace
		if (c == -1) { // If stream is over return EOF
			building.setStart(this.file_position+1);
			building.setType(TokenType.EOF);
			building.setEnd(this.file_position+1);
			building.setText("");
			return building;
		}
		if (isDigit(c)) { 
			tokenText += (char) c;
			while (isDigit(this.peak())) { // Continue while more digits
				c = nextChar();
				tokenText += (char) c;
			}
			building.setType(TokenType.NUMBER); // Return NUMBER token
			building.setEnd(this.file_position);
			building.setText(tokenText);
			try {
				building.setVal(java.lang.Integer.parseInt(tokenText));
			} catch (NumberFormatException e) {
				throw new ScannerException("Using too large an integer!");
			}
		} else if (isLetter(c)) {
			tokenText += (char) c;
			while (isDigit(this.peak()) || isLetter(this.peak())) { // Continue while Letters or digits
				c = nextChar();
				tokenText += (char) c;
			}
			if (isKeyword(tokenText)) { // Check if tokenText is a keyword
				building.setType(TokenType.KEYWORD);
			} else {
				building.setType(TokenType.IDENTIFIER); // If not its an identifier
			}
			building.setEnd(this.file_position);
			building.setText(tokenText);
		} else if (isSymbol(c)) {
			tokenText += (char) c;
			int next = this.peak(); // Check the next character for a two character symbol
			if (next != -1) { // If there is a next character
				String test = tokenText;
				test += (char) next;
				if (isTwoSymbol(test)) { // Test the resulting two characters
					tokenText += (char) this.nextChar(); // If is a two symbol add the nextChar
				}
			}
			building.setType(TokenType.SYMBOL); // Return type symbol
			building.setEnd(this.file_position);
			building.setText(tokenText);
		} else {
			throw new ScannerException("Invalid symbol found at position: " + this.file_position);
		}
		
		return building;
	}
	
	/**
	 * Function for getting a collection of all the tokens in the stream. Ends with an EOF token.
	 * @return the collection of all the tokens in the stream. 
	 */
	public List<Token> all() {
		if (this.nextCalled) {
			throw new ScannerException("all called after next");
		}
		List<Token> tokens = new ArrayList<Token>();
		Token returned;
		do {
			returned = this.next();
			tokens.add(returned);
		} while (returned.getType() != TokenType.EOF);
		return tokens;
	}
	
	/**
	 * Function for getting the next character in the stream. Advances pointers as necessary.
	 * @return The next character in the stream, -1 if there are no more. 
	 */
	private int nextChar() {
		int c = this.peak();
		if (c == -1) {
			return -1;
		}
		this.file_position++;
		this.buf_pos++;
		return c;
	}
	
	/**
	 * Peaks at the next character in the stream. 
	 * i.e. Does not advance any pointers, simply returns the next character. 
	 * @return Returns the next character in the stream, returns -1 if none left.
	 */
	private int peak() {
		if (this.buf_pos == this.buf.length()) {
			if (! this.readMore()) {
				return -1;
			}
		}
		return this.buf.charAt(this.buf_pos);
	}
	
	private boolean isFullComment(String s) {
		return s.startsWith("/*") && s.endsWith("*/");
	}
	
	private boolean isWhiteSpace(int c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\f' || c == '\r';
	}
	
	private boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isLetter(int c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	private boolean isKeyword(String s) {
		return keywords.contains(s);
	}
	
	private boolean isSymbol(int c) {
		return this.symbols.contains((char) c);
	}
	
	private boolean isTwoSymbol(String s) {
		return this.twoSymbols.contains(s);
	}
	
	private void initKeywords() {
		this.keywords = new HashSet<String>();
		this.keywords.add("PROGRAM");
		this.keywords.add("BEGIN");
		this.keywords.add("END");
		this.keywords.add("CONST");
		this.keywords.add("TYPE");
		this.keywords.add("VAR");
		this.keywords.add("ARRAY");
		this.keywords.add("OF");
		this.keywords.add("RECORD");
		this.keywords.add("DIV");
		this.keywords.add("MOD");
		this.keywords.add("IF");
		this.keywords.add("THEN");
		this.keywords.add("ELSE");
		this.keywords.add("REPEAT");
		this.keywords.add("UNTIL");
		this.keywords.add("WHILE");
		this.keywords.add("DO");
		this.keywords.add("WRITE");
		this.keywords.add("READ");
		this.keywords.add("ELSEIF");
		this.keywords.add("AND");
		this.keywords.add("OR");
		this.keywords.add("NOT");
		this.keywords.add("PROCEDURE");
		this.keywords.add("RETURN");
	}
	
	private void initSymbols() {
		this.symbols = new HashSet<Character>();
		this.symbols.add(';');
		this.symbols.add('.');
		this.symbols.add('=');
		this.symbols.add(':');
		this.symbols.add('+');
		this.symbols.add('-');
		this.symbols.add('*');
		this.symbols.add('(');
		this.symbols.add(')');
		this.symbols.add('#');
		this.symbols.add('<');
		this.symbols.add('>');
		this.symbols.add('[');
		this.symbols.add(']');
		this.symbols.add(',');
	}
	
	private void initTwoSymbols() { // Set of all the two character symbols
		this.twoSymbols = new HashSet<String>();
		this.twoSymbols.add(":=");
		this.twoSymbols.add("<=");
		this.twoSymbols.add(">=");
	}
	
	/**
	 * Function to read more from the stream into the buffer. Uses the byte[] for reading
	 * then constructs a string and adds it to the existing string buffer. Removes all old
	 * already processed characters from the string buffer.
	 * @return Returns true if successfully read additional characters from the stream, false if not. 
	 */
	private boolean readMore() {
		try {
			int count = this.input.read(this.file_buf);
			if (count == -1 || count == 0) {
				return false;
			}
			String more = new String(this.file_buf, 0, count);
			this.buf = this.buf.substring(this.buf_pos); // forget old characters
			this.buf += more; // add in new characters
			this.buf_pos = 0; // first character to process is now the start of the string
			return true;
		} catch(IOException e) {
			throw new ScannerException("File Not Found");
		}
	}
}
