package parser.ast;

public abstract class Expression extends Node {
	public abstract Expression fold();
}
