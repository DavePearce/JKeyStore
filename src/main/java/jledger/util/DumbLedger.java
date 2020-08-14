package jledger.util;

import jledger.core.Content;
import jledger.core.Ledger;

public class DumbLedger<T extends Content.Proxy> implements Ledger<T> {
	private final Content.Layout layout;

	private int length;
	private byte[][] ledger;

	public DumbLedger(Content.Layout layout, int capacity) {
		this.layout = layout;
		this.ledger = new byte[capacity][];
	}

	@Override
	public int versions() {
		return length;
	}

	@Override
	public T get(int timestamp) {
		throw new IllegalArgumentException();
	}

	@Override
	public void put(T object) {
		// At this point, need to determine diff against previous version of the object.
		// Also need to check sequence is correct as well? Well, if they don't interfere
		// then can fast forward.
		//
		// Actually, only need to be able to convert object into a string of bytes in
		// this case.
		Content.Blob blob = object.getContents();
		// Write new blob into ledger
		ledger[length++] = blob.get();
		// Done

		// Should proxy object return a blob?
		//
		// Or should proxy object contain list of modifications and, using layout, we
		// convert these into a blob.
		//
		// We want to be able to interact with the proxy object quite efficiently. For
		// example, when traversing a method and changing all the type information then
		// still need to be able to read structure, etc.
	}
}
