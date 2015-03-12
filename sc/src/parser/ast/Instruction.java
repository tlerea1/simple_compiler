package parser.ast;

public abstract class Instruction extends Node {
	private Instruction next;

	public Instruction getNext() {
		return next;
	}

	public void setNext(Instruction next) {
		this.next = next;
	}
	
}
