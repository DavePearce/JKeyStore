package jledger.util;

import jledger.core.Content;
import jledger.core.Ledger;

public class NonSequentialLedger<T extends Content.Proxy> implements Ledger<T> {
	private final Content.Layout<T> layout;
	private int length;
	private Content.Blob[] ledger;

	public NonSequentialLedger(Content.Layout<T> layout, int capacity) {
		this.layout = layout;
		this.ledger = new Content.Blob[capacity];
	}

	@Override
	public int versions() {
		return length;
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
		// At this point, need to determine diff against previous version of the object.
		// Also need to check sequence is correct as well? Well, if they don't interfere
		// then can fast forward.
		// Write new blob into ledger
		ledger[length++] = object.getBlob();
		// Done
	}
}
