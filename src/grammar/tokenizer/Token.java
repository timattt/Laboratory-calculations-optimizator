/**
 * 
 */
package grammar.tokenizer;

import java.math.BigDecimal;

/**
 * @author timat
 *
 */
public class Token {

	private TokenType type;
	private String value;
	private int line;

	public Token(TokenType type, String value, int line) {
		super();
		this.type = type;
		this.value = value;
		this.line = line;
	}

	public BigDecimal toNumber() {
		return new BigDecimal(value);
	}
	
	public final int getLine() {
		return line;
	}

	public final void setLine(int line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return value;
	}

	public final TokenType getType() {
		return type;
	}

	public final void setType(TokenType type) {
		this.type = type;
	}

	public final String getValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

}
