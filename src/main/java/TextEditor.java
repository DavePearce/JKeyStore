import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import jledger.util.ByteArrayDiff;

public class TextEditor extends JFrame implements ActionListener {
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
	private final JPanel workArea;
	/**
	 * Provides access to the status bar.
	 */
	private final JPanel statusBar;
	/**
	 * Access to the editor
	 */
	private JTextArea editor;

	public TextEditor() {
		super("Simple Text Editor");
		// Construct main components
		this.menuBar = buildMenuBar();
		this.toolBar = buildToolBar();
		this.workArea = buildWorkArea();
		this.statusBar = buildStatusBar();
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
		toolBar.add(makeToolbarButton("Save file", "Save"));

		return toolBar;
	}

	private JPanel buildWorkArea() {
		editor = new JTextArea(20,50);
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		panel.add(editor);
		return panel;
	}

	private JPanel buildStatusBar() {
		// build the status bar.
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
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

	public static void main(String[] args) {
		new TextEditor();
	}

	private String last = "";

	@Override
	public void actionPerformed(ActionEvent e) {
		String after = editor.getText();
		ByteArrayDiff diff = ByteArrayDiff.construct(last,after);
		//
		System.out.println("DIFF: " + diff);
		last = after;
	}
}
