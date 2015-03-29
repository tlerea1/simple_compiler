package util;

import parser.symbolTable.Integer;

public class Singleton {
	private static Integer integer;
	
	public static Integer getInteger() {
		if (integer == null) {
			integer = new Integer();
		}
		return integer;
	}
}
