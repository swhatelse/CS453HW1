import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parse {
	
	private static Stack<String> stack;
	private static boolean debug = false;

	public static void printDebug(String s){
		if(debug)
			System.out.println(s);
	}
	
	public static void printRes(String s){
		if(!debug)
			System.out.print(s);
	}
	
	public static void String(String s){
		Pattern pexpr = Pattern.compile(Expression.REG_EXPR);
		Pattern pstr_pr = Pattern.compile(Expression.REG_STRING_PRIME);
	    Matcher mexpr = pexpr.matcher(s);
	    Matcher mstr_pr = pstr_pr.matcher(s);
	    
	    if(mexpr.find()){
	    	Expr(mexpr.group());
	    }
	    else{
	    	System.out.println("Error: Expression incorrect");
	    }
	    
	    if(mstr_pr.find()){
	    	String_Prime(mstr_pr.group());
	    }		
	}
	
	public static void String_Prime(String s){
    	printDebug("String' = " + s);
	}
	
	public static void Expr(String s){
		printDebug("Expr = " + s);
		
		Pattern ppre = Pattern.compile(Expression.REG_PRE);
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
		Pattern pexpr_pr = Pattern.compile("("+Expression.REG_BINOP + Expression.REG_PRE+")");
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
		Pattern pincrop = Pattern.compile("^" + Expression.REG_INCROP);
		Matcher mincrop = pincrop.matcher(part2);
		Stack<String> pre_stack = new Stack<String>();
		while(mincrop.find()){
			pre_stack.push(mincrop.group());
			part2 = part2.substring(mincrop.group().length());
			mincrop = pincrop.matcher(part2);
		}
		
		Pattern ppost = Pattern.compile(Expression.REG_POST);
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
		Pattern pref = Pattern.compile(Expression.REG_REF);
		Matcher mref = pref.matcher(s);
		String part2;
		if(mref.find()){
			part2 = s.substring(mref.group().length());
			Ref(mref.group());

			Pattern pincrop = Pattern.compile(Expression.REG_INCROP);
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
		
		Pattern pterm = Pattern.compile(Expression.REG_TERM);
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
		Pattern pnum = Pattern.compile("^" + Expression.REG_NUM);
		Matcher mnum = pnum.matcher(s);
		
		Pattern ppar = Pattern.compile("^" + Expression.REG_EXPR_PAR);
		Matcher mpar = ppar.matcher(s);
		
		if(mnum.find()){
			printRes(mnum.group() + " ");
		}
		else if(mpar.find()){
			String part2 = mpar.group().substring(1, mpar.group().length() - 1);
			Expr(part2);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String filename = args[0]; 
			Scanner sc = new Scanner(new File(filename)).useDelimiter("\\s*\n\\s*");
			Pattern p = Pattern.compile(Expression.REG_STRING);
			while(sc.hasNext()){
				stack = new Stack<String>();
				String s = sc.next();
				Matcher m = p.matcher(s);
				if(m.matches()){		
					System.out.print(s + " = ");
					String(s);
					System.out.println();
				}
				else{	
					System.out.println("Error: not a string");
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
