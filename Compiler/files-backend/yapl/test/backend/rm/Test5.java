package yapl.test.backend.rm;

import java.io.FileOutputStream;
import java.io.PrintStream;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

/**
 * BackendAsmRM test: reference parameters, 1-dimensional arrays.
 * @author Mario Taschwer
 */
public class Test5
{
    private static BackendAsmRM backend;
    
    /**
     * Emit single-argument procedure call code.
     * @param name   procedure name.
     * @param reg    argument register.
     */
    private static void callProc(String name, byte reg)
    {
        backend.prepareProcCall(1);
        backend.passArg(0, reg);
        backend.callProc((byte) -1, name);
    }
    
    /**
     * Emit code to load (word-sized) array element a[i].
     * @param destReg       destination register receiving a[i].
     * @param baseReg       register holding base address of array a.
     * @param idxReg        register holding element index i.
     */
    private static void loadArrayElement(byte destReg, byte baseReg, byte idxReg)
    {
        backend.arrayOffset(destReg, baseReg, idxReg);
        backend.loadWordReg(destReg, destReg);
    }
    
    /**
     * Emit code to store value as array element a[i].
     * @param valueReg       register holding value to store.
     * @param baseReg        register holding base address of array a.
     * @param idxReg         register holding element index i (will be modified).
     */
    private static void storeArrayElement(byte valueReg, byte baseReg, byte idxReg)
    {
        backend.arrayOffset(idxReg, baseReg, idxReg);
        backend.storeWordReg(valueReg, idxReg);
    }
    
    /**
     * Usage: java yapl.test.backend.rm.Test5 [asm_file]
     */
    public static void main(String[] args) throws Exception
    {
        PrintStream out = (args.length > 0) 
        		? new PrintStream(new FileOutputStream(args[0])) : System.out;
        backend = new BackendMIPS(out);
        int wordSize = backend.wordSize();
        
        // we optimize register allocation for readability here

        // procedure swap(int[] a, int i, int j): swap contents of a[i] and a[j]
        backend.enterProc("swap", 3);
        int tmp = backend.allocStack(wordSize, "local variable tmp");
        byte r1 = backend.allocReg();
        byte r2 = backend.allocReg();
        byte r3 = backend.allocReg();
        byte r4 = backend.allocReg();
        // tmp = a[i]
        backend.loadWord(r1, backend.paramOffset(0), false);  // load base address of a
        backend.loadWord(r2, backend.paramOffset(1), false);  // load i
        loadArrayElement(r1, r1, r2);                                      // load a[i]
        backend.storeWord(r1, tmp, false);                          // tmp = a[i]
        // a[i] = a[j]
        backend.loadWord(r1, backend.paramOffset(0), false);  // load base address of a
        backend.loadWord(r2, backend.paramOffset(1), false);  // load i
        backend.loadWord(r3, backend.paramOffset(0), false);  // load base address of a
        backend.loadWord(r4, backend.paramOffset(2), false);  // load j
        loadArrayElement(r3, r3, r4);                                      // load a[j]
        storeArrayElement(r3, r1, r2);                                     // a[i] = a[j]
        // a[j] = tmp
        backend.loadWord(r1, backend.paramOffset(0), false);  // load base address of a
        backend.loadWord(r2, backend.paramOffset(2), false);  // load j
        backend.loadWord(r3, tmp, false);                           // load tmp
        storeArrayElement(r3, r1, r2);                                     // a[j] = tmp
        backend.freeReg(r1);
        backend.freeReg(r2);
        backend.freeReg(r3);
        backend.freeReg(r4);
        backend.exitProc("swap_end");

        int a = backend.allocStaticData(wordSize, "global variable a");
        int separator = backend.allocStringConstant(" : ");
        
        // main program
        backend.enterMain();
        r1 = backend.allocReg();
        r2 = backend.allocReg();
        r3 = backend.allocReg();

        // allocate 1-dimensional array of length 3
        backend.loadConst(r1, 3);
        backend.storeArrayDim(0, r1);
        backend.allocArray(r1);
        backend.storeWord(r1, a, true);               // store array base address

        // a[0] = 1
        backend.loadWord(r1, a, true);                // load array base address
        backend.loadConst(r2, 0);                      // load index
        backend.loadConst(r3, 1);                      // load element value
        storeArrayElement(r3, r1, r2);
        // a[1] = 2
        backend.loadWord(r1, a, true);
        backend.loadConst(r2, 1);
        backend.loadConst(r3, 2);
        storeArrayElement(r3, r1, r2);
        // a[2] = 3
        backend.loadWord(r1, a, true);
        backend.loadConst(r2, 2);
        backend.loadConst(r3, 3);
        storeArrayElement(r3, r1, r2);

        // call swap(a, 1, 2)
        backend.loadWord(r1, a, true);
        backend.loadConst(r2, 1);
        backend.loadConst(r3, 2);
        backend.prepareProcCall(3);
        backend.passArg(0, r1);
        backend.passArg(1, r2);
        backend.passArg(2, r3);
        backend.callProc((byte) -1, "swap");
        
        // print a[0] (value 1)
        backend.loadWord(r1, a, true);
        backend.loadConst(r2, 0);
        loadArrayElement(r1, r1, r2);
        callProc("writeint", r1);
        backend.writeString(separator);
        // print a[1] (value 3)
        backend.loadWord(r1, a, true);
        backend.loadConst(r2, 1);
        loadArrayElement(r1, r1, r2);
        callProc("writeint", r1);
        backend.writeString(separator);
        // print a[2] (value 2)
        backend.loadWord(r1, a, true);
        backend.loadConst(r2, 2);
        loadArrayElement(r1, r1, r2);
        callProc("writeint", r1);

        backend.freeReg(r1);
        backend.freeReg(r2);
        backend.freeReg(r3);
        backend.exitMain("main_end");
    }
}
