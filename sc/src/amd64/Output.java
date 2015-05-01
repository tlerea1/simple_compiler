package amd64;

public class Output {
	private String output;
	
	public Output() {
		this.output = "";
	}
	
	public void println(String msg) {
		this.output += msg + "\n";
	}
	
	public String toString() {
		return this.output;
	}
}
