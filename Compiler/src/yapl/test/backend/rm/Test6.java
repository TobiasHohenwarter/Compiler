package yapl.test.backend.rm;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * BackendAsmRM test: records.
 * @author Mario Taschwer
 */

public class Test6 
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
	 * Usage: java yapl.test.backend.sm.Test6 asm_file
	 */
	public static void main(String[] args) throws IOException
	{
        PrintStream out = (args.length > 0)
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        backend = new BackendMIPS(out);
        int wordSize = backend.wordSize();
        int separator = backend.allocStringConstant(" : ");

        // main program
        backend.enterMain();
        // To enhance readability, we allocate registers in advance, and free them at the end only once.
        byte r1 = backend.allocReg();
        byte r2 = backend.allocReg();
        // allocate local variable r for record reference
        int recordAddr = backend.allocStack(wordSize, "local variable r");
        // allocate record 'List' for linked list (2 words):
        // field 'value' at offset 0: integer value
        // field 'next'  at offset 1: address of next list element
        // r = new List
        backend.allocHeap(r1, 2 * wordSize);
        backend.storeWord(r1, recordAddr, false);
        // r.value = 5
        backend.loadWord(r1, recordAddr, false);
        // 'value' field has offset 0, so no address computation required
        backend.loadConst(r2, 5);
        backend.storeWordReg(r2, r1);
        // r.next = new List
        backend.loadWord(r1, recordAddr, false);
        backend.addConst(r1, r1, wordSize);                // compute address of r.next
        backend.allocHeap(r2, 2 * wordSize);
        backend.storeWordReg(r2, r1);
        // r.next.value = 10
        backend.loadWord(r1, recordAddr, false);
        backend.loadWordReg(r1, r1, wordSize);             // load value of r.next (address of List object)
        // 'value' field has offset 0, so no address computation required
        backend.loadConst(r2, 10);
        backend.storeWordReg(r2, r1);
        // print r.value
        backend.loadWord(r1, recordAddr, false);
        backend.loadWordReg(r2, r1);
        callProc("writeint", r2);                    // call predefined procedure
        backend.writeString(separator);
        // print r.next.value
        backend.loadWord(r1, recordAddr, false);
        backend.loadWordReg(r1, r1, wordSize);             // load value of r.next (address of List object)
        backend.loadWordReg(r2, r1, 0);              // 'value' field has offset 0
        callProc("writeint", r2);                    // call predefined procedure
        // free registers
        backend.freeReg(r1);
        backend.freeReg(r2);
        // exit main program
        backend.exitMain("main_end");
	}

}
