package visitor;

import amd64.ConstantOffset;
import amd64.Item;
import amd64.Memory;

public interface CodeGenVisitor {
	
	
	// Symbol Table Entries
	public Item visit(parser.symbolTable.Entry v);
	public Item visit(parser.symbolTable.Constant v);
	public Item visit(parser.symbolTable.Scope v);

	// Types
	public Item visit(parser.symbolTable.Array v);
	public Item visit(parser.symbolTable.Bool v);
	public Item visit(parser.symbolTable.Char v);
	public Item visit(parser.symbolTable.Integer v);
	public Item visit(parser.symbolTable.Record v);
	public Item visit(parser.symbolTable.SimpleString v);
	
	// Variables
	public Memory visit(parser.symbolTable.Variable v);
	//public Item visit(parser.symbolTable.Field v);
	public Memory visit(parser.symbolTable.FormalVariable v);
	public ConstantOffset visit(parser.symbolTable.LocalVariable v);
	
	// SymbolTable.procedures
	public Item visit(parser.symbolTable.procedures.Procedure v);
	public Item visit(parser.symbolTable.procedures.Char v);
	public Item visit(parser.symbolTable.procedures.Len v);
	
	// AST
	public Item visit(parser.ast.Node v);
	
	// Expressions
	public Item visit(parser.ast.Expression v);
	public Item visit(parser.ast.Binary v);
	public Item visit(parser.ast.RelBinary v);
	public Item visit(parser.ast.FunctionCall v);
	
	// Locations (Type of Expression)
	public Memory visit(parser.ast.Location v);
	public Memory visit(parser.ast.Field v);
	public Memory visit(parser.ast.Index v);
	public Memory visit(parser.ast.Variable v);
	public Item visit(parser.ast.Number v);
	
	// Instructions
	public Item visit(parser.ast.Assign v);
	public Item visit(parser.ast.If v);
	public Item visit(parser.ast.ProcedureCall v);
	public Item visit(parser.ast.Read v);
	public Item visit(parser.ast.Repeat v);
	public Item visit(parser.ast.Write v);
	

	
	
}