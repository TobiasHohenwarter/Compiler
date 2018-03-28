package yapl.test.backend.rm;

import java.io.FileOutputStream;
import java.io.PrintStream;

import yapl.impl.BackendMIPS;
import yapl.interfaces.BackendAsmRM;

/**
 * BackendAsmRM test: printing a string constant.
 * @author Mario Taschwer
 */
public class Test1
{
    /**
     * Usage: java yapl.test.backend.rm.Test1 [asm_file]
     */
    public static void main(String[] args) throws Exception
    {
        PrintStream out = (args.length > 0) 
                ? new PrintStream(new FileOutputStream(args[0])) : System.out;
        BackendAsmRM backend = new BackendMIPS(out);
        backend.enterMain();
        int addr = backend.allocStringConstant("Hello world!");
        backend.writeString(addr);
        backend.exitMain("main_end");
    }
}
