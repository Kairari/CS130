package cs130_131286;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void main(String[] args) {
		
		int count, nextToken;
		String filename = "src/TestFiles/tab1.html";
		
		lexicalAnalyzer(filename);
		
		
		
//		For outside files
//		for(int i=0; i < args.length; i++) {
//			lexicalAnalyzer(args[i]);
//		}
		
	}
	
	public static void lexicalAnalyzer(String filename) {
		try {
			
		Pattern commentPattern = Pattern.compile("(<)(!)(-)(-)\\s*\\w*\\s*(-)(-)(>)");
			
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			StreamTokenizer st = new StreamTokenizer(br);
			st.resetSyntax();
			st.whitespaceChars(0, ' ');
			st.ordinaryChar('"');
			st.ordinaryChar('\'');
			st.wordChars(33, 64);
			st.wordChars(58, 127);
			
			Boolean comment = false;
			
			LexicalAnalyzer la = new LexicalAnalyzer();
			
			
			while(st.nextToken() != StreamTokenizer.TT_EOF) {
				List<Token> tokens = new ArrayList<Token>();
				if(st.sval!=null) {
					Matcher commentMatcher = commentPattern.matcher(st.sval);
					if(!commentMatcher.matches()) {
						if(st.sval.equals("<!--")) {
							comment = true;
						}
						if(!comment) {
//						System.out.println(st.sval);
						tokens = la.analyzer(st.sval);
						}
						
						if(st.sval.equals("-->")) {
							comment = false;
						}
					}
					
				}
				
				for(Token token : tokens) {
					if(token.token.name()!="EOF") {
		            System.out.println(token.getToken(token));
					}
					else {
						break;
					}
		        }
			}
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
