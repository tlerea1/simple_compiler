package parser.symbolTable;

import visitor.Visitor;

/**
 * Class to represent a RECORD type for SIMPLE.
 * @author tuvialerea
 *
 */
public class Record extends Type {
	private Scope scope;
	
	/**
	 * The Record Constructor. 
	 * @param scope the scope for the record
	 */
	public Record(Scope scope) {
		this.scope = scope;
	}
	
	/**
	 * Debugging string representation of the RECORD.
	 * @return a string representation
	 */
	public String toString() {
		return "RECORD";
	}
	
	/**
	 * Function to get the scope.
	 * @return the scope of the record.
	 */
	public Scope getScope() {
		return this.scope;
	}
	
	/**
	 * Function to accept the given visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}
}
