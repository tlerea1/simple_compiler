package visitor;

import java.util.Stack;

import parser.ParserException;
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
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Write;

public class ASTGraphicalVisitor extends GraphicalVisitor implements
		ASTVisitor {

	private int current;
	
	public ASTGraphicalVisitor() {
		this.current = -1;
	}
	
	@Override
	public int visit(Node n) {
		throw new ParserException("ASTgraphicalVisitor: visiting node");
	}

	@Override
	public int visit(Instruction i) {
		throw new ParserException("ASTgraphicalVisitor: visiting node");
	}

	@Override
	public int visit(Assign a) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\":=\",shape=box];\n";
		int loc = a.getLoc().accept(this);
		int exp = a.getExp().accept(this);

		this.data += "label" + current + " -> label" + loc + " [label=\"location\"];\n";
		this.data += "label" + current + " -> label" + exp + " [label=\"expression\"];\n";
		if (a.getNext() != null) {
			int next = a.getNext().accept(this);
			this.data += "label" + current + " -> label" + next + " [label=\"next\"];\n";
			this.data += "{rank=same; label" + current + " label" + next + "};\n";
		}
		return current;
	}

	@Override
	public int visit(If a) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"If\",shape=box];\n";
		int con = a.getCondition().accept(this);
		this.data += "label" + current + " -> label" + con + " [label=\"condition\"];\n";
		int iftrue = a.getIfTrue().accept(this);
		this.data += "label" + current + " -> label" + iftrue + " [label=\"true\"];\n";
		if (a.getIfFalse() != null) {
			int iffalse = a.getIfFalse().accept(this);
			this.data += "label" + current + " -> label" + iffalse + " [label=\"false\"];\n";
		}
		if (a.getNext() != null) {
			int next = a.getNext().accept(this);
			this.data += "label" + current + " -> label" + next + " [label=\"next\"];\n";
			this.data += "{rank=same; label" + current + " label" + next + "};\n";
		}
		return current;
	}

	@Override
	public int visit(Repeat r) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"Repeat\",shape=box];\n";
		int con = r.getCondition().accept(this);
		this.data += "label" + current + " -> label" + con + " [label=\"condition\"];\n";
		int inst = r.getInstructions().accept(this);
		this.data += "label" + current + " -> label" + inst + " [label=\"instructions\"];\n";

		if (r.getNext() != null) {
			int next = r.getNext().accept(this);
			this.data += "label" + current + " -> label" + next + " [label=\"next\"];\n";
			this.data += "{rank=same; label" + current + " label" + next + "};\n";
		}
		return current;
	}

	@Override
	public int visit(Read r) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"Read\",shape=box];\n";
		int loc = r.getLoc().accept(this);
		this.data += "label" + current + " -> label" + loc + " [label=\"location\"];\n";
		if (r.getNext() != null) {
			int next = r.getNext().accept(this);
			this.data += "label" + current + " -> label" + next + " [label=\"next\"];\n";
			this.data += "{rank=same; label" + current + " label" + next + "};\n";
		}
		return current;
	}

	@Override
	public int visit(Write w) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"Write\",shape=box];\n";
		int exp = w.getExp().accept(this);
		this.data += "label" + current + " -> label" + exp + " [label=\"expression\"];\n";
		if (w.getNext() != null) {
			int next = w.getNext().accept(this);
			this.data += "label" + current + " -> label" + next + " [label=\"next\"];\n";
			this.data += "{rank=same; label" + current + " label" + next + "};\n";
		}
		return current;
	}

	@Override
	public int visit(Expression e) {
		throw new ParserException("ASTgraphicalVisitor: visiting expression");
	}

	@Override
	public int visit(Binary b) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"" + b.getOperator() + "\",shape=box];\n";
		int left = b.getLeft().accept(this);
		this.data += "label" + current + " -> label" + left + " [label=\"left\"];\n";
		int right = b.getRight().accept(this);
		this.data += "label" + current + " -> label" + right + " [label=\"right\"];\n";	
		return current;
	}

	@Override
	public int visit(Location l) {
		throw new ParserException("ASTgraphicalVisitor: location");

	}

	@Override
	public int visit(parser.ast.Variable v) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"Variable\",shape=box];\n";
		this.data += "label" + (++this.current) + " [label=\"" + v.getIdentifer() + "\",shape=circle];\n";
		this.data += "label" + current + " -> label" + (current + 1) + " [label=\"ST\"];\n";
		return current;
	}

	@Override
	public int visit(Index i) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"Index\",shape=box];\n";
		int loc = i.getLoc().accept(this);
		this.data += "label" + current + " -> label" + loc + " [label=\"location\"];\n";
		int exp = i.getExp().accept(this);
		this.data += "label" + current + " -> label" + exp + " [label=\"expression\"];\n";
		return current;
	}

	@Override
	public int visit(Field f) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"Field\",shape=box];\n";
		int loc = f.getLoc().accept(this);
		this.data += "label" + current + " -> label" + loc + " [label=\"location\"];\n";
		int var = f.getVar().accept(this);
		this.data += "label" + current + " -> label" + var + " [label=\"variable\"];\n";
		return current;

	}

	@Override
	public int visit(Condition c) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"" + c.getOperator() + "\",shape=box];\n";
		int left = c.getLeft().accept(this);
		this.data += "label" + current + " -> label" + left + " [label=\"left\"];\n";
		int right = c.getRight().accept(this);
		this.data += "label" + current + " -> label" + right + " [label=\"right\"];\n";
		return current;
	}

	@Override
	public int visit(parser.ast.Number n) {
		this.current++;
		int current = this.current;
		this.data += "label" + current + " [label=\"Number\",shape=box];\n";
		this.data += "label" + (++this.current) + " [label=\"" + n.getNum().getValue() + "\",shape=diamond];\n";
		this.data += "label" + current + " -> label" + (current+1) + " [label=\"ST\"];\n";
		return current;
	}

}
