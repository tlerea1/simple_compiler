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
	
	/**
	 * Standard Environment Constructor.
	 */
	public Environment() {
		this.environment = new HashMap<String, Box>();
	}
	
	/**
	 * Gets the value for the given key.
	 * @param identifier the key
	 * @return the value, null if no mapping
	 */
	public Box get(String identifier) {
		return this.environment.get(identifier);
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
