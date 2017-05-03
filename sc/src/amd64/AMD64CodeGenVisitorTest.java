package amd64;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import parser.ast.Assign;
import parser.symbolTable.Constant;
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
		assertEquals("movq $10, -4(%r15)\n", os.toString("UTF-8"));
	}

}
