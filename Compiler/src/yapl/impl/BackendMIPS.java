package yapl.impl;

import java.io.PrintStream;

public class BackendMIPS implements yapl.interfaces.BackendAsmRM {

	public BackendMIPS(PrintStream out) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int wordSize() {
		// TODO Auto-generated method stub
		return Integer.parseInt(System.getProperty("sun.arch.data.model")) / 8;
	}

	@Override
	public int boolValue(boolean value) {
		// TODO Auto-generated method stub
		return value ? 1 : 0;
	}

	@Override
	public byte allocReg() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void freeReg(byte reg) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte zeroReg() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void comment(String comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void emitLabel(String label, String comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public int allocStaticData(int bytes, String comment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int allocStringConstant(String string) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int allocStack(int bytes, String comment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void allocHeap(byte destReg, int bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeArrayDim(int dim, byte lenReg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void allocArray(byte destReg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadConst(byte reg, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadAddress(byte reg, int addr, boolean isStatic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadWord(byte reg, int addr, boolean isStatic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeWord(byte reg, int addr, boolean isStatic) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void neg(byte regDest, byte regX) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addConst(byte regDest, byte regX, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sub(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mul(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void div(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mod(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void isLess(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void isLessOrEqual(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void isEqual(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void not(byte regDest, byte regSrc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void and(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void or(byte regDest, byte regX, byte regY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void branchIf(byte reg, boolean value, String label) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jump(String label) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterMain() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitMain(String label) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enterProc(String label, int nParams) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitProc(String label) {
		// TODO Auto-generated method stub

	}

	@Override
	public void returnFromProc(String label, byte reg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareProcCall(int numArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void passArg(int arg, byte reg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void callProc(byte reg, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public int paramOffset(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

}
