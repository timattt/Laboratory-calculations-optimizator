/**
 * 
 */
package grammar.tokenizer;

import java.util.LinkedList;

import lang.LabLang;

/**
 * @author timat
 *
 */
public class Tokenizer {

	private static final char[] delimiters = new char[] { '\n', '\t', ' ', '\r' };
	private static final char[] operators = new char[] { '"', ',', ';', '+', '-', '/', '*', '=', '(', ')', '[', ']',
			'#', '^', '$' };

	private static boolean isDividor(char c) {
		for (int i = 0; i < delimiters.length; i++) {
			if (delimiters[i] == c) {
				return true;
			}
		}

		return false;
	}

	private static Token[] toArray(LinkedList<Token> lst) {
		Token[] result = new Token[lst.size()];
		for (int i = 0; i < lst.size(); i++) {
			result[i] = lst.get(i);
		}
		return result;
	}

	private static boolean isOperator(char s) {
		for (char o : operators) {
			if (o == s) {
				return true;
			}
		}

		return false;
	}

	private static boolean isNumber(char s) {
		return s >= '0' && s <= '9';
	}

	/**
	 * Splits string by delimiters into tokens array.
	 * 
	 * @param text
	 *            - string to split.
	 * @return - array of tokens.
	 */
	public static Token[] tokenize(String text) {
		text += "\n";
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		text = text.replace("-", "+-");
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		String text_ = "";
		boolean com = false;
		for (int i = 0; i < text.length() - 1; i++) {
			if (text.charAt(i) == '/' && text.charAt(i + 1) == '/') {
				com = true;
			}
			if (text.charAt(i) == '\n') {
				com = false;
			}
			if (!com) {
				text_ += text.charAt(i);
			}
		}
		text = text_;

		// Checking quotes
		int count_ = 0;
		int line_num = 1;
		int last_line = -1;
		for (int i = 0; i < text.length(); i++) {
			char s = text.charAt(i);
			if (s == '"') {
				count_++;
				last_line = line_num;
			}
			if (s == '\n') {
				line_num++;
			}
		}

		if (count_ % 2 != 0) {
			LabLang.syntaxError("Open quotes", last_line);
		}

		// Tokens
		LinkedList<Token> raw = new LinkedList<Token>();
		LinkedList<Token> string_tokens = new LinkedList<Token>();

		StringBuilder text_no_strings = new StringBuilder();

		int start = -1;
		line_num = 1;
		// Checking strings
		for (int i = 0; i < text.length(); i++) {
			char s = text.charAt(i);
			if (s == '\n') {
				line_num++;
			}
			if (s == '"') {
				if (start == -1) {
					start = i;
				} else {
					int end = i;
					string_tokens.add(new Token(TokenType.string, text.substring(start + 1, end), line_num));
					start = -1;
				}
			}
			if (start == -1) {
				text_no_strings.append(s);
			}
		}

		text = text_no_strings.toString();
		
		// Checking operators, names and numbers
		line_num = 1;
		start = -1;
		for (int i = 0; i < text.length(); i++) {
			char s = text.charAt(i);
			if (s == '\n') {
				line_num++;
			}

			boolean isOperator = isOperator(s);

			// CHECKING something like 10E-2
			if (s == '-' && i > 0 && (text.charAt(i - 1) == 'E' || text.charAt(i - 1) == 'e')
					&& isNumber(text.charAt(start + 1))) {
				isOperator = false;
			}

			if (isDividor(text.charAt(i)) || isOperator) {
				int end = i;

				if (start + 1 != end) {
					String value = text.substring(start + 1, end);
					//System.out.println("value = " + value);
					TokenType type = null;

					try {
						Double.parseDouble(value);
						type = TokenType.number;
					} catch (Exception e) {
						type = TokenType.name;

						if (isNumber(value.charAt(0))) {
							LabLang.syntaxError("Name may not start with number!", line_num);
						}
					}

					raw.add(new Token(type, value, line_num));
				}

				start = i;

				if (isOperator) {
					raw.add(new Token(TokenType.operator, s + "", line_num));
				}
			}

			if (s == '"') {
				raw.removeLast();
				raw.add(string_tokens.removeFirst());
			}

		}
		return toArray(raw);
	}

}
