package parser.ast;

public interface Visitor {
	public void visit(Node n);
	
	public void visit(Instruction i);
	public void visit(Assign a);
	public void visit(If a);
	public void visit(Repeat r);
	public void visit(Read r);
	public void visit(Write w);
	
	public void visit(Expression e);
	public void visit(Binary b);
	public void visit(Number n);
	public void visit(Location l);
	public void visit(Variable v);
	public void visit(Index i);
	public void visit(Field f);
	
	public void visit(Condition c);

}
