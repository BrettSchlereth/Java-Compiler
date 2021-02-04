import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class Main {

    // Main Function
    public static void main(String[] args) {
        //Tests the arguments for proper invocation
        testArgs(args);

        //If the invocation is proper, it will set the file name
        String fileName = args[0];

        //Formats the name for the output file
        String outputFileName = Scanner.getOutputFileName(fileName);

        //Filters the input file to remove white spaces and comments
        //Creates a new file called filteredFile.txt
        Scanner.filterFile(fileName);

        //Parses the input file and returns the parse tree
        Node tree = Parser.Parser();

        //Checks the parse tree for proper use of variable declarations
        STV.checkForSem(tree);

        //Translates to assembly language
        try {
            FileWriter fw = new FileWriter(outputFileName);
            Assembly.translate(tree, fw);
            Assembly.writeToOutput("\nSTOP", fw);
            fw.close();
            System.out.println(outputFileName + " has been generated");
        }
        catch (IOException e){
            System.out.println("ASSEMBLY ERROR: COULD NOT CREATE OUTPUT FILE");
            exit(-1);
        }
    }

    //Tests the arguments for proper invocation and returns an error message if needed
    public static void testArgs(String [] args) {
        if (args.length < 1) {
            System.out.println("INVOCATION ERROR: comp [file]");
            exit(-1);
        }
    }
}




