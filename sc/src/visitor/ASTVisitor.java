package visitor;

import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Condition;
import parser.ast.Expression;
import parser.ast.Field;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Node;
import parser.ast.Number;
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Variable;
import parser.ast.Write;

public interface ASTVisitor extends Visitor {
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
	public void visit(parser.ast.Variable v);
	public void visit(Index i);
	public void visit(Field f);
	
	public void visit(Condition c);

}
