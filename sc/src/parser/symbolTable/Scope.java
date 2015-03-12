package parser.symbolTable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import visitor.Visitor;

/**
 * Scope class. Used as the symbol table and in RECORDs.
 * @author tuvialerea
 *
 */
public class Scope {
	private Scope outer;
	private HashMap<String, Entry> table;
	
	/**
	 * Constructor for a Scope. 
	 * @param outer the outer scope. The Universe scope has outer == null
	 */
	public Scope(Scope outer) {
		this.outer = outer;
		this.table = new HashMap<String, Entry>();
	}
	
	/**
	 * Inserts a new mapping of identifier to value.
	 * @param identifier The string identifier
	 * @param value the entry value it should map too.
	 * @return the value inserted
	 */
	public Entry insert(String identifier, Entry value) {
		return this.table.put(identifier, value);
	}
	
	/**
	 * Finds the given identifier in the scope.
	 * If not found will recurse to the outer scope
	 * if it exists.
	 * @param identifier the identifier to search for.
	 * @return the Entry that identifier maps to. null if not found
	 */
	public Entry find(String identifier) {
		Entry toReturn = this.table.get(identifier);
		if (toReturn == null) {
			if (this.outer != null) {
				return this.outer.find(identifier);
			} else {
				return null;
			}
		}
		return toReturn;
	}
	
	/**
	 * Checks to see if the given identifier has a mapping in the scope.
	 * @param identifier the identifier to search for.
	 * @return true if found, false if not
	 */
	public boolean local(String identifier) {
		return this.table.containsKey(identifier);
	}
	
	/**
	 * Returns a debugging string representation of the scope.
	 */
	public String toString() {
		String toReturn = "";
		for (java.util.HashMap.Entry<String, Entry> e : table.entrySet()) {
			toReturn += e.getKey() + ":" + e.getValue();
		}
		return toReturn;
	}

	/**
	 * Function to get the outer scope.
	 * @return the outer scope
	 */
	public Scope getOuter() {
		return outer;
	}

	/**
	 * Function to set the outer scope.
	 * @param outer the scope to set to.
	 */
	public void setOuter(Scope outer) {
		this.outer = outer;
	}
	
	/**
	 * Function to accept this scope for the visitor.
	 * @param v the visitor to visit
	 */
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	/**
	 * Function to get an ordered set of all entries in the scope. Sorted by key.
	 * @return A SortedSet of all scope entries.
	 */
	public SortedSet<Map.Entry<String, Entry>> getEntries() {
		SortedSet<Map.Entry<String, Entry>> set = 
				new TreeSet<Map.Entry<String, Entry>>(new Comparator<Map.Entry<String, Entry>>() {

			@Override
			public int compare(java.util.Map.Entry<String, Entry> o1,
					java.util.Map.Entry<String, Entry> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
			
		});
		set.addAll(this.table.entrySet());
		return set;
	}
	
}
