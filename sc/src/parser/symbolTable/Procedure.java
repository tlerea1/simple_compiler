package parser.symbolTable;

import interpreter.environment.Environment;

import java.util.List;

import parser.Formal;
import parser.ast.Expression;
import parser.ast.Instruction;
import visitor.Visitor;

public class Procedure extends Entry {
	
	private Scope scope;
	private Instruction body;
	private Expression ret;
	private List<Formal> formals;
	private Type returnType;
	
	public List<Formal> getFormals() {
		return formals;
	}

	public void setFormals(List<Formal> formals) {
		this.formals = formals;
	}

	public Procedure() {
		this.scope = new Scope(null);
		this.body = null;
		this.ret = null;
		this.formals = null;
		this.returnType = null;
	}
	
	public Procedure(Scope scope, Instruction body, Expression returnVal, List<Formal> formals, Type type) {
		this.scope = scope;
		this.body = body;
		this.ret = returnVal;
		this.formals = formals;
		this.returnType = type;
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
	
	public Environment getEnvironment() {
		return this.scope.getEnvironment();
	}
	
	public Type getType() {
		return this.returnType;
	}
	
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	public int size() {
		int size = this.scope.size();
		for (Formal f : this.formals) {
			size -= f.getType().size();
		}
		return size;
	}
	
}
