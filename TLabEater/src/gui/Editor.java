/**
 * 
 */
package gui;

import java.awt.BorderLayout;
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

import lang.LabLang;

/**
 * @author timat
 *
 */
@SuppressWarnings("serial")
public class Editor extends JPanel implements KeyListener {

	// Writing areas
	private JEditorPane src_text_pane;
	private JEditorPane comp_text_pane;
	private JEditorPane info_text_pane;

	// Font
	private final Font font = new Font("Times new roman", Font.PLAIN, 20);

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

		JTextField tp = new JTextField("Source code:");
		tp.setEditable(false);
		tp.setFocusable(false);
		tp.setBackground(null);
		add(tp, con);

		tp = new JTextField("Compiled code:");
		tp.setEditable(false);
		tp.setFocusable(false);
		tp.setBackground(null);
		con.gridx = 3;
		add(tp, con);

		// con.insets.bottom = con.insets.top = con.insets.left = con.insets.right = 0;
		con.fill = GridBagConstraints.BOTH;
		con.weightx = 0.5;
		con.weighty = 1;
		con.gridy = 1;
		con.gridx = 0;
		add(new JScrollPane(src_text_pane = new JEditorPane()), con);

		con.gridx = 3;
		add(new JScrollPane(comp_text_pane = new JEditorPane()), con);

		con.gridy = 4;
		con.gridx = 3;
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
		});
		p.add(compile, BorderLayout.NORTH);
		p.add(autoCompile, BorderLayout.SOUTH);
		con.gridy = 3;
		con.gridx = 0;
		con.weightx = 0.5;
		con.weighty = 0;
		add(p, con);

		// INFO
		p = new JPanel();
		p.setLayout(new BorderLayout());

		p.add(info_text_pane = new JEditorPane(), BorderLayout.SOUTH);
		tp = new JTextField("Compilation status:");
		tp.setEditable(false);
		tp.setFocusable(false);
		tp.setBackground(null);
		p.add(tp, BorderLayout.NORTH);

		con.gridx = 3;
		con.gridy = 3;
		add(p, con);

		comp_text_pane.setEditable(false);
		src_text_pane.addKeyListener(this);
		info_text_pane.setEditable(false);
		info_text_pane.setBorder(BorderFactory.createEtchedBorder());

		src_text_pane.setFont(font);
		comp_text_pane.setFont(font);
		info_text_pane.setFont(font);
	}

	public void setSrcFile(File src) {
		this.src_file = src;
		this.comp_file = new File(src.getParent(), src.getName().replace(GUI.ext_dot, "") + "_compiled" + GUI.ext_dot);

		try {
			this.src_text_pane.setText(LabLang.readFile(src));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void compile() {
		String src_code = src_text_pane.getText();
		try {
			String comp_code = LabLang.parseLabLang(src_code, src_file.getParentFile());
			comp_text_pane.setText(comp_code);
			info_text_pane.setText("No exception...");
			save();
		} catch (Exception ex) {
			if (ex.getMessage().length() == 0) {
				info_text_pane.setText("Unresolved compilation error! Maybe bug!");
				ex.printStackTrace();
			} else {
				info_text_pane.setText(ex.getMessage());
			}
		}
	}

	public void save() {
		try {
			if (src_file != null) {
				LabLang.writeFile(comp_file, this.comp_text_pane.getText());
				LabLang.writeFile(src_file, this.src_text_pane.getText());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void flushFiles() {
		src_file = null;
		comp_file = null;
		src_text_pane.setText("");
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
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (autoCompile) {
			compile();
		}
	}

}
