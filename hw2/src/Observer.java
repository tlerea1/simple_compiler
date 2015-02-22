/**
 * Observer Interface for Parser.
 * @author tuvialerea
 *
 */
public interface Observer {
	/**
	 * Decend into another function in the stacktrace.
	 */
	public void decend();
	/**
	 * Accend from the current function in the stacktrace.
	 */
	public void accend();
	/**
	 * Add the given string to the Observer. Meant for Non-Terminals
	 * @param item - the Non-terminal to add
	 */
	public void add(String item);
	/**
	 * Add the given Token to the Observer. Meant for terminals.
	 * @param item add the given terminal to the Observer
	 */
	public void add(Token item);
}
