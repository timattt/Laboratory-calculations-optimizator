/**
 * 
 */
package lang.tree.vertices;

import static lang.InfilicityCounter.calcInfl;
import static lang.InfilicityCounter.correctValue;
import static lang.LangStorage.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import grammar.parser.GrammarTreeCreator.Nonterminal;
import lang.LabLang;

/**
 * @author timat
 *
 */
public class ValueVertex extends LVertex {

	// Vertices
	private final ExprVertex expr;
	private final ExprVertex infl;

	public ValueVertex(Nonterminal ntrm) {
		super(6, ntrm.getChildren().size());
		children[0] = expr = new ExprVertex(ntrm.getChildren().get(0));
		if (ntrm.getChildren().size() > 1) {
			children[1] = infl = new ExprVertex(ntrm.getChildren().get(1));
		} else {
			infl = null;
		}
	}

	@Override
	public BigDecimal[] process() {
		ArrayList<String> vars = new ArrayList<String>();
		expr.getAllVariables(vars);
		
		int total = 1;
		for (String var_name : vars) {
			if (!hasVariable(var_name)) {
				LabLang.compilationError("No such variable " + var_name);
			}
			total = Math.max(total, getVariable(var_name).values.length);
		}

		BigDecimal[] result = new BigDecimal[total * 2];

		for (calcIndex = 0; calcIndex < total; calcIndex++) {
			result[calcIndex] = expr.process()[0];
			if (result[calcIndex] == null) {
				LabLang.compilationError("Function " + expr.getFunct() + " has no return statement");
			}
			if (infl != null) {
				result[total + calcIndex] = infl.process()[0];
			} else {
				result[total + calcIndex] = calcInfl(expr);
			}

			result[calcIndex] = correctValue(result[calcIndex], result[total + calcIndex]);
		}

		return result;
	}

	@Override
	public String toString() {
		return "Value";
	}

	public final ExprVertex getExpr() {
		return expr;
	}

	public final ExprVertex getInfl() {
		return infl;
	}

}
