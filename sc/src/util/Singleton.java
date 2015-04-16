package util;

import parser.symbolTable.Bool;
import parser.symbolTable.Integer;

public class Singleton {
	private static Integer integer;
	private static Bool bool;
	
	public static Integer getInteger() {
		if (integer == null) {
			integer = new Integer();
		}
		return integer;
	}
	
	public static Bool getBool() {
		if (bool == null) {
			bool = new Bool();
		}
		return bool;
	}
}
