import java.util.ArrayList;

public class STV {

    public static ArrayList<String> ST = new ArrayList<>();

    //Checks the parse tree for proper variable semantics
    public static void checkForSem(Node root) {
        Node p = root;

        if (p != null) {
            for (int i = 0; i < p.tokens.size(); i++) {
                //adds declared variables to an ArrayList
                if (p.tokens.get(i).tokenInst.equals("declare")) {
                    insert(p.tokens.get(i+1).tokenInst);
                }
            }
            if (p.children != null) {
                for (int k = 0; k < p.children.size(); k++) {
                    STV.checkForSem(p.children.get(k));
                }
            }
        }
    }

    //Inserts the variable to the list
    public static void insert(String var) {
        if (verify(var)) {
            //add variable to list
            ST.add(var);
        }
        //displays an error if the variable has already been declared
        else {
            STV.ERROR(var);
        }
    }

    //Verifies if the variable is already in the declared list
    public static boolean verify(String var) {
        if (ST != null) {
            for (int i = 0; i < ST.size(); i++) {
                if (var.equals(ST.get(i)))
                    return false;
            }
            return true;
        }
        else
            return true;
    }

    //Prints an error message if a variable has already been declared
    public static void ERROR(String var) {
        System.out.println("ERROR: variable " + var + " already declared");
    }
}
