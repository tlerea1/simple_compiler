package parser.symbolTable;

import util.Singleton;
import interpreter.environment.Box;

public class SimpleString extends Type {

	private Scope scope;
	
	public SimpleString() {
		this.scope.insert("length", new Field(Singleton.getInteger()));
		this.scope.insert("data", new Field(Singleton.getInteger()));
	}
	
	@Override
	public Box getBox() {
		return null;
	}

	@Override
	public int size() {
		return this.scope.size();
	}

}
