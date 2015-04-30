package visitor;

import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Expression;
import parser.ast.Field;
import parser.ast.FunctionCall;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Node;
import parser.ast.Number;
import parser.ast.ProcedureCall;
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Write;

public interface ASTVisitor extends Visitor {
	/**
	 * Visits the given node
	 * @param n the node to visit
	 * @return return value
	 */
	public int visit(Node n);
	/**
	 * Visits the given Instruction
	 * @param i the instruction to visit
	 * @return return value
	 */
	public int visit(Instruction i);
	/**
	 * Visits the given assignment node.
	 * @param a the assignment to visit
	 * @return the return value
	 */
	public int visit(Assign a);
	/**
	 * Visits the given IF node.
	 * @param a the if to visit
	 * @return the return value
	 */
	public int visit(If a);
	/**
	 * Visits the given Repeat node.
	 * @param r the repeat to visit.
	 * @return the return value.
	 */
	public int visit(Repeat r);
	/**
	 * Visits the given Read node.
	 * @param r the read to visit
	 * @return the return value
	 */
	public int visit(Read r);
	/**
	 * Visits the given write node.
	 * @param w the write to visit.
	 * @return the return value.
	 */
	public int visit(Write w);
	/**
	 * Visits the given ProcedureCall node.
	 * @param proc the procedureCall to visit.
	 * @return the return value.
	 */
	public int visit(ProcedureCall proc);
	/**
	 * Visits the given expression,
	 * @param e the expression to visit
	 * @return the return value
	 */
	public int visit(Expression e);
	/**
	 * Visits the given FunctionCall.
	 * @param func the function call to visit.
	 * @return the return value.
	 */
	public int visit(FunctionCall func);
	/**
	 * Visits the given binary node.
	 * @param b the binary to visit.
	 * @return the return value.
	 */
	public int visit(Binary b);
	/**
	 * Visits the given number node.
	 * @param n the number to visit.
	 * @return the return value.
	 */
	public int visit(Number n);
	/**
	 * Visits the given location node.
	 * @param l the location to visit.
	 * @return the return value.
	 */
	public int visit(Location l);
	/**
	 * Visits the given Variable node.
	 * @param v the variable to visit.
	 * @return the return value.
	 */
	public int visit(parser.ast.Variable v);
	/**
	 * Visits the given Index node.
	 * @param i the index node to visit.
	 * @return the return value
	 */
	public int visit(Index i);
	/**
	 * Visits the given field node.
	 * @param f the field to visit
	 * @return the return value
	 */
	public int visit(Field f);
}
