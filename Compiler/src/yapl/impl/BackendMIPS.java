package yapl.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class BackendMIPS implements yapl.interfaces.BackendAsmRM {

	private final String LF = "\n";

	private PrintStream outputFile;
	private String tempOutput;

	private Map<Byte, Boolean> registers;
	private int stackOffset;
	private Map<Integer, String> staticData;
	private int staticOffset;

	private byte getNextFreeReg() {
		byte j = -1;
		for (byte i = 8; i < 26 && j == -1; i++) {
			if (registers.get(i) == false) {
				j = i;
			}
		}
		return j;
	}

	public BackendMIPS(PrintStream out) {
		this.outputFile = out;
		tempOutput = "";
		stackOffset = 0;
		staticOffset = 0;
		stringCount = 1;
		staticData = new HashMap<>();
		registers = new HashMap<>();
		for (byte i = 8; i < 26; i++) {
			registers.put(i, false);
		}
	}

	@Override
	public int wordSize() {
		return 4;
	}

	@Override
	public int boolValue(boolean value) {
		return value ? 1 : 0;
	}

	@Override
	public byte allocReg() {
		return getNextFreeReg();
	}

	@Override
	public void freeReg(byte reg) {
		registers.put(reg, false);

	}

	@Override
	public byte zeroReg() {
		return 0;
	}

	@Override
	public void comment(String comment) {
		tempOutput += " # " + comment + LF;

	}

	@Override
	public void emitLabel(String label, String comment) {
		tempOutput += label + ":";
		if (comment != null) {
			comment(comment);
		} else {
			tempOutput += LF;
		}
	}

	@Override
	public int allocStaticData(int bytes, String comment) {
		int startAddress = staticOffset;
		// word-aligned
		staticOffset += (int) Math.ceil(bytes / wordSize()) * wordSize();
		return startAddress;
	}

	private int stringCount;

	@Override
	public int allocStringConstant(String string) {
		int startAddress = staticOffset;
		string += '\0';
		staticData.put(startAddress, "str" + stringCount);
		staticOffset += string.getBytes().length;
		int index = tempOutput.indexOf(".text");
		String str1;
		String str2;
		if (index != -1) {
			str1 = tempOutput.substring(0, index - 1);
			str2 = tempOutput.substring(index);
		} else {
			str1 = tempOutput;
			str2 = "";
		}
		str1 += LF + "str" + stringCount + ": .asciiz \"" + string + "\"" + LF;
		tempOutput = str1 + str2;
		stringCount++;
		return startAddress;
	}

	@Override
	public int allocStack(int bytes, String comment) {
		int retVal = stackOffset;
		int wordAlignedAddition = (int) Math.ceil(bytes / wordSize()) * wordSize();
		stackOffset += wordAlignedAddition;
		tempOutput += "addi $sp, $sp" + wordAlignedAddition;
		if (comment != null) {
			tempOutput += " # " + comment;
		}
		tempOutput += LF;
		return retVal;
	}

	@Override
	public void allocHeap(byte destReg, int bytes) {
		int wordAlignedSize = (int) Math.ceil(bytes / wordSize()) * wordSize();
		tempOutput += "li $a0, " + wordAlignedSize + LF;
		tempOutput += "li $v0, 9" + LF; // syscall for heap alloc - mem address
										// is in v0
		tempOutput += "syscall " + LF;
		tempOutput += "add $" + destReg + ", $" + zeroReg() + ", $v0" + LF;

	}

	@Override
	public void storeArrayDim(int dim, byte lenReg) {
		// TODO Auto-generated method stub
		int wordAlignedAddition = (int) Math.ceil(lenReg / wordSize()) * wordSize();
		allocStack(wordAlignedAddition * dim, null);

	}

	@Override
	public void allocArray(byte destReg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadConst(byte reg, int value) {
		tempOutput += "li $" + String.valueOf(reg) + ", " + String.valueOf(value) + LF;

	}

	@Override
	public void loadAddress(byte reg, int addr, boolean isStatic) {
		// TODO Auto-generated method stub
		// tempOutput += "la $" + String.valueOf(reg) + ", " +
		// String.valueOf(value) + LF;

	}

	@Override
	public void loadWord(byte reg, int addr, boolean isStatic) {
		if (isStatic) {
			tempOutput += "lw $" + String.valueOf(reg) + ", " + String.valueOf(addr) + "($gp)" + LF;
		} else {
			tempOutput += "lw $" + String.valueOf(reg) + ", " + String.valueOf(addr) + "($sp)" + LF;
		}

	}

	@Override
	public void storeWord(byte reg, int addr, boolean isStatic) {
		if (isStatic) {
			tempOutput += "sw $" + String.valueOf(reg) + ", " + String.valueOf(addr) + "($gp)" + LF;
		} else {
			tempOutput += "sw $" + String.valueOf(reg) + ", " + String.valueOf(addr) + "($sp)" + LF;
		}

	}

	@Override
	public void loadWordReg(byte reg, byte addrReg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadWordReg(byte reg, byte addrReg, int offset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeWordReg(byte reg, int addrReg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void arrayOffset(byte dest, byte baseAddr, byte index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void arrayLength(byte dest, byte baseAddr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeString(int addr) {
		tempOutput += "la $a0, " + staticData.get(addr) + LF + "li $v0, 4" + LF + "syscall" + LF;
	}

	@Override
	public void neg(byte regDest, byte regX) {
		tempOutput += "sub $" + regDest + ", $" + zeroReg() + ", $" + regX + LF;
	}

	@Override
	public void add(byte regDest, byte regX, byte regY) {
		tempOutput += "add $" + regDest + ", $" + regX + ", $" + regY + LF;

	}

	@Override
	public void addConst(byte regDest, byte regX, int value) {
		tempOutput += "addi $" + regDest + ", $" + regX + ", " + value + LF;

	}

	@Override
	public void sub(byte regDest, byte regX, byte regY) {
		tempOutput += "sub $" + regDest + ", $" + regX + ", $" + regY + LF;
	}

	@Override
	public void mul(byte regDest, byte regX, byte regY) {
		tempOutput += "mult $" + regDest + ", $" + regX + ", $" + regY + LF;

	}

	@Override
	public void div(byte regDest, byte regX, byte regY) {
		tempOutput += "divu $" + regDest + ", $" + regX + ", $" + regY + LF;
	}

	@Override
	public void mod(byte regDest, byte regX, byte regY) {
		tempOutput += "rem $" + regDest + ", $" + regX + ", $" + regY + LF;
	}

	@Override
	public void isLess(byte regDest, byte regX, byte regY) {
		tempOutput += "slt $" + regDest + ", $" + regX + ", $" + regY + LF;
	}

	@Override
	public void isLessOrEqual(byte regDest, byte regX, byte regY) {
		tempOutput += "sle $" + regDest + ", $" + regX + ", $" + regY + LF;
	}

	@Override
	public void isEqual(byte regDest, byte regX, byte regY) {
		tempOutput += "seq $" + regDest + ", $" + regX + ", $" + regY + LF;

	}

	@Override
	public void not(byte regDest, byte regSrc) {
		tempOutput += "not $" + regDest + ", $" + regSrc + LF;

	}

	@Override
	public void and(byte regDest, byte regX, byte regY) {
		tempOutput += "and $" + regDest + ", $" + regX + ", $" + regY + LF;

	}

	@Override
	public void or(byte regDest, byte regX, byte regY) {
		tempOutput += "or $" + regDest + ", $" + regX + ", $" + regY + LF;

	}

	@Override
	public void branchIf(byte reg, boolean value, String label) {
		byte bool = (byte) boolValue(value);
		byte freeRegNum = allocReg();
		tempOutput += "addi $" + freeRegNum + ", $" + freeRegNum + ", " + bool + LF;
		tempOutput += "beq $" + reg + ", $" + freeRegNum + ", " + label + LF;
		freeReg(freeRegNum);
	}

	@Override
	public void jump(String label) {
		tempOutput += "j " + label + LF;

	}

	@Override
	public void enterMain() {
		tempOutput = ".data" + LF + ".text" + LF + "main:" + LF;

	}

	@Override
	public void exitMain(String label) {
		tempOutput += label + ":" + LF + "li $v0, 10" + LF + "syscall";
		outputFile.print(tempOutput);
		outputFile.flush();
	}

	@Override
	public void enterProc(String label, int nParams) {
		tempOutput += label + ": " + LF;
		tempOutput += "subi $sp, $sp, " + wordSize() + LF;
		tempOutput += "sw $ra, 0($sp)" + LF;
	}

	@Override
	public void exitProc(String label) {
		tempOutput += "exit_" + label + ": " + LF;
		tempOutput += "lw $ra, 0($sp)" + LF;
		tempOutput += "addi $sp, $sp, " + wordSize() + LF;
		tempOutput += "jr $ra " + LF;
	}

	@Override
	public void returnFromProc(String label, byte reg) {
		if (reg != -1) {
			tempOutput += "move $v0, $" + reg + LF;
		}
		tempOutput += "j exit_" + label + ": " + LF;
	}

	@Override
	public void prepareProcCall(int numArgs) {
		// TODO Auto-generated method stub
		tempOutput += "subi $sp, $sp, " + (numArgs + 1) * wordSize() + LF;
		tempOutput += "sw $ra,0($sp)" + LF;
	}

	@Override
	public void passArg(int arg, byte reg) {
		int arrAccess = (arg + 1) * wordSize();
		tempOutput += " sw $" + reg + ", " + arrAccess + "($sp) " + LF;
	}

	@Override
	public void callProc(byte reg, String name) {
		tempOutput += " jal " + name + LF;
		if (reg != -1) {
			tempOutput += " move $" + reg + ", $v0 " + LF;
		}
	}

	@Override
	public int paramOffset(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

}
