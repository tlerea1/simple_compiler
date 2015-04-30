package amd64;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterAllocator {
	private int current;
	private HashMap<String, Boolean> registers; // False means free
	private Item rax, rdx;
	private PrintStream out;
	
	public RegisterAllocator(PrintStream out) {
		this.current = 0;
		this.registers = new HashMap<String, Boolean>();
		this.setupRegisters();
		this.out = out;
	}
	
	public String pop() {
		for (Map.Entry<String, Boolean> e : this.registers.entrySet()) {
			if (e.getKey().equals("%rax") || e.getKey().equals("%rdx")) {
				continue;
			}
			if (! e.getValue()) {
				String reg = e.getKey();
				this.registers.put(reg, true);
				return reg;
			}
		}
		throw new AMD64Exception("Need to spill");
	}
	
	public void pop(String reg) {
		Boolean b = this.registers.get(reg);
		if (b) {
			throw new AMD64Exception("explicitly popping register in use");
		} else {
			this.registers.put(reg, true);
		}
	}
	
	public void use(String register, Item i) {
		if (register.equals("%rax")) {
			if (this.rax != null) {
				if (this.rax != i) {
					this.spill("%rax");
				}
			}
			this.rax = i;
		} else if (register.equals("%rdx")) {
			if (this.rdx != null) {
				if (this.rdx != i) {
					this.spill("%rdx");
				}
			}
			this.rdx = i;
		} else {
			throw new AMD64Exception("cannot try to use: " + register);
		}
	}
	
	/**
	 * Spill the register in the given Item.
	 * This will cause the register held by the 
	 * Item to be freed and the Item given a new
	 * register if available.
	 * @param i the Item to spill
	 */
	public void spill(Item i) {
		
	}
	
	public void spill(String reg) {
		if (reg.equals("%rax")) {
			String newReg = this.pop();
			this.assign(this.rax, newReg);
			this.rax = null;
		} else if (reg.equals("%rdx")) {
			String newReg = this.pop();
			this.assign(this.rdx, newReg);
			this.rdx = null;
		} else {
			throw new AMD64Exception("Cannot directly spill non rax or rdx");
		}
	}
	
	private void assign(Item i, String reg) {
		if (i instanceof Address) {
			this.out.println("movq " + ((Address) i).getRegister() + ", " + reg);
			((Address) i).setRegister(reg);
		} else if (i instanceof ExpressionValue) {
			this.out.println("movq " + ((ExpressionValue) i).getRegister() + ", " + reg);
			((ExpressionValue) i).setRegister(reg);
		} else {
			throw new AMD64Exception("Allocator: Assigning to unknown type");
		}
	}
	
	public void push(String reg) {
		
		Boolean b = this.registers.get(reg);
		if (b == null) {
			throw new RuntimeException("Register Allocation: pushing non-register: " + reg);
		}
		if (!b && !(reg.equals("%rax") || reg.equals("%rdx"))) {
			throw new RuntimeException("Register Allocation: pushing free register");
		} else {
			this.registers.put(reg, false);
		}
		if (reg.equals("%rax")) {
			this.rax = null;
		} else if (reg.equals("%rdx")) {
			this.rdx = null;
		}
			
	
	}
	
	private void setupRegisters() {
		this.registers.put("%r8", false);
		this.registers.put("%r9", false);
		this.registers.put("%r10", false);
		this.registers.put("%r11", false);
		this.registers.put("%r12", false);
		this.registers.put("%r13", false);
		this.registers.put("%r14", false);
		this.registers.put("%rax", false);
		this.registers.put("%rdx", false);
	}
	
	/**
	 * Function to check if all registers are free
	 * @return true if all are free
	 */
	public void free() {
		for (Map.Entry<String, Boolean> e : this.registers.entrySet()) {
			if (e.getValue()) {
				throw new AMD64Exception("Not all registers free: " + e.getKey());
			}
		}
		if (this.rax != null) {
			throw new AMD64Exception("Not all registers free: %rax");
		}
		if (this.rdx != null) {
			throw new AMD64Exception("Not all registers free: %rdx");
		}
	}
	
	public List<String> inUse() {
		List<String> registers = new ArrayList<String>();
		for (Map.Entry<String, Boolean> e : this.registers.entrySet()) {
			if (e.getValue()) {
				registers.add(e.getKey());
			}
		}
		if (this.rax != null) {
			registers.add("%rax");
		}
		
		if (this.rdx != null) {
			registers.add("%rdx");
		}
		return registers;
	}
}
