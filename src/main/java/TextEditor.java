import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import jledger.util.ByteArrayDiff;

public class TextEditor extends JFrame implements TreeSelectionListener, ActionListener, MouseListener {
	private final LanguageServer.Workspace root;
	/**
	 * List of open files
	 */
	private final ArrayList<LanguageServer.File> files;
	/**
	 * Encloses everything.
	 */
	private final JPanel outerpanel;
	/**
	 * Provides access to the menu.
	 */
	private final JMenuBar menuBar;
	/**
	 * Provide access to the toolbar
	 */
	private final JToolBar toolBar;
	/**
	 * Provides access to the files in the workspace.
	 */
	private final JTree fileView;
	/**
	 * Provides access to the work area.
	 */
	private final JTabbedPane workArea;

	private final JLabel statusView;
	private final JLabel lineNumberView;

	public TextEditor(LanguageServer.Workspace workspace) {
		super("Simple Text Editor");
		//
		this.root = workspace;
		this.files = new ArrayList<>();
		statusView = new JLabel(" Status");
		lineNumberView = new JLabel("0:0");
		// Construct main components
		this.menuBar = buildMenuBar();
		this.toolBar = buildToolBar();
		this.fileView = buildFileView();
		this.workArea = buildWorkArea();
		JPanel statusBar = buildStatusBar();
		// Configure the menu bar
		setJMenuBar(menuBar);
		// Configure layout
		outerpanel = new JPanel();
		outerpanel.setLayout(new BorderLayout());
		outerpanel.add(toolBar, BorderLayout.PAGE_START);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,fileView,workArea);
		outerpanel.add(splitPane, BorderLayout.CENTER);
		outerpanel.add(statusBar, BorderLayout.SOUTH);
		getContentPane().add(outerpanel);
		// Done
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private JMenuBar buildMenuBar() {
		// This function builds the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(makeMenuItem("New"));
		fileMenu.add(makeMenuItem("Open"));
		fileMenu.addSeparator();
		fileMenu.add(makeMenuItem("Save"));
		fileMenu.add(makeMenuItem("Save As"));
		fileMenu.addSeparator();
		fileMenu.add(makeMenuItem("Exit"));
		menuBar.add(fileMenu);
		// edit menu
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(makeMenuItem("cut"));
		menuBar.add(editMenu);
		return menuBar;
	}

	private JToolBar buildToolBar() {
		// build tool bar
		JToolBar toolBar = new JToolBar("Toolbar");
		toolBar.add(makeToolbarButton("Save file", "New"));
		toolBar.add(makeToolbarButton("Save file", "Open"));
		toolBar.add(makeToolbarButton("Save file", "Save"));
		//
		return toolBar;
	}

	private JTree buildFileView() {
		LanguageServer.Project[] projects = root.list();
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Workspace");
		for(int i=0;i!=projects.length;++i) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode("A project");
			for(LanguageServer.File f : projects[i].list()) {
				node.add(new DefaultMutableTreeNode(new FileInfo(f)));
			}
			top.add(node);
		}
		JTree tree = new JTree(top);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
		tree.addMouseListener(this);
		return tree;
	}

	private JTabbedPane buildWorkArea() {
		JTabbedPane tp = new JTabbedPane();
		for (LanguageServer.File file : files) {
			Buffer buffer = new Buffer(new String(file.read()));
			tp.addTab("file.whiley", buffer);
		}
		return tp;
	}

	private JPanel buildStatusBar() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(statusView, BorderLayout.WEST);
		panel.add(lineNumberView, BorderLayout.EAST);
		panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		return panel;
	}

	private JMenuItem makeMenuItem(String s) {
	    JMenuItem item = new JMenuItem(s);
	    item.setActionCommand(s);
	    item.addActionListener(this);
	    return item;
	}

	private JButton makeToolbarButton(String toolTipText,
			String action) {
		// Create and initialize the button.
		JButton button = new JButton(action);
		button.setToolTipText(toolTipText);
		button.setActionCommand(action);
		button.addActionListener(this);
		return button;
	}

	public void openFile(LanguageServer.File file) {
		Buffer buffer = new Buffer(new String(file.read()));
		workArea.addTab("file.whiley", buffer);
		files.add(file);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Save")) {
			for(int i=0;i!=workArea.getTabCount();++i) {
				LanguageServer.File file = files.get(i);
				Buffer b = (Buffer) workArea.getComponentAt(i);
				if(b.isDirty()) {
					file.write(b.getText().getBytes());
					b.reset();
				}
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int row = fileView.getRowForLocation(e.getX(),e.getY());
		TreePath path = fileView.getPathForLocation(e.getX(), e.getY());
		if(row != -1 && e.getClickCount() == 2 && path.getPathCount() == 3) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			FileInfo info = (FileInfo) node.getUserObject();
			openFile(info.getFile());
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	private class FileInfo {
		private final LanguageServer.File file;

		public FileInfo(LanguageServer.File file) {
			this.file = file;
		}

		public LanguageServer.File getFile() {
			return file;
		}

		@Override
		public String toString() {
			return "a file";
		}
	}

	/**
	 * Represents an open buffer of text which is currently being edited.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Buffer extends JPanel implements KeyListener {
		private final JTextArea buffer;
		private boolean dirty = false;

		public Buffer(String contents) {
			super();
			this.buffer = new JTextArea();
			this.buffer.setText(contents);
			this.buffer.setFont(new Font("Courier New",Font.PLAIN,16));
			this.buffer.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			this.buffer.addKeyListener(this);
			this.setLayout(new BorderLayout());
			this.add(new JScrollPane(buffer), BorderLayout.CENTER);
		}

		public boolean isDirty() {
			return dirty;
		}

		public void reset() {
			dirty = false;
		}

		public String getText() {
			return buffer.getText();
		}

		@Override
		public void keyTyped(KeyEvent e) {
			dirty = true;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			dirty = true;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			dirty = true;
		}
	}


	public static void main(String[] args) {
		new TextEditor(new Workspace());
	}

	private static class Workspace implements LanguageServer.Workspace {
		private Project project = new Project();

		@Override
		public LanguageServer.Project[] list() {
			return new LanguageServer.Project[] {project};
		}

		@Override
		public LanguageServer.Project create(String name) {
			throw new IllegalArgumentException();
		}

		@Override
		public void close() {
			throw new IllegalArgumentException();
		}

		@Override
		public void flush() {
			throw new IllegalArgumentException();
		}

	}

	private static class Project implements LanguageServer.Project {
		private File[] files;

		public Project() {
			String contents = "function f(int x) -> int r:";
			this.files = new File[] { new File(contents.getBytes()) };
		}

		@Override
		public File[] list() {
			return files;
		}

		@Override
		public File create(String name) {
			throw new IllegalArgumentException();
		}

	}

	private static class File implements LanguageServer.File {
		private byte[] data;

		public File(byte[] data) {
			this.data = Arrays.copyOf(data, data.length);
		}

		@Override
		public void delete() {
			throw new IllegalArgumentException();
		}

		@Override
		public void write(byte[] contents) {
			System.out.println("WRITING CONTENTS");
			this.data = contents;
		}

		@Override
		public void write(ByteArrayDiff diff) {
			throw new IllegalArgumentException();
		}

		@Override
		public byte[] read() {
			return data;
		}

		@Override
		public byte[] read(int offset, int length) {
			throw new IllegalArgumentException();
		}

		@Override
		public void read(int srcOffset, byte[] dest, int dstOffset, int length) {
			// TODO Auto-generated method stub

		}

	}

}
