package parser.symbolTable;

/**
 * Visitor interface. Used to create representations of the symbol table.
 * @author tuvialerea
 *
 */
public interface Visitor {
	/**
	 * Should never be called. Generic function
	 * @param e the entry
	 */
	public void visit(Entry e);
	/**
	 * Visits the given constant. Which includes its value and type.
	 * @param constant the constant to visit.
	 */
	public void visit(Constant constant);
	/**
	 * Visits the given Variable. Which includes its type.
	 * @param var the Variable to visit.
	 */
	public void visit(Variable var);
	/**
	 * Visits the given Integer.
	 * @param i the Integer to visit.
	 */
	public void visit(Integer i);
	/**
	 * Visits the given Array. Which includes its length and elements' Type.
	 * @param ra the array to visit.
	 */
	public void visit(Array ra);
	/**
	 * Visits the given Record. Which includes its Scope.
	 * @param r the Record to visit.
	 */
	public void visit(Record r);
	/**
	 * Visits the given Scope. Which includes all of its entries.
	 * @param s the Scope to visit.
	 */
	public void visit(Scope s);
	
}
