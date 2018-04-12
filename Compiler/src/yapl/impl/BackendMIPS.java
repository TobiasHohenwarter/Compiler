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
	private int staticDataOffset;
	private byte staticDataRegister;
	private int currentFrameOffset;
	private int saveFrameOffset;

	public BackendMIPS(PrintStream out) {
		this.outputFile = out;
		tempOutput = "";
		stackOffset = 0;
		staticDataOffset = 0;
		stringCount = 1;
		currentFrameOffset = -4;
		staticData = new HashMap<>();
		registers = new HashMap<>();
		for (byte i = 8; i < 26; i++) {
			registers.put(i, false);
		}

		tempOutput = ".data" + LF + "staticData: " + LF + ".text" + LF;
	}

	private byte getNextFreeReg() {
		byte j = -1;
		for (byte i = 8; i < 26 && j == -1; i++) {
			if (registers.get(i) == false) {
				j = i;
			}
		}
		return j;
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
		int startAddress = staticDataOffset;
		// word-aligned
		staticDataOffset += (int) Math.ceil(bytes / wordSize()) * wordSize();
		return startAddress;
	}

	private int stringCount;

	@Override
	public int allocStringConstant(String string) {
		int startAddress = staticDataOffset;
		string += '\0';
		staticData.put(startAddress, "str" + stringCount);
		staticDataOffset += string.getBytes().length;
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
		tempOutput += "addi $sp, $sp, -" + wordAlignedAddition;
		if (comment != null) {
			tempOutput += " # " + comment;
		}
		tempOutput += LF;
		currentFrameOffset = currentFrameOffset - wordAlignedAddition;
		return currentFrameOffset;
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

	int currentArrayDim;

	@Override
	public void storeArrayDim(int dim, byte lenReg) {
		dim++;
		// dimensions > 2 cannot be created
		if ((dim == 1)) {
			storeWord(lenReg, (dim * 4), true);
		} else {
			// throw new Exception
		}
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
			tempOutput += "lw $" + reg + ", " + addr + "($gp)" + LF;
		} else {
			tempOutput += "lw $" + reg + ", " + addr + "($fp)" + LF;
		}

	}

	@Override
	public void storeWord(byte reg, int addr, boolean isStatic) {
		if (isStatic) {
			tempOutput += "sw $" + reg + ", " + addr + "($gp)" + LF;
		} else {
			tempOutput += "sw $" + reg + ", " + addr + "($fp)" + LF;
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
		tempOutput += "main:" + LF; // TODO
		currentFrameOffset = -4;
	}

	private void emitProvidedProcedures() {
		// write an int to stdout
		enterProc("writeint", 1);
		// load param in a0
		loadWord((byte) 4, paramOffset(0), false);
		loadConst((byte) 2, 1); // syscall - prep
		tempOutput += "syscall" + LF;
		exitProc("writeint_end");

		// write a string to stdout
		enterProc("write", 1);
		// load param in a0
		loadWord((byte) 4, paramOffset(0), false);
		loadConst((byte) 2, 4); // syscall - prep
		tempOutput += "syscall" + LF;
		exitProc("write_end");

		// write a new line to stdout
		enterProc("writeln", 0);
		int addrNL = this.allocStringConstant("\\n");
		writeString(addrNL);
		exitProc("writeln_end");

		// readint procedure prolog
		enterProc("readint", 0);
		byte returnint = this.allocReg();
		addConst((byte) 2, zeroReg(), 5); // add 5 to v0
		tempOutput += "syscall" + LF;
		add(returnint, (byte) 2, zeroReg()); // get the entered nbr
		this.freeReg(returnint);
		this.exitProc("readint_end");

		// write a bool-exp to stdout
		enterProc("writebool", 1);
		// string representations of bool values
		int addrTrue = allocStringConstant("True");
		int addrFalse = allocStringConstant("False");
		// get true/false constants in registers
		byte regTrue = allocReg();
		byte regFalse = allocReg();
		loadAddress(regTrue, addrTrue, true);
		loadAddress(regFalse, addrFalse, true);
		byte boolReg = allocReg();
		loadWord(boolReg, this.paramOffset(0), false);
		// store false address into $a0 (default)
		add((byte) 4, regFalse, (byte) 0);
		branchIf(boolReg, false, "boolCalculated");
		// store true as branch if was not correct
		add((byte) 4, regTrue, (byte) 0);
		emitLabel("boolCalculated", null);
		this.loadConst((byte) 2, 4); // syscall - prep
		tempOutput += "syscall" + LF;
		freeReg(boolReg);
		freeReg(regTrue);
		freeReg(regFalse);
		exitProc("writebool_end");

	}

	@Override
	public void exitMain(String label) {
		tempOutput += label + ":" + LF + "li $v0, 10" + LF + "syscall" + LF;
		emitProvidedProcedures();
		outputFile.print(tempOutput);
		outputFile.flush();
	}

	@Override
	public void enterProc(String label, int nParams) {
		tempOutput += label + ": " + LF;
		allocStack(2 * wordSize(), null);
		tempOutput += "sw $fp, 0($sp)" + LF;
		tempOutput += "sw $ra, 4($sp)" + LF;

		tempOutput += " addi $fp,$sp,4" + LF;
		currentFrameOffset = -4;
	}

	@Override
	public void exitProc(String label) {
		tempOutput += label + ": " + LF;
		currentFrameOffset = -4;

		this.loadWord((byte) 31, 0, false);
		this.loadWord((byte) 30, -4, false);

		tempOutput += " addi $sp,$sp,4" + LF;

		currentFrameOffset = saveFrameOffset;

		tempOutput += "jr $ra" + LF;

	}

	@Override
	public void returnFromProc(String label, byte reg) {
		if (reg != -1) {
			tempOutput += "move $v0, $" + reg + LF;
		}
		tempOutput += "j exit_" + label + ": " + LF;
	}

	private ArrayList<Byte> storedRegs = new ArrayList<>();
	private int numArgs = 0;

	@Override
	public void prepareProcCall(int numArgs) {
		// TODO Auto-generated method stub
		saveFrameOffset = this.currentFrameOffset;
		int tempFrameOffset = this.currentFrameOffset;
		for (byte k : registers.keySet()) {
			if (registers.get(k)) {
				storedRegs.add(k);
				tempFrameOffset -= 4;
				allocStack(4, "");
				storeWord(k, tempFrameOffset, false);
			}
		}

		allocStack(numArgs * 4, "");
		this.numArgs = numArgs;

	}

	@Override
	public void passArg(int arg, byte reg) {
		tempOutput += " sw $" + reg + ", " + arg + "($sp) " + LF;
	}

	@Override
	public void callProc(byte reg, String name) {
		for (byte b : storedRegs)
			freeReg(b);

		tempOutput += " jal " + name + LF;

		currentFrameOffset += 4 * this.numArgs;
		tempOutput += "addi $sp,$sp," + 4 * this.numArgs + LF;

		int tempFrameOffset = this.saveFrameOffset;

		for (byte k : storedRegs) {
			registers.put(k, true);
			tempFrameOffset -= 4;
			loadWord(k, tempFrameOffset, false);
		}

		tempOutput += " addi $sp,$sp," + 4 * storedRegs.size() + LF;
		storedRegs.clear();

		if (reg != -1) {
			tempOutput += " move $" + reg + ", $v0 " + LF;
		}

	}

	@Override
	public int paramOffset(int index) {
		return (index * 4 + 4); // fp + 4 * index = offset
	}

}
