/**
 * 
 */
package lang;

import static lang.Functions.cos;
import static lang.Functions.leastSquares;
import static lang.Functions.makeGraph;
import static lang.Functions.sin;
import static lang.Functions.ln;
import static lang.Functions.diff;
import static lang.Functions.useExp;
import static lang.Functions.disableExp;
import static lang.Functions.loadCsv;
import static lang.Functions.exp;

import java.math.BigDecimal;
import java.util.TreeMap;

import lang.tree.vertices.ExprVertex;

/**
 * @author timat
 *
 */
public class LangStorage {

	/**
	 * When calculations are made, this number determines which item from each
	 * variable will be used. Example: calcIndex = 1; a = [1, 2, 3, 4]; x = a + 3;
	 * if we use function getVariableValue("a") while calculating then if will
	 * return 2.
	 */
	public static int calcIndex = 0;
	public static boolean printExponent = false;

	private static final TreeMap<String, Variable> vars = new TreeMap<String, Variable>();

	private static final TreeMap<String, Function> funcs = new TreeMap<String, Function>();

	/**
	 * Initializes functions and variables storage.
	 */
	public static void init() {
		vars.clear();
		funcs.clear();

		funcs.put("sin", sin);
		funcs.put("cos", cos);
		funcs.put("leastSquares", leastSquares);
		funcs.put("makeGraph", makeGraph);
		funcs.put("ln", ln);
		funcs.put("diff", diff);
		funcs.put("useExp", useExp);
		funcs.put("disableExp", disableExp);
		funcs.put("loadCsv", loadCsv);
		funcs.put("exp", exp);
	}

	/**
	 * Gives value from named variable with index [calcIndex % vr.values.length].
	 * 
	 * @param varName
	 *            - name of the variable
	 * @return - value.
	 */
	public static BigDecimal getVariableValue(String varName) {
		if (!vars.containsKey(varName)) {
			LabLang.compilationError("No such variable " + varName);
		}
		Variable vr = vars.get(varName);
		return vr.values[calcIndex % vr.values.length];
	}

	public static boolean hasVariable(String name) {
		return vars.containsKey(name);
	}

	/**
	 * Gives variable instance. If there is no such variable so it will be created.
	 * 
	 * @param name
	 *            - name
	 * @return - instance
	 */
	public static Variable getVariable(String name) {
		if (vars.containsKey(name)) {
			return vars.get(name);
		} else {
			Variable vr = new Variable();
			vars.put(name, vr);
			return vr;
		}
	}

	/**
	 * Gives instance of the function.
	 * 
	 * @param name
	 *            - requested function name.
	 * @return - instance
	 */
	public static Function getFunction(String name) {
		if (!funcs.containsKey(name)) {
			LabLang.compilationError("No such function " + name);
		}
		return funcs.get(name);
	}

	/**
	 * Gives variable infl. Works like getVariableValue() function.
	 * 
	 * @param varName
	 *            - name
	 * @return - infl
	 */
	public static BigDecimal getVariableInfl(String varName) {
		if (!vars.containsKey(varName)) {
			LabLang.compilationError("No such variable " + varName);
		}
		Variable vr = vars.get(varName);
		return vr.infls[calcIndex % vr.infls.length];
	}

	/**
	 * Invokes function with given parameters and return some value.
	 * 
	 * @param funcName
	 *            - name of function to invoke.
	 * @param args
	 *            - expressions for the arguments.
	 * @return - function return value.
	 */
	public static BigDecimal invokeFunction(String funcName, ExprVertex[] args) {
		if (!funcs.containsKey(funcName)) {
			LabLang.compilationError("No such function " + funcName);
		}
		Function f = funcs.get(funcName);
		if (args.length != f.total_args && f.total_args != -1) {
			LabLang.compilationError("Incorrect number of " + funcName + " function arguments");
		}
		return f.invoke(args);
	}

	public static class Variable {
		public BigDecimal[] values;
		public BigDecimal[] infls;

		public void setSize(int q) {
			values = new BigDecimal[q];
			infls = new BigDecimal[q];
		}
	}

	public abstract static class Function {
		public final int total_args;

		protected Function(int total_args) {
			this.total_args = total_args;
		}

		public abstract BigDecimal invoke(ExprVertex[] args);

		public abstract ExprVertex diff(ExprVertex v, String var);
	}

	/**
	 * Invokes when new compilation begins.
	 */
	public static void preInit() {
		printExponent = false;
	}
}
