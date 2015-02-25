import java.util.HashMap;


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
}
