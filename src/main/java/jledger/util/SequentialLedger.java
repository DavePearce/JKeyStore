package jledger.util;

import java.util.Arrays;

import jledger.core.Content;
import jledger.core.Ledger;

public class SequentialLedger<T extends Content.Proxy> implements Ledger<T> {
	private final Content.Layout<T> layout;
	private int length;
	private Content.Blob[] ledger;

	public SequentialLedger(T initial) {
		this(initial,10);
	}

	public SequentialLedger(T initial, int capacity) {
		this.layout = (Content.Layout) initial.getLayout();
		this.ledger = new Content.Blob[capacity];
		this.ledger[0] = initial.getBlob();
		this.length = 1;
	}

	@Override
	public int versions() {
		return length;
	}

	public T last() {
		return get(length-1);
	}

	@Override
	public T get(int timestamp) {
		// Get the blob at the given timestamp
		Content.Blob blob = ledger[timestamp];
		// Decode it
		return layout.read(blob, 0);
	}

	@Override
	public void put(T object) {
		append(object.getBlob());
	}

	private void append(Content.Blob b) {
		if (b instanceof Content.Diff) {
			Content.Diff d = (Content.Diff) b;
			// Append any parent transactions
			append(d.parent());
			// Ensure sufficient capacity
			if (ledger.length == length) {
				ledger = Arrays.copyOf(ledger, length * 2);
			}
			// Append this transaction
			ledger[length++] = b;
		} else if (b != ledger[length - 1]) {
			System.out.println("GOT: " + b + " : " + ledger[length-1]);
			throw new IllegalArgumentException("Non-sequential put");
		}
	}
}
