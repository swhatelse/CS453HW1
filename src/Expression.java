import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Expression {
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
	public static String REG_BINOP = "(\\+|-)";
	
	public static String REG_TERM = "(" + REG_NUM + "|\\([^\\(|.]*\\))";
	public static String REG_REF = "F*" + REG_TERM;
	public static String REG_POST_PRIME = "";
	public static String REG_POST = REG_REF + REG_INCROP + "*";
	public static String REG_PRE = REG_INCROP + "*" + REG_POST;
	public static String REG_EXPR_PRIME = "(" + REG_BINOP + REG_PRE + ")";
	public static String REG_EXPR = REG_PRE + REG_EXPR_PRIME + "*";
	
	public static String REG_EXPR_PAR = "\\(" + REG_EXPR + "\\)";
	
	public static String REG_STRING_PRIME = "( " + REG_EXPR + ")*";
	public static String REG_STRING = REG_EXPR + REG_STRING_PRIME;
	
	private static boolean isSomething(String s, String regex){
		Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
		return m.matches();
	}
	
	public static boolean isString(String s){
		return isSomething(s, REG_STRING);
	}
	
	public static boolean isString_Prime(String s){
		return isSomething(s, REG_STRING_PRIME);
	}
	
	public static boolean isExpr(String s){
		return isSomething(s, REG_EXPR);
	}
	
	public static boolean isExpr_Prime(String s){
		return isSomething(s, REG_EXPR_PRIME);
	}
	
	public static boolean isPre(String s){
		return isSomething(s, REG_PRE);
	}
	
	public static boolean isPost(String s){
		return isSomething(s, REG_POST);
	}
	
	public static boolean isRef(String s){
		return isSomething(s, REG_REF);
	}
	
	public static boolean isTerm(String s){
		return isSomething(s, REG_TERM);
	}
}
