/**
 * 
 */
package lang.tree.vertices;

import grammar.parser.GrammarTreeCreator.Nonterminal;

/**
 * @author timat
 *
 */
public class HeadVertex extends LVertex {

	/**
	 * @param id
	 */
	public HeadVertex(Nonterminal ntrm) {
		super(1, 1);
		children[0] = new ActionVertex(ntrm.getFirstChild());
	}

	@Override
	public String toString() {
		return "Head";
	}

}
