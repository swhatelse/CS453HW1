import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
	public static int TK_PLUS = 0;
	public static int TK_MINUS = 1;
	public static int TK_DECR = 2;
	public static int TK_INCR = 3;
	public static int TK_SPACE = 4;
	public static int TK_LEFT_PAR = 5;
	public static int TK_RIGHT_PAR = 6;
	public static int TK_NUM = 7;
	
    public static String REG_NUM = "(\\d+)";
    public static String REG_INCROP = "((\\+\\+)|--)";
	public static String REG_BINOP = "[ ]*(\\+|-)[ ]*";
	
	public static String REG_TERM = "(" + REG_NUM + "|\\([^\\(|.]*\\))";
	public static String REG_REF = "F*" + REG_TERM;
	public static String REG_POST_PRIME = "";
	public static String REG_POST = REG_REF + REG_INCROP + "*";
	public static String REG_PRE = REG_INCROP + "*" + REG_POST;
	public static String REG_EXPR_PRIME = "(" + REG_BINOP + REG_PRE + ")";
	public static String REG_EXPR = REG_PRE + REG_EXPR_PRIME + "*";
	
	public static String REG_EXPR_PAR = "\\(" + REG_EXPR + "\\)";
	
	public static String REG_STRING_PRIME = "(^ [^\\+] " + REG_EXPR + ")*";
	public static String REG_STRING = REG_EXPR + REG_STRING_PRIME;
	
	private static Stack<String> stack;
	private static boolean debug = false;
	private static int linecount = 0;

	public static void printDebug(String s){
		if(debug)
			System.out.println(s);
	}
	
	public static void printRes(String s){
		if(!debug)
			System.out.print(s);
	}
	
	public static void printError(){
		System.out.println("Parse error in line " + linecount);
	}
	
	public static void String(String s){
		Pattern pexpr = Pattern.compile(REG_EXPR);
	    Matcher mexpr = pexpr.matcher(s);
		Pattern pstr_pr = Pattern.compile(REG_STRING_PRIME);
		Matcher mstr_pr;
		String part2;
	    
	    if(mexpr.find()){
	    	part2 = s.substring(mexpr.group().length());
	    	Expr(mexpr.group());
	    	
	    	printRes("_ ");
		    
		    mstr_pr = pstr_pr.matcher(part2);
		    while(mstr_pr.find()){
		    	String_Prime(mstr_pr.group());
		    }		
		    System.out.println("\nExpression parsed successfully");
	    }
	    else{
	    	System.out.println("Error: Expression incorrect");
	    }
	}
	
	public static void String_Prime(String s){
    	printDebug("String' = " + s);
    	
    	Expr(s);
	}
	
	public static void Expr(String s){
		printDebug("Expr = " + s);
		
		Pattern ppre = Pattern.compile(REG_PRE);
	    Matcher mpre = ppre.matcher(s);
	    
	    if(mpre.find()){
	    	printDebug("pre = " + mpre.group());
	    	Pre(mpre.group());
	    	String part2 = s.substring(mpre.group().length());
	    	Expr_Prime(part2);
	    }
	}
	
	public static void Expr_Prime(String s){
    	printDebug("expr' = " + s);
		Pattern pexpr_pr = Pattern.compile("("+REG_BINOP + REG_PRE+")");
		Matcher mexpr_pr = pexpr_pr.matcher(s);
		if(mexpr_pr.find()){
			Pre(mexpr_pr.group());
			stack.push(mexpr_pr.group().charAt(0) + " ");
			if(!stack.empty()){
				printRes(stack.pop());
			}
			Expr_Prime(s.substring(mexpr_pr.group().length()));
		}
	}
	
	public static void Pre(String s){
		printDebug("pre = " + s);
		String part2 = s;
		Pattern pincrop = Pattern.compile("^" + REG_INCROP);
		Matcher mincrop = pincrop.matcher(part2);
		Stack<String> pre_stack = new Stack<String>();
		while(mincrop.find()){
			pre_stack.push(mincrop.group());
			part2 = part2.substring(mincrop.group().length());
			mincrop = pincrop.matcher(part2);
		}
		
		Pattern ppost = Pattern.compile(REG_POST);
		Matcher mpost = ppost.matcher(s);
		// As it is recursive we need to consume each pre incrop token
		// until there are no more left
        if(mpost.find()){
			Post(part2);
		}
        
        while(!pre_stack.empty()){
        	printRes(pre_stack.pop() + "_ ");
        }
	}
	
	public static void Post(String s){
		printDebug("post = " + s);
		Pattern pref = Pattern.compile(REG_REF);
		Matcher mref = pref.matcher(s);
		String part2;
		if(mref.find()){
			part2 = s.substring(mref.group().length());
			Ref(mref.group());

			Pattern pincrop = Pattern.compile(REG_INCROP);
			Matcher mincrop = pincrop.matcher(part2);
			while(mincrop.find()){
				printRes("_" + mincrop.group() + " ");
				part2 = part2.substring(mincrop.group().length());
				mincrop = pincrop.matcher(part2);
			}
		}
	}
	
	public static void Ref(String s){
		printDebug("ref = " + s);
		Pattern plval = Pattern.compile("^F");
		Matcher mlval = plval.matcher(s);
		
		Pattern pterm = Pattern.compile(REG_TERM);
		Matcher mterm = pterm.matcher(s);
		if(mterm.find()){
			Term(mterm.group());
		}

		while(mlval.find()){
			printRes(mlval.group() + " ");
		}
	}
	
	public static void Term(String s){
		printDebug("term = " + s);
		Pattern pnum = Pattern.compile("^" + REG_NUM);
		Matcher mnum = pnum.matcher(s);
		
		Pattern ppar = Pattern.compile("^" + REG_EXPR_PAR);
		Matcher mpar = ppar.matcher(s);
		
		if(mnum.find()){
			printRes(mnum.group() + " ");
		}
		else if(mpar.find()){
			String part2 = mpar.group().substring(1, mpar.group().length() - 1);
			Expr(part2);
		}
		else{
			printDebug(s);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String filename = args[0]; 
			String content = new String(Files.readAllBytes(Paths.get(filename)));
			content = content.replaceAll("#.*", "");
			Scanner sc = new Scanner(content).useDelimiter("\\s*\n\\s*");
			Pattern p = Pattern.compile(REG_STRING);
			while(sc.hasNext()){
				linecount++;
				stack = new Stack<String>();
				String s = sc.next();
				Matcher m = p.matcher(s);
				if(m.matches()){		
					System.out.print(s + " = ");
					String(s);
				}
				else{	
					printError();
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
