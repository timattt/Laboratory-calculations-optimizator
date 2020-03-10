/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import com.roveramd.RoverLabLangSyntaxHighlighter;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import lang.LabLang;
import com.roveramd.RoverGTKRCParser;

/**
 * @author timat
 *
 */
@SuppressWarnings("serial")
public class Editor extends JPanel implements KeyListener {

	// Writing areas
	private JEditorPane src_text_pane;
	private JEditorPane comp_text_pane;
	private JTextPane info_text_pane;
	private boolean justCompiled = false;
	private boolean enableSyntaxHighlighting = false;
	private String[] colorPalette = { "#C1C3CA", "#7CDC59", "#DCB559", "#5C6E9E", "#597CDC", "#754B4B", "#FFFFFF",
			"#000000" };
	private RoverLabLangSyntaxHighlighter privateConverter = null;

	// Font
	private Font font = new Font("Times new roman", Font.PLAIN, 20);

	// Files
	private File src_file;
	private File comp_file;

	private boolean autoCompile = false;

	public Editor() {
		setLayout(new GridBagLayout());

		GridBagConstraints con = new GridBagConstraints();

		con.gridx = 0;
		con.gridy = 0;
		con.insets.bottom = con.insets.top = 3;
		con.insets.left = con.insets.right = 20;
		con.fill = GridBagConstraints.HORIZONTAL;
		con.ipadx = 10;
		JTextField tp = new JTextField("Source code:");
		tp.setEditable(false);
		tp.setFocusable(false);
		tp.setBackground(null);
		add(tp, con);// !!!!!!!!!!!

		tp = new JTextField("Compiled code:");
		tp.setEditable(false);
		tp.setFocusable(false);
		tp.setBackground(null);
		con.gridx = 1;
		add(tp, con);// !!!!!!!!!!!

		con.fill = GridBagConstraints.BOTH;
		con.weightx = 0.5;
		con.weighty = 1;
		con.gridy = 1;
		con.gridx = 0;
		add(new JScrollPane(src_text_pane = new JEditorPane()), con);// !!!!!!!!!!!

		con.gridx = 1;
		add(new JScrollPane(comp_text_pane = new JEditorPane()), con);// !!!!!!!!!!!

		con.gridy = 2;
		con.weightx = 0.5;
		con.weighty = 0;
		con.fill = GridBagConstraints.HORIZONTAL;

		// BUTTONS
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		JButton compile = new JButton("Compile");
		compile.addActionListener((event) -> {
			compile();
		});
		JCheckBox autoCompile = new JCheckBox("Autocompile");
		autoCompile.addActionListener((event) -> {
			this.autoCompile = !this.autoCompile;
			justCompiled = false;
			if (privateConverter != null) {
				src_text_pane.setContentType("text/plain");
				src_text_pane.setText(src_text_pane.getText());
			}

		});
		p.add(compile, BorderLayout.NORTH);
		p.add(autoCompile, BorderLayout.SOUTH);
		con.gridx = 0;
		con.weightx = 0.5;
		con.weighty = 0;
		add(p, con);// !!!!!!!!!!!

		// INFO
		p = new JPanel();
		p.setLayout(new BorderLayout());

		p.add(info_text_pane = new JTextPane(), BorderLayout.SOUTH);
		tp = new JTextField("Compilation status:");
		tp.setEditable(false);
		tp.setFocusable(false);
		tp.setBackground(null);
		p.add(tp, BorderLayout.NORTH);

		con.gridx = 1;
		con.weightx = 0.5;
		add(p, con);// !!!!!!!!!!!

		comp_text_pane.setEditable(false);
		src_text_pane.addKeyListener(this);
		info_text_pane.setEditable(false);
		info_text_pane.setBorder(BorderFactory.createEtchedBorder());

		src_text_pane.setFont(font);
		comp_text_pane.setFont(font);
		info_text_pane.setFont(font);

		info_text_pane.setText("No exception...");
	}

	public void loadNecessarySettings(RoverGTKRCParser configParser) {
		if (configParser == null)
			return;
		enableSyntaxHighlighting = (configParser.containsBoolean("lco-hl-feature"))
				? configParser.getBoolean("lco-hl-feature")
				: true;
		String fontFamily = "Times New Roman";
		int fontSize = 20;
		if (configParser.containsInteger("lco-font-size"))
			fontSize = configParser.getInteger("lco-font-size");
		if (configParser.containsString("lcon-font-family"))
			fontFamily = configParser.getString("lco-font-family");
		font = new Font(fontFamily, Font.PLAIN, fontSize);
		src_text_pane.setFont(font);
		comp_text_pane.setFont(font);
		info_text_pane.setFont(font);
		for (int i = 0; i < 8; i++) {
			String vlName = "lco-hl-color" + i;
			if (configParser.containsString(vlName))
				colorPalette[i] = configParser.getString(vlName);
		}
	}

	public void setSrcFile(File src) {
		this.src_file = src;
		this.comp_file = new File(src.getParent(), src.getName().replace(GUI.ext_dot, "") + "_compiled" + GUI.ext_dot);
		try {
			String code = LabLang.readFile(src);
			src_text_pane.setContentType("text/plain");
			if (code.length() == 0)
				code = "// Simple example\n$ a = 10 # 2;\n$ b = 20 # 3;\n$ c = a * b;\n";
			src_text_pane.setText(code);
			compile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void compile() {
		if (justCompiled)
			return;
		String src_code = src_text_pane.getText();
		if (privateConverter == null) {
			privateConverter = new RoverLabLangSyntaxHighlighter(src_code, font.getFamily(), font.getSize());
			privateConverter.setColorScheme(colorPalette[0], colorPalette[1], colorPalette[2], colorPalette[3],
					colorPalette[4], colorPalette[5], colorPalette[6], colorPalette[7]);
		} else
			privateConverter.setText(src_code);
		if (!autoCompile && enableSyntaxHighlighting) {
			src_text_pane.setContentType("text/html");
			src_text_pane.setText(privateConverter.toHTML());
			justCompiled = true;
		}
		try {
			String comp_code = LabLang.parseLabLang(src_code.trim(), src_file.getParentFile());
			comp_text_pane.setText(comp_code);
			info_text_pane.setText("No exception...");
			save();
		} catch (Exception ex) {
			System.err.println(ex);
			if (ex.getMessage() == null || ex.getMessage().length() == 0) {
				info_text_pane.setText("Unresolved compilation error! Maybe bug!");
				ex.printStackTrace();
			} else
				info_text_pane.setText(ex.getMessage());
		}
	}

	public void save() {
		try {
			if (src_file != null) {
				LabLang.writeFile(comp_file, this.comp_text_pane.getText());
				String toActuallyWrite = src_text_pane.getText();
				if (privateConverter != null)
					toActuallyWrite = privateConverter.toString();
				LabLang.writeFile(src_file, toActuallyWrite);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void flushFiles() {
		src_file = null;
		comp_file = null;
		src_text_pane.setText("");
		privateConverter = null;
		comp_text_pane.setText("");
		info_text_pane.setText("");
	}

	public final File getSrc_file() {
		return src_file;
	}

	public final void setSrc_file(File src_file) {
		this.src_file = src_file;
	}

	public final File getComp_file() {
		return comp_file;
	}

	public final void setComp_file(File comp_file) {
		this.comp_file = comp_file;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (justCompiled) {
			justCompiled = false;
			src_text_pane.setContentType("text/plain");
			if (privateConverter != null)
				src_text_pane.setText(privateConverter.toString());
			keyPressed(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (autoCompile) {
			compile();
		}
	}

}
