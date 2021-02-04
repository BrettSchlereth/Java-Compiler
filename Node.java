import java.util.ArrayList;

//Class for the nodes of the tree
public class Node {
    //Stores the node's tokens, the name of the token, the most recent child node,
    //the next token, and a list of all of it's children
    ArrayList<Token> tokens;
    String label;
    Node child;
    String tok;
    String tokInst;
    ArrayList<Node> children;

    //Node constructor
    Node(Token tok, String label) {
        this.tokens = new ArrayList<>();
        this.label = "<" + label + ">";
        this.child = null;
        this.tok = null;
        this.children = new ArrayList<>();
    }

    //adds the token to the given node
    public Node addToNode(Node p, Token tok) {
        p.tokens.add(tok);
        return p;
    }

    //adds the child to the parent node
    public Node addChild(Node p) {
        boolean In = false;
        //makes sure the same child isnt added twice
        if (p.children != null) {
            for (int i = 0; i < p.children.size(); i++) {
                if (p.label.equals(p.children.get(i).label)) {
                    In = true;
                }
            }
        }
        if (!In) {
            p.children.add(p.child);
        }
        return p;
    }
}
