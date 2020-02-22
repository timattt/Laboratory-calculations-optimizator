/**
 * 
 */
package grammar.parser;

import java.util.Arrays;
import java.util.LinkedList;

import grammar.tokenizer.Token;
import grammar.tokenizer.TokenType;
import lang.LabLang;

/**
 * @author timat
 *
 */
public class GrammarTreeCreator {

	/**
	 * Token counter
	 */
	private int s = 0;

	/**
	 * Tokens
	 */
	private final Token[] text;

	/**
	 * Vertices that lies on the dfs path.
	 */
	private final LinkedList<Nonterminal> vertices = new LinkedList<Nonterminal>();

	/**
	 * Head of the result tree.
	 */
	private Nonterminal head = null;

	public GrammarTreeCreator(Token[] text) {
		super();
		this.text = text;
	}

	public final Nonterminal getHead() {
		return head;
	}

	public void G() {
		climbDown("G");
		action();

		head = vertices.removeFirst();
	}

	private void farmToken() {
		vertices.getLast().tokens.add(text[s]);
		s++;
	}

	private boolean hasMoreTokens() {
		return s < text.length;
	}

	private boolean tryNumber() {
		if (hasMoreTokens() && text[s].getType() == TokenType.number) {
			farmToken();
			return true;
		}
		return false;
	}

	private boolean tryString() {
		if (hasMoreTokens() && text[s].getType() == TokenType.string) {
			farmToken();
			return true;
		} else {
			return false;
		}
	}

	private boolean tryOperators(char... args) {
		if (hasMoreTokens() && text[s].getType() == TokenType.operator) {
			for (char arg : args) {
				if (text[s].getValue().equals("" + arg)) {
					farmToken();
					return true;
				}
			}
		}

		return false;
	}

	private boolean catchOperators(char... args) {
		if (hasMoreTokens() && text[s].getType() == TokenType.operator) {
			for (char arg : args) {
				if (text[s].getValue().equals("" + arg)) {
					farmToken();
					return true;
				}
			}
		}

		LabLang.syntaxError("One of " + Arrays.toString(args) + " operators expected!",
				text[Math.min(s, text.length - 1)].getLine());

		return false;
	}

	private void catchNumber() {
		if (!hasMoreTokens() || text[s].getType() != TokenType.number) {
			LabLang.syntaxError("Number expected!", text[Math.min(s, text.length - 1)].getLine());
		} else {
			farmToken();
		}
	}

	private void catchName() {
		if (!hasMoreTokens() || text[s].getType() != TokenType.name) {
			LabLang.syntaxError("Name expected!", text[Math.min(s, text.length - 1)].getLine());
		} else {
			farmToken();
		}
	}

	private void climbDown(String name) {
		Nonterminal v = new Nonterminal(name);
		if (!vertices.isEmpty())
			vertices.getLast().getChildren().add(v);
		vertices.addLast(v);
	}

	private void climbUp() {
		vertices.removeLast();
	}

	private void action() {
		climbDown("action");

		if (text.length != 0)
			while (true) {
				tryOperators('$');
				tryOperators('$');

				if (s + 1 >= text.length) {
					LabLang.syntaxError("Incorrect action", text[text.length - 1].getLine());
				}

				if (text[s + 1].getValue().equals("=")) {
					assig();
				}

				else

				if (text[s + 1].getValue().equals("(")) {
					func();
				}

				catchOperators(';');

				if (s == text.length) {
					break;
				}
			}

		climbUp();
	}

	private void assig() {
		climbDown("assig");

		catchName();

		catchOperators('=');

		if (tryOperators('[')) {
			expr_infl();
			while (tryOperators(',')) {
				expr_infl();
			}
			catchOperators(']');
		} else {
			expr_infl();
		}

		climbUp();
	}

	private void expr_infl() {
		climbDown("expr_infl");
		Expr();
		if (tryOperators('#')) {
			infl();
		}
		climbUp();
	}

	private void infl() {
		climbDown("infl");
		catchNumber();
		climbUp();
	}

	private void func() {
		climbDown("func");

		catchName();

		catchOperators('(');

		if (!tryOperators(')')) {
			Expr();

			while (tryOperators(',')) {
				Expr();
			}

			catchOperators(')');
		}

		climbUp();
	}

	private void Expr() {
		climbDown("Expr");

		if (!tryString()) {

			T();

			if (tryOperators('+')) {
				Expr();
			}

		}

		climbUp();
	}

	private void T() {
		climbDown("T");

		P();
		if (tryOperators('^', '*', '/')) {
			T();
		}

		climbUp();
	}

	private void P() {
		climbDown("P");

		//!
		if (tryOperators('+', '-')) {
			P();
		}
		
		else
		//!
		
		if (tryOperators('(')) {
			Expr();
			catchOperators(')');
		} 
		
		else 
			
		if (!tryNumber()) {
			if (s + 1 < text.length && text[s + 1].getValue().equals("(")) {
				func();
			} else {
				catchName();
			}
		}

		climbUp();
	}

	public static class Nonterminal {

		/**
		 * Tokens that was caught by this nonterminal
		 */
		private final LinkedList<Token> tokens = new LinkedList<Token>();

		/**
		 * Children of this vertex
		 */
		private final LinkedList<Nonterminal> children = new LinkedList<Nonterminal>();
		private final String name;

		private Nonterminal(String name) {
			super();
			this.name = name;
		}

		private Nonterminal() {
			super();
			name = "NO NAME";
		}

		public final LinkedList<Token> getTokens() {
			return tokens;
		}

		@Override
		public String toString() {
			return name + " " + tokens.toString();
		}

		public LinkedList<Nonterminal> getChildren() {
			return children;
		}

		public final String getName() {
			return name;
		}

		public final Token getFirstToken() {
			return tokens.getFirst();
		}

		public final Nonterminal getFirstChild() {
			return children.getFirst();
		}

	}
}
