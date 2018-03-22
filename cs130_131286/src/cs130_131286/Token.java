package cs130_131286;

public class Token {
	
	public static enum Type {
		PLUS, MINUS, MULT, DIVIDE, MODULO, EXP, LPAREN, RPAREN,
		EQUALS, LTHAN, GTHAN, COLON, COMMA, SCOLON, PERIOD,
		QUOTE, DQUOTE, ENDTAGHEAD,
		NUMBER, IDENT, TAGIDENT, EOF;
	}
	public Type token;
	public String character;
	
	public Token(Type token, String character) {
		this.token = token;
		this.character = character;
	}
	
	public String getToken(Token t) {
		String token = t.token.name();
		String lexeme = t.character;
		return token + " " + character;
	}
}
