package parser.ast;

import java.util.List;

import amd64.Item;
import parser.symbolTable.Scope;
import parser.symbolTable.procedures.Procedure;
import visitor.ASTVisitor;
import visitor.CodeGenVisitor;

public class ProcedureCall extends Instruction {
	private String function;
	private List<Expression> actuals;
	private Procedure proc;
	private Scope scope;
	
	public ProcedureCall(String function, List<Expression> actuals, Procedure proc) {
		this.function = function;
		this.actuals = actuals;
		this.proc = proc;
		this.scope = this.proc.getScope().clone();
		this.setTypes();
	}
	
	public Procedure getProc() {
		return proc;
	}

	public void setProc(Procedure proc) {
		this.proc = proc;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	private void setTypes() {
		for (int i=0;i<this.actuals.size();i++) {
			((parser.symbolTable.Variable) this.scope.find(this.proc.getFormals().get(i).getIdent()))
				.setType(this.actuals.get(i).getType());
		}
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public List<Expression> getActuals() {
		return actuals;
	}

	public void setActuals(List<Expression> actuals) {
		this.actuals = actuals;
	}
	
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}

	@Override
	public Item accept(CodeGenVisitor v) {
		return v.visit(this);
	}
}
