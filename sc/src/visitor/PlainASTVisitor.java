package visitor;

import parser.ParserException;
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

public class PlainASTVisitor extends PlainVisitor implements ASTVisitor  {
	
	public PlainASTVisitor() {
		super();
		this.data += "instructions =>\n";
		this.indent = "  ";
	}
	
	@Override
	public int visit(Node n) {
		throw new ParserException("visiting node");
	}

	@Override
	public int visit(Instruction i) {
		throw new ParserException("visiting instruction");
	}

	@Override
	public int visit(Assign a) {
		this.data += this.indent + "Assign:\n";
		this.data += this.indent + "location =>\n";
		this.indent();
		a.getLoc().accept(this);
		this.dedent();
		this.data += this.indent + "expression =>\n";
		this.indent();
		a.getExp().accept(this);
		this.dedent();
		if (a.getNext() != null) {
			a.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(If a) {
		this.data += this.indent + "If:\n";
		this.data += this.indent + "condition =>\n";
		this.indent();
		a.getCondition().accept(this);
		this.dedent();
		this.data += this.indent + "true =>\n";
		this.indent();
		a.getIfTrue().accept(this);
		this.dedent();
		if (a.getIfFalse() != null) {
			this.data += this.indent + "false =>\n";
			this.indent();
			a.getIfFalse().accept(this);
			this.dedent();
		}
		if (a.getNext() != null) {
			a.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(Repeat r) {
		this.data += this.indent + "Repeat:\n";
		this.data += this.indent + "condition =>\n";
		this.indent();
		r.getCondition().accept(this);
		this.dedent();
		this.data += this.indent + "instructions =>\n";
		this.indent();
		r.getInstructions().accept(this);
		this.dedent();
		if (r.getNext() != null) {
			r.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(Read r) {
		this.data += this.indent + "Read:\n";
		this.data += this.indent + "location =>\n";
		this.indent();
		r.getLoc().accept(this);
		this.dedent();
		if (r.getNext() != null) {
			r.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(Write w) {
		this.data += this.indent + "Write:\n";
		this.data += this.indent + "expression =>\n";
		this.indent();
		w.getExp().accept(this);
		this.dedent();
		if (w.getNext() != null) {
			w.getNext().accept(this);
		}
		return 0;
	}

	@Override
	public int visit(Expression e) {
		throw new ParserException("visiting expression");
	}

	@Override
	public int visit(Binary b) {
		this.data += this.indent + "Binary (" + b.getOperator() + "):\n";
		this.data += this.indent + "left =>\n";
		this.indent();
		b.getLeft().accept(this);
		this.dedent();
		this.data += this.indent + "right =>\n";
		this.indent();
		b.getRight().accept(this);
		this.dedent();
		return 0;
	}

	@Override
	public int visit(Number n) {
		this.data += this.indent + "Number:\n";
		this.data += this.indent + "value =>\n";
		this.indent();
		n.getNum().accept(this);
		this.dedent();
		return 0;
	}

	@Override
	public int visit(Location l) {
		throw new ParserException("visiting location");
	}

	@Override
	public int visit(Variable v) {
		this.data += this.indent + "Variable:\n";
		this.data += this.indent + "variable =>\n";
		this.indent();
		v.getVar().accept(this);
		this.dedent();
		return 0;
	}

	@Override
	public int visit(Index i) {
		this.data += this.indent + "Index:\n";
		this.data += this.indent + "location =>\n";
		this.indent();
		i.getLoc().accept(this);
		this.dedent();
		this.data += this.indent + "expression =>\n";
		this.indent();
		i.getExp().accept(this);
		this.dedent();
		return 0;
	}

	@Override
	public int visit(Field f) {
		this.data += this.indent + "Field:\n";
		this.data += this.indent + "location =>\n";
		this.indent();
		f.getLoc().accept(this);
		this.dedent();
		this.data += this.indent + "variable =>\n";
		this.indent();
		f.getVar().accept(this);
		this.dedent();
		return 0;
	}

//	@Override
//	public int visit(Condition c) {
//		this.data += this.indent + "Condition (" + c.getOperator() + "):\n";
//		this.data += this.indent + "left =>\n";
//		this.indent();
//		c.getLeft().accept(this);
//		this.dedent();
//		this.data += this.indent + "right =>\n";
//		this.indent();
//		c.getRight().accept(this);
//		this.dedent();
//		return 0;
//	}

}
