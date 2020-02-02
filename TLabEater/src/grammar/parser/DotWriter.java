/**
 * 
 */
package grammar.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import grammar.parser.GrammarTreeCreator.Nonterminal;
import lang.tree.vertices.LVertex;

/**
 * @author timat
 *
 */
public class DotWriter {

	/**
	 * Invokes console command to use dot system.
	 * @param fileName - dot src file.
	 * @param imgName - image with graph.
	 */
	public static void createDotImage(String fileName, String imgName) {
		String result = "dot -Tpng " + fileName + " -o " + imgName + ".png";

		try {

			Process p = Runtime.getRuntime().exec(result);

			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			reader.close();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves nonterminals tree as dot src file.
	 * @param head - head of the tree.
	 * @param f - file where code will be saved.
	 * @throws IOException
	 */
	public static void saveNonterminalAsDotFile(Nonterminal head, File f) throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter(f));

		wr.write("digraph grammar {");
		wr.newLine();

		write_verts_nonterminal(wr, head);
		write_edges_nonterminal(wr, head);

		wr.write("}");
		wr.newLine();

		wr.close();
	}

	private static void write_verts_nonterminal(BufferedWriter wr, Nonterminal v) throws IOException {
		wr.write("vert" + v.hashCode() + " [label=" + '"' + v.toString() + '"' + "];");
		wr.newLine();

		for (Nonterminal u : v.getChildren()) {
			write_verts_nonterminal(wr, u);
		}
	}

	private static void write_edges_nonterminal(BufferedWriter wr, Nonterminal v) throws IOException {
		for (Nonterminal u : v.getChildren()) {
			wr.write("vert" + v.hashCode() + " -> vert" + u.hashCode() + ";");
			wr.newLine();
			write_edges_nonterminal(wr, u);
		}
	}

	/**
	 * Saves language tree as dot src file.
	 * @param head - head of the tree.
	 * @param f - file where code will be saved.
	 * @throws IOException
	 */
	public static void saveLVertexAsDotFile(LVertex head, File f) throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter(f));

		wr.write("digraph grammar {");
		wr.newLine();

		write_verts_lvertex(wr, head);
		write_edges_lvertex(wr, head);

		wr.write("}");
		wr.newLine();

		wr.close();
	}

	private static void write_verts_lvertex(BufferedWriter wr, LVertex v) throws IOException {
		wr.write("vert" + v.hashCode() + " [label=" + '"' + v.toString() + '"' + "];");
		wr.newLine();

		for (LVertex u : v.getChildren()) {
			write_verts_lvertex(wr, u);
		}
	}

	private static void write_edges_lvertex(BufferedWriter wr, LVertex v) throws IOException {
		for (LVertex u : v.getChildren()) {

			wr.write("vert" + v.hashCode() + " -> vert" + u.hashCode() + ";");
			wr.newLine();
			write_edges_lvertex(wr, u);
		}
	}

}
