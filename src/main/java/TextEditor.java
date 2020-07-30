import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jledger.util.ByteArrayLedger;

/**
 * This is a temporary class designed to test the ledger functionality in a
 * visible manner.
 * 
 * @author David J. Pearce
 *
 */
public class TextEditor extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static class State {
		/**
		 * The underlying ledger
		 */
		private final ByteArrayLedger ledger = new ByteArrayLedger(10);
		/**
		 * Indicates whether currently recording or not.
		 */
		private boolean active = false;
		
		public boolean getState() {
			return active;
		}
		
		public void setState(boolean state) {
			this.active = state;
		}
		
		public ByteArrayLedger getLedger() {
			return ledger;
		}
	}
	
	private final State state = new State();
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			TextEditor frame = new TextEditor();
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.getRootPane().setLayout(new BorderLayout());
			//
			JPanel editor = createEditorPanel(frame.state);
			JPanel tools = createToolPanel(frame);
			JPanel status = createStatusPanel(frame.state);
			frame.getRootPane().add(tools, BorderLayout.NORTH);
			frame.getRootPane().add(editor, BorderLayout.CENTER);
			frame.getRootPane().add(status, BorderLayout.SOUTH);
			//
			frame.pack();
			frame.setVisible(true);
		});
	}
	
	private static JPanel createEditorPanel(State state) {
		JPanel panel = new JPanel();
		panel.add(new JTextArea(10,80) {
			@Override
			public boolean isEnabled() {
				return state.getState();	
			}
		});
		return panel;
	}
	
	private static JPanel createToolPanel(TextEditor frame) {
		JPanel panel = new JPanel();
		panel.add(new JButton(new AbstractAction("[]") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.state.setState(false);
				frame.repaint();
			}
		}) {
			@Override
			public boolean isEnabled() {
				return frame.state.getState();	
			}
		});
		panel.add(new JButton(new AbstractAction(">") {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.state.setState(true);
				frame.repaint();
			}
		}) {
			@Override
			public boolean isEnabled() {
				return !frame.state.getState();	
			}
		});
		return panel;
	}
	
	private static JPanel createStatusPanel(State state) {
		JPanel panel = new JPanel();
		panel.add(new JLabel() {
			@Override
			public String getText() {
				return String.format("%d transactions, ??? bytes", state.getLedger().size());
			}
		});
		return panel;
	}
	
	/**
	 * The Clock Thread is responsible for producing a consistent "pulse" which is
	 * used to fire a downwards move to the game on every cycle.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class ClockThread extends Thread {
		private final TextEditor frame;
		private volatile int delayMillis; // delay between ticks in ms

		public ClockThread(TextEditor frame) {
			this.frame = frame;
		}

		@Override
		public void run() {
			while (1 == 1) {
				// Loop forever
				try {
					Thread.sleep(delayMillis);
					//
					frame.repaint();
				} catch (InterruptedException e) {
					// If we get here, then something wierd happened. It doesn't matter, we can just
					// ignore this and continue.
				}
			}
		}
	}
}
