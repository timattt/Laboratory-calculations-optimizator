/**
 * 
 */
package lang.tree.vertices;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

import bigMath.DefaultBigDecimalMath;
import grammar.parser.GrammarTreeCreator.Nonterminal;
import grammar.tokenizer.TokenType;
import lang.LabLang;
import lang.LangStorage;

/**
 * @author timat
 *
 */
public class ExprVertex extends LVertex {

	// Operator type
	private char operator = 0;

	// Variable
	private String variable = null;

	// Constant
	private BigDecimal constant = null;

	// Function
	private String funct = null;

	// String
	private String string;

	// Expression children
	private ExprVertex[] children = new ExprVertex[0];

	public ExprVertex() {
		super(7, 0);
	}

	/**
	 * @param id
	 * @param children_count
	 */
	public ExprVertex(Nonterminal ntrm) {
		super(7, 0);

		// Skipping unused nonterminals
		while (ntrm.getChildren().size() == 1 && !ntrm.getName().equals("func")) {

			// !
			if (ntrm.getChildren().size() == 1 && ntrm.getTokens().size() == 1
					&& ntrm.getFirstToken().getValue().equals("-")) {
				operator = 'n';
				initChildren(ntrm);
				return;
			}
			// !

			ntrm = ntrm.getFirstChild();
		}

		// !
		if (ntrm.getChildren().size() == 1 && ntrm.getTokens().size() == 1
				&& ntrm.getFirstToken().getValue().equals("-")) {
			operator = 'n';
			initChildren(ntrm);
			return;
		}
		// !

		// If it is a function call
		if (ntrm.getName().equals("func")) {
			funct = ntrm.getFirstToken().getValue();
			initChildren(ntrm);
			return;
		}

		// So there will be 0 or 2 children

		// Operator only when there are two children
		if (ntrm.getChildren().size() == 2) {
			operator = ntrm.getFirstToken().getValue().charAt(0);
			initChildren(ntrm);
			return;
		}

		if (ntrm.getFirstToken().getType() == TokenType.number) {
			constant = ntrm.getFirstToken().toNumber();
		}

		if (ntrm.getFirstToken().getType() == TokenType.name) {
			variable = ntrm.getFirstToken().getValue();
		}

		if (ntrm.getFirstToken().getType() == TokenType.string) {
			string = ntrm.getFirstToken().getValue();
		}
	}

	public static ExprVertex createOperation(char oper, ExprVertex l, ExprVertex r) {
		ExprVertex res = new ExprVertex();
		res.operator = oper;
		res.children = new ExprVertex[] { l, r };
		return res;
	}

	public static ExprVertex createFunction(String name, ExprVertex arg) {
		ExprVertex res = new ExprVertex();
		res.funct = name;
		res.children = new ExprVertex[] { arg };
		return res;
	}

	public static ExprVertex createConstant(BigDecimal val) {
		ExprVertex res = new ExprVertex();
		res.constant = val;
		return res;
	}

	private void initChildren(Nonterminal parent) {
		children = new ExprVertex[parent.getChildren().size()];
		super.children = new LVertex[children.length];
		for (int i = 0; i < children.length; i++) {
			super.children[i] = children[i] = new ExprVertex(parent.getChildren().get(i));
		}
	}

	@Override
	public String toString() {
		String info = "";
		if (operator != 0) {
			info += "operator: " + operator;
		}
		if (string != null) {
			info += "string: " + string;
		}
		if (variable != null) {
			info += "var: " + variable;
		}
		if (constant != null) {
			info += "const: " + constant;
		}
		if (funct != null) {
			info += "funct: " + funct;
		}
		return info;
	}

	@Override
	public BigDecimal[] process() {
		BigDecimal res = null;
		if (string != null) {
			LabLang.compilationError("String in expression!");
		}
		if (operator != 0) {
			res = processOperator();
		}
		if (variable != null) {
			res = processVariable();
		}
		if (constant != null) {
			res = processConst();
		}
		if (funct != null) {
			res = processFunct();
		}
		return new BigDecimal[] { res };
	}

	private ExprVertex l() {
		return children[0];
	}

	private ExprVertex r() {
		return children[1];
	}

	private boolean hasOperatorsIn(ExprVertex v, char... ops) {
		for (int j = 0; j < ops.length; j++) {
			if (v.operator == ops[j]) {
				return true;
			}
		}

		return false;
	}

	public String buildString() {
		String res = "";
		if (operator != 0) {

			// !
			if (operator == 'n') {
				if (l().operator == 0) {
					res += "-" + l().buildString();
				} else {
					res += "-(" + l().buildString() + ")";
				}

				return res;
			}
			// !

			if ((operator == '^' || operator == '*' || operator == '/') && hasOperatorsIn(l(), '+', '-', '/', '*')) {
				res += "(" + l().buildString() + ")";
			} else {
				res += l().buildString();
			}
			res += " " + operator + " ";
			if ((operator == '^' || operator == '*' || operator == '/') && hasOperatorsIn(r(), '+', '-', '/', '*')) {
				res += "(" + r().buildString() + ")";
			} else {
				res += r().buildString();
			}
		}
		if (variable != null) {
			res = variable;
		}
		if (constant != null) {
			res = constant.toString();
		}
		if (funct != null) {
			res = funct + "(";
			for (int i = 0; i < children.length; i++) {
				res += children[i].buildString();
				if (i + 1 < children.length) {
					res += ", ";
				}
			}
			res += ")";
		}
		return res;
	}

	public ExprVertex copy() {
		ExprVertex copy = new ExprVertex();
		copy.children = new ExprVertex[children.length];
		copy.constant = constant;
		copy.funct = funct;
		copy.operator = operator;
		copy.variable = variable;
		for (int i = 0; i < children.length; i++) {
			copy.children[i] = children[i].copy();
		}
		return copy;
	}

	public void getAllVariables(ArrayList<String> vars) {
		if (variable != null) {
			vars.add(variable);
		}
		for (ExprVertex v : children) {
			v.getAllVariables(vars);
		}
	}

	public boolean hasVariables() {
		if (variable != null) {
			return true;
		}
		for (ExprVertex v : children) {
			if (v.hasVariables()) {
				return true;
			}
		}

		return false;
	}

	private BigDecimal processOperator() {
		BigDecimal l = l().process()[0];
		BigDecimal r = null;

		if (children.length > 1) {
			r = r().process()[0];
		}

		switch (operator) {
		case '+':
			return l.add(r);
		case '-':
			return l.subtract(r);
		case '*':
			return l.multiply(r);
		case '/':
			return l.divide(r, MathContext.DECIMAL128);
		case '^':
			return DefaultBigDecimalMath.pow(l, r);

		// !
		case 'n':
			return BigDecimal.ONE.negate().multiply(l);
		// !

		}

		return null;
	}

	private BigDecimal processVariable() {
		return LangStorage.getVariableValue(variable);
	}

	private BigDecimal processConst() {
		return constant;
	}

	public ExprVertex dif(String var) {
		ExprVertex res = null;
		if (operator != 0) {
			switch (operator) {
			case '+':
				return createOperation('+', l().dif(var), r().dif(var));
			case '-':
				return createOperation('-', l().dif(var), r().dif(var));
			case '*':
				return createOperation('+', createOperation('*', l().copy(), r().dif(var)), createOperation('*', r().copy(), l().dif(var)));
			case '/':
				return createOperation('/', createOperation('-', createOperation('*', l().dif(var), r().copy()), createOperation('*', r().dif(var), l().copy())),
						createOperation('^', r().copy(), createConstant(BigDecimal.valueOf(2))));
			case '^':
				return createOperation('*', this.copy(), createOperation('+', createOperation('*', r().dif(var), createFunction("ln", l().copy())),
						createOperation('/', createOperation('*', r().copy(), l().dif(var)), l().copy())));
			// !
			case 'n':
				ExprVertex cp = this.copy();
				cp.children[0] = cp.children[0].dif(var);
				return cp;
			// !

			}
		}
		if (variable != null) {
			if (variable.equals(var)) {
				res = createConstant(BigDecimal.ONE);
			} else {
				res = createConstant(BigDecimal.ZERO);
			}
		}
		if (constant != null) {
			res = createConstant(BigDecimal.ZERO);
		}
		if (funct != null) {
			res = LangStorage.getFunction(funct).diff(this, var);
		}
		return res;
	}

	private BigDecimal processFunct() {
		return LangStorage.invokeFunction(funct, children);
	}

	public final String getString() {
		return string;
	}

	public final String getFunct() {
		return funct;
	}

	public final String getVariable() {
		return variable;
	}

}
