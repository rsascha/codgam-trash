import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class Simulator {
    static InputStream inStream;
    static PrintStream outStream;

    public static void main(String[] args) {
        try {
            inStream = new FileInputStream("./data/four-rounds.txt");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            outStream = new PrintStream(baos);
            System.setIn(inStream);
            PrintStream oldOut = System.out;
            System.setOut(outStream);
            try {
                Player.main(args);
            } catch (Exception e) {
                oldOut.println();
                oldOut.println("Simulation died");
                e.printStackTrace(oldOut);
            } finally {
                oldOut.println("Simulation output: ");
                oldOut.print(baos.toString());
                oldOut.println("Done!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
