package visitor;

import parser.ast.Assign;
import parser.ast.Binary;
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
	public int visit(Node n);
	
	public int visit(Instruction i);
	public int visit(Assign a);
	public int visit(If a);
	public int visit(Repeat r);
	public int visit(Read r);
	public int visit(Write w);
	
	public int visit(Expression e);
	public int visit(Binary b);
	public int visit(Number n);
	public int visit(Location l);
	public int visit(parser.ast.Variable v);
	public int visit(Index i);
	public int visit(Field f);
}
