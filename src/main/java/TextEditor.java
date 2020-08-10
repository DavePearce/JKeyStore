import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import jledger.util.ByteArrayDiff;

public class TextEditor extends JFrame implements ActionListener {
	private final LanguageServer.Workspace root;
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
	 * Provides access to the work area.
	 */
	private final JTabbedPane workArea;

	private final JLabel statusView;
	private final JLabel lineNumberView;

	public TextEditor(LanguageServer.Workspace workspace) {
		super("Simple Text Editor");
		//
		this.root = workspace;
		statusView = new JLabel(" Status");
		lineNumberView = new JLabel("0:0");
		// Construct main components
		this.menuBar = buildMenuBar();
		this.toolBar = buildToolBar();
		this.workArea = buildWorkArea();
		JPanel statusBar = buildStatusBar();
		// Configure the menu bar
		setJMenuBar(menuBar);
		// Configure layout
		outerpanel = new JPanel();
		outerpanel.setLayout(new BorderLayout());
		outerpanel.add(toolBar, BorderLayout.PAGE_START);
		outerpanel.add(workArea, BorderLayout.CENTER);
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

	private JTabbedPane buildWorkArea() {
		JTabbedPane tp = new JTabbedPane();
		for (String file : root.list()) {
			Buffer buffer = new Buffer(new String(root.read(file)));
			tp.addTab(file, buffer);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Save")) {
			for(int i=0;i!=workArea.getTabCount();++i) {
				String file = workArea.getTitleAt(i);
				Buffer b = (Buffer) workArea.getComponentAt(i);
				if(b.isDirty()) {
					root.write(file, b.getText());
					b.reset();
				}
			}
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
			throw new IllegalArgumentException();
		}

		@Override
		public void write(ByteArrayDiff diff) {
			throw new IllegalArgumentException();
		}

		@Override
		public byte[] read() {
			return bytes;
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
