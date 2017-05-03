package parser.symbolTable;
import interpreter.environment.Environment;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import amd64.CodeGen;
import visitor.CodeGenVisitor;
import visitor.Visitor;

/**
 * Scope class. Used as the symbol table and in RECORDs.
 * @author tuvialerea
 *
 */
public class Scope {
	private Scope outer;
	private HashMap<String, Entry> table;
	private int offset, formalOffset, fieldOffset;
	
	/**
	 * Constructor for a Scope. 
	 * @param outer the outer scope. The Universe scope has outer == null
	 */
	public Scope(Scope outer) {
		this.outer = outer;
		this.table = new HashMap<String, Entry>();
		this.offset = 0;
		this.formalOffset = CodeGen.SIZEOF_INT;
		this.fieldOffset = 0;
	}
	
	/**
	 * Inserts a new mapping of identifier to value.
	 * @param identifier The string identifier
	 * @param value the entry value it should map too.
	 * @return the value inserted
	 */
	public Entry insert(String identifier, Entry value) {
		if (value instanceof FormalVariable) {
			this.formalOffset += CodeGen.SIZEOF_INT;
			FormalVariable formalValue = (FormalVariable) value;
			formalValue.setLocation(this.formalOffset);
		} else if (value instanceof Field) {
			((Field) value).setLocation(this.fieldOffset);
			this.fieldOffset += ((Field) value).getType().size();
		} else if (value instanceof Variable) {
			this.offset -= ((Variable) value).getType().size();
			((Variable) value).setLocation(this.offset);
		}
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
	
	public void accept(CodeGenVisitor v) {
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
	
	public Environment getEnvironment() {
		Environment toReturn = new Environment();
		for (Map.Entry<String, Entry> entry : this.table.entrySet()) {
			if (entry.getValue() instanceof Variable) {
				toReturn.put(entry.getKey(), ((Variable) entry.getValue()).getType().getBox());
			}
		}
		return toReturn;
	}
	
	public int size() {
		int size = 0;
		for (Map.Entry<String, Entry> entry : this.table.entrySet()) {
			if (entry.getValue() instanceof Variable) {
				size += ((Variable) entry.getValue()).size();
			}
		}
		return size;
	}
	
	public int getOffset() {
		return this.offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public Scope clone() {
		Scope toReturn = new Scope(this.outer);
		for (Map.Entry<String, Entry> e : this.table.entrySet()) {
			toReturn.insert(e.getKey(), e.getValue());
		}
		return toReturn;
	}
	
}
