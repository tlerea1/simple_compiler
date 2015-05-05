package parser.symbolTable.procedures;

import java.util.ArrayList;
import java.util.List;

import parser.Formal;
import parser.ast.Expression;
import parser.ast.Number;
import parser.symbolTable.Array;
import parser.symbolTable.Constant;
import parser.symbolTable.FormalVariable;
import parser.symbolTable.Scope;
import parser.symbolTable.Type;
import parser.symbolTable.Variable;
import util.Singleton;

public class Len extends Procedure {
	
	public Len() {
		super();
		Scope s = new Scope(null);
		Formal f = new Formal("array", new Array(-1, null));
		List<Formal> formals = new ArrayList<Formal>();
		formals.add(f);
		super.setFormals(formals);
		s.insert("array", new FormalVariable(f.getType()));
		super.setScope(s);
	}
	
	public Expression getRet() {
		return new Number(new Constant(((Array) ((Variable) super.getScope().find("array")).getType()).getLength(), Singleton.getInteger()));
	}
	
	public Type getType() {
		return Singleton.getInteger();
	}
	
	public boolean match(List<Expression> exp) {
		if (exp.size() != 1) {
			return false;
		}
		if (exp.get(0).getType() instanceof Array) {
			return true;
		}
		return false;
	}
}
