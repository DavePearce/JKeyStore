import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jledger.core.Value;
import jledger.util.ByteArrayLedger;
import jledger.util.ByteArrayValue;
import jledger.util.Pair;

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
		 * 
		 */
		private final ByteArrayLedger.Key key;
		/**
		 * Indicates whether currently recording or not.
		 */
		private boolean active = false;
		
		public State() {
			this.key = this.ledger.add("text");
			ByteArrayLedger.print(ledger);
		}
		
		public boolean getState() {
			return active;
		}
		
		public void setState(boolean state) {
			this.active = state;
		}
		
		public ByteArrayLedger getLedger() {
			return ledger;
		}
		
		/**
		 * Apply the latest text from the text area. Using this, we need to identify the
		 * diff and update the ledger accordingly.
		 * 
		 * @param text
		 */
		public synchronized void apply(String text) {
			// Extract bytes to use
			byte[] newBytes = text.getBytes();
			ByteArrayLedger.Data currentValue = ledger.get(key);
			if(currentValue == null) {
				// Construct fresh value
				ByteArrayLedger.Data newValue = ledger.add(new ByteArrayValue(text.getBytes()));
				// Add to ledger
				ledger.add(new Pair<>(key, newValue));
			} else {
				// Extract previous value
				byte[] currentBytes = currentValue.get();
				// Sanity check something changed
				if(!Arrays.equals(currentBytes, newBytes)) {
					// Construct delta against previous value
					Value delta = currentValue.replace(0, currentBytes.length, newBytes);
					// Add to ledger
					ByteArrayLedger.Data newValue = ledger.add(delta);
					ByteArrayLedger.print(ledger);
					ledger.add(new Pair<>(key, newValue));
				}
			}
		}
	}
	
	private final State state = new State();
	private JTextArea text;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			TextEditor frame = new TextEditor();
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.getRootPane().setLayout(new BorderLayout());
			//
			JPanel editor = createEditorPanel(frame);
			JPanel tools = createToolPanel(frame);
			JPanel status = createStatusPanel(frame.state);
			frame.getRootPane().add(tools, BorderLayout.NORTH);
			frame.getRootPane().add(editor, BorderLayout.CENTER);
			frame.getRootPane().add(status, BorderLayout.SOUTH);
			//
			frame.pack();
			frame.setVisible(true);
			//
			new ClockThread(1000,frame).start();
		});
	}
	
	private static JPanel createEditorPanel(TextEditor frame) {
		JPanel panel = new JPanel();
		frame.text = new JTextArea(10, 80) {
			@Override
			public boolean isEnabled() {
				return frame.state.getState();
			}
		};
		panel.add(frame.text);
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
				return String.format("%d items, %d bytes", state.getLedger().size(), state.getLedger().space());
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

		public ClockThread(int delayMillis, TextEditor frame) {
			this.frame = frame;
			this.delayMillis = delayMillis;
		}

		@Override
		public void run() {
			while (1 == 1) {
				// Loop forever
				try {
					Thread.sleep(delayMillis);
					// check whether play enabled
					if(frame.state.active) {
						// Apply the text
						String text = frame.text.getText();
						frame.state.apply(text);
						frame.revalidate();
						frame.repaint();
					}
				} catch (InterruptedException e) {
					// If we get here, then something wierd happened. It doesn't matter, we can just
					// ignore this and continue.
				}
			}
		}
	}
}
