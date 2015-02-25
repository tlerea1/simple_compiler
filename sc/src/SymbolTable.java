
public class SymbolTable {
	private Scope table;
	
	public SymbolTable(Scope Universe) {
		this.table = new Scope(Universe);
	}
	
	public Entry insert(String identifier, Entry val) {
		return this.table.insert(identifier, val);
	}
	
	public Entry find(String identifier) {
		return this.find(identifier);
	}
	
	public boolean local(String identifier) {
		return this.table.local(identifier);
	}
	
	public Scope getTable() {
		return this.table;
	}
	
	public String toString() {
		return "SYMBOL TABLE";
	}
}
