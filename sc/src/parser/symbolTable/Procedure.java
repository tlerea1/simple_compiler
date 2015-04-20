package parser.symbolTable;

import java.util.List;

import parser.Formal;
import parser.ast.Expression;
import parser.ast.Instruction;

public class Procedure extends Entry {
	
	private Scope scope;
	private Instruction body;
	private Expression ret;
	private List<Formal> formals;
	
	public Procedure() {
		this.scope = new Scope(null);
		this.body = null;
		this.ret = null;
		this.formals = null;
	}
	
	public Procedure(Scope scope, Instruction body, Expression returnVal, List<Formal> formals) {
		this.scope = scope;
		this.body = body;
		this.ret = returnVal;
		this.formals = formals;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public Instruction getBody() {
		return body;
	}

	public void setBody(Instruction body) {
		this.body = body;
	}

	public Expression getRet() {
		return ret;
	}

	public void setRet(Expression ret) {
		this.ret = ret;
	}
	
	public boolean match(List<Expression> exps) {
		if (exps.size() != this.formals.size()) {
			return false;
		}
		for (int i=0;i<this.formals.size();i++) {
			if (exps.get(i).getType() != this.formals.get(i).getType()) {
				return false;
			}
		}
		return true;
	}
	
}
