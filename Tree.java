public class Tree {

    //prints the parse tree
    public static void printTree(Node root, int indent) {
        //customizes the indent size
        String tab = " ";
        for (int i = 0; i < indent; i++) {
            tab += " ";
        }
        Node p = root;
        //prints the information from every node in the tree
        if (p != null) {
            System.out.println(tab + p.label + " Tokens: "  + p.tokens);
            if (p.children != null) {
                for (int k = 0; k < p.children.size(); k++) {
                    //sets the next indent size
                    if (k == 0) {
                        Tree.printTree(p.children.get(k), indent++);
                    }
                    else {
                        Tree.printTree(p.children.get(k), indent--);
                    }
                }
            }
        }
    }
}
