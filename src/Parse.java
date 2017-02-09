import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parse {

	public static void String(String s){
		Pattern pexpr = Pattern.compile(Expression.REG_EXPR);
		Pattern pstr_pr = Pattern.compile(Expression.REG_STRING_PRIME);
	    Matcher mexpr = pexpr.matcher(s);
	    Matcher mstr_pr = pstr_pr.matcher(s);
	    
	    if(mexpr.find()){
	    	//System.out.println("Expr = " + mexpr.group());
	    	Expr(mexpr.group());
	    }
	    else{
	    	System.out.println("Error: Expression incorrect");
	    }
	    
	    if(mstr_pr.find()){
	    	//System.out.println("String' = " + mstr_pr.group());
	    	String_Prime(mstr_pr.group());
	    }		
	}
	
	public static void String_Prime(String s){
		
	}
	
	public static void Expr(String s){
		Pattern ppre = Pattern.compile(Expression.REG_PRE);
		Pattern pexpr_pr = Pattern.compile("("+Expression.REG_BINOP + Expression.REG_PRE+")+");
	    Matcher mpre = ppre.matcher(s);
	    Matcher mexpr_pr = pexpr_pr.matcher(s);
	    
	    if(mpre.find()){
	    	Pre(mpre.group());
	    }
	    
	    if(mexpr_pr.find()){
	    	Expr_Prime(mexpr_pr.group());
	    }
	}
	
	public static void Expr_Prime(String s){
		
	}
	
	public static void Pre(String s){
		//Pattern pincrop = Pattern.compile(Expression.REG_INCROP + Expression.REG_PRE);
		//Matcher mincrop = pincrop.matcher(s);
		Pattern ppost = Pattern.compile(Expression.REG_POST);
		Matcher mpost = ppost.matcher(s);
		// As it is recursive we need to consume each pre incrop token
		// until there are no more left
        if(mpost.find()){
			Post(s);
		}
		
        String part2 = s;
		Pattern pincrop = Pattern.compile("^" + Expression.REG_INCROP + "+");
		Matcher mincrop = pincrop.matcher(part2);
		while(mincrop.find()){
			System.out.print(mincrop.group() + "_");
			part2 = part2.substring(mincrop.group().length());
			mincrop = pincrop.matcher(part2);
		}
	}
	
	public static void Post(String s){
		Pattern pref = Pattern.compile(Expression.REG_REF);
		Matcher mref = pref.matcher(s);
		String part2;
		if(mref.find()){
			Ref(mref.group());
			part2 = mref.group().substring(mref.group().length());
			
			Pattern pincrop = Pattern.compile(Expression.REG_INCROP + "+");
			Matcher mincrop = pincrop.matcher(part2);
			while(mincrop.find()){
				System.out.print("_" + mincrop.group());
				part2 = part2.substring(mincrop.group().length());
				mincrop = pincrop.matcher(part2);
			}
		}
	}
	
	public static void Ref(String s){
		Pattern plval = Pattern.compile("^F+");
		Matcher mlval = plval.matcher(s);
		
		Pattern pterm = Pattern.compile(Expression.REG_TERM);
		Matcher mterm = pterm.matcher(s);
		if(mterm.find()){
			Term(mterm.group());
		}

		while(mlval.find()){
			System.out.print(mlval.group());
		}
	}
	
	public static void Term(String s){
		Pattern pnum = Pattern.compile("^" + Expression.REG_NUM);
		Matcher mnum = pnum.matcher(s);
		
		if(mnum.find()){
			System.out.print(mnum.group());
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(new File("test")).useDelimiter("\\s*\n\\s*");
			Pattern p = Pattern.compile(Expression.REG_STRING);
			while(sc.hasNext()){
				String s = sc.next();
				Matcher m = p.matcher(s);
				if(m.matches()){					
					String(s);
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
