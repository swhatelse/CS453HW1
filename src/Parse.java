import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
	static class Token{
		public int code;
		public int line;
		public String value;
		
		public Token(int code, String value, int line){
			this.code = code;
			this.value = value;
			this.line = line;
		}
		
		public String toString(){
			String sym = "";
			switch (code){
			case TK_BINOP : sym = "BINOP"; break;
			case TK_INCR : sym = "INCR"; break;
			case TK_NUM : sym = "NUM"; break;
			case TK_LEFT_PAR : sym = "LPAR"; break;
			case TK_RIGHT_PAR : sym = "RPAR"; break;
			case TK_REF : sym = "REF"; break;
			}
			return sym + "(" + value + ")" + "<" + line + ">";
		}
	}
	static class Lexer{
		public static void tokenize(String s, int line){
			
			s = s.replaceAll("#.*$","");
			
			Pattern pbinop = Pattern.compile("^ *(\\+|-) *");
			Pattern pincrop = Pattern.compile("^(\\+\\+|--)");
			Pattern pref = Pattern.compile("^\\$");
			Pattern plpar = Pattern.compile("^\\(");
			Pattern prpar = Pattern.compile("^\\)");
			Pattern pnum = Pattern.compile("^\\d+");
			
			while(s.length() > 0){
				Matcher mbinop = pbinop.matcher(s);
				Matcher mincrop = pincrop.matcher(s);
				Matcher mref = pref.matcher(s);
				Matcher mlpar = plpar.matcher(s);
				Matcher mrpar = prpar.matcher(s);
				Matcher mnum = pnum.matcher(s);
				
				if(mincrop.find()){ tokens.add(new Token(TK_INCR, mincrop.group(), line)); s = s.substring(mincrop.group().length());}
				else if (mbinop.find()){ tokens.add(new Token(TK_BINOP, mbinop.group(), line)); s = s.substring(mbinop.group().length());}
				else if(mref.find()){ tokens.add(new Token(TK_REF, mref.group(), line)); s = s.substring(mref.group().length());}
				else if(mlpar.find()){ tokens.add(new Token(TK_LEFT_PAR, mlpar.group(), line)); s = s.substring(mlpar.group().length());}
				else if(mrpar.find()){ tokens.add(new Token(TK_RIGHT_PAR, mrpar.group(), line)); s = s.substring(mrpar.group().length());}
				else if(mnum.find()){ tokens.add(new Token(TK_NUM, mnum.group(), line)); s = s.substring(mnum.group().length());}			
				else{s = s.substring(1);}
			}
		}
	}
	
	static class Node{
		public Node parent;
		public List<Node> children = new ArrayList<Node>();
		
		public Node(Node parent){
			this.parent = parent;
		}
	}
	
	public static final int TK_BINOP = 0;
	public static final int TK_INCR = 1;
	public static final int TK_SPACE = 2;
	public static final int TK_LEFT_PAR = 3;
	public static final int TK_RIGHT_PAR = 4;
	public static final int TK_REF = 5;
	public static final int TK_NUM = 6;
	
    public static String REG_NUM = "(\\d+)";
    public static String REG_INCROP = "((\\+\\+)|--)";
	public static String REG_BINOP = "[ ]*(\\+|-)[ ]*";
	
	private static boolean debug = false;
	private static int linecount = 0;
	private static Vector<Token> tokens;
	private static Token currentToken = null;
	private static Token previousToken = null;
	private static boolean erroneous = false;
	private static String res = "";
	private static int par_level = 0;

	private static Token getToken(){
		previousToken = currentToken;
		if(tokens.size() > 0){
			return tokens.remove(0);
		}
		else{
			return null;
		}
	}
	
	public static void printDebug(String s){
		if(debug)
			System.out.println(s);
	}
	
	public static void printRes(String s){
		res += s;
	}
	
	public static void printError(Token t){
		erroneous = true;
		System.out.println("Parse error in line <" + t.line + ">");
	}
	
	public static void String(){
		currentToken = getToken();
		if(currentToken != null){
			printDebug("String: " + currentToken);
			if(currentToken.code == TK_INCR | currentToken.code == TK_REF | currentToken.code == TK_LEFT_PAR | currentToken.code == TK_NUM){
				Expr();
				String_Prime();
			}
			else{
				printError(currentToken);
			}
		}
	}
	
	public static void String_Prime(){
		if(currentToken != null){
			printDebug("String_Prime: " + currentToken);
			if(currentToken.code == TK_INCR | currentToken.code == TK_REF | currentToken.code == TK_LEFT_PAR | currentToken.code == TK_NUM){
				Expr();
				printRes("_ ");
				String_Prime();
			}
			else if(currentToken.code == TK_RIGHT_PAR){
				
			}
			else{
				printError(currentToken);
			}
		}
	}
	
	public static void Expr(){
		printDebug("Expr: " + currentToken);
	    if(currentToken.code == TK_INCR | currentToken.code == TK_REF | currentToken.code == TK_LEFT_PAR | currentToken.code == TK_NUM){
    		Pre();	
    		Expr_Prime();

	    	if(currentToken != null && currentToken.code == TK_RIGHT_PAR){
	    		if(par_level < 1){
	    			printError(currentToken);
	    		}
	    		par_level--;
	    		currentToken = getToken();
	    	}
	    }
	    else{
	    	printError(currentToken);
	    }
	}
	
	public static void Expr_Prime(){
		if(currentToken != null){
			Stack<String> stack = new Stack<String>();
			printDebug("Expr_Prime: " + currentToken);
			if(currentToken.code == TK_BINOP){
				stack.push(currentToken.value + " ");
				currentToken = getToken();
				if(currentToken != null){
					Pre();
					if(!stack.empty()){
						printRes(stack.pop());
					}
					Expr_Prime();
				}
				else{
					printError(previousToken);
				}
			}
			else if (currentToken.code == TK_INCR | currentToken.code == TK_REF | currentToken.code == TK_LEFT_PAR | currentToken.code == TK_RIGHT_PAR | currentToken.code == TK_NUM){
				
			}
			else{
				printError(currentToken);
			}	
		}
}

	
	public static void Pre(){
		printDebug("Pre: " + currentToken);
		Stack<String> pre_stack = new Stack<String>();
		if(currentToken.code == TK_INCR){
			pre_stack.push(currentToken.value);
			currentToken = getToken();
			if(currentToken != null){
				Pre();
			}
		}
		else if(currentToken.code == TK_REF | currentToken.code == TK_LEFT_PAR | currentToken.code == TK_NUM){
			Post();
		}
		else{
			printError(currentToken);
		}
		
        while(!pre_stack.empty()){
        	printRes(pre_stack.pop() + "_ ");
        }
	}
	
	public static void Post(){
		printDebug("Post: " + currentToken);
		if(currentToken.code == TK_REF | currentToken.code == TK_LEFT_PAR | currentToken.code == TK_NUM){
			Ref();
			
			while(currentToken != null && currentToken.code == TK_INCR){
				printRes("_" + currentToken.value + " ");
				currentToken = getToken();
			}
		}
		else{
			printError(currentToken);
		}
	}
	
	public static void Ref(){
		printDebug("Ref: " + currentToken);
		Stack<String> stack = new Stack<String>();
        while(currentToken != null && currentToken.code == TK_REF){
        	stack.push(currentToken.value);
        	currentToken = getToken();
        }
        if(currentToken.code == TK_NUM | currentToken.code == TK_LEFT_PAR){
			Term();
		}
        else{
        	printError(currentToken);
        }

		while(!stack.empty()){
			printRes(stack.pop() + " ");
		}
	}
	
	public static void Term(){
		printDebug("Term: " + currentToken);
		if(currentToken.code == TK_NUM){
			printRes(currentToken.value + " ");
			currentToken = getToken();
		}
		else if(currentToken.code == TK_LEFT_PAR){
			par_level++;
			currentToken = getToken();
			if(currentToken != null)
				Expr();
		}
		else{
			printError(currentToken);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in).useDelimiter("\\s*\n\\s*");
		tokens = new Vector<Token>();
		while(sc.hasNext()){
			linecount++;
			Lexer.tokenize(sc.next(),linecount);	
		}
		sc.close();
		String();
		if(par_level > 0){
			printError(previousToken);
		}
		
		if(!erroneous){
			if(!debug)
				System.out.println(res + "Expression parsed successfully");
		}
	}

}
