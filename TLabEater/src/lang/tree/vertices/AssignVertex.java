/**
 * 
 */
package lang.tree.vertices;

import java.math.BigDecimal;
import java.util.LinkedList;

import grammar.parser.GrammarTreeCreator.Nonterminal;
import lang.LabLang;
import lang.LangStorage;
import lang.LangStorage.Variable;

/**
 * @author timat
 *
 */
public class AssignVertex extends LVertex {

	// Variable to assign
	private final String variable_name;

	/**
	 * @param id
	 * @param children_count
	 */
	public AssignVertex(Nonterminal ntrm) {
		super(3, ntrm.getChildren().size());
		variable_name = ntrm.getFirstToken().getValue();
		for (int i = 0; i < children.length; i++) {
			children[i] = new ValueVertex(ntrm.getChildren().get(i));
		}
	}

	@Override
	public String toString() {
		return "Assign " + variable_name;
	}

	@Override
	public BigDecimal[] process() {
		// Process assign and calculates infelicity
		
		// Variable to assign
		Variable var = LangStorage.getVariable(variable_name);

		// Quantity of numbers that will be assign to this var
		int total_values = 0;

		// Values, infelicities and expressions for each cell of the var array.
		LinkedList<BigDecimal> values = new LinkedList<BigDecimal>();
		LinkedList<BigDecimal> infls = new LinkedList<BigDecimal>();
		LinkedList<ExprVertex> exprs = new LinkedList<ExprVertex>();

		// Calculate all expressions
		for (int j = 0; j < children.length; j++) {
			ValueVertex val = (ValueVertex) children[j];
			BigDecimal[] calc = val.process();

			boolean hasVariables = val.getExpr().hasVariables();

			for (int i = 0; i < calc.length / 2; i++) {
				values.add(calc[i]);
				infls.add(calc[calc.length / 2 + i]);
				if (hasVariables) {
					exprs.add(val.getExpr());
				} else {
					exprs.add(null);
				}
			}
			total_values += calc.length / 2;
		}

		// Resize var array
		var.setSize(total_values);

		// Write to compiled file
		LabLang.builder.append(variable_name + " = ");
		if (total_values > 1) {
			LabLang.builder.append("[");
		}
		for (int i = 0; i < total_values; i++) {
			var.values[i] = values.removeFirst();
			var.infls[i] = infls.removeFirst();

			ExprVertex expr = exprs.removeFirst();

			if (expr != null && total_values == 1) {
				LabLang.builder.append(expr.buildString()).append(" = ");
			}

			LabLang.builder.append(var.values[i].toPlainString());
			if (!var.infls[i].equals(BigDecimal.ZERO)) {
				LabLang.builder.append(" # ").append(var.infls[i].toPlainString());
			}
			if (i + 1 < total_values) {
				LabLang.builder.append(", ");
			}

		}

		if (total_values > 1) {
			LabLang.builder.append("]");
		}
		LabLang.builder.append(";\n");

		return null;
	}

}
