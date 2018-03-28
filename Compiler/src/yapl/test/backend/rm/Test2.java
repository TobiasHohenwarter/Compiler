package yapl.test.backend.rm;

import java.io.FileOutputStream;
import java.io.PrintStream;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

/**
 * BackendAsmRM test: printing an integer by calling a predefined procedure.
 * @author Mario Taschwer
 */
public class Test2
{
    /**
     * Usage: java yapl.test.backend.rm.Test2 [asm_file]
     */
    public static void main(String[] args) throws Exception
    {
        PrintStream out = (args.length > 0) 
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        BackendAsmRM backend = new BackendMIPS(out);
        backend.enterMain();

        // call writeint(7)
        backend.prepareProcCall(1);
        byte reg = backend.allocReg();
        backend.loadConst(reg, 7);
        backend.passArg(0, reg);
        backend.freeReg(reg);
        backend.callProc((byte) -1, "writeint");

        backend.exitMain("main_end");
    }
}
