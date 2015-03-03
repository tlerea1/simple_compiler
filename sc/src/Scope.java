import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


public class Scope {
	private Scope outer;
	private HashMap<String, Entry> table;
	
	public Scope(Scope outer) {
		this.outer = outer;
		this.table = new HashMap<String, Entry>();
	}
	
	public Entry insert(String identifier, Entry value) {
		return this.table.put(identifier, value);
	}
	
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
	
	public boolean local(String identifier) {
		return this.table.containsKey(identifier);
	}
	
	public String toString() {
		String toReturn = "";
		for (java.util.HashMap.Entry<String, Entry> e : table.entrySet()) {
			toReturn += e.getKey() + ":" + e.getValue();
		}
		return toReturn;
	}

	public Scope getOuter() {
		return outer;
	}

	public void setOuter(Scope outer) {
		this.outer = outer;
	}
	
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
