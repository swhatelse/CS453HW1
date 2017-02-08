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
			Pattern p = Pattern.compile(Expression.REG_PRE);
			while(sc.hasNext()){
				String s = sc.next();
				Matcher m = p.matcher(s);
				while(m.find()){
					System.out.println(m.group().length());
					System.out.println(s + " = " + m.group());
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
