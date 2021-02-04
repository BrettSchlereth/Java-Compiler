import java.io.FileWriter;
import java.io.IOException;

import static java.lang.System.exit;

public class Assembly {

    //Static Accumulator holder
    public static String ACC;
    public static String DIVORMULT;

    //Traverses the tree from the given node
    public static void translate(Node p, FileWriter fw) {
        if (p != null) {
            for (int i = 0; i < p.tokens.size(); i++) {
                String thing = p.tokens.get(i).tokenInst;
                //Switch statment for each node label
                switch(p.label) {
                    //Controls the out function
                    case "<out>":
                        if (p.tokens.size() == 1)
                            translate(p.children.get(0), fw);
                            writeToOutput("STORE " + ACC + "\n", fw);
                            writeToOutput("WRITE " + ACC + "\n", fw);
                        return;
                    case "<in>":
                        writeToOutput("READ " + ACC + "\n", fw);
                        writeToOutput("LOAD " + ACC + "\n", fw);
                        return;
                    case "<expr>":
                        if (p.children.size() == 2) {
                            translate(p.children.get(0), fw);
                            String temp1 = newName("VAR", fw);
                            writeToOutput(temp1 + " " + ACC + "\n", fw);
                            writeToOutput("LOAD " + temp1 + "\n", fw);
                            translate(p.children.get(1), fw);
                            writeToOutput("SUB " + ACC + "\n", fw);
                            ACC = temp1;
                            return;
                        }
                        if (p.children.size() == 1)
                        {
                            translate(p.children.get(0), fw);
                            return;
                        }
                        return;
                    case "<N>":
                        if (p.children.size() == 2) {
                            translate(p.children.get(0), fw);
                            String T = newName("VAR", fw);
                            writeToOutput(T + " " + ACC + "\n", fw);
                            writeToOutput("LOAD " + T + "\n", fw);
                            translate(p.children.get(1), fw);
                            if (DIVORMULT.equals("MULT_tok"))
                                writeToOutput("MULT " + ACC + "\n", fw);
                            else writeToOutput("DIV " + ACC + "\n", fw);
                            ACC = T;
                            return;
                        }
                        if (p.children.size() == 1)
                        {
                            translate(p.children.get(0), fw);
                            return;
                        }
                        return;
                    case "<A>":
                        if (p.children.size() == 2) {
                            translate(p.children.get(0), fw);
                            String T = newName("VAR", fw);
                            writeToOutput(T + " " + ACC + "\n", fw);
                            writeToOutput("LOAD " + T + "\n", fw);
                            translate(p.children.get(1), fw);
                            writeToOutput("ADD " + ACC + "\n", fw);
                            ACC = T;
                            return;
                        }
                        if (p.children.size() == 1)
                        {
                            translate(p.children.get(0), fw);
                            return;
                        }
                        return;
                    case "<R>":
                        if (!p.children.isEmpty()) {
                            translate(p.children.get(0), fw);
                            return;
                        }
                        else if (Character.isLetterOrDigit(p.tokens.get(i).tokenInst.charAt(0))) {
                            if (Character.isDigit(p.tokens.get(i).tokenInst.charAt(0))) {
                                ACC = thing;
                            }
                            else {
                                //writeToOutput(thing + "\n", fw);
                                ACC = thing;
                            }
                            return;
                        }
                        else {

                        }
                        return;
                    case "<vars>":
                        if (Character.isLetter(thing.charAt(0)) && !thing.equals("declare")) {
                            writeToOutput(thing + " ", fw);
                            ACC = thing;
                        }
                        else if (Character.isDigit(thing.charAt(0))) {
                            writeToOutput(thing + '\n', fw);
                        }
                        break;
                    case "<assign>":
                        printTokens(p);
                        writeToOutput("LOAD " + ACC + "\n", fw);
                        translate(p.children.get(0), fw);
                        writeToOutput("\nSTORE " + ACC + "\n", fw);
                        return;
                    case "<if>":
                        translate(p.children.get(2), fw);
                        String temp2 = newName("VAR", fw);
                        writeToOutput(temp2 + " " + ACC + "\n", fw);
                        writeToOutput("STORE " + temp2 + "\n", fw);
                        translate(p.children.get(0), fw);
                        writeToOutput("SUB " + temp2 + "\n", fw);
                        String label1 = newName("LAB", fw);
                        writeToOutput("BRNEG " + label1 + "\n", fw);
                        writeToOutput("BRPOS " + label1 + "\n", fw);
                        translate(p.children.get(3), fw);
                        writeToOutput(label1 + ": NOOP\n", fw);
                        return;
                }
            }
            if (p.children != null) {
                for (int k = 0; k < p.children.size(); k++) {
                    Assembly.translate(p.children.get(k), fw);
                }
            }
        }
    }

    //Writes to the output file
    public static void writeToOutput(String str, FileWriter fw) {
        try {
            fw.write(str);
        }
        catch (IOException e) {
            System.out.println("ASSEMBLY ERROR: COULD NOT WRITE TO FILE");
            exit(-1);
        }
    }

    public static void printTokens(Node p) {
        System.out.print("Node " + p.label + " ");
        for (int i = 0; i < p.tokens.size(); i++) {
            System.out.print(p.tokens.get(i).tokenID);
            System.out.println(" "+ p.tokens.get(i).tokenInst);
        }
    }

    //Code for making a new variable or label
    static int nextVarNum = 0;
    static int nextLabNum = 0;
    public static String Name;

    public static String newName(String type, FileWriter fw) {
        if (type.equals("VAR")) {
            Name = "T" + nextVarNum++;
        }
        else {
            Name = "L" + nextLabNum++;
        }
        return Name;
    }

}
