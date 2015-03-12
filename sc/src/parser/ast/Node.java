package parser.ast;

public abstract class Node {
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}
}
