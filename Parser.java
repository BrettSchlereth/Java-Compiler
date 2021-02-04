public class Parser {

    //Makes a public static token
    public static Token tok;

    //Main Parser funciton that gets called from main
    public static Node Parser () {
        //Requests the first token to start the Parsing process
        tok = Scanner.Scanner();
        //Token is sent to program non-terminal which will return the parse tree
        Node tree = program(tok);
        tok = Scanner.Scanner();
        //Checks if the file was finished parsing
        if (tok.tokenID.equals("EOF_tok")) {
            System.out.println("File parsed successfully");
            //Prints out the parse tree
            //Tree.printTree(tree, 1);
        }
        else ERROR(tok, "EOF_tok");

        return tree;
    }

    //Error message generator
    public static void ERROR(Token tok, String expect) {
        System.out.println("ERROR: " + tok.tokenID + " received on line " + tok.lineNum
                + ", " + expect + " expected");
    }

    //Program Non-terminal
    public static Node program (Token tok) {
        //Creates a node for program
        Node p = new Node(tok, "program");
        //sends the token to vars non-terminal
        p.child = vars(tok);
        //adds the result node of vars to the list of program's children
        p.addChild(p);
        p.tok = tok.tokenID = p.child.tok;
        //sends the token to the block non-terminal
        p.child = block(tok);
        //adds the result of block to the list of program's children
        p.addChild(p);
        //returns program's parse tree
        return p;
    }

    //vars non-terminal
    public static Node vars(Token tok) {
        //creates a node for vars
        Node p = new Node(tok, "vars");
        p.tok = tok.tokenID;
        //checks if the token given is an empty token
        if (tok.tokenID.equals("EMPT_tok")) {
            //adds the token to the vars node
            //p.addToNode(p, tok);
            //gets the next token
            tok = Scanner.Scanner();
            tok = Scanner.Scanner();
            //checks if the next token is a declare token
            if (tok.tokenID.equals("DECL_tok")) {
                //sends the token to vars again
                p.child = vars(tok);
                //adds the result to the node
                p.addChild(p);
                tok.tokenID = p.child.tok;
            }
            p.tok = tok.tokenID;
            //returns the vars node
            return p;
        }
        //Checks for declare Identifier := Integer ; <vars>
        else if (tok.tokenID.equals("DECL_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            if (tok.tokenID.equals("IDENT_tok")) {
                p = p.addToNode(p, tok);
                tok = Scanner.Scanner();
                if (tok.tokenID.equals("ASS_tok")) {
                    p = p.addToNode(p, tok);
                    tok = Scanner.Scanner();
                    if (tok.tokenID.equals("INT_tok")) {
                        p = p.addToNode(p, tok);
                        tok = Scanner.Scanner();
                        if (tok.tokenID.equals("SEMI_tok")) {
                            tok = Scanner.Scanner();
                            p.child = vars(tok);
                            p.addChild(p);
                            tok.tokenID = p.child.tok;
                            p.tok = p.child.tok;
                            p.tokInst = tok.tokenInst;
                            return p;
                        }
                        //prints error messages if needed
                        else ERROR(tok, "SEMI_tok");
                    }
                    else ERROR(tok, "INT_tok");
                }
                else ERROR(tok, "ASS_tok");
            }
            else ERROR(tok, "IDENT_tok");
        }
        else ERROR(tok, "DECL_tok or EMPT_tok");
        p.tok = tok.tokenID;
        p.tokInst = tok.tokenInst;
        return p;
    }

    //block non-terminal
    public static Node block (Token tok) {
        Node p = new Node(tok, "block");
        //checks for empty token
        if (tok.tokenID.equals("EMPT_tok")) {
            tok = Scanner.Scanner();
            tok = Scanner.Scanner();
        }
        if (tok.tokenID.equals("LBRAC_tok")) {
            tok = Scanner.Scanner();
            //sends the token to vars
            p.child = vars(tok);
            p.addChild(p);
            tok.tokenID = p.child.tok;
            p.tokInst = tok.tokenInst;
            //sends the token to stats
            p.child = stats(tok);
            p.addChild(p);
            tok.tokenID = p.child.tok;
            p.tokInst = tok.tokenInst;
            if (tok.tokenID.equals("RBRAC_tok")) {
                tok = Scanner.Scanner();
                if (tok.tokenID.equals("EMPT_tok")) {
                    tok = Scanner.Scanner();
                    tok = Scanner.Scanner();
                }
                p.tok = tok.tokenID;
                p.tokInst = tok.tokenInst;
                return p;
            }
            // sends an error message if needed
            else ERROR(tok, "RBRAC_tok");
            p.tok = tok.tokenID;
            p.tokInst = tok.tokenInst;
            return p;
        }
        else ERROR(tok, "LBRAC_tok");
        p.tok = tok.tokenID;
        p.tokInst = tok.tokenInst;
        return p;
    }

    //stats non-terminal
    public static Node stats(Token tok) {
        Node p = new Node(tok, "stats");
        //sends the token to stat
        p.child = stat(tok);
        p.addChild(p);
        tok.tokenID = p.child.tok;
        p.tokInst = tok.tokenInst;
        //sends the token to mStat
        p.child = mStat(tok);
        p.addChild(p);
        tok.tokenID = p.child.tok;
        p.tok = tok.tokenID;
        return p;
    }

    //mStat non-terminal
    public static Node mStat(Token tok) {
        Node p = new Node(tok, "mStat");
        //checks for empty token
        if (tok.tokenID.equals("EMPT_tok")) {
            tok = Scanner.Scanner();
            tok = Scanner.Scanner();
            if (!tok.tokenID.equals("RBRAC_tok")) {
                //sends token to mStat
                p.child = mStat(tok);
                p.addChild(p);
                tok.tokenID = p.child.tok;
            }
            p.tok = tok.tokenID;
            p.tokInst = tok.tokenInst;
            return p;
        }
        //sends token to stat
        p.child = stat(tok);
        p.addChild(p);
        tok.tokenID = p.child.tok;
        //sends token to mStat
        p.child = mStat(tok);
        p.addChild(p);
        p.tok = tok.tokenID = p.child.tok;
        return p;
    }

    //stat non-terminal
    public static Node stat(Token tok) {
        Node p = new Node(tok, "stat");
        p.tok = tok.tokenID;
        p.tokInst = tok.tokenInst;
        //switch statement for determining which non-terminal is next
        switch(tok.tokenID) {
            case "IN_tok":
                //sends token to in
                p.child = in(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return checkForSemi(tok, p);
            case "OUT_tok":
                //sends the token to out
                p.child = out(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return checkForSemi(tok, p);
            case "LBRAC_tok":
                //sends the token to block
                p.child = block(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return p;
            case "IFFY_tok":
                //sends the token to iffy
                p.child = iffy(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return checkForSemi(tok, p);
            case "LOOP_tok":
                //sends the token to loop
                p.child = loop(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return checkForSemi(tok, p);
            case "IDENT_tok":
//                p.addToNode(p, tok);
//                tok = Scanner.Scanner();
                //sends token to assign
                p.child = assign(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                p.tokInst = tok.tokenInst;
                return checkForSemi(tok, p);
            case "GOTO_tok":
                //sends token to goto
                p.child = go_to(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return checkForSemi(tok, p);
            case "LAB_tok":
                //sends token to label
                p.child = label(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return checkForSemi(tok, p);
            default:
                //if nothing matches, an error message is displayed
                ERROR(tok, "IN, OUT, [, IFFY, LOOP, ASS, GOTO, LAB");
                tok = Scanner.Scanner();
                p.tok = tok.tokenID;
                p.tokInst = tok.tokenInst;
                return p;
        }
    }

    //in non-terminal
    public static Node in(Token tok) {
        Node p = new Node(tok, "in");
        p.addToNode(p, tok);
        tok = Scanner.Scanner();
        if (tok.tokenID.equals("IDENT_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            p.tok = tok.tokenID;
            return p;
        }
        //prints an error message if needed
        else ERROR(tok, "IDENT_tok");
        p.tok = tok.tokenID = p.child.tok;
        return p;
    }

    //out non-terminal
    public static Node out(Token tok) {
        Node p = new Node(tok, "out");
        p.addToNode(p, tok);
        tok = Scanner.Scanner();
        p.child = expr(tok);
        p.addChild(p);
        p.tok = tok.tokenID = p.child.tok;
        return p;
    }

    //if non-terminal
    public static Node iffy(Token tok) {
        Node p = new Node(tok, "if");
        p.addToNode(p, tok);
        tok = Scanner.Scanner();
        if (tok.tokenID.equals("LBRAK_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            //sends the token to expr
            p.child = expr(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            //sends the token to RO
            p.child = RO(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            //sends the token to expr
            p.child = expr(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            if (tok.tokenID.equals("RBRAK_tok")) {
                p = p.addToNode(p, tok);
                tok = Scanner.Scanner();
                if (tok.tokenID.equals("THEN_tok")) {
                    p = p.addToNode(p, tok);
                    tok = Scanner.Scanner();
                    p.child = checkForEmpty(tok, p);
                    p.tok = tok.tokenID = p.child.tok;
                    //sends the token to stat
                    p.child = stat(tok);
                    p.addChild(p);
                    p.tok = tok.tokenID = p.child.tok;
                    return p;
                }
                //prints an error message if needed
                else ERROR(tok, "THEN_tok");
            }
            else ERROR(tok, "RBRAK_tok");
        }
        else ERROR(tok, "LBRAK_tok");
        p.tok = tok.tokenID;
        return p;
    }

    //loop non-terminal
    public static Node loop(Token tok) {
        Node p = new Node(tok, "loop");
        p.addToNode(p, tok);
        tok = Scanner.Scanner();
        if (tok.tokenID.equals("LBRAK_tok")) {
            p.addToNode(p, tok);
            tok = Scanner.Scanner();
            //sends token to expr
            p.child = expr(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            //sends the token to RO
            p.child = RO(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            //sends the token to expr
            p.child = expr(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            if (tok.tokenID.equals("RBRAK_tok")) {
                tok = Scanner.Scanner();
                p.child = checkForEmpty(tok, p);
                p.tok = tok.tokenID = p.child.tok;
                //sends token to stat
                p.child = stat(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                return p;
            }
            //prints an error message if needed
            else ERROR(tok, "RBRAK_tok");
        }
        else ERROR(tok, "LBRAK_tok");
        p.tok = tok.tokenID;
        return p;
    }

    //assign non-terminal
    public static Node assign(Token tok) {
        Node p = new Node(tok, "assign");
        p.addToNode(p, tok);
        tok = Scanner.Scanner();
        if (tok.tokenID.equals("ASS_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            //sends the token to expr
            p.child = expr(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            return p;
        }
        //prints an error message if needed
        else ERROR(tok, "ASS_tok");
        p.tok = tok.tokenID;
        return p;
    }

    //goto non-terminal
    public static Node go_to(Token tok) {
        Node p = new Node(tok, "goto");
        p.addToNode(p, tok);
        tok = Scanner.Scanner();
        if (tok.tokenID.equals("IDENT_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            p.tok = tok.tokenID;
            return p;
        }
        //prints an error message if needed
        else ERROR(tok, "IDENT_tok");
        p.tok = tok.tokenID;
        return p;
    }

    //label non-terminal
    public static Node label(Token tok) {
        Node p = new Node(tok, "label");
        p.addToNode(p, tok);
        tok = Scanner.Scanner();
        if (tok.tokenID.equals("IDENT_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            p.tok = tok.tokenID;
            return p;
        }
        //prints an error message if needed
        else ERROR(tok, "IDENT_tok");
        p.tok = tok.tokenID;
        return p;
    }

    //RO non-terminal
    public static Node RO (Token tok) {
        Node p = new Node(tok, "RO");
        if (tok.tokenID.equals("LESS_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            p.tok = tok.tokenID;
            if (tok.tokenID.equals("LESS_tok") || tok.tokenID.equals("GREA_tok")) {
                p = p.addToNode(p, tok);
                tok = Scanner.Scanner();
                p.tok = tok.tokenID;
                return p;
            }
            else {
                p.tok = tok.tokenID;
                return p;
            }
        }
        else if (tok.tokenID.equals("GREA_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            if (tok.tokenID.equals("GREA_tok")) {
                p = p.addToNode(p, tok);
                tok = Scanner.Scanner();
                p.tok = tok.tokenID;
                return p;
            }
            p.tok = tok.tokenID;
            return p;
        }
        else if (tok.tokenID.equals("EQUIV_tok")) {
            p.addToNode(p, tok);
            tok = Scanner.Scanner();
            p.tok = tok.tokenID;
            return p;
        }
        else ERROR(tok, "<, <<, >, >>, ==, <>");
        p.tok = tok.tokenID;
        return p;
    }

    //expr non-terminal
    public static Node expr(Token tok) {
        Node p = new Node(tok, "expr");
        p.child = N(tok);
        p.addChild(p);
        p.tok = tok.tokenID = p.child.tok;
        if (tok.tokenID.equals("SUB_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            //sends the token to expr
            p.child = expr(tok);
            p.addChild(p);
            p.tok = tok.tokenID = p.child.tok;
            return p;
        }
        p.tok = tok.tokenID;
        return p;
    }

    //N non-terminal
    public static Node N(Token tok) {
        Node p = new Node(tok, "N");
        //sends token to A
        p.child = A(tok);
        p.addChild(p);
        tok.tokenID = p.tok = p.child.tok;
        if (tok.tokenID.equals("DIV_tok") || tok.tokenID.equals("MULT_tok")) {
            if (tok.tokenID.equals("MULT_tok")) {
                Assembly.DIVORMULT = "MULT_tok";
            }
            else if (tok.tokenID.equals("DIV_tok"))
                Assembly.DIVORMULT = "DIV_tok";
            p = p.addToNode(p, tok);
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            //sends token to N
            p.child = N(tok);
            p.addChild(p);
            tok.tokenID = p.tok = p.child.tok;
            return p;
        }
        p.tok = tok.tokenID;
        return p;
    }

    //A non-terminal
    public static Node A(Token tok) {
        Node p = new Node(tok, "A");
        //sends token to M
        p.child = M(tok);
        p.addChild(p);
        p.tok = tok.tokenID = p.child.tok;
        if (tok.tokenID.equals("ADD_tok")) {
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            //sends token to A
            p.child = A(tok);
            p.addChild(p);
        }
        p.tok = p.child.tok;
        return p;
    }

    //M non-terminal
    public static Node M(Token tok) {
        Node p = new Node(tok, "M");
        if (tok.tokenInst.equals("*")) {
            tok.tokenID = "MULT_tok";
            p = p.addToNode(p, tok);
            tok = Scanner.Scanner();
            p = p.addToNode(p, tok);
            //sends token to M
            p.child = M(tok);
            p.addChild(p);
            p.tok = p.child.tok;
            return p;
        }
        else {
            //sends token to R
            p.child = R(tok);
            p.addChild(p);
            tok.tokenID = p.child.tok;
        }
        p.tok = tok.tokenID;
        return p;
    }

    //R non-terminal
    public static Node R(Token tok) {
        Node p = new Node(tok, "R");
        //switch statement for next token
        switch (tok.tokenID) {
            case "LPAR_tok":
                tok = Scanner.Scanner();
                //sends token to expr
                p.child = expr(tok);
                p.addChild(p);
                p.tok = tok.tokenID = p.child.tok;
                if (tok.tokenID.equals("RPAR_tok")) {
                    p = p.addToNode(p, tok);
                    tok = Scanner.Scanner();
                    p.tok = tok.tokenID;
                    return p;
                }
                else ERROR(tok, "RPAR_tok");
                p.tok = tok.tokenID;
                return p;
            case "IDENT_tok":
                p = p.addToNode(p, tok);
                tok = Scanner.Scanner();
                p.tok = tok.tokenID;
                return p;
            case "INT_tok":
                p = p.addToNode(p, tok);
                tok = Scanner.Scanner();
                p.tok = tok.tokenID;
                return p;
            default:
                ERROR(tok, "( <expr> ) | IDENT | INT");
        }
        p.tok = tok.tokenID;
        return p;
    }

    //checks if the token is a semicolon and returns the next token if true
    public static Node checkForSemi(Token tok, Node p) {
        tok.tokenID = p.tok;
        if (tok.tokenID.equals("SEMI_tok")) {
            tok = Scanner.Scanner();
            p.tok = tok.tokenID;
            return p;
        }
        else {
            //returns an error if token is not a semicolon
            ERROR(tok, "SEMI_tok");
            p.tok = tok.tokenID;
            return p;
        }
    }

    //checks if the token is an empty token and returns the next token if true
    public static Node checkForEmpty (Token tok, Node p) {
        if (tok.tokenID.equals("EMPT_tok")) {
            tok = Scanner.Scanner();
            tok = Scanner.Scanner();
            p.tok = tok.tokenID;
            return p;
        }
        p.tok = tok.tokenID;
        return p;
    }
}
