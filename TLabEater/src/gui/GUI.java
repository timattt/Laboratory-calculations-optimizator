/**
 * 
 */
package gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.roveramd.RoverGTKRCParser;
import static lang.LabLang.applicationParentDirectory;

/**
 * @author timat
 *
 */
public class GUI {

	private static JFrame frame = new JFrame("Lab calculator");

	private static Editor editor = new Editor();
	private static Welcome welcome = new Welcome();
	private static RoverGTKRCParser configParser = null;

	public static void init() {
		String configPath = applicationParentDirectory + "/lcorc.conf";
		System.err.println("configPath = " + configPath);
		try {
			configParser = new RoverGTKRCParser(configPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int width = (configParser != null && configParser.containsInteger("lco-window-width")) ? configParser.getInteger("lco-window-width") : 800;
		int height = (configParser != null && configParser.containsInteger("lco-window-height")) ? configParser.getInteger("lco-window-height") : 800;
		
		frame.setTitle("Laboratory calculator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);

		frame.setContentPane(welcome);
		initMenu();

		frame.setVisible(true);
		
		refresh();
	}

	public static final String extension = "lco";
	public static final String ext_dot = "." + extension;
	
	public static void createSourceFile() {
		File src_file = selectFile("Create file for source code", extension);

		if (src_file == null) {
			return;
		}
		if (!src_file.getName().contains(ext_dot)) {
			src_file = new File(src_file.getPath() + ext_dot);
		}

		try {
			src_file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		editor.loadNecessarySettings(configParser);
		editor.setSrcFile(src_file);

		frame.setContentPane(editor);
		refresh();
	}

	public static void openSourceFile() {
		File src_file = selectFile("Select file with calculator source code", extension);

		if (src_file == null) {
			return;
		}

		if (!src_file.exists()) {
			return;
		}

		editor.loadNecessarySettings(configParser);
		editor.setSrcFile(src_file);

		frame.setContentPane(editor);
		refresh();
	}

	public static void returnToMenu() {
		frame.setContentPane(welcome);
		editor.flushFiles();
		refresh();
	}

	public static void saveSourceFile() {
		editor.save();
	}

	private static void initMenu() {
		JMenuBar menu_bar = new JMenuBar();

		JMenu file = new JMenu("File");

		JMenuItem open = new JMenuItem("Open source file");
		JMenuItem save = new JMenuItem("Save source file");
		JMenuItem create = new JMenuItem("Create new source file");
		JMenuItem toWelc = new JMenuItem("Back to start page");

		open.addActionListener((event) -> {
			GUI.openSourceFile();
		});
		save.addActionListener((event) -> {
			GUI.saveSourceFile();
		});
		create.addActionListener((event) -> {
			GUI.createSourceFile();
		});
		toWelc.addActionListener((event) -> {
			GUI.returnToMenu();
		});

		file.add(save);
		file.add(open);
		file.add(create);
		file.add(toWelc);
		
		menu_bar.add(file);

		frame.setJMenuBar(menu_bar);
	}

	public static void refresh() {
		SwingUtilities.updateComponentTreeUI(frame);
		welcome.resetBackground();
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static File selectFile(String title, String... extension) {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		jfc.setDialogTitle(title);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(title, extension);
		jfc.addChoosableFileFilter(filter);

		int returnValue = jfc.showOpenDialog(frame);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			return selectedFile;
		}

		return null;
	}
}
