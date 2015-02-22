
public class BasicObserver implements Observer {

	private String indent;
	private String data;
	
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
	
	public void add(Token item) {
		this.data += this.indent + item + "\n";
	}
	
	public String toString() {
		return this.data;
	}

}
