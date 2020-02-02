/**
 * 
 */
package lang.tree.vertices;

import java.math.BigDecimal;

import grammar.parser.GrammarTreeCreator.Nonterminal;
import lang.LabLang;

/**
 * @author timat
 *
 */
public class ActionVertex extends LVertex {

	// What to print
	private final byte[] printFlags;

	public ActionVertex(Nonterminal ntr) {
		super(2, ntr.getChildren().size());
		printFlags = new byte[children.length];
		int t = 0;
		for (int i = 0; i < children.length; i++) {
			if (ntr.getTokens().get(t).getValue().equals("$")) {
				t++;
				printFlags[i] |= 2;
			}
			if (ntr.getTokens().get(t).getValue().equals("$")) {
				t++;
				printFlags[i] |= 4;
			}
			t++;

			

			Nonterminal cur = ntr.getChildren().get(i);
			switch (cur.getName()) {
			case "assig":
				pushChild(new AssignVertex(cur));
				break;
			case "func":
				pushChild(new ExprVertex(cur));
				break;
			}
		}
	}

	@Override
	public BigDecimal[] process() {
		int i = 0;
		for (LVertex v : children) {
			int len = LabLang.builder.length();
			v.process();
			byte flag = printFlags[i++];
			if ((flag & 2) == 0) {
				LabLang.builder = new StringBuilder(LabLang.builder.substring(0, len));
			}
			if ((flag & 4) != 0) {
				LabLang.builder.append("\n");
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "Action";
	}

}
