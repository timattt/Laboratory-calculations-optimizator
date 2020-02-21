/**
 * 
 */
package lang;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

import grammar.parser.GrammarTreeCreator;
import grammar.parser.GrammarTreeCreator.Nonterminal;
import grammar.tokenizer.Token;
import grammar.tokenizer.Tokenizer;
import lang.LangStorage.Variable;
import lang.tree.vertices.HeadVertex;
import lang.tree.vertices.LVertex;

/**
 * @author timat
 *
 */
public class LabLang {

	/**
	 * String builder instance when language is processing any vertex can write any
	 * information here. Later it will be written into result string.
	 */
	public static StringBuilder builder;
	public static File homeDirectory;

	public static String readFile(File f) throws FileNotFoundException {
		StringBuilder b = new StringBuilder();

		Scanner sc = new Scanner(f);

		while (sc.hasNext()) {
			b.append(sc.nextLine()).append('\n');
		}

		sc.close();

		return b.toString();
	}

	/**
	 * Prints variable to compiled code
	 * @param name
	 * @param var
	 */
	public static void writeVariable(String name, Variable var) {
		int total_values = var.values.length;
		LabLang.builder.append(name + " = ");
		if (total_values > 1) {
			LabLang.builder.append("[");
		}
		for (int i = 0; i < total_values; i++) {
			LabLang.builder.append(
					LangStorage.printExponent ? var.values[i].toEngineeringString() : var.values[i].toPlainString());
			if (!var.infls[i].equals(BigDecimal.ZERO)) {
				LabLang.builder.append(" # ").append(
						LangStorage.printExponent ? var.infls[i].toEngineeringString() : var.infls[i].toPlainString());
			}
			if (i + 1 < total_values) {
				LabLang.builder.append(", ");
			}
		}

		if (total_values > 1) {
			LabLang.builder.append("]");
		}
		LabLang.builder.append(";\n");
	}

	/**
	 * Writes this string into given file
	 * @param f
	 * @param str
	 * @throws IOException
	 */
	public static void writeFile(File f, String str) throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter(f));

		wr.write(str);

		wr.close();
	}

	public static void syntaxError(String mes, int line_num) {
		throw new RuntimeException("Syntax error in line " + line_num + ". " + mes);
	}

	public static void compilationError(String mes) {
		throw new RuntimeException("Compilation error. " + mes);
	}

	/**
	 * Parses laboratory language file. Returns result into string.
	 * 
	 * @param in
	 *            - source code.
	 * @return - compiled code.
	 * @throws IOException
	 */
	public static String parseLabLang(String in, File dir) throws IOException {
		homeDirectory = dir;

		// tokenizing text
		Token[] tokens = Tokenizer.tokenize(in);

		// initializing grammar creator
		GrammarTreeCreator creator = new GrammarTreeCreator(tokens);

		// parsing code
		creator.G();

		// head of the nonterminals tree
		Nonterminal head = creator.getHead();

		// creating lang tree
		LVertex lang = new HeadVertex(head);

		// Debug
		/*
		File dotfile = new File("dotsrc");
		DotWriter.saveNonterminalAsDotFile(head, dotfile);
		DotWriter.createDotImage("dotsrc", "img_gram");
		DotWriter.saveLVertexAsDotFile(lang, dotfile);
		DotWriter.createDotImage("dotsrc", "img_lang");
		 */

		builder = new StringBuilder();

		LangStorage.init();

		lang.process();

		return builder.toString();
	}

}
