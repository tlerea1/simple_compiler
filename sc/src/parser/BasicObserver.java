package parser;
import scanner.Token;


public class BasicObserver implements Observer {

	/**
	 * The indent level. Represents the depth of the stack.
	 */
	private String indent;
	/**
	 * The observer string that gets built.
	 */
	private String data;
	
	/**
	 * Create a new Basic Observer.
	 */
	public BasicObserver() {
		this.indent = "";
		this.data = "";
	}
	
	@Override
	public void decend() {
		this.indent += "  ";
	}

	@Override
	public void accend() {
		if (this.indent.length() >= 2) {
			this.indent = this.indent.substring(0, this.indent.length() - 2);
		} else {
			throw new RuntimeException("basicObserver: accending without deccent");
		}
	}

	@Override
	public void add(String item) {
		this.data += this.indent + item + "\n";
	}
	
	@Override
	public void add(Token item) {
		this.data += this.indent + item + "\n";
	}
	
	/**
	 * Observer toString.
	 * @return A string representation of the Observer.
	 */
	public String toString() {
		return this.data;
	}

}
