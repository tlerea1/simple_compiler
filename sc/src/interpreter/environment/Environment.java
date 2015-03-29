package interpreter.environment;

import java.util.HashMap;
import java.util.Map;

public class Environment {
	private HashMap<String, Box> environment;
	
	public Environment() {
		this.environment = new HashMap<String, Box>();
	}
	
	public Box get(String identifier) {
		return this.environment.get(identifier);
	}
	
	public void put(String identifier, Box b) {
		this.environment.put(identifier, b);
	}
	
	public Environment clone() {
		Environment toReturn = new Environment();
		for (Map.Entry<String, Box> entry : this.environment.entrySet()) {
			toReturn.put(new String(entry.getKey()), entry.getValue().clone());
		}
		return toReturn;
	}
}
