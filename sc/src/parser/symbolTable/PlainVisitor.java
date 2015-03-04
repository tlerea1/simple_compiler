package parser.symbolTable;
import java.util.Map;
import java.util.SortedSet;

import parser.ParserException;

/**
 * Class to store and build the basic symbol table representation.
 * @author tuvialerea
 *
 */
public class PlainVisitor implements Visitor {

	private String indent; // indent string
	private String data; // String representation
	
	public PlainVisitor() {
		this.indent = "";
		this.data = "";
	}
	
	@Override
	public void visit(Entry e) {
		throw new RuntimeException("Using generic Entry visit");
	}

	@Override
	public void visit(Constant constant) {
		this.data += this.indent + "CONST BEGIN\n";
		this.indent();
		this.data += this.indent + "type:\n";
		this.indent();
		constant.getType().accept(this);
		this.dedent();
		this.data += this.indent + "value:\n";
		this.indent();
		this.data += this.indent + constant.getValue() + "\n";
		this.dedent();
		this.dedent();
		this.data += this.indent + "END CONST\n";
	}

	@Override
	public void visit(Variable var) {
		this.data += this.indent + "VAR BEGIN\n";
		this.indent();
		this.data += this.indent + "type:\n";
		this.indent();
		var.getType().accept(this);
		this.dedent();
		this.dedent();
		this.data += this.indent + "END VAR\n";
	}

	@Override
	public void visit(Integer i) {
		this.data += this.indent + "INTEGER\n";
	}

	@Override
	public void visit(Array ra) {
		this.data += this.indent + "ARRAY BEGIN\n";
		this.indent();
		this.data += this.indent + "type:\n";
		this.indent();
		ra.getElemType().accept(this);
		this.dedent();
		this.data += this.indent + "length:\n";
		this.indent();
		this.data += this.indent + ra.getLength() + "\n";
		this.dedent();
		this.dedent();
		this.data += this.indent + "END ARRAY\n";
	}

	@Override
	public void visit(Record r) {
		this.data += this.indent + "RECORD BEGIN\n";
		this.indent();
		r.getScope().accept(this);
		this.dedent();
		this.data += this.indent + "END RECORD\n";
	}

	@Override
	public void visit(Scope s) {
		this.data += this.indent + "SCOPE BEGIN\n";
		this.indent();
		SortedSet<Map.Entry<String, Entry>> set = s.getEntries();
		for (Map.Entry<String, Entry> e : set) {
			this.data += this.indent + e.getKey() + " =>\n";
			this.indent();
			e.getValue().accept(this);
			this.dedent();
		}
		this.dedent();
		this.data += this.indent + "END SCOPE\n";
	}
	
	private void indent() {
		this.indent += "  "; // Adds two spaces to the indent string
	}
	
	private void dedent() {
		if (this.indent.length() >= 2) {
			this.indent = this.indent.substring(0, this.indent.length() - 2); // removes to spaces from the indent string
		} else {
			throw new ParserException("PlainVisitor Dedent fail!");
		}
	}
	
	public String toString() {
		return this.data;
	}

}
