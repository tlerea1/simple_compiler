package interpreter.environment;

import parser.symbolTable.Record;
import parser.symbolTable.Type;

/**
 * Class to hold the memory for a RECORD.
 * @author tuvialerea
 *
 */
public class RecordBox extends Box {
	private Environment environment;
	private Record type;
	
	public RecordBox(Environment e, Record type) {
		this.environment = e;
		this.type = type;
	}
	
	public Box get(String identifier) {
		return this.environment.get(identifier);
	}
	
	public void put(String identifier, Box value) {
		this.environment.put(identifier, value);
	}
	
	public RecordBox clone() {
		return new RecordBox(this.environment.clone(), this.type);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public Type getType() {
		return this.type;
	}
}
