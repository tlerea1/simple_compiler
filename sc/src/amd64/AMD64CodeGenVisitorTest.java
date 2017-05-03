package amd64;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import parser.ast.Assign;
import parser.symbolTable.Array;
import parser.symbolTable.Constant;
import parser.symbolTable.FormalVariable;
import parser.symbolTable.LocalVariable;
import parser.symbolTable.Scope;
import parser.symbolTable.Variable;
import util.Singleton;

public class AMD64CodeGenVisitorTest {

	@Test
	public void assignNumberToVariable() throws UnsupportedEncodingException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		Assign a = new Assign(
				new parser.ast.Variable("a", new parser.symbolTable.Variable(Singleton.getInteger(), -4)), 
				new parser.ast.Number(new Constant(10, Singleton.getInteger())));
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		a.accept(visit);
		assertEquals("movq $10, -4(%r15)" + System.getProperty("line.separator"), os.toString("UTF-8"));
	}
	
	@Test
	public void assignVariableToVariable() throws UnsupportedEncodingException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		Assign a = new Assign(
				new parser.ast.Variable("a", new parser.symbolTable.Variable(Singleton.getInteger(), -4)), 
				new parser.ast.Variable("a", new parser.symbolTable.Variable(Singleton.getInteger(), -8)));
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		a.accept(visit);
		String expected = "leaq -8(%r15), %r14\n"
						+ "movq (%r14), %r14\n"
						+ "movq %r14, -4(%r15)" + System.getProperty("line.separator");
		
		assertEquals(expected, os.toString());
	}
	
	@Test
	public void assignNumberToField() throws UnsupportedEncodingException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		Assign a = new Assign(
				new parser.ast.Field(
						new parser.ast.Variable("b", 
								new parser.symbolTable.Variable(
										new parser.symbolTable.Record(new Scope(null)))),
						new parser.ast.Variable("a", new parser.symbolTable.Variable(Singleton.getInteger(), -4))), 
					new parser.ast.Number(new Constant(10, Singleton.getInteger())));
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		a.accept(visit);
		assertEquals("movq $10, -4(%r15)" + System.getProperty("line.separator"), os.toString("UTF-8"));
	}
	
	@Test
	public void assignVariableToField() throws UnsupportedEncodingException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		Assign a = new Assign(
				new parser.ast.Field(
						new parser.ast.Variable("b", 
								new parser.symbolTable.Variable(
										new parser.symbolTable.Record(new Scope(null)))),
						new parser.ast.Variable("a", new parser.symbolTable.Variable(Singleton.getInteger(), -4))), 
					new parser.ast.Variable("a", new parser.symbolTable.Variable(Singleton.getInteger(), -8)));
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		a.accept(visit);
		String expected = "leaq -8(%r15), %r14\n"
				+ "movq (%r14), %r14\n"
				+ "movq %r14, -4(%r15)" + System.getProperty("line.separator");
		
		assertEquals(expected, os.toString("UTF-8"));
	}

	@Test
	public void symbolTableVariableVisit() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		parser.symbolTable.Variable v = new Variable(Singleton.getInteger(), -4);
		Memory off = v.accept(visit);
		assertTrue(off instanceof ConstantOffset);
		ConstantOffset co = (ConstantOffset) off;
		assertEquals(-4, co.getOffset());
		assertEquals("%r15", co.getRegister());
	}
	
	@Test
	public void symbolTableFormalVariableVisitValue() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		parser.symbolTable.FormalVariable v = new FormalVariable(Singleton.getInteger());
		v.setLocation(-4);
		Memory off = v.accept(visit);
		assertTrue(off instanceof ConstantOffset);
		ConstantOffset co = (ConstantOffset) off;
		assertEquals(-4, co.getOffset());
		assertEquals("%rbp", co.getRegister());
	}
	
	@Test
	public void symbolTableFormalVariableVisitReference() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		parser.symbolTable.FormalVariable v = new FormalVariable(new Array(5, Singleton.getInteger()));
		v.setLocation(-4);
		Memory off = v.accept(visit);
		assertTrue(off instanceof Address);
		assertTrue(os.toString().startsWith("movq -4(%rbp)"));
	}
	
	@Test
	public void symbolTableLocalVariableVisit() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		parser.symbolTable.LocalVariable v = new LocalVariable(Singleton.getInteger());
		v.setLocation(-4);
		Memory off = v.accept(visit);
		assertTrue(off instanceof ConstantOffset);
		ConstantOffset co = (ConstantOffset) off;
		assertEquals(-4, co.getOffset());
		assertEquals("%rbp", co.getRegister());
	}
	
	@Test
	public void astVariableVisit() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		parser.ast.Variable v = 
				new parser.ast.Variable("a", new Variable(Singleton.getInteger(), -4));
		Memory off = v.accept(visit);
		assertTrue(off instanceof ConstantOffset);
		ConstantOffset co = (ConstantOffset) off;
		assertEquals(-4, co.getOffset());
		assertEquals("%r15", co.getRegister());		
	}
	
	@Test
	public void astIndexVisitIndexByNumber() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		AMD64CodeGenVisitor visit = new AMD64CodeGenVisitor(ps);
		parser.ast.Index i = 
				new parser.ast.Index(
						new parser.ast.Variable("a", new Variable(new Array(5, Singleton.getInteger()), -40)),
						new parser.ast.Number(new parser.symbolTable.Constant(4, Singleton.getInteger())));
		Memory mem = i.accept(visit);
		assertEquals("", os.toString());
		assertTrue(mem instanceof ConstantOffset);
		ConstantOffset off = (ConstantOffset) mem;
		assertEquals("%r15", off.getRegister());
		assertEquals(-8, off.getOffset());
	}
	
	
}
