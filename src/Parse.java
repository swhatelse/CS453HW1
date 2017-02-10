import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
	public static final int TK_PLUS = 0;
	public static final int TK_MINUS = 1;
	public static final int TK_DECR = 2;
	public static final int TK_INCR = 3;
	public static final int TK_SPACE = 4;
	public static final int TK_LEFT_PAR = 5;
	public static final int TK_RIGHT_PAR = 6;
	public static final int TK_0 = 7;
	public static final int TK_1 = 8;
	public static final int TK_2 = 9;
	public static final int TK_3 = 10;
	public static final int TK_4 = 11;
	public static final int TK_5 = 12;
	public static final int TK_6 = 13;
	public static final int TK_7 = 14;
	public static final int TK_8 = 15;
	public static final int TK_9 = 16;
	public static final int TK_REF = 17;
	
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
	private static Vector<Integer> tokens;

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
	
	public static void tokenize(String s){
		tokens = new Vector<Integer>();
		
		Pattern pplus = Pattern.compile("^( *\\+ *)");
		Pattern pminus = Pattern.compile("^-");
		Pattern pincrop = Pattern.compile("^\\+\\+");
		Pattern pdecrop = Pattern.compile("^--");
		Pattern pref = Pattern.compile("^F");
		Pattern plpar = Pattern.compile("^\\(");
		Pattern prpar = Pattern.compile("^\\)");
		Pattern p0 = Pattern.compile("^0");
		Pattern p1 = Pattern.compile("^1");
		Pattern p2 = Pattern.compile("^2");
		Pattern p3 = Pattern.compile("^3");
		Pattern p4 = Pattern.compile("^4");
		Pattern p5 = Pattern.compile("^5");
		Pattern p6 = Pattern.compile("^6");
		Pattern p7 = Pattern.compile("^7");
		Pattern p8 = Pattern.compile("^8");
		Pattern p9 = Pattern.compile("^9");
		
		while(s.length() > 0){
			Matcher mplus = pplus.matcher(s);
			Matcher mminus = pminus.matcher(s);
			Matcher mincrop = pincrop.matcher(s);
			Matcher mdecrop = pdecrop.matcher(s);
			Matcher mref = pref.matcher(s);
			Matcher mlpar = plpar.matcher(s);
			Matcher mrpar = prpar.matcher(s);
			Matcher m0 = p0.matcher(s);
			Matcher m1 = p1.matcher(s);
			Matcher m2 = p2.matcher(s);
			Matcher m3 = p3.matcher(s);
			Matcher m4 = p4.matcher(s);
			Matcher m5 = p5.matcher(s);
			Matcher m6 = p6.matcher(s);
			Matcher m7 = p7.matcher(s);
			Matcher m8 = p8.matcher(s);
			Matcher m9 = p9.matcher(s);
			
			if(mincrop.find()){ tokens.add(TK_INCR); s = s.substring(2);}
			else if (mplus.find()){ tokens.add(TK_PLUS); s = s.substring(1);}
			else if(mdecrop.find()){ tokens.add(TK_DECR); s = s.substring(2);}
			else if(mminus.find()){ tokens.add(TK_MINUS); s = s.substring(1);}
			else if(mref.find()){ tokens.add(TK_REF); s = s.substring(1);}
			else if(mlpar.find()){ tokens.add(TK_LEFT_PAR); s = s.substring(1);}
			else if(mrpar.find()){ tokens.add(TK_RIGHT_PAR); s = s.substring(1);}
			else if(m0.find()){ tokens.add(TK_0); s = s.substring(1);}
			else if(m1.find()){ tokens.add(TK_1); s = s.substring(1);}
			else if(m2.find()){ tokens.add(TK_2); s = s.substring(1);}
			else if(m3.find()){ tokens.add(TK_3); s = s.substring(1);}
			else if(m4.find()){ tokens.add(TK_4); s = s.substring(1);}
			else if(m5.find()){ tokens.add(TK_5); s = s.substring(1);}
			else if(m6.find()){ tokens.add(TK_6); s = s.substring(1);}
			else if(m7.find()){ tokens.add(TK_7); s = s.substring(1);}
			else if(m8.find()){ tokens.add(TK_8); s = s.substring(1);}
			else if(m9.find()){ tokens.add(TK_9); s = s.substring(1);}
			else{s = s.substring(1);}
		}
		
		for(Integer i: tokens){
			System.out.println(i);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in).useDelimiter("\\s*$\\s*");
		tokenize(sc.next());
		
		for(Integer t: tokens){
			switch(t){
				case TK_PLUS: break;	
				case TK_MINUS : break;
				case TK_DECR : break;
				case TK_INCR : break;
				case TK_SPACE : break;
				case TK_LEFT_PAR : break;
				case TK_RIGHT_PAR : break;
				case TK_0 : break;
				case TK_1 : break;
				case TK_2 : break;
				case TK_3 : break;
				case TK_4 : break;
				case TK_5 : break;
				case TK_6 : break;
				case TK_7 : break;
				case TK_8 : break;
				case TK_9 : break;
				case TK_REF : break;
			}
		}
		//Pattern p = Pattern.compile(REG_STRING);
//		while(sc.hasNext()){
//			linecount++;
//			stack = new Stack<String>();
//			String s = sc.next();
//			Matcher m = p.matcher(s);
//			if(m.matches()){		
//				System.out.print(s + " = ");
//				String(s);
//			}
//			else{	
//				printError();
//			}
//		}
		sc.close();
	}

}
