
public interface Visitor {

	public void visit(Entry e);
	public void visit(Constant constant);
	public void visit(Variable var);
	public void visit(Integer i);
	public void visit(Array ra);
	public void visit(Record r);
	public void visit(Scope s);
	
}
