package interpreter.environment;

import java.util.HashMap;
import java.util.Map;
/**
 * Class to hold the environment. Each Record has their own along with the Program.
 * @author tuvialerea
 *
 */
public class Environment {
	private HashMap<String, Box> environment;
	private Environment outer;
	
	/**
	 * Standard Environment Constructor.
	 */
	public Environment() {
		this.environment = new HashMap<String, Box>();
		this.outer = null;
	}
	
	
	public HashMap<String, Box> getEnvironment() {
		return environment;
	}


	public void setEnvironment(HashMap<String, Box> environment) {
		this.environment = environment;
	}


	public Environment getOuter() {
		return outer;
	}


	public void setOuter(Environment outer) {
		this.outer = outer;
	}


	/**
	 * Gets the value for the given key.
	 * @param identifier the key
	 * @return the value, null if no mapping
	 */
	public Box get(String identifier) {
		Box b = this.environment.get(identifier);
		if (b == null && this.outer != null) {
			return this.outer.get(identifier);
		}
		return b;
	}
	
	/**
	 * Puts a mapping from identifier to Box.
	 * @param identifier the identifier for the key.
	 * @param b the Box for the value
	 */
	public void put(String identifier, Box b) {
		this.environment.put(identifier, b);
	}
	
	/**
	 * Produces a clone of the environment. 
	 * Clone is deep and with clone all sub environments.
	 * @return the clone
	 */
	public Environment clone() {
		Environment toReturn = new Environment();
		for (Map.Entry<String, Box> entry : this.environment.entrySet()) {
			toReturn.put(new String(entry.getKey()), entry.getValue().clone());
		}
		return toReturn;
	}
}
