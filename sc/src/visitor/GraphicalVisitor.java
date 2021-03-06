package visitor;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;

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

/**
 * Class for GraphicalVisitor to create graphical DOT representation of the symbol table.
 * @author tuvialerea
 *
 */
public class GraphicalVisitor implements Visitor {

	protected String data;
	private HashSet<java.lang.Integer> included; // Set of HashCodes of types already visited.
												 // Not visiting the same types twice prevents duplicate arrows.
	
	public GraphicalVisitor() {
		this.data = "";
		this.included = new HashSet<java.lang.Integer>();
	}
	
	@Override
	public void visit(Entry e) {
		throw new RuntimeException("use of default entry visit");
	}

	@Override
	public void visit(Constant constant) {
		this.data += "_anchor_" + System.identityHashCode(constant) + " [label=\"" + constant.getValue() + "\",shape=diamond]\n";
		constant.getType().accept(this);
		
		this.data += "_anchor_" + System.identityHashCode(constant) + " -> " + "_anchor_" + System.identityHashCode(constant.getType()) + "\n";

	}

	@Override
	public void visit(Variable var) {
		this.data += "_anchor_" + System.identityHashCode(var) + " [label=\"\",shape=circle]\n";
		var.getType().accept(this);
		this.data += "_anchor_" + System.identityHashCode(var) + " -> " + "_anchor_" + System.identityHashCode(var.getType()) + "\n";

	}

	@Override
	public void visit(Integer i) {
		if (! this.included.contains(System.identityHashCode(i))) {
			this.included.add(System.identityHashCode(i));
			this.data += "_anchor_" + System.identityHashCode(i) + " [label=\"Integer\",shape=box,style=rounded]\n";
		}
	}

	@Override
	public void visit(Array ra) {
		if (! this.included.contains(System.identityHashCode(ra))) {
			this.included.add(System.identityHashCode(ra));
			this.data += "_anchor_" + System.identityHashCode(ra) + " [label=\"Array\\nlength: " + ra.getLength() + "\",shape=box,style=rounded]\n";
			ra.getElemType().accept(this);
			this.data += "_anchor_" + System.identityHashCode(ra) + " -> " + "_anchor_" + System.identityHashCode(ra.getElemType()) + "\n";
		}
	}

	@Override
	public void visit(Record r) {
		if (! this.included.contains(System.identityHashCode(r))) {
			this.included.add(System.identityHashCode(r));
			this.data += "_anchor_" + System.identityHashCode(r) + " [label=\"Record\",shape=box,style=rounded]\n";
			r.getScope().accept(this);
			this.data += "_anchor_" + System.identityHashCode(r) + " -> " + "_anchor_" + System.identityHashCode(r.getScope()) + "\n";
		}
	}

	@Override
	public void visit(Scope s) {
		SortedSet<Map.Entry<String, Entry>> entrySet = s.getEntries();
		for (Map.Entry<String, Entry> e : entrySet) {
			e.getValue().accept(this);
		}
		this.data += "subgraph cluster_" + System.identityHashCode(s) + " {\n";
		for (Map.Entry<String, Entry> e : entrySet) {
			this.data += e.getKey() + "_" + System.identityHashCode(s) + 
					" [label=\"" + e.getKey() + "\",shape=box,color=white,fontcolor=black]\n";
		}
		this.data += "_anchor_" + System.identityHashCode(s) + " [label=\"\",style=invis]\n";
		this.data += "}\n";
		for (Map.Entry<String, Entry> e : entrySet) {
			this.data += e.getKey() + "_" + System.identityHashCode(s) + " -> " + "_anchor_" + System.identityHashCode(e.getValue()) + "\n";
		}
	}
	
	public String toString() {
		return "digraph X {\n" + this.data + "}\n";
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
	public void visit(Procedure p) {
		// TODO Auto-generated method stub
		
	}

}
