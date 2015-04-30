package util;

import parser.symbolTable.Bool;
import parser.symbolTable.Integer;
import parser.symbolTable.Type;

public class Singleton {
	private static Integer integer;
	private static Bool bool;
	
	/**
	 * Function to get the singleton instance of the Integer type
	 * @return the Integer type
	 */
	public static Integer getInteger() {
		if (integer == null) {
			integer = new Integer();
		}
		return integer;
	}
	
	/**
	 * Function to get the singleton instance of the Boolean type
	 * @return the Boolean type
	 */
	public static Bool getBool() {
		if (bool == null) {
			bool = new Bool();
		}
		return bool;
	}
	
	/**
	 * Function to test if the given type is a Value type
	 * @param t the given type
	 * @return true if given type is a value type i.e. Type on which arithmetic is valid.
	 * false if not.
	 */
	public static boolean isValueType(Type t) {
		if (t instanceof Integer || t instanceof Bool) {
			return true;
		}
		return false;
	}
}
