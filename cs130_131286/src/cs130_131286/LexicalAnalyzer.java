package cs130_131286;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs130_131286.Token.Type;

import java.util.ArrayList;

public class LexicalAnalyzer {
	
	public void errorHandling(String string) {
		String error = "";
		if(string.matches("(([A-Z|a-z|,|.|;|:|\\\"|\\'|\\)|\\(|<|>|*|%|\\d|\\-|+|\\/|=])*([^A-Z|a-z|,|.|;|:|\\\"|\\'|\\)|\\(|<|>|*|%|\\d|\\-|+|\\/|=])+([A-Z|a-z|,|.|;|:|\\\"|\\'|\\)|\\(|<|>|*|%|\\d|\\-|+|\\/|=])*)+")) {
			Pattern pattern = Pattern.compile(("(([A-Z|a-z|,|.|;|:|\\\"|\\'|\\)|\\(|<|>|*|%|\\d|\\-|+|\\/|=])*([^A-Z|a-z|,|.|;|:|\\\"|\\'|\\)|\\(|<|>|*|%|\\d|\\-|+|\\/|=])+([A-Z|a-z|,|.|;|:|\\\"|\\'|\\)|\\(|<|>|*|%|\\d|\\-|+|\\/|=])*)"));
			Matcher matcher = pattern.matcher(string);
			while(matcher.find()) {
				if(matcher.group(3)!=null) {
					if(matcher.group(3).length()!=0) {
						System.out.println( "**lexical error: illegal character ( " + matcher.group(3) + " )" );
					}
				}
			}
		}
	}
	
	public List<Token> analyzer(String string) {
		List<Token> tokens = new ArrayList<Token>();
		
		errorHandling(string);
		
		if(string.matches("(((<\\w[^><]+)+(>)?))*")) {
			tokens.addAll(tagidentFinder(string));
		}
		else if(string.matches("(((<\\/)([A-Z]*[a-z]*[^><])+)+(>)?)")) {
			tokens.addAll(endTagheadFinder(string));
		}
		else if(string.matches("(-)?\\d*(\\.\\d*)?((e)?(-)?(\\d*))?")) {
			tokens.add(new Token(Type.NUMBER,string));
		}
		else if(string.matches("(([A-Z]*[a-z]*)?((\\*\\*)|(<\\/)|[\\*|\\-|\\+|\\=|\\<|\\>|\\(|\\)|\\'|\\\"|\\/|\\%|\\:|\\,|\\.)])([A-Z]*[a-z]*)?)*")) {
			tokens.addAll(findSymbols(string));
		}
		else if(string.matches("((=)?((-?([^-+*%/=_\\'\\\":;<>]*))(([-+*%/<>]|\\*\\*)(-?([^-+*%/=_\\'\\\":;<>]))+)*)(=?)(-?([^-*%/=_\\'\\\":;<>]*)))*")) {
			tokens.addAll(equationAnalyzer(string));
		}
		else if(string.matches("([A-Z]*[a-z]*)(=)?(\\'|\\\")?((([A-Z]*[a-z]*|\\d*)+:?;?)*)(\\'|\\\")?")) {
			tokens.addAll(propertiesFinder(string));
		}
		else if(string.matches("(([A-Z]*[a-z]*)(=)?(\\'|\\\")?((([A-Z]*[a-z]*[0-9]*)+:?;?)*)(\\'|\\\")?)(>)?")) {
			tokens.addAll(propertiesWithEndTag(string));
		}
		else if(string.matches("(((<[A-Z]*[a-z]*[0-9]*)+))*\\s*(([A-Z]*[a-z]*)(=)?(\\'|\\\")?((([A-Z]*[a-z]*[0-9]*)+:?;?)*)(\\'|\\\")?)*([A-Z]*[a-z]*[0-9]*)(>)([A-Z]*[a-z]*[0-9]*)((((<\\/\\w[^><]+)+(>)?)))")) {
			tokens.addAll(htmlParser(string));
		}
		else if(string.matches("(([A-Z]*[a-z]*)*)")) {
			tokens.add(new Token(Type.IDENT,string));
		}
		else {
			if(typeEnferencer(string)!=null){
				tokens.add(new Token(typeEnferencer(string),string));
			}
			else {
				tokens.add(new Token(Type.EOF,string));
			}
		}
		
		return tokens;
	}
	
	public List<Token> htmlParser(String string) {
		List<Token> tokens = new ArrayList<Token>();
		Pattern pattern = Pattern.compile("(((<[A-Z]*[a-z]*[0-9]*)+))*\\s*(([A-Z]*[a-z]*)(=)?(\\'|\\\")?((([A-Z]*[a-z]*[0-9]*)+:?;?)*)(\\'|\\\")?)*([A-Z]*[a-z]*[0-9]*)(>)([A-Z]*[a-z]*[0-9]*)((((<\\/\\w[^><]+)+(>)?)))");
		Matcher matcher = pattern.matcher(string);
		
		while(matcher.find()) {
			if(matcher.group(1)!=null) {
				if(matcher.group(1).length()!=0) {
					tokens.addAll(tagidentFinder(matcher.group(1)));
				}
			}
			
			if(matcher.group(4)!=null) {
				if(matcher.group(4).length()!=0) {
					tokens.addAll(propertiesFinder(matcher.group(4)));
				}
			}
			
			
			
			if(matcher.group(12)!=null) {
				if(matcher.group(12).length()!=0) {
					tokens.add(new Token(Type.GTHAN,matcher.group(12)));
				}
			}
			
			
			if(matcher.group(13)!=null) {
				if(matcher.group(13).length()!=0) {
					tokens.addAll(digitFinder(matcher.group(13)));
				}
			}
			
			if(matcher.group(15)!=null) {
				if(matcher.group(15).length()!=0) {
					tokens.addAll(endTagheadFinder(matcher.group(15)));
				}
			}
		}
		
		return tokens;
	}
	
	public List<Token> findSymbols(String string) {
		List<Token> tokens = new ArrayList<Token>();
		Pattern pattern = Pattern.compile(("([A-Z]*[a-z]*([\\d](\\.\\d)*)*)?((\\*\\*)|(<\\/)|[\\*|\\-|\\+|\\=|\\<|\\>|\\(|\\)|\\'|\\\"|\\/|\\%|\\:|\\,|\\.)])([A-Z]*[a-z]*([\\d](.\\d)*)*)?"));
		Matcher matcher = pattern.matcher(string);
		
		while(matcher.find()) {
			if(matcher.group(1)!=null) {
				if(matcher.group(1).length()!=0) {
					tokens.addAll(digitFinder(matcher.group(1)));
				}
			}
			
			if(matcher.group(4)!=null) {
				if(matcher.group(4).length()!=0) {
					tokens.add(new Token(typeEnferencer(matcher.group(4)),matcher.group(4)));
				}
			}
			
			if(matcher.group(6)!=null) {
				if(matcher.group(6).length()!=0) {
					tokens.addAll(digitFinder(matcher.group(6)));
				}
			}
		}
		
		return tokens;
	}
	
	public List<Token> propertiesWithEndTag(String string) {
		List<Token> tokens = new ArrayList<Token>();
		Pattern pattern = Pattern.compile("(([A-Z]*[a-z]*)(=)?(\\'|\\\")?((([A-Z]*[a-z]*[0-9]*)+:?;?)*)(\\'|\\\")?)(>)?");
		Matcher matcher = pattern.matcher(string);
		
		while(matcher.find()) {
			if(matcher.group(1)!=null) {
				if(matcher.group(1).length()!=0) {
					tokens.addAll(propertiesFinder(matcher.group(1)));
				}
			}
			
			if(matcher.group(9)!=null) {
				if(matcher.group(9).length()!=0) {
					tokens.add(new Token(typeEnferencer(matcher.group(9)),matcher.group(9)));
				}
			}
		}
		
		return tokens;
	}
	
	public List<Token> propertiesFinder(String string) {
		List<Token> tokens = new ArrayList<Token>();
		Pattern pattern = Pattern.compile("([A-Z]*[a-z]*)(=)?(\\'|\\\")?((([A-Z]*[a-z]*[0-9]*)+:?;?)*)(\\'|\\\")?");
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()) {
			if(matcher.group(1)!=null) {
				if(matcher.group(1).length()!=0) {
					tokens.add(new Token(Type.IDENT,matcher.group(1)));
				}
			}
			
			if(matcher.group(2)!=null) {
				if(matcher.group(2).length()!=0) {
					tokens.add(new Token(Type.EQUALS,matcher.group(2)));
				}
			}
			
			if(matcher.group(3)!=null) {
				if(matcher.group(3).length()!=0) {
					tokens.add(new Token(typeEnferencer(matcher.group(3)),matcher.group(3)));
				}
			}
			if(matcher.group(4)!=null) {
				if(matcher.group(4).length()!=0) {
					tokens.addAll(properties(matcher.group(4)));
				}
			}
			
			if(matcher.group(7)!=null) {
				if(matcher.group(7).length()!=0) {
					tokens.add(new Token(typeEnferencer(matcher.group(7)),matcher.group(7)));
				}
			}
			
		}
		
		return tokens;
	}
	
	//+((:)?)((;)?)
	
	public List<Token> properties(String string) {
		List<Token> tokens = new ArrayList<Token>();
//		System.out.println(string);
		Pattern pattern = Pattern.compile("(([A-Z]*[a-z]*[0-9]*))?((:)?)((;)?)");
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()) {
			if(matcher.group(1)!=null) {
				if(matcher.group(1).length()!=0) {
//					tokens.add(new Token(Type.IDENT,matcher.group(1)));
					tokens.addAll(digitFinder(matcher.group(1)));
				}
			}
			
			if(matcher.group(3)!=null) {
				if(matcher.group(3).length()!=0) {
					tokens.add(new Token(typeEnferencer(matcher.group(3)),matcher.group(3)));
				}
			}
			
			if(matcher.group(5)!=null) {
				if(matcher.group(5).length()!=0) {
					tokens.add(new Token(typeEnferencer(matcher.group(5)),matcher.group(5)));
				}
			}
		}
		
		return tokens;
	}
	
	public List<Token> tagidentFinder(String string) {
		List<Token> tokens = new ArrayList<Token>();
		Pattern pattern = Pattern.compile("((<\\w[^><]+)+(>)?)");
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()) {
			if(matcher.group(2)!=null) {
				if(matcher.group(2).length()!=0 ) { 
					tokens.add(new Token(Type.TAGIDENT,matcher.group(2)));
				}
			}
			
			if(matcher.group(3)!=null) {
				if (matcher.group(3).length()!=0) {
				tokens.add(new Token(Type.GTHAN,matcher.group(3)));
				}
			}
			
		}
		
		return tokens;
	}
	
	public List<Token> endTagheadFinder(String string) {
		List<Token> tokens = new ArrayList<Token>();
		Pattern pattern = Pattern.compile("(((<\\/)([A-Z]*[a-z]*[^><])+)+(>)?)");
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()) {

			if(matcher.group(3)!=null) {
				if(matcher.group(3).length()!=0) {
					tokens.add(new Token(Type.ENDTAGHEAD,matcher.group(3)));
				}
			}
			
			
			if(matcher.group(4)!=null) {
				if(matcher.group(4).length()!=0) {
					tokens.add(new Token(Type.IDENT,matcher.group(4)));
				}
			}
			
			if(matcher.group(5)!=null) {
				if(matcher.group(5).length()!=0) {
					tokens.add(new Token(Type.GTHAN,matcher.group(5)));
				}
			}
			
		}
		
		return tokens;
	}
	
	public Type typeEnferencer(String operand) {
		Type type = null;
		switch(operand) {
		case "+":
			type=Type.PLUS;
			break;
		case "-":
			type=Type.MINUS;
			break;
		case "*":
			type=Type.MULT;
			break;
		case "/":
			type=Type.DIVIDE;
			break;
		case "%":
			type=Type.MODULO;
			break;
		case "**":
			type=Type.EXP;
			break;
		case "(":
			type=Type.LPAREN;
			break;
		case ")":
			type=Type.RPAREN;
			break;
		case "=":
			type=Type.EQUALS;
			break;
		case "<":
			type=Type.LTHAN;
			break;
		case ">":
			type=Type.GTHAN;
			break;
		case ":":
			type=Type.COLON;
			break;
		case ",":
			type=Type.COMMA;
			break;
		case ";":
			type=Type.SCOLON;
			break;
		case ".":
			type=Type.PERIOD;
			break;
		case "'":
			type=Type.QUOTE;
			break;
		case "\"":
			type=Type.DQUOTE;
			break;
		case "</":
			type=Type.ENDTAGHEAD;
			break;
		default:
			type = null;
			break;
		}
		return type;
	}
	
	public List<Token> digitFinder(String string) {
		List<Token> tokens= new ArrayList<Token>();
		Pattern pattern = Pattern.compile("(-?\\d*(\\.\\d+)?)");
		Matcher matcher = pattern.matcher(string);
		if(matcher.find()) {
			if(matcher.group(0)!=null) {
				if(matcher.group(0).length()!=0) {
					tokens.add(new Token(Type.NUMBER,string));
				}
				else {
					tokens.add(new Token(Type.IDENT,string));	
				}
			}
			
			else {
				tokens.add(new Token(Type.IDENT,string));	
			}
		}
		else {
			tokens.add(new Token(Type.IDENT,string));
		}
		return tokens;
	}
	
	public List<Token> parenthesesFinder(String string) {
		List<Token> tokens= new ArrayList<Token>();
		Pattern pattern = Pattern.compile("(\\(*)([^-+*%/=\\)\\(]*)(\\)*)");
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()) {
			if(matcher.group(1)!=null) {
				if(matcher.group(1).length()!=0) {
					tokens.add(new Token(Type.LPAREN,matcher.group(1)));
				}
			}
			if(matcher.group(2)!=null) {
				if(matcher.group(2).length()!=0) {
					tokens.addAll(digitFinder(matcher.group(2)));
				}
			}
			if(matcher.group(3)!=null) {
				if(matcher.group(3).length()!=0) {
					tokens.add(new Token(Type.RPAREN,matcher.group(3)));
				}
			}
		}
		
		return tokens;
	}
	
	public List<Token> equationAnalyzer(String string) {
		Pattern pattern = Pattern.compile("(=)?((-?([^-+*%/=_\\'\\\":;<>]*))(([-+*%/<>]|\\*\\*)(-?([^-+*%/=_\\'\\\":;<>]))+)*)(=?)(-?([^-*%/=_\\'\\\":;<>]*))");
		List<Token> tokens= new ArrayList<Token>();
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()) {
			Pattern pattern2 = Pattern.compile("(([-+%/<>]|\\*\\*|[*])*(-?([^-+*%/=_\\'\\\":;<>]*)))");
			Matcher matcher2 = pattern2.matcher(matcher.group(2));
			if(matcher.group(1)!=null) {
				if(matcher.group(1).length()!=0) {
					tokens.add(new Token(Type.EQUALS,matcher.group(1)));
				}
			}
			while(matcher2.find()) {
				if(matcher2.group(2)!=null) {
					if(matcher2.group(2).length()!=0) {
						tokens.add(new Token(typeEnferencer(matcher2.group(2)),matcher2.group(2)));
					}
				}
				if(matcher2.group(3)!=null) {
					if(matcher2.group(3).length()!=0) {
						tokens.addAll(parenthesesFinder(matcher2.group(3)));
					}
				}
			}
			
			if(matcher.group(9)!=null) {
				if(matcher.group(9).length()!=0) {
					tokens.add(new Token(Type.EQUALS,matcher.group(9)));
				}
			}
			
			
			if(matcher.group(10)!=null) {
				if(matcher.group(10).length()!=0) {
					tokens.addAll(parenthesesFinder(matcher.group(10)));
				}
			}
	
		}	
		return tokens;
	}
}
