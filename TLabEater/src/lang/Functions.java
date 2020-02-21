/**
 * 
 */
package lang;

import static lang.InfilicityCounter.correctValue;
import static lang.InfilicityCounter.rountToFirstSignificantDigit;
import static lang.LabLang.compilationError;
import static lang.LangStorage.getVariable;
import static lang.LangStorage.hasVariable;
import static lang.tree.vertices.ExprVertex.createConstant;
import static lang.tree.vertices.ExprVertex.createFunction;
import static lang.tree.vertices.ExprVertex.createOperation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;

import javax.imageio.ImageIO;

import bigMath.BigDecimalMath;
import bigMath.DefaultBigDecimalMath;
import lang.LangStorage.Function;
import lang.LangStorage.Variable;
import lang.tree.vertices.ExprVertex;

import com.roveramd.RoverCSV;

/**
 * @author timat
 * @author tim
 */
public class Functions {

	// Functions
	static final Function exp = new Function(1) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			return DefaultBigDecimalMath.pow(BigDecimalMath.e(MathContext.DECIMAL128), args[0].process()[0]);
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			ExprVertex arg = (ExprVertex) v.getChildren()[0];
			return createOperation('*', arg.dif(var), v.copy());
		}

	};
	
	static final Function loadCsv = new Function(-1) {
		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			ExprVertex vtxArg = args[0];
			String essentialFileName = vtxArg.getString();
			if (essentialFileName == null) {
				LabLang.compilationError("It is required that you specify absolute or relative path to the CSV file that you want to read as the first argument to loadCsv()");
				return null;
			}
			String delimiter = ",";
			if (args.length > 1 && args[1].getString() != null)
				delimiter = args[1].getString();
			try {
				File file = new File(LabLang.homeDirectory, essentialFileName);
				essentialFileName = file.getAbsolutePath();
				RoverCSV csvRdr = new RoverCSV(essentialFileName, delimiter);
				List<Map<String, String>> allRows = csvRdr.getAll();
				Iterator<Map<String, String>> listIter = allRows.iterator();
				Map<String, Variable> variablesAvailable = new HashMap<>();
				int idx = 0;
				while (listIter.hasNext()) {
					Map<String, String> row = listIter.next();
					Set<String> keysSet = row.keySet();
					Iterator<String> keysIter = keysSet.iterator();
					while (keysIter.hasNext()) {
						String heading = keysIter.next();
						if (!variablesAvailable.containsKey(heading)) {
							Variable podstVar = LangStorage.getVariable(heading);
							podstVar.setSize(allRows.size());
							variablesAvailable.put(heading, podstVar);
						}
						Variable workingVar = variablesAvailable.get(heading);
						String workingValue = row.get(heading);
						if (!workingValue.toLowerCase().startsWith("nan") && !workingValue.isEmpty()) {
							workingVar.values[idx] = new BigDecimal(Double.parseDouble(workingValue));
							workingVar.infls[idx] = BigDecimal.ZERO;
						} else {
							// TODO: NaN отхендлить правильно
						}
						variablesAvailable.remove(heading);
						variablesAvailable.put(heading, workingVar);
					}
					idx += 1;
				}
				Iterator<String> vaIter = variablesAvailable.keySet().iterator();
				while (vaIter.hasNext()) {
					String name = vaIter.next();
					Variable vr = variablesAvailable.get(name);
					LabLang.writeVariable(name, vr);
				}
				
			} catch (IOException err) {
				LabLang.compilationError("Failed to read file '" + essentialFileName + "'. ");
			} catch (RoverCSV.CSVUnfinishedOrCorruptFileException e) {
				LabLang.compilationError("File '" + essentialFileName + "' is either an incorrectly formatted CSV file or is not a CSV file at all. Don't forget to specify the delimiter as the second argument if the file is not delimited with commas.");
			}
			return null;
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			return null;
		}
	};

	static final Function useExp = new Function(0) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			LangStorage.printExponent = true;
			return null;
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			return null;
		}

	};

	static final Function disableExp = new Function(0) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			LangStorage.printExponent = false;
			return null;
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			return null;
		}

	};

	static final Function diff = new Function(2) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			LabLang.builder.append("df/d" + args[1].getVariable() + " = ")
					.append(args[0].dif(args[1].getVariable()).buildString()).append(";");
			return null;
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			return null;
		}

	};
	
	static final Function makeCsv = new Function(-1) {
		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			if (args.length < 2)
				LabLang.compilationError("Not enough arguments: please specify the path to the CSV file and at least one variable to dump.");
			LabLang.compilationError("OwO not ready yet");
			return null;
		}
		
		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			return null;
		}
	};

	static final Function makeGraph = new Function(-1) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			if (args.length == 0 || (args.length) % 3 != 0) {
				compilationError("Incorrect number of arguments for makeGraph function");
			}
			String img_name = args[args.length - 3].getString();
			String x_axis_name = args[args.length - 2].getString();
			String y_axis_name = args[args.length - 1].getString();
			if (x_axis_name == null) {
				compilationError("No x axis name specifed for makeGraph function!");
			}
			if (y_axis_name == null) {
				compilationError("No y axis name specified for makeGraph function!");
			}
			if (img_name == null) {
				compilationError("No image name specified for makeGraph function!");
			}

			Variable[] xs = new Variable[(args.length - 3) / 3];
			Variable[] ys = new Variable[(args.length - 3) / 3];
			String[] types = new String[xs.length];

			for (int i = 0; i < xs.length; i++) {
				String x_name = args[3 * i].getVariable();
				String y_name = args[3 * i + 1].getVariable();
				String type = args[3 * i + 2].getString();
				if (x_name == null || y_name == null || type == null || !hasVariable(x_name) || !hasVariable(y_name)) {
					compilationError("Incorrect argument for makeGraph function");
				}
				xs[i] = getVariable(x_name);
				ys[i] = getVariable(y_name);
				types[i] = type;
			}

			try {
				BufferedImage img = makeGraph(xs, ys, types, x_axis_name, y_axis_name, img_name, 5);
				if (!img_name.contains(".png")) {
					img_name += ".png";
				}
				ImageIO.write(img, "png", new File(LabLang.homeDirectory, img_name));
				img.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			return null;
		}

	};

	static final Function ln = new Function(1) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			return DefaultBigDecimalMath.log(args[0].process()[0]);
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			ExprVertex arg = (ExprVertex) v.getChildren()[0];
			return createOperation('/', arg.dif(var), arg.copy());
		}

	};

	static final Function sin = new Function(1) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			return DefaultBigDecimalMath.sin(args[0].process()[0]);
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			ExprVertex arg = (ExprVertex) v.getChildren()[0];
			return createOperation('*', createFunction("cos", arg.copy()), arg.dif(var));
		}

	};

	static final Function cos = new Function(1) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			return DefaultBigDecimalMath.cos(args[0].process()[0]);
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			ExprVertex arg = (ExprVertex) v.getChildren()[0];
			return createOperation('*', createConstant(BigDecimal.ONE.negate()), createOperation('*', createFunction("sin", arg.copy()), arg.dif(var)));
		}

	};

	static final Function leastSquares = new Function(2) {

		@Override
		public BigDecimal invoke(ExprVertex[] args) {
			String a_ = args[0].getVariable();
			String b_ = args[1].getVariable();

			if (a_ == null || b_ == null) {
				LabLang.compilationError("Incorrect least squares variable names!");
			}

			BigDecimal[] x = getVariable(a_).values;
			BigDecimal[] y = getVariable(b_).values;

			int sz = x.length;

			if (sz <= 1) {
				LabLang.compilationError("Incorrect least squares variable array size!");
			}

			if (x.length != y.length) {
				LabLang.compilationError("Incorrect least squares variable sizes!");
			}

			BigDecimal av_x = BigDecimal.ZERO;
			for (int i = 0; i < sz; i++) {
				av_x = av_x.add(x[i]);
			}
			av_x = av_x.divide(BigDecimal.valueOf(sz), MathContext.DECIMAL128);

			BigDecimal av_xy = BigDecimal.ZERO;
			for (int i = 0; i < sz; i++) {
				av_xy = av_xy.add(x[i].multiply(y[i]));
			}
			av_xy = av_xy.divide(BigDecimal.valueOf(sz), MathContext.DECIMAL128);

			BigDecimal av_y = BigDecimal.ZERO;
			for (int i = 0; i < sz; i++) {
				av_y = av_y.add(y[i]);
			}
			av_y = av_y.divide(BigDecimal.valueOf(sz), MathContext.DECIMAL128);

			BigDecimal av_x2 = BigDecimal.ZERO;
			for (int i = 0; i < sz; i++) {
				av_x2 = av_x2.add(x[i].multiply(x[i]));
			}
			av_x2 = av_x2.divide(BigDecimal.valueOf(sz), MathContext.DECIMAL128);

			BigDecimal av_y2 = BigDecimal.ZERO;
			for (int i = 0; i < sz; i++) {
				av_y2 = av_y2.add(y[i].multiply(y[i]));
			}
			av_y2 = av_y2.divide(BigDecimal.valueOf(sz), MathContext.DECIMAL128);

			BigDecimal b = (av_xy.subtract(av_x.multiply(av_y))).divide(av_x2.subtract(av_x.multiply(av_x)),
					MathContext.DECIMAL128);
			BigDecimal a = av_y.subtract(b.multiply(av_x));

			BigDecimal db = DefaultBigDecimalMath.pow(av_y2.subtract(av_y.multiply(av_y))
					.divide(av_x2.subtract(av_x.multiply(av_x)), MathContext.DECIMAL128).subtract(b.multiply(b))
					.divide(BigDecimal.valueOf(sz), MathContext.DECIMAL128), BigDecimal.valueOf(0.5));
			BigDecimal da = DefaultBigDecimalMath.pow(av_x2.subtract(av_x.multiply(av_x)), BigDecimal.valueOf(0.5))
					.multiply(db);

			da = rountToFirstSignificantDigit(da);
			db = rountToFirstSignificantDigit(db);

			a = correctValue(a, da);
			b = correctValue(b, db);

			Variable va = getVariable("a");
			Variable vb = getVariable("b");

			va.setSize(1);
			vb.setSize(1);

			va.values[0] = a;
			vb.values[0] = b;

			va.infls[0] = da;
			vb.infls[0] = db;

			LabLang.writeVariable("a", va);
			LabLang.writeVariable("b", vb);

			return null;
		}

		@Override
		public ExprVertex diff(ExprVertex v, String var) {
			return null;
		}

	};

	private static int width;
	private static int height;

	private static int x(double xd) {
		return (int) (width * xd);
	}

	private static int y(double yd) {
		return (int) (height * yd);
	}

	private static BufferedImage makeGraph(Variable[] xs, Variable[] ys, String[] types, String xname, String yname,
			String img_name, int total_divs) {
		Font fnt = new Font("Times new roman", Font.ITALIC, 25);
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

		width = 1000;
		height = 1000;

		BigDecimal minx = xs[0].values[0];
		BigDecimal maxx = xs[0].values[0];
		BigDecimal miny = ys[0].values[0];
		BigDecimal maxy = ys[0].values[0];

		for (Variable v : xs) {
			for (BigDecimal n : v.values) {
				minx = minx.min(n);
				maxx = maxx.max(n);
			}
		}
		for (Variable v : ys) {
			for (BigDecimal n : v.values) {
				miny = miny.min(n);
				maxy = maxy.max(n);
			}
		}

		// ERRORS!!!
		if (minx.compareTo(BigDecimal.ZERO) == -1 || minx.compareTo(BigDecimal.ZERO) == 0) {
			// LabLang.compilationError("Graph values lower or equals to zero!");
		}
		if (miny.compareTo(BigDecimal.ZERO) == -1 || miny.compareTo(BigDecimal.ZERO) == 0) {
			// LabLang.compilationError("Graph values lower or equals to zero!");
		}

		BigDecimal dx = InfilicityCounter.rountToFirstSignificantDigit(
				maxx.subtract(minx).divide(BigDecimal.valueOf(total_divs), MathContext.DECIMAL128));
		BigDecimal dy = InfilicityCounter.rountToFirstSignificantDigit(
				maxy.subtract(miny).divide(BigDecimal.valueOf(total_divs), MathContext.DECIMAL128));

		for (int i = -1000;; i++) {
			if ((BigDecimal.valueOf(i + 1).multiply(dx)).compareTo(minx) == 1) {
				minx = BigDecimal.valueOf(i).multiply(dx);
				break;
			}
		}
		for (int i = -1000;; i++) {
			if ((BigDecimal.valueOf(i + 1).multiply(dy)).compareTo(miny) == 1) {
				miny = BigDecimal.valueOf(i).multiply(dy);
				break;
			}
		}
		for (int i = -1000;; i++) {
			BigDecimal v = null;
			if ((v = BigDecimal.valueOf(i).multiply(dx)).compareTo(maxx) == 1) {
				maxx = v;
				break;
			}
		}
		for (int i = -1000;; i++) {
			BigDecimal v = null;
			if ((v = BigDecimal.valueOf(i).multiply(dy)).compareTo(maxy) == 1) {
				maxy = v;
				break;
			}
		}

		// Create image
		BufferedImage gr = new BufferedImage((int) (width + fnt.getStringBounds(xname, frc).getWidth()), height,
				BufferedImage.TYPE_BYTE_INDEXED);
		Graphics2D g = gr.createGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHints(rh);
		g.setFont(fnt);
		g.setStroke(new BasicStroke(3));

		// Clear
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, gr.getWidth(), gr.getHeight());
		g.setColor(Color.black);

		// Marks on axis
		BigDecimal v = dx;
		for (int j = -1000;; j++) {
			v = dx.multiply(BigDecimal.valueOf(j));
			if (v.compareTo(minx) == 0 || v.compareTo(minx) == 1) {

				int xc = x(v.subtract(minx).divide(maxx.subtract(minx), MathContext.DECIMAL128)
						.multiply(BigDecimal.valueOf(0.8)).add(BigDecimal.valueOf(0.1)).doubleValue());
				g.drawLine(xc, y(0.94), xc, y(0.96));
				g.drawString(v.stripTrailingZeros().toPlainString(), xc + x(0.005), y(0.973));

				g.setColor(Color.LIGHT_GRAY);

				g.drawLine(xc, y(0.95), xc, y(0));

				g.setColor(Color.black);
			}
			v = dx.multiply(BigDecimal.valueOf(j + 1));
			if (maxx.compareTo(v) == -1) {
				break;
			}
		}

		for (int j = -1000;; j++) {
			v = dy.multiply(BigDecimal.valueOf(j));
			if (v.compareTo(miny) == 0 || v.compareTo(miny) == 1) {
				int yc = y(1 - v.subtract(miny).divide(maxy.subtract(miny), MathContext.DECIMAL128)
						.multiply(BigDecimal.valueOf(0.8)).add(BigDecimal.valueOf(0.1)).doubleValue());

				g.setColor(Color.black);
				g.drawLine(x(0.04), yc, x(0.06), yc);
				g.drawString(v.stripTrailingZeros().toPlainString(), x(0.053), yc - y(0.005));

				g.setColor(Color.LIGHT_GRAY);

				g.drawLine(x(0.05), yc, x(1), yc);

				g.setColor(Color.black);
			}
			v = dy.multiply(BigDecimal.valueOf(j + 1));
			if (maxy.compareTo(v) == -1) {
				break;
			}
		}

		// Axis
		g.setColor(Color.BLACK);
		g.drawLine(x(0.1), y(0.95), x(1), y(0.95));
		g.drawLine(x(0.05), y(0), x(0.05), y(0.9));

		// a
		g.drawLine(x(1), y(0.95), x(0.97), y(0.94));
		g.drawLine(x(1), y(0.95), x(0.97), y(0.96));
		g.drawString(xname, x(0.97), y(0.98));

		// b
		g.drawLine(x(0.04), y(0.03), x(0.05), y(0));
		g.drawLine(x(0.06), y(0.03), x(0.05), y(0));
		g.drawString(yname, x(0.0645), y(0.03));

		for (int i = 0; i < xs.length; i++) {
			Variable xs_ = xs[i];
			Variable ys_ = ys[i];

			boolean isline = (types[i].contains("line"));
			boolean ispoints = types[i].contains("points");
			boolean drawInfl = (types[i].contains("infl") || types[i].contains("inflilicity")
					|| types[i].contains("inflilicities"));

			Point[] points = new Point[xs_.values.length];
			int[] infls_x = new int[xs_.values.length];
			int[] infls_y = new int[xs_.values.length];

			for (int j = 0; j < xs_.values.length; j++) {
				BigDecimal x = xs_.values[j];
				BigDecimal y = ys_.values[j];

				int xc = x(x.subtract(minx).divide(maxx.subtract(minx), MathContext.DECIMAL128)
						.multiply(BigDecimal.valueOf(0.8)).add(BigDecimal.valueOf(0.1)).doubleValue());
				int yc = y(1 - y.subtract(miny).divide(maxy.subtract(miny), MathContext.DECIMAL128)
						.multiply(BigDecimal.valueOf(0.8)).add(BigDecimal.valueOf(0.1)).doubleValue());

				int infl_y = y(ys_.infls[j].divide(maxy.subtract(miny), MathContext.DECIMAL128)
						.multiply(BigDecimal.valueOf(0.8)).doubleValue());
				int infl_x = x(xs_.infls[j].divide(maxx.subtract(minx), MathContext.DECIMAL128)
						.multiply(BigDecimal.valueOf(0.8)).doubleValue());

				points[j] = new Point(xc, yc);
				infls_y[j] = infl_y;
				infls_x[j] = infl_x;
			}

			Arrays.sort(points, new Comparator<Point>() {
				@Override
				public int compare(Point o1, Point o2) {
					if (o1.x == o2.x) {
						return o1.y - o2.y;
					}
					return o1.x - o2.x;
				}
			});

			if (drawInfl) {
				g.setColor(Color.BLUE);
				for (int j = 0; j < xs_.values.length; j++) {
					// Y
					g.drawLine(points[j].x, points[j].y - infls_y[j], points[j].x, points[j].y + infls_y[j]);
					g.drawLine(points[j].x - x(0.001), points[j].y - infls_y[j], points[j].x + x(0.001),
							points[j].y - infls_y[j]);
					g.drawLine(points[j].x - x(0.001), points[j].y + infls_y[j], points[j].x + x(0.001),
							points[j].y + infls_y[j]);

					// X
					g.drawLine(points[j].x - infls_x[j], points[j].y, points[j].x + infls_x[j], points[j].y);
					g.drawLine(points[j].x - infls_x[j], points[j].y - y(0.001), points[j].x - infls_x[j],
							points[j].y + y(0.001));
					g.drawLine(points[j].x + infls_x[j], points[j].y - y(0.001), points[j].x + infls_x[j],
							points[j].y + y(0.001));
				}
			}
			if (isline)
				for (int j = 0; j < xs_.values.length - 1; j++) {
					g.setColor(Color.darkGray);
					g.drawLine(points[j].x, points[j].y, points[j + 1].x, points[j + 1].y);
				}
			if (ispoints)
				for (Point p : points) {
					int xc = p.x;
					int yc = p.y;
					g.setColor(Color.RED);
					g.drawOval(xc - x(0.005), yc - x(0.005), x(0.01), x(0.01));

				}

		}

		return gr;
	}
}
