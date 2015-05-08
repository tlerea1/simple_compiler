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
import parser.symbolTable.Integer;
import util.Singleton;

/** NOT DONE
 *TODO
 * @author tuvialerea
 *
 */

public class Char extends Procedure {
	public Char() {
		super();
		Scope s = new Scope(null);
		Formal f = new Formal("int", Singleton.getInteger());
		List<Formal> formals = new ArrayList<Formal>();
		formals.add(f);
		super.setFormals(formals);
		s.insert("int", new FormalVariable(f.getType()));
		super.setScope(s);
	}
	
	public Expression getRet() {
		FormalVariable f = (FormalVariable) super.getScope().find("int");
		return new parser.ast.Variable("int", f);
	}
	
	public Type getType() {
		return Singleton.getChar();
	}
	
	public boolean match(List<Expression> exp) {
		if (exp.size() != 1) {
			return false;
		}
		if (exp.get(0).getType() instanceof Integer) {
			return true;
		}
		return false;
	}
}
