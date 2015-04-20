package parser.ast;

import java.util.List;

import visitor.ASTVisitor;

public class ProcedureCall extends Instruction {
	private String function;
	private List<Expression> actuals;
	
	public ProcedureCall(String function, List<Expression> actuals) {
		this.function = function;
		this.actuals = actuals;
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
}
