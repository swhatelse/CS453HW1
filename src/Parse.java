import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parse {

	public void String(String s){
		if(Expression.isString_Prime(s)){
			//Pattern p = Pattern.compile();
	        //Matcher m = p.matcher(s);
			Expr();
			String_Prime();
		}
		else{
			
		}
	}
	
	public void String_Prime(){
		
	}
	
	public void Expr(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Scanner sc = new Scanner(new File("test")).useDelimiter("\\s*\n\\s*");
			//Pattern p = Pattern.compile(Expression.REG_POST);
			Pattern p = Pattern.compile(Expression.REG_EXPR);
			//MatchResult result = sc.match();
		    // for (int i=0; i < result.groupCount(); i++)
		    //     System.out.println(result.group(i));
			while(sc.hasNext()){
				String s = sc.next();
				Matcher m = p.matcher(s);
				System.out.println(s + " = " + m.matches());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
