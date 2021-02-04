import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;

import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.isWhitespace;
import static java.lang.System.exit;

public class Scanner {

    //Declares variables
    public static boolean carry;
    public static char nextChar;
    public static double whiteSpace = 1;
    public static int lineNum = 1;
    public static String [][] FSA = null;
    public static FileReader fr;

    //Creates the alphabet for the scanner
    public static String [] Alphabet = new String[] {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", ":", "=", "<",
            ">", "+", "-", "*", "/", "%", ".", "(", ")",
            ",", "{", "}", ";", "[", "]", "A", "B", "C",
            "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z", "0", "1", "2", "3",
            "4", "5", "6", "7", "8", "9", "\n"
    };

    //Main scanner function. Returns one token at a time
    public static Token Scanner() {

        //If the FSA has not been loaded yet, it will be loaded now
        if (FSA == null) {
            try {
                FSA = loadFSA("FSA.csv");

                //Starts reading from the filtered file if it has not done so yet
                fr = new FileReader("filteredFile.txt");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Creates a blank token from the token class
        Token nextTok = new Token();
        char n;
        try {

            //Each token starts in state 1
            int currentState = 1;
            String tokInst = null;

            //Breaks the loop when the state is over 1000, signaling a final state token has been found
            while (currentState < 1000) {

                //If there is not a character being carried from the previous token, the next character is read
                //from the filtered file.
                if (!carry) {
                    int i = fr.read();

                    //If the file is at the end, an EOF token will be made and returned.
                    if (i == -1) {
                        nextTok.lineNum = lineNum;
                        nextTok.tokenID = "EOF_tok";
                        nextTok.tokenInst = "EOF";
                        return nextTok;
                    }
                    n = (char) i;
                }
                //If there is a character being carried from the previous token, the carried token will be the
                //first character of this next token.
                else {
                    n = nextChar;
                    carry = false;
                }

                //Counts whitespace characters to track the number of lines
                if (isWhitespace(n)) {
                    if (n == '\r' || n == '\n') {
                        nextTok.tokenInst = " ";
                        nextTok.tokenID = "EMPT_tok";
                        nextTok.tokenID = "EMPT_tok";
                        return nextTok;
                    }
                    whiteSpace += 0.5;
                    if (whiteSpace % 0.5 == 0) {
                        lineNum = (int) whiteSpace;
                    }
                    nextTok.lineNum = lineNum;
                    return nextTok;
                }

                //Changes the character to a String
                String s = Character.toString(n);

                //If the token is empty, the character will begin the instance of the token
                if (tokInst == null)
                    tokInst = s;
                //Otherwise it will be added to the token instance
                else {

                    //Carries the most recent character to the next token if a token has been found
                    //before it. ie. "Alpha+" carries the "+" to the next token.
                    if (isSpecialOp(tokInst.charAt(0)) && !isSpecialOp(n)) {
                        carry(n);
                    }
                    else if (isLetterOrDigit(tokInst.charAt(0)) && isSpecialOp(n)) {
                        carry(n);
                    }
                    else if (isLetterOrDigit(tokInst.charAt(0)) && !isLetterOrDigit(n)) {
                        carry(n);
                    }
                    else {
                        //Adds the character to the token instance
                        tokInst += s;
                    }
                }

                //Finds the index of the character in the alphabet
                int input = java.util.Arrays.asList(Alphabet).indexOf(s)+1;
                //Sets the state to the state value of the next character
                try {
                    currentState = Integer.valueOf(FSA[currentState][input]) - 1;
                }
                //Prints an error if an invalid character was found. This will not stop
                //the program from running. It will just skip the invalid character.
                catch (NumberFormatException e){
                    System.out.println("SCANNER ERROR: INVALID CHARACTER '" + n + "' ON LINE: " + lineNum);
                    break;
                }
            }

            //Sets the token's information and returns it
            nextTok.tokenID = FSA[currentState][0];
            nextTok.tokenInst = tokInst;
            nextTok.lineNum = lineNum;
            //printToken(nextTok);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //Scanner.printToken(nextTok);
        return nextTok;
    }

    //Filters the input file by removing white spaces and comments
    public static void filterFile(String fileName) {
        try {
            //The file name is given in the argument of the main invocation
            FileReader fr = new FileReader(fileName);

            //Starts writing a new file called "filteredFile.txt"
            FileWriter fw = new FileWriter("filteredFile.txt");
            int i;
            char n;
            boolean comment = false;

            //Loop will continue until there are no more characters in the input file to read
            while (((i = fr.read()) != -1)) {
                n = (char) i;

                //Detects if it is the beginning or end of a comment.
                if (n == '#' && !comment)
                    comment = true;
                else if (n == '#')
                    comment = false;

                //If it is not a space or a comment, it will be written to the filtered file
                if (!comment) {
                    if (n == ' ' || n == '#') {

                    } else {
                        fw.write(n);
                    }

                    if (n == '\n') {
                        lineNum++;
                    }
                }
            }

            //Closes the filtered file writer when it is done
            fw.close();
        }
        catch (IOException e) {
            System.out.println("SCANNER ERROR: INPUT FILE NOT FOUND");
            exit(-1);
        }
    }

    //Loads the FSA from the .csv file to a 2-D Array
    public static String[][] loadFSA(String path) throws IOException {

        //Creates an empty array
        String FSA [][] = new String [1100][100];
        try {
            int row = 0;
            int column = 0;
            int i;
            char n;

            //Reads the FSA.csv file
            FileReader fr = new FileReader(path);

            //Reads the FSA until it reaches the end of the file
            while ((i = fr.read()) != -1) {
                n = (char) i;
                String s = Character.toString(n);

                //Puts characters into appropriate columns and rows
                if (s.equals("\n")) {
                    row++;
                    column = 0;
                }

                //Words are delimited by commas in the csv file
                else if (s.equals(",")){
                    column++;
                }
                else {
                    if (FSA[row][column] == null)
                        FSA[row][column] = s;
                    else
                        FSA[row][column] += s;
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        //Returns the FSA to the scanner
        return FSA;
    }


    //Tests if the character is a special operator
    public static boolean isSpecialOp(char n) {
        return (n == ':' || n == '=');
    }

    //Creates and returns the alphabet to the scanner
    public static String [] makeAlphabet() {
        String [] Alphabet = new String[]
                {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                        "l", "m", "n", "o", "p", "q", "r", "s", "t",
                        "u", "v", "w", "x", "y", "z", ":", "=", "<",
                        ">", "+", "-", "*", "/", "%", ".", "(", ")",
                        ",", "{", "}", ";", "[", "]", "A", "B", "C",
                        "D", "E", "F", "G", "H", "I", "J", "K", "L",
                        "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                        "V", "W", "X", "Y", "Z", "0", "1", "2", "3",
                        "4", "5", "6", "7", "8","9", "\n"};
        return Alphabet;
    }

    //Function for declaring that a character needs to be carried
    public static void carry(char n) {
            carry = true;
            nextChar = n;
    }

    //Prints the information of a token to the user
    public static void printToken(Token tok) {
        if (tok.tokenID != null) {
            System.out.println("{"+tok.tokenID + ", " + tok.tokenInst + ", " + tok.lineNum+"}");
        }
    }

    public static String getOutputFileName(String inputFile) {
        String outputFileName;
        //StringBuffer sbf = new StringBuffer(inputFile);
        StringBuffer sbf = new StringBuffer(inputFile);
        for (int i = 0; i < 7; i++) {
            int j = sbf.length();
            sbf.deleteCharAt(j-1);
        }
        outputFileName = sbf.toString() + ".asm";
        return outputFileName;
    }
}
