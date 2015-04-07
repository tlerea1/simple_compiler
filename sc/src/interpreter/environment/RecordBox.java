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
	
	/**
	 * RecordBox Constructor.
	 * @param e the environment to set to.
	 * @param type the ST record object
	 */
	public RecordBox(Environment e, Record type) {
		this.environment = e;
		this.type = type;
	}
	
	/**
	 * Gets the Box for the given identifier.
	 * @param identifier the identifier to search for.
	 * @return the Box which the identifier maps to.
	 */
	public Box get(String identifier) {
		return this.environment.get(identifier);
	}
	
	/**
	 * Puts a mapping of the given identifier to the given Box.
	 * @param identifier the identifier for the Key
	 * @param value the Box for the value
	 */
	public void put(String identifier, Box value) {
		this.environment.put(identifier, value);
	}
	
	public RecordBox clone() {
		return new RecordBox(this.environment.clone(), this.type);
	}

	/**
	 * Gets the environment for the Record.
	 * @return the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * Sets the environment for the Record.
	 * @param environment the environment to set to.
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public Type getType() {
		return this.type;
	}
}
