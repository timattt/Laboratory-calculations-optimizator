/**
 * 
 */
package lang;

import static lang.LangStorage.getVariableInfl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

import bigMath.DefaultBigDecimalMath;
import lang.tree.vertices.ExprVertex;

/**
 * @author timat
 *
 */
public class InfilicityCounter {

	/**
	 * This function calculates infelicity for the equation. It uses formula
	 * sigma(f) = sqrt ( sum( (sigma(xi) * df/dxi)^2 ) )
	 * 
	 * @param f
	 *            - function to count infelicity for.
	 * @return value of infelicity.
	 */
	public static BigDecimal calcInfl(ExprVertex f) {
		ArrayList<String> vars = new ArrayList<String>();
		f.getAllVariables(vars);
		ExprVertex[] derivative = new ExprVertex[vars.size()];

		BigDecimal result = BigDecimal.ZERO;

		for (int i = 0; i < vars.size(); i++) {
			derivative[i] = f.dif(vars.get(i));
			BigDecimal a = derivative[i].process()[0].multiply(getVariableInfl(vars.get(i)));
			result = result.add(a.multiply(a));
		}

		//System.out.println(result.toPlainString());
		result = DefaultBigDecimalMath.pow(result, BigDecimal.valueOf(0.5));
		//return result;
		return rountToFirstSignificantDigit(result).stripTrailingZeros();
	}

	/**
	 * Rounds given value by its infelicity
	 * 
	 * @param val
	 *            - number to round.
	 * @param infl
	 *            - its infelicity
	 * @return
	 */
	public static BigDecimal correctValue(BigDecimal val, BigDecimal infl) {
		if (infl.equals(BigDecimal.ZERO)) {
			return val;
		}

		infl = infl.add(BigDecimal.valueOf(0.0));
		BigDecimal mul = BigDecimal.TEN.pow(infl.toPlainString().length() - 2);

		if (infl.compareTo(BigDecimal.ONE) == 1 || infl.compareTo(BigDecimal.ONE) == 0) {
			mul = BigDecimal.ONE.divide(mul).multiply(BigDecimal.TEN);
		}

		val = val.multiply(mul).add(BigDecimal.valueOf(0.0));
		val = val.round(new MathContext(val.toPlainString().indexOf("."))).add(BigDecimal.valueOf(0.0));
		return new BigDecimal(val.toPlainString().substring(0, val.toPlainString().indexOf("."))).divide(mul);
	}

	/**
	 * Rounds number to its first significant digit. Example: 0.0003456 -> 0.0003,
	 * 15355.785 - > 20000
	 * 
	 * @param v
	 *            - number to round.
	 * @return - rounded number.
	 */
	public static BigDecimal rountToFirstSignificantDigit(BigDecimal v) {
		v = v.add(BigDecimal.valueOf(0.0));
		String str = v.toPlainString();
		if (v.compareTo(BigDecimal.ZERO) == 0) {
			return v;
		}
		if (v.abs().compareTo(BigDecimal.ONE) == -1) {
			for (int i = 0; i < str.length(); i++) {
				char s = str.charAt(i);
				if (s != '-' && s != '0' && s != '.') {
					str = str.substring(0, i + 1);
					return new BigDecimal(str);
				}
			}
		} else {
			char[] arr = str.toCharArray();
			for (int i = 1; i < arr.length; i++) {
				char s = arr[i];
				if (s == '.') {
					return new BigDecimal(new String(arr).substring(0, i));
				} else {
					arr[i] = '0';
				}
			}
		}

		return v;
	}

}
