import java.util.Map;
import java.util.SortedSet;


public class PlainVisitor implements Visitor {

	private String indent;
	private String data;
	
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
		this.indent += "  ";
	}
	
	private void dedent() {
		this.indent = this.indent.substring(0, this.indent.length() - 2);
	}
	
	public String toString() {
		return this.data;
	}

}
