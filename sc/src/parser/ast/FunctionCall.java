package parser.ast;

import java.util.List;

import parser.symbolTable.Procedure;
import parser.symbolTable.Scope;
import parser.symbolTable.Type;
import visitor.ASTVisitor;

public class FunctionCall extends Expression {

	private String ident;
	private List<Expression> actuals;
	private Procedure proc;
	private Scope scope;
	
	public FunctionCall(String identifier, List<Expression> actuals, Procedure proc) {
		this.setIdent(identifier);
		this.setActuals(actuals);
		this.proc = proc;
		this.scope = this.proc.getScope().clone();
		this.setTypes();
	}
	
	/*
	 * Private helper for setting all of the types of the formalVariables in the call scope
	 */
	private void setTypes() {
		for (int i=0;i<this.actuals.size();i++) {
			((parser.symbolTable.Variable) this.scope.find(this.proc.getFormals().get(i).getIdent()))
				.setType(this.actuals.get(i).getType());
		}
	}
	
	@Override
	public Expression fold() {
		return this;
	}

	@Override
	public Type getType() {
		return proc.getType();
	}

	@Override
	public Expression getOpposite() {
		return proc.getRet().getOpposite();
	}

	public List<Expression> getActuals() {
		return actuals;
	}

	public void setActuals(List<Expression> actuals) {
		this.actuals = actuals;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public Procedure getProcedure() {
		return this.proc;
	}
	
	public int accept(ASTVisitor v) {
		return v.visit(this);
	}
	
}
