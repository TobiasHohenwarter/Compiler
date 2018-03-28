package yapl.test.backend.rm;

import java.io.FileOutputStream;
import java.io.PrintStream;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

/**
 * BackendAsmRM test: printing a boolean value by defining a procedure.
 * (We imitate the predefined procedure 'writebool' here.)
 * @author Mario Taschwer
 */
public class Test3
{
    /**
     * Usage: java yapl.test.backend.rm.Test3 [asm_file]
     */
    public static void main(String[] args) throws Exception
    {
        PrintStream out = (args.length > 0) 
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        BackendAsmRM backend = new BackendMIPS(out);
        
        // string constants (static data)
        int addrTrue = backend.allocStringConstant("True");
        int addrFalse = backend.allocStringConstant("False");
        int addrSeparator = backend.allocStringConstant(" : ");
        
        // procedure writeboolean(boolean b): print boolean value
        backend.enterProc("writeboolean", 1);                  // procedure prolog
        byte reg = backend.allocReg();
        backend.loadWord(reg, backend.paramOffset(0), false);  // load parameter 0 from stack frame
        backend.branchIf(reg, false, "L1");                      // if (b == false) goto L1
        backend.freeReg(reg);
        // print "True"
        backend.writeString(addrTrue);
        backend.jump("writeboolean_end");                              // jump to epilog
        // print "False"
        backend.emitLabel("L1", "write 'False'");
        backend.writeString(addrFalse);
        backend.exitProc("writeboolean_end");                          // procedure epilog
        
        // main program
        backend.enterMain();

        // call writeboolean(true)
        backend.prepareProcCall(1);
        reg = backend.allocReg();
        backend.loadConst(reg, backend.boolValue(true));
        backend.passArg(0, reg);
        backend.freeReg(reg);
        backend.callProc((byte) -1, "writeboolean");
        backend.writeString(addrSeparator);

        // call writeboolean(false)
        backend.prepareProcCall(1);
        reg = backend.allocReg();
        backend.loadConst(reg, backend.boolValue(false));
        backend.passArg(0, reg);
        backend.freeReg(reg);
        backend.callProc((byte) -1, "writeboolean");

        backend.exitMain("main_end");
    }
}
