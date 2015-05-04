package amd64;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import parser.ast.Assign;
import parser.ast.Binary;
import parser.ast.Expression;
import parser.ast.Field;
import parser.ast.FunctionCall;
import parser.ast.If;
import parser.ast.Index;
import parser.ast.Instruction;
import parser.ast.Location;
import parser.ast.Number;
import parser.ast.ProcedureCall;
import parser.ast.Read;
import parser.ast.Repeat;
import parser.ast.Write;
import parser.symbolTable.Array;
import parser.symbolTable.Entry;
import parser.symbolTable.FormalVariable;
import parser.symbolTable.LocalVariable;
import parser.symbolTable.Procedure;
import parser.symbolTable.Scope;
import parser.symbolTable.Variable;
import util.Singleton;

public class ImprovedCodeGen {
	
	private RegisterAllocator registers;
	private Instruction ast;
	private Scope st;
	private PrintStream outputstream;
	private int currentLabel;
	private Output out;
	
	public ImprovedCodeGen(Instruction ast, Scope st, PrintStream out) {
		this.ast = ast;
		this.st = st;
		this.outputstream = out;
		this.currentLabel = 0;
		this.registers = new RegisterAllocator(out);
		this.out = new Output();
	}
	
	public void generateAMD64() {
		this.out.println(".text");
		this.out.println(".globl main");
		this.visit(this.st); // Handle declarations

		this.genFunctions(); // Start of main
		if (this.ast != null) {
			this.visit(this.ast); // Program instructions
		}
		this.registers.free();
		this.text(); // End of assembly
		this.outputstream.print(this.out);
	}
	
	/*
	 * Function to output the start of main, includes stack allocation and memset
	 */
	private void genFunctions() {


		
		this.out.println("main:\n");
		this.out.println("push %rbp");
		this.out.println("movq %rsp, %rbp");
		this.out.println("subq $" + this.st.size() + ", %rsp");
		this.out.println("movl $" + this.st.size() + ", %edx");
		this.out.println("movl $0, %esi");
		this.out.println("movq %rsp, %rdi");
		this.out.println("call memset");
		this.out.println("movq %rbp, %r15"); // r15 holds global bp
		
	}
	
	/*
	 * Function for end of main / After main stuff, strings and error segments
	 */
	private void text() {
		this.out.println("movl $0, %eax");
		this.out.println("leave");
		this.out.println("retq\n\n\n");
		
		// Array out of bounds error
		this.out.println("array_out_of_bounds:");
		this.out.println("movl $printf_arg, %edi");
		this.out.println("movl $array_str, %esi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		this.out.println("movl $1, %edi");
		this.out.println("call exit\n\n");
		
		// Divide by zero error
		this.out.println("div_by_zero:");
		this.out.println("movl $printf_arg, %edi");
		this.out.println("movl $div_zero_mes, %esi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		this.out.println("movl $1, %edi");
		this.out.println("call exit\n\n");
		
		// Mod by zero error
		this.out.println("mod_by_zero:");
		this.out.println("movl $printf_arg, %edi");
		this.out.println("movl $mod_zero_mes, %esi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		this.out.println("movl $1, %edi");
		this.out.println("call exit\n\n");
		
		// Saved strings
		this.out.println(".section .rodata");
		this.out.println("array_str:");
		this.out.println(".string \"error: Array index out of bounds!\\n\"");
		this.out.println("printf_arg:");
		this.out.println(".string \"%s\"");
		this.out.println("printf_num:");
		this.out.println(".string \"%d\\n\"");
		this.out.println("scanf_num:");
		this.out.println(".string \"%d\"");
		this.out.println("div_zero_mes:");
		this.out.println(".string \"error: Division by Zero!\\n\"");
		this.out.println("mod_zero_mes:");
		this.out.println(".string \"error: Mod by Zero!\\n\"");
		this.out.println(".section .data");
		this.out.println("_globals:");
		this.out.println(".long 0");

	}

	private void visit(Entry e) {
		if (e instanceof Procedure) {
			this.visit((Procedure) e);
		} else {
			throw new AMD64Exception("Processing invalid type of entry");
		}
	}

	// Generate the code for the functions
	private void visit(Procedure p) {
		this.out.println("push %rbp"); // Save bp
		this.out.println("movq %rsp, %rbp");
		this.out.println("subq $" + p.size() + ", %rsp");
		this.out.println("movl $" + p.size() + ", %edx");
		this.out.println("movl $0, %esi");
		this.out.println("movq %rsp, %rdi");
		this.out.println("call memset"); // Allocate and zero space for locals
		
		if (p.getBody() != null) {
			this.visit(p.getBody()); // Generate instructions
		}
		
		// TODO: Work with returns
		if (p.getRet() != null) {
			Item ret = this.visit(p.getRet());// Generate return code
			this.registers.use("%rax", null); // Cause whatever is in %rax to be moved out
			this.out.println("movq " + this.compareHelper(ret) + ", %rax");
			this.free(ret);
		}
		this.out.println("movq %rbp, %rsp"); // Deallocate stack space
		this.out.println("pop %rbp"); // Return bp
		this.out.println("retq\n\n"); // Ret
	}

	private Item visit(Variable var) {
		return new ConstantOffset(var.getLocation(), "%r15");
	}

	private Item visit(FormalVariable var) {
		if (! Singleton.isValueType(var.getType())) {
			String reg = this.registers.pop(); // TODO: Spill
			this.out.println("movq " + var.getLocation() + "(%rbp), " + reg);
//			this.out.println("movq (" + reg + "), " + reg);
			return new Address(reg);
		} else {
			return new ConstantOffset(var.getLocation(), "%rbp");
		}		
	}

	private Item visit(LocalVariable var) {
		return new ConstantOffset(var.getLocation(), "%rbp");
	}

	private void visit(Scope s) {
		for (Map.Entry<String, Entry> e : s.getEntries()) {
			if (e.getValue() instanceof Procedure) { // Accept all variables
				this.out.println(e.getKey() + ":");
				this.visit(e.getValue());
			}
		}
	}

	private void visit(Instruction i) {
		while (i != null) {
			if (i instanceof Assign) {
				this.visit((Assign) i);
			} else if (i instanceof If) {
				this.visit((If) i);
			} else if (i instanceof Repeat) {
				this.visit((Repeat) i);
			} else if (i instanceof Read) {
				this.visit((Read) i);
			} else if (i instanceof Write) {
				this.visit((Write) i);
			} else if (i instanceof ProcedureCall) {
				this.visit((ProcedureCall) i);
			} else {
				throw new AMD64Exception("Unknown Instruction type");
			}
			i = i.getNext();
		}
	}

	private void visit(Assign a) {
		Item location = this.visit(a.getLoc());
		Item exp = this.visit(a.getExp());
		
		if (! Singleton.isValueType(a.getExp().getType())) {
			// TODO: Assignment by memcpy
			if (exp instanceof Address) {
				this.out.println("movq " + ((Address) exp).getRegister() + ", %rsi");
			} else if (exp instanceof ConstantOffset) {
				this.out.println("leaq " + ((ConstantOffset) exp).getOffset() + "(" + ((ConstantOffset) exp).getRegister() + "), %rsi");
			}
			
			if (location instanceof Address) {
				this.out.println("movq " + ((Address) location).getRegister() + ", %rdi");
			} else if (location instanceof ConstantOffset) {
				this.out.println("leaq " + ((ConstantOffset) location).getOffset() + "(" + ((ConstantOffset) location).getRegister() + "), %rdi");
			}
			
			this.registers.use("%rdx", null);
			this.out.println("movq $" + a.getExp().getType().size() + ", %rdx");
			List<String> registers = this.pushRegisters();
			this.out.println("call memcpy");
			this.popRegisters(registers);
			return;
		}

		if (location instanceof Address) {
			String loc = ((Address) location).getRegister();
			if (exp instanceof Address) {
				this.out.println("movq (" + ((Address) exp).getRegister() + "), " + ((Address) exp).getRegister()); // Dereference exp
				this.out.println("movq " + ((Address) exp).getRegister() + ", (" + loc + ")");
				this.registers.push(((Address) exp).getRegister());
			} else if (exp instanceof ExpressionValue) {
				this.out.println("movq " + ((ExpressionValue) exp).getRegister() + ", (" + loc + ")");
				this.registers.push(((ExpressionValue) exp).getRegister());
			} else if (exp instanceof ConstantExpression) {
				this.out.println("movq $" + ((ConstantExpression) exp).getValue() + ", (" + loc + ")");
			} else if (exp instanceof ConstantOffset) {
				String reg = this.registers.pop();
				this.out.println("leaq " + this.compareHelper(exp) + ", " + reg);
				this.out.println("movq (" + reg + "), " + reg);
				this.out.println("movq " + reg + ", (" + loc + ")");
				this.registers.push(reg);
			} else {
				throw new AMD64Exception("Assign: unknown expression item type");
			}
			this.registers.push(loc);
		} else if (location instanceof ConstantOffset) {
			String loc = ((ConstantOffset) location).getRegister();
			int offset = ((ConstantOffset) location).getOffset();

			if (exp instanceof Address) {
				this.out.println("movq (" + ((Address) exp).getRegister() + "), " + ((Address) exp).getRegister()); // Dereference exp
				this.out.println("movq " + ((Address) exp).getRegister() + ", " + offset + "(" + loc + ")");
				this.registers.push(((Address) exp).getRegister());
			} else if (exp instanceof ExpressionValue) {
				this.out.println("movq " + ((ExpressionValue) exp).getRegister() + ", " + offset + "(" + loc + ")");
				this.registers.push(((ExpressionValue) exp).getRegister());
			} else if (exp instanceof ConstantExpression) {
				this.out.println("movq $" + ((ConstantExpression) exp).getValue() + ", " + offset + "(" + loc + ")");
			} else if (exp instanceof ConstantOffset) {
				String reg = this.registers.pop();
				this.out.println("leaq " + this.compareHelper(exp) + ", " + reg);
				this.out.println("movq (" + reg + "), " + reg);
				this.out.println("movq " + reg + ", " + this.compareHelper(location));
				this.registers.push(reg);
			} else {
				throw new AMD64Exception("Assign: unknown expression item type");
			}
		}
//		this.registers.free();
	}

	private void visit(If a) {
		Item exp = this.visit(a.getCondition()); // cmp done here
		int current = ++this.currentLabel;
		if (exp instanceof ConstantExpression) {
			if (((ConstantExpression) exp).getValue() != 1) { // If not true
				if (a.getIfFalse() != null) { // If have false instructions
					this.visit(a.getIfFalse());
				}
			} else { // If true
				this.visit(a.getIfTrue()); // Do true instructions
			}
			return;
		} else if (exp instanceof ExpressionValue) {
			this.out.println("cmp $1, " + ((ExpressionValue) exp).getRegister());
		} else if (exp instanceof ConstantOffset) {
			this.out.println("cmp $1, " + ((ConstantOffset) exp).getOffset() + "(" + ((ConstantOffset) exp).getRegister() + ")");
		} else if (exp instanceof Address) {
			this.out.println("cmp $1, (" + ((Address) exp).getRegister() + ")");
		}
		this.free(exp);
		this.out.println("jne L" + current); // Jump if false

		this.visit(a.getIfTrue());
		if (a.getIfFalse() != null) { // else segment
			int current2 = ++this.currentLabel;
			this.out.println("jmp L" + current2);
			this.out.println("L" + current + ":");
			this.visit(a.getIfFalse());
			this.out.println("L" + current2 + ":");
		} else {
			this.out.println("L" + current + ":");
		}
//		this.registers.free();
	}

	private void visit(Repeat r) {
		int current = ++this.currentLabel;
		this.out.println("L" + current + ":"); // label to jmp to
		this.visit(r.getInstructions()); // Accept instructions 
		Item exp = this.visit(r.getCondition()); // Do cmp
		if (exp instanceof ConstantExpression) {
			int value = ((ConstantExpression) exp).getValue();
			if (value != 1) {
				this.out.println("jmp L" + current);
			}
		} else {
			this.out.println("cmpq $1, " + this.compareHelper(exp));
			this.out.println("jne L" + current); // If not true, repeat
		}
		this.free(exp);
	}

	private void visit(Read r) {
		Item i = this.visit(r.getLoc());
		if (i instanceof Address) {
			this.out.println("movq " + ((Address) i).getRegister() + ", %rsi");
		} else if (i instanceof ConstantOffset) {
			this.out.println("leaq " + ((ConstantOffset) i).getOffset() + "(" + ((ConstantOffset) i).getRegister() + "), %rsi");
		}
		List<String> registers = this.pushRegisters();
		this.out.println("movl $scanf_num, %edi");
		this.out.println("movl $0, %eax");
		this.out.println("call __isoc99_scanf");
		this.out.println("cmpl $-1, %eax"); // If EOF, done
		this.popRegisters(registers);
		// Labels for different cmps
		int notNegOne = ++this.currentLabel;
		int done = ++this.currentLabel;
		this.out.println("jne L" + notNegOne);
		if (i instanceof Address) {
			this.out.println("movq $-1, (" + ((Address) i).getRegister() + ")");
		} else if (i instanceof ConstantOffset) {
			this.out.println("movq $-1, " + ((ConstantOffset) i).getOffset() + "(" + ((ConstantOffset) i).getRegister() + ")");
		}
		this.out.println("jmp L" + done);
		
		
		// Done
		this.out.println("L" + notNegOne + ":");
		this.out.println("cmpl $0, %eax"); // If zero, done
		this.out.println("jne L" + done);
		if (i instanceof Address) {
			this.out.println("movq $-1, (" + ((Address) i).getRegister() + ")");
		} else if (i instanceof ConstantOffset) {
			this.out.println("movq $-1, " + ((ConstantOffset) i).getOffset() + "(" + ((ConstantOffset) i).getRegister() + ")");
		}		
		this.out.println("L" + done + ":");
		this.free(i);
//		this.registers.free();
	}

	private void visit(Write w) {
		Item i = this.visit(w.getExp());
		if (i instanceof Address) {
			this.out.println("movq (" + ((Address) i).getRegister() + "), %rsi");
			this.registers.push(((Address) i).getRegister());
		} else if (i instanceof ConstantExpression) {
			this.out.println("movq $" + ((ConstantExpression) i).getValue() + ", %rsi");
		} else if (i instanceof ExpressionValue) {
			this.out.println("movq " + ((ExpressionValue) i).getRegister() + ", %rsi");
			this.registers.push(((ExpressionValue) i).getRegister());
		} else if (i instanceof ConstantOffset) {
			this.out.println("movq " + ((ConstantOffset) i).getOffset() + "(" + ((ConstantOffset) i).getRegister() + "), %rsi");
		}
		this.out.println("movl $printf_num, %edi");
		this.out.println("movl $0, %eax");
		this.out.println("call printf");
		this.free(i);
//		this.registers.free();
		
	}

	private void visit(ProcedureCall func) {
		List<String> reg = this.pushRegisters();
		for (int i=func.getActuals().size()-1;i>=0;i--) {
			if (func.getActuals().get(i).getType() instanceof Array) {
				((Array) ((parser.symbolTable.Variable) func.getProc().getScope()
						.find(func.getProc().getFormals().get(i).getIdent())).getType())
						.setCurrentLength(((Array) func.getActuals().get(i).getType()).getLength());
			}
			Item actual = this.visit(func.getActuals().get(i));
			if (actual instanceof ConstantExpression) {
				this.out.println("push " + this.compareHelper(actual));
			} else if (actual instanceof ExpressionValue) {
				this.out.println("push " + this.compareHelper(actual));
			} else if (actual instanceof Address) {
				if (Singleton.isValueType(func.getActuals().get(i).getType())) {
					this.out.println("push " + this.compareHelper(actual));
				} else {
					String register = this.registers.pop();
					this.out.println("leaq " + this.compareHelper(actual) + ", " + register);
					this.out.println("push " + register);
					this.registers.push(register);
				}
			} else if (actual instanceof ConstantOffset) {
				if (Singleton.isValueType(func.getActuals().get(i).getType())) {
					this.out.println("push " + this.compareHelper(actual));
				} else {
					String register = this.registers.pop();
					this.out.println("leaq " + this.compareHelper(actual) + ", " + register);
					this.out.println("push " + register);
					this.registers.push(register);
				}
			}
			this.free(actual);
		} // Push all args in reverse order since first formal has lowest address
		this.out.println("call " + func.getFunction());
		this.out.println("addq $" + func.getActuals().size() * CodeGen.SIZEOF_INT + ", %rsp");
		this.popRegisters(reg);
	}

	public Item visit(Expression e) {
		if (e instanceof FunctionCall) {
			return this.visit((FunctionCall) e);
		} else if (e instanceof Binary) {
			return this.visit((Binary) e);
		} else if (e instanceof Number) {
			return this.visit((Number) e);
		} else if (e instanceof Location) {
			return this.visit((Location) e);
		} else {
			throw new AMD64Exception("Unknown Expression type");
		}
	}

	public Item visit(FunctionCall func) {
		List<String> reg = this.pushRegisters();
		for (int i=func.getActuals().size()-1;i>=0;i--) {
			Item actual = this.visit(func.getActuals().get(i));
			if (actual instanceof ConstantExpression) {
				this.out.println("push " + this.compareHelper(actual));
			} else if (actual instanceof ExpressionValue) {
				this.out.println("push " + this.compareHelper(actual));
			} else if (actual instanceof Address) {
				if (Singleton.isValueType(func.getActuals().get(i).getType())) {
					this.out.println("push " + this.compareHelper(actual));
				} else {
					String register = this.registers.pop();
					this.out.println("leaq " + this.compareHelper(actual) + ", " + register);
					this.out.println("push " + register);
					this.registers.push(register);
				}
			} else if (actual instanceof ConstantOffset) {
				if (Singleton.isValueType(func.getActuals().get(i).getType())) {
					this.out.println("push " + this.compareHelper(actual));
				} else {
					String register = this.registers.pop();
					this.out.println("leaq " + this.compareHelper(actual) + ", " + register);
					this.out.println("push " + register);
					this.registers.push(register);
				}
			}
			this.free(actual);
		} // Push all args in reverse order since first formal has lowest address
		this.out.println("call " + func.getIdent());
		this.out.println("addq $" + func.getActuals().size() * CodeGen.SIZEOF_INT + ", %rsp");
		ExpressionValue e = new ExpressionValue("%rax");
		this.registers.use("%rax", e);
		this.popRegisters(reg);
		return e;
	}
	/*
	 * Function for doing addq, subq, and, or, imulq
	 */
	private Item operate(String op, Item left, Item right) { //TODO need to make sure not storing result in memory
		if (left instanceof Address) {
			String leftReg = ((Address) left).getRegister();
			if (right instanceof Address) {
				String rightReg = ((Address) right).getRegister();
				this.out.println("movq (" + leftReg + "), " + leftReg); // Dereference left
				this.out.println(op + " (" + rightReg + "), " + leftReg); // Add dereferenced right to left
				this.registers.push(rightReg); // free right
				return new ExpressionValue(leftReg); // return value in left not address since left was dereferenced
			} else if (right instanceof ConstantExpression) {
				this.out.println("movq (" + leftReg + "), " + leftReg);
				this.out.println(op + " $" + ((ConstantExpression) right).getValue() + ", " + leftReg); // add to memory
				return new ExpressionValue(leftReg);
			} else if (right instanceof ExpressionValue) {
				String rightReg = ((ExpressionValue) right).getRegister();
				if (op.equals("subq")) {
					this.out.println("movq (" + leftReg + "), " + leftReg);
					this.out.println("subq " + rightReg + ", " + leftReg);
					this.registers.push(rightReg);
					return new ExpressionValue(leftReg);
				} else {
					this.out.println(op + " (" + leftReg + "), " + rightReg); // add to memory
					this.registers.push(leftReg); // free right register
					return new ExpressionValue(rightReg); // still have address
				}
				
			} else if (right instanceof ConstantOffset) {
				String rightReg = ((ConstantOffset) right).getRegister();
				int value = ((ConstantOffset) right).getOffset();
				this.out.println("movq (" + leftReg + "), " + leftReg); // dereference left
				this.out.println(op + " " + value + "(" + rightReg + "), " + leftReg);
				// Dont free right register since either rbp or r15 both of which werent allocated by register allocation
				return new ExpressionValue(leftReg);
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else if (left instanceof ConstantExpression) {
			int value = ((ConstantExpression) left).getValue();
			if (right instanceof Address) { 
				if (op.equals("subq")) {
					String reg = this.registers.pop();
					this.out.println("movq $" + value + ", " + reg);
					this.out.println("subq (" + ((Address) right).getRegister() + "), " + reg);
					this.registers.push(((Address) right).getRegister());
					return new ExpressionValue(reg);
				} else {
					this.out.println("movq (" + ((Address) right).getRegister() + "), " + ((Address) right).getRegister());
					this.out.println(op + " $" + value + ", " + ((Address) right).getRegister());
					return new ExpressionValue(((Address) right).getRegister()); // still address
				}
			} else if (right instanceof ConstantExpression) {
				throw new AMD64Exception("Binary: left and right cannot both be Constants (due to folding)");
			} else if (right instanceof ExpressionValue) {
				if (op.equals("subq")) {
					String reg = this.registers.pop();
					this.out.println("movq $" + value + ", " + reg);
					this.out.println("subq " + ((ExpressionValue) right).getRegister() + ", " + reg);
					this.registers.push(((ExpressionValue) right).getRegister());
					return new ExpressionValue(reg);
				} else {
					this.out.println(op + " $" + value + ", " + ((ExpressionValue) right).getRegister()); // add to the expression 
					return right;
				}
			} else if (right instanceof ConstantOffset) {
				String reg = this.registers.pop();
				this.out.println("movq $" + value + ", " + reg);
				String rightReg = ((ConstantOffset) right).getRegister();
				int offset = ((ConstantOffset) right).getOffset();
				this.out.println(op + " " + offset + "(" + rightReg + "), " + reg);
				// Dont free right register since either rbp or r15 both of which werent allocated by register allocation

				return new ExpressionValue(reg);
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else if (left instanceof ExpressionValue) {
			String leftReg = ((ExpressionValue) left).getRegister();
			if (right instanceof Address) {
				this.out.println(op + " (" + ((Address) right).getRegister() + "), " + leftReg); // add to expression
				this.registers.push(((Address) right).getRegister()); // free right register
				return left; 
			} else if (right instanceof ConstantExpression) {
				this.out.println(op + " $" + ((ConstantExpression) right).getValue() + ", " + leftReg);
				return left;
			} else if (right instanceof ExpressionValue) {
				this.out.println(op + " " + ((ExpressionValue) right).getRegister() + ", " + leftReg);
				this.registers.push(((ExpressionValue) right).getRegister());
				return left;
			} else if (right instanceof ConstantOffset) {
				String rightReg = ((ConstantOffset) right).getRegister();
				int offset = ((ConstantOffset) right).getOffset();
				this.out.println(op + " " + offset + "(" + rightReg + "), " + leftReg);
				// Dont free right register since either rbp or r15 both of which werent allocated by register allocation
				return left;
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else if (left instanceof ConstantOffset) {
			String leftReg = ((ConstantOffset) left).getRegister();
			int offset = ((ConstantOffset) left).getOffset();
			if (right instanceof Address) {
				String rightReg = ((Address) right).getRegister();
				this.out.println("movq (" + rightReg + "), " + rightReg); // Dereference right
				this.out.println(op + " " + rightReg + ", " + offset + "(" + leftReg + ")"); // Add dereferenced right to left
				return new ExpressionValue(rightReg); // return value in left not address since left was dereferenced
			} else if (right instanceof ConstantExpression) {
				int value = ((ConstantExpression) right).getValue();
				String reg = this.registers.pop();
				this.out.println("movq " + offset + "(" + leftReg + "), " + reg);
				this.out.println(op + " $" + value + ", " + reg);
				return new ExpressionValue(reg);
			} else if (right instanceof ExpressionValue) {
				
				if (op.equals("subq")) {
					String reg = this.registers.pop();
					this.out.println("movq " + offset + "(" + leftReg + "), " + reg);
					this.out.println("subq " + ((ExpressionValue) right).getRegister() + ", " + reg);
					this.registers.push(((ExpressionValue) right).getRegister());
					return new ExpressionValue(reg);
				} else {
					this.out.println(op + " " + offset + "(" + leftReg + ")," + ((ExpressionValue) right).getRegister());
					return right;
				}
			} else if (right instanceof ConstantOffset) {
				String reg = this.registers.pop();
				this.out.println("movq " + offset + "(" + leftReg + "), " + reg);
				this.out.println(op + " " + ((ConstantOffset) right).getOffset() 
						+ "(" + ((ConstantOffset) right).getRegister() + "), " + reg);
				return new ExpressionValue(reg);
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else {
			throw new AMD64Exception("Binary: Left is unknown item type");
		}
	}
	
	private ExpressionValue divOp(String op, Item left, Item right) {
		this.registers.use("%rdx", null);
		this.out.println("movq $0, %rdx");

		if (left instanceof Address) {
			String leftReg = ((Address) left).getRegister();
			this.registers.use("%rax", left);
			if (right instanceof Address) {
				String rightReg = ((Address) right).getRegister();
				this.out.println("movq (" + leftReg + "), %rax"); // Dereference left into rax
				this.out.println(op + " (" + rightReg + ")"); // Add dereferenced right to left
				this.registers.push(rightReg); // free right
				return new ExpressionValue(leftReg); // return value in left not address since left was dereferenced
			} else if (right instanceof ConstantExpression) {
				this.out.println("movq (" + leftReg + "), %rax"); // Dereference left into rax
				String reg = this.registers.pop();
				this.out.println("movq $" + ((ConstantExpression) right).getValue() + ", " + reg);
				this.out.println(op + " " + reg); // add to memory //TODO instruction hardcoded
				this.registers.push(reg);
				return new ExpressionValue(leftReg);
			} else if (right instanceof ExpressionValue) {
				String rightReg = ((ExpressionValue) right).getRegister();
				this.out.println("movq (" + leftReg + "), %rax"); // Dereference left into rax
				this.out.println(op + " " + rightReg); // add to memory
				this.registers.push(rightReg); // free right register
				return new ExpressionValue(leftReg); // still have address
			} else if (right instanceof ConstantOffset) {
				this.out.println("movq (" + leftReg + "), %rax"); // Dereference left into rax
				String rightReg = ((ConstantOffset) right).getRegister();
				int offset = ((ConstantOffset) right).getOffset();
				this.out.println(op + " " + offset + "(" + rightReg + ")");
				return new ExpressionValue(leftReg);
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else if (left instanceof ConstantExpression) {
			int value = ((ConstantExpression) left).getValue();
			this.registers.use("%rax", null);
			if (right instanceof Address) {
				this.out.println("movq $" + value + ", %rax");
				this.out.println(op + " (" + ((Address) right).getRegister() + ")"); // add to memory
				return new ExpressionValue(((Address) right).getRegister()); // holds allocated register
			} else if (right instanceof ConstantExpression) {
				throw new AMD64Exception("Binary: left and right cannot both be Constants (due to folding)");
			} else if (right instanceof ExpressionValue) {
				this.out.println("movq $" + value + ", %rax");
				this.out.println(op + " " + ((ExpressionValue) right).getRegister()); // add to the expression 
				return (ExpressionValue) right;
			} else if (right instanceof ConstantOffset) {
				this.out.println("movq $" + value + ", %rax");
				String rightReg = ((ConstantOffset) right).getRegister();
				int offset = ((ConstantOffset) right).getOffset();
				this.out.println(op + " " + offset + "(" + rightReg + ")");
				String reg = this.registers.pop(); // TODO: spill
				return new ExpressionValue(reg);
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else if (left instanceof ExpressionValue) {
			String leftReg = ((ExpressionValue) left).getRegister();
			this.registers.use("%rax", left);

			if (right instanceof Address) {
				this.out.println("movq " + leftReg + ", %rax");
				this.out.println(op + " (" + ((Address) right).getRegister() + ")"); // add to expression
				this.registers.push(((Address) right).getRegister()); // free right register
				return (ExpressionValue) left; 
			} else if (right instanceof ConstantExpression) {
				this.out.println("movq " + leftReg + ", %rax");
				String reg = this.registers.pop();
				this.out.println("movq $" + ((ConstantExpression) right).getValue() + ", " + reg);
				this.out.println(op + " " + reg); // TODO instruction hardcoded
				this.registers.push(reg);
				return (ExpressionValue) left;
			} else if (right instanceof ExpressionValue) {
				this.out.println("movq " + leftReg + ", %rax");
				this.out.println(op + " " + ((ExpressionValue) right).getRegister());
				this.registers.push(((ExpressionValue) right).getRegister());
				return (ExpressionValue) left;
			} else if (right instanceof ConstantOffset) {
				this.out.println("movq " + leftReg + ", %rax");
				String rightReg = ((ConstantOffset) right).getRegister();
				int offset = ((ConstantOffset) right).getOffset();
				this.out.println(op + " " + offset + "(" + rightReg + ")");
				return new ExpressionValue(leftReg);
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else if (left instanceof ConstantOffset) {
			String leftReg = ((ConstantOffset) left).getRegister();
			this.registers.use("%rax", null);

			int offset = ((ConstantOffset) left).getOffset();
			if (right instanceof Address) {
				this.out.println("movq " + offset + "(" + leftReg + "), %rax");
				this.out.println(op + " (" + ((Address) right).getRegister() + ")"); // add to expression
				this.registers.push(((Address) right).getRegister()); // free right register
				return new ExpressionValue(((Address) right).getRegister()); 
			} else if (right instanceof ConstantExpression) {
				this.out.println("movq " + offset + "(" + leftReg + "), %rax");
				String reg = this.registers.pop();
				this.out.println("movq $" + ((ConstantExpression) right).getValue() + ", " + reg);
				this.out.println(op + " " + reg); //TODO instruction hardcoded
				this.registers.push(reg);
				return new ExpressionValue(this.registers.pop()); //TODO spill
			} else if (right instanceof ExpressionValue) {
				this.out.println("movq " + offset + "(" + leftReg + "), %rax");
				this.out.println(op + " " + ((ExpressionValue) right).getRegister());
				return (ExpressionValue) right;
			} else if (right instanceof ConstantOffset) {
				this.out.println("movq " + offset + "(" + leftReg + "), %rax");
				String rightReg = ((ConstantOffset) right).getRegister();
				int offset2 = ((ConstantOffset) right).getOffset();
				this.out.println(op + " " + offset2 + "(" + rightReg + ")");
				return new ExpressionValue(this.registers.pop()); // TODO spill
			} else {
				throw new AMD64Exception("Binary: right is unknown item type");
			}
		} else {
			throw new AMD64Exception("Binary: Left is unknown item type");
		}

	}
	
	/*
	 * Returns string version of the item given.
	 * Always dereferences if needed
	 */
	private String compareHelper(Item i) {
		if (i instanceof ConstantExpression) {
			return "$" + ((ConstantExpression) i).getValue();
		} else if (i instanceof ExpressionValue) {
			return ((ExpressionValue) i).getRegister();
		} else if (i instanceof Address) {
			return "(" + ((Address) i).getRegister() + ")";
		} else if (i instanceof ConstantOffset) {
			return ((ConstantOffset) i).getOffset() + "(" + ((ConstantOffset) i).getRegister() + ")";
		} else {
			throw new AMD64Exception("SHOULD NOT BE THROWN compareHelper");
		}
	}
	
	private void free(Item i) {
		if (i instanceof ConstantExpression) {
			return;
		} else if (i instanceof ExpressionValue) {
			this.registers.push(((ExpressionValue) i).getRegister());
		} else if (i instanceof Address) {
			this.registers.push(((Address) i).getRegister());
		} else if (i instanceof ConstantOffset) {
			if (!((ConstantOffset) i).getRegister().equals("%r15") && ! ((ConstantOffset) i).getRegister().equals("%rbp")) {
				this.registers.push(((ConstantOffset) i).getRegister());
			}
		} else {
			throw new AMD64Exception("SHOULD NOT BE THROWN free");
		}
	}
	
	/*
	 * Compares left against right.
	 * Dereferences one if need be
	 */
	private void compare(Item left, Item right) {
		if (left instanceof Address && (right instanceof Address || right instanceof ConstantOffset)) {
			this.out.println("movq " + this.compareHelper(left) + ", " + ((Address) left).getRegister());
			left = new ExpressionValue(((Address) left).getRegister());
		} else if (left instanceof ConstantOffset && (right instanceof Address || right instanceof ConstantOffset)) {
			this.out.println("movq " + this.compareHelper(left) + ", " + ((ConstantOffset) left).getRegister());
			left = new ExpressionValue(((ConstantOffset) left).getRegister());
		}
		
		if (right instanceof ConstantExpression) {
			String reg = this.registers.pop();
			this.out.println("movq " + this.compareHelper(right) + ", " + reg);
			right = new ExpressionValue(reg);
		}
		
		this.out.println("cmpq " + this.compareHelper(left) + ", " + this.compareHelper(right));
		this.free(left);
		this.free(right);
	}
	
	private Item visit(Binary b) {
		Item left = this.visit(b.getLeft());
		Item right = this.visit(b.getRight());
		String reg;
		switch (b.getOperator()) {
			case "+":
				return this.operate("addq", left, right);
			case "-":
				return this.operate("subq", left, right);
			case "*":
				return this.operate("imulq", left, right);
			case "DIV":
				if (right instanceof Address) {
					this.out.println("cmp $0, (" + ((Address) right).getRegister() + ")");
					this.out.println("je div_by_zero");
//					this.out.println("cltd");
				} else if (right instanceof ExpressionValue) {
					this.out.println("cmp $0, " + ((ExpressionValue) right).getRegister());
					this.out.println("je div_by_zero");
//					this.out.println("cltd");
				} else if (right instanceof ConstantOffset) {
					this.out.println("cmp $0, " 
							+ ((ConstantOffset) right).getOffset() + "(" + ((ConstantOffset) right).getRegister() + ")");
					this.out.println("je div_by_zero");
//					this.out.println("cltd");
				} else if (right instanceof ConstantExpression) {
					if (((ConstantExpression) right).getValue() == 0) {
						throw new AMD64Exception("Division by zero detected");
					}
				}
				ExpressionValue e = this.divOp("idivq", left, right);
				// TODO need to spill rax and rdx if necessary
				this.registers.use("%rax", e);
				this.registers.push(e.getRegister());
				e.setRegister("%rax");
				return e;
			case "MOD":
				if (right instanceof Address) {
					this.out.println("cmp $0, (" + ((Address) right).getRegister() + ")");
					this.out.println("je div_by_zero");
//					this.out.println("cltd");
				} else if (right instanceof ExpressionValue) {
					this.out.println("cmp $0, " + ((ExpressionValue) right).getRegister());
					this.out.println("je div_by_zero");
//					this.out.println("cltd");
				} else if (right instanceof ConstantOffset) {
					this.out.println("cmp $0, " 
							+ ((ConstantOffset) right).getOffset() + "(" + ((ConstantOffset) right).getRegister() + ")");
					this.out.println("je div_by_zero");
//					this.out.println("cltd");
				} else if (right instanceof ConstantExpression) {
					if (((ConstantExpression) right).getValue() == 0) {
						throw new AMD64Exception("Division by zero detected");
					}
				}
				ExpressionValue modResult = this.divOp("idivq", left, right);
				this.registers.use("%rdx", modResult);
				this.registers.push(modResult.getRegister());
				modResult.setRegister("%rdx");
				return modResult;
			case "AND":
				return this.operate("and", left, right);
			case "OR":
				return this.operate("or", left, right);
			case "NOT":
				if (right instanceof Address) {
					this.out.println("subq $1, (" + ((Address) right).getRegister() + ")");
					this.out.println("neg (" + ((Address) right).getRegister() + ")");
					return right;
				} else if (right instanceof ExpressionValue) {
					this.out.println("subq $1, " + ((Address) right).getRegister());
					this.out.println("neg " + ((Address) right).getRegister());
					return right;
				} else {
					throw new AMD64Exception("Binary: NOT right must be expressionvalue or Address");
				}
			case "=":
				this.compare(left, right);
				reg = this.registers.pop();
				this.out.println("movq $0, " + reg);
				this.out.println("movq $1, %rbx");
				this.out.println("cmove %rbx, " + reg);
				return new ExpressionValue(reg);
			case "#":
				this.compare(left, right);
				reg = this.registers.pop();
				this.out.println("movq $0, " + reg);
				this.out.println("movq $1, %rbx");
				this.out.println("cmovne %rbx, " + reg);
				return new ExpressionValue(reg);
			case ">":
				this.compare(left, right);
				reg = this.registers.pop();
				this.out.println("movq $0, " + reg);
				this.out.println("movq $1, %rbx");
				this.out.println("cmovl %rbx, " + reg);
				return new ExpressionValue(reg);
			case "<":
				this.compare(left, right);
				reg = this.registers.pop();
				this.out.println("movq $0, " + reg);
				this.out.println("movq $1, %rbx");
				this.out.println("cmovg %rbx, " + reg);
				return new ExpressionValue(reg);
			case ">=":
				this.compare(left, right);
				reg = this.registers.pop();
				this.out.println("movq $0, " + reg);
				this.out.println("movq $1, %rbx");
				this.out.println("cmovle %rbx, " + reg);
				return new ExpressionValue(reg);
			case "<=":
				this.compare(left, right);
				reg = this.registers.pop();
				this.out.println("movq $0, " + reg);
				this.out.println("movq $1, %rbx");
				this.out.println("cmovge %rbx, " + reg);
				return new ExpressionValue(reg);
			default:
				throw new AMD64Exception("Illegal operator");
		}
	}

	public Item visit(Number n) {
		return new ConstantExpression(n.getNum().getValue());
	}

	public Item visit(Location l) {
		if (l instanceof parser.ast.Variable) {
			return this.visit((parser.ast.Variable) l);
		} else if (l instanceof Index) {
			return this.visit((Index) l);
		} else if (l instanceof Field) {
			return this.visit((Field) l);
		} else {
			throw new AMD64Exception("Unknown Location type");
		}
	}

	public Item visit(parser.ast.Variable v) {
		if (v.getVar() instanceof FormalVariable) {
			return this.visit((FormalVariable) v.getVar());
		} else if (v.getVar() instanceof LocalVariable) {
			return this.visit((LocalVariable) v.getVar());
		} else if (v.getVar() instanceof Variable) {
			return this.visit(v.getVar());
		} else {
			throw new AMD64Exception("Unknown Variable type");
		}
	}

	public Item visit(Index i) {
		Item index = this.visit(i.getExp());
		Item location = this.visit(i.getLoc());
		int arrayLength = ((Array) i.getLoc().getType()).getLength();
		if (index instanceof ConstantExpression) {
			int indexVal = ((ConstantExpression) index).getValue();
			if (indexVal < 0 || indexVal >= arrayLength) {
				throw new AMD64Exception("Index out of bounds exception");
			}
			if (location instanceof Address) {
				String reg = ((Address) location).getRegister();
				int offset = ((ConstantExpression) index).getValue() * ((Array) i.getLoc().getType()).getElemType().size();
				return new ConstantOffset(offset, reg);
			} else if (location instanceof ConstantOffset) {
				String reg = ((ConstantOffset) location).getRegister();
				int offset = ((ConstantOffset) location).getOffset() 
						+ ((ConstantExpression) index).getValue() 
						* ((Array) i.getLoc().getType()).getElemType().size();
				return new ConstantOffset(offset, reg);
			} else {
				throw new AMD64Exception("Index: unknown location item type");
			}
		} else if (index instanceof Address) {
			if (location instanceof Address) {
				String register = ((Address) index).getRegister();
				this.out.println("movq (" + register + "), " + register); // Dereference address
				this.out.println("cmpq $" + arrayLength + ", " + register);
				this.out.println("jae array_out_of_bounds");
				String locationRegister = ((Address) location).getRegister();
				int size = (int) (Math.log(((Array) i.getLoc().getType()).getElemType().size()) / Math.log(2)); // Shift amount
				this.out.println("sal $" + size + ", " + register); // Shift to jump elements in array
				this.out.println("addq " + register + ", " + locationRegister); // add offset to original
				this.registers.push(register); // Free index register
				return location; // Return adjusted address
			} else if (location instanceof ConstantOffset) {
				String register = ((Address) index).getRegister();
				this.out.println("movq (" + register + "), " + register); // Dereference address
				this.out.println("cmpq $" + arrayLength + ", " + register);
				this.out.println("jae array_out_of_bounds");
				String locationRegister = ((ConstantOffset) location).getRegister();
				int size = (int) (Math.log(((Array) i.getLoc().getType()).getElemType().size()) / Math.log(2)); // Shift amount
				this.out.println("sal $" + size + ", " + register); // Shift to jump elements in array
				String newReg = this.registers.pop();
				this.out.println("leaq " + ((ConstantOffset) location).getOffset() + "(" + locationRegister + "), " + newReg);
				this.out.println("addq " + newReg + ", " + register); // add offset to original
				this.registers.push(newReg); // Free index register
				return new Address(register); // Return address after applied indexing
			} else {
				throw new AMD64Exception("Index: unknown location item type");
			}
		} else if (index instanceof ExpressionValue) {
			if (location instanceof Address) {
				String register = ((ExpressionValue) index).getRegister();
				this.out.println("cmpq $" + arrayLength + ", " + register);
				this.out.println("jae array_out_of_bounds");
				String locationRegister = ((Address) location).getRegister();
				int size = (int) (Math.log(((Array) i.getLoc().getType()).getElemType().size()) / Math.log(2)); // Shift amount
				this.out.println("sal $" + size + ", " + register); // Shift to jump elements in array
				this.out.println("addq " + register + ", " + locationRegister); // add offset to original
				this.registers.push(register); // Free index register
				return location; // Return adjusted address
			} else if (location instanceof ConstantOffset) {
				String register = ((ExpressionValue) index).getRegister();
				this.out.println("cmpq $" + arrayLength + ", " + register);
				this.out.println("jae array_out_of_bounds");
				String locationRegister = ((ConstantOffset) location).getRegister();
				int size = (int) (Math.log(((Array) i.getLoc().getType()).getElemType().size()) / Math.log(2)); // Shift amount
				this.out.println("sal $" + size + ", " + register); // Shift to jump elements in array
				String newReg = this.registers.pop();
				this.out.println("leaq " + ((ConstantOffset) location).getOffset() + "(" + locationRegister + "), " + newReg);
				this.out.println("addq " + newReg + ", " + register); // add offset to original
				this.registers.push(newReg); // Free index register
				return new Address(register); // Return address after applied indexing
			} else {
				throw new AMD64Exception("Index: unknown location item type");
			}
		} else if (index instanceof ConstantOffset) {
			if (location instanceof Address) {
				String register = ((ConstantOffset) index).getRegister();
				String newReg = this.registers.pop();
				this.out.println("movq " + this.compareHelper(index) + ", " + newReg); // newReg holds index
				this.out.println("cmpq $" + arrayLength + ", " + newReg);
				this.out.println("jae array_out_of_bounds");
				String locationRegister = ((Address) location).getRegister();
				int size = (int) (Math.log(((Array) i.getLoc().getType()).getElemType().size()) / Math.log(2)); // Shift amount
				this.out.println("sal $" + size + ", " + newReg); // Shift to jump elements in array
				this.out.println("addq " + newReg + ", " + locationRegister); // add offset to original
				this.registers.push(newReg); // Free index register
				return location;
			} else if (location instanceof ConstantOffset) {
				String indexRegister = ((ConstantOffset) index).getRegister();
				int indexOffset = ((ConstantOffset) index).getOffset();
				String newReg1 = this.registers.pop();
				this.out.println("movq " + indexOffset + "(" + indexRegister + "), " + newReg1); // newReg1 holds index
				this.out.println("cmpq $" + arrayLength + ", " + newReg1);
				this.out.println("jae array_out_of_bounds");
				String locationRegister = ((ConstantOffset) location).getRegister();
				int locationOffset = ((ConstantOffset) location).getOffset();
				int size = (int) (Math.log(((Array) i.getLoc().getType()).getElemType().size()) / Math.log(2)); // Shift amount
				this.out.println("sal $" + size + ", " + newReg1); // Shift to jump elements in array
				String newReg2 = this.registers.pop();
				this.out.println("leaq " + locationOffset + "(" + locationRegister + "), " + newReg2); // newReg2 holds address of location
				this.out.println("addq " + newReg1 + ", " + newReg2); // add offset to original
				this.registers.push(newReg1); // Free Temp register
				return new Address(newReg2); // Return address after applied indexing
			} else {
				throw new AMD64Exception("Index: unknown location item type");
			}
		} else {
			throw new AMD64Exception("Index: unknown index item type");
		}
	}

	public Item visit(Field f) {
		Item location = this.visit(f.getLoc());
		int offset = f.getVar().getVar().getLocation();
		
		if (location instanceof Address) {
			return new ConstantOffset(offset, ((Address) location).getRegister());
		} else if (location instanceof ConstantOffset) {
			return new ConstantOffset(offset + ((ConstantOffset) location).getOffset(), ((ConstantOffset) location).getRegister());
		} else {
			throw new AMD64Exception("Field: unknown location type");
		}
	}
	
	/*
	 * Function pushes all inUse registers on the stack.
	 * Marks them as free
	 */
	private List<String> pushRegisters() {
		this.registers.use("%rax", null);
		this.registers.use("%rdx", null);
		List<String> reg = this.registers.inUse();
		for (int i=0;i<reg.size();i++) {
			this.out.println("push " + reg.get(i));
			this.registers.push(reg.get(i));
		}
		return reg;
	}
	
	/*
	 * Pulls all registers pushed by pushRegisters
	 * Marks them as in use.
	 */
	private void popRegisters(List<String> reg) {
		for (int i=reg.size() - 1;i>=0;i--) {
			this.out.println("pop " + reg.get(i));
			this.registers.pop(reg.get(i));
		}
	}

}
