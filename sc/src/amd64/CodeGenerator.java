package amd64;

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
import parser.symbolTable.Array;
import parser.symbolTable.Constant;
import parser.symbolTable.Entry;
import parser.symbolTable.FormalVariable;
import parser.symbolTable.Integer;
import parser.symbolTable.LocalVariable;
import parser.symbolTable.Record;
import parser.symbolTable.Scope;
import parser.symbolTable.Variable;
import parser.symbolTable.procedures.Procedure;
import visitor.ASTVisitor;

public class CodeGenerator implements ASTVisitor {

	@Override
	public void visit(Entry e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Constant constant) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Procedure p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Variable var) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FormalVariable var) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(LocalVariable var) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Integer i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Array ra) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Record r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Scope s) {
		// TODO Auto-generated method stub

	}

	@Override
	public int visit(Node n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Instruction i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Assign a) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(If a) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Repeat r) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Read r) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Write w) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(ProcedureCall proc) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Expression e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(FunctionCall func) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Binary b) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Number n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Location l) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(parser.ast.Variable v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Index i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int visit(Field f) {
		// TODO Auto-generated method stub
		return 0;
	}

}
