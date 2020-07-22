package jledger.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import jledger.core.Ledger;
import jledger.core.Transaction;
import jledger.core.Transaction.Failure;
import jledger.core.Value;
import jledger.core.Value.Delta;
import jledger.io.BinaryOutputStream;

/**
 * Provides an in-memory ledger implementation which stores
 * <code>ByteArrayValue</code>s and provides an implementation of
 * <code>Store</code> for the head.
 * 
 * @author David J. Pearce
 *
 */
public class ByteArrayLedger implements Ledger<String,ByteArrayValue> {
	/**
	 * The sequence of ledger objects.
	 */
	private Item[] items;
	/**
	 * Number of transactions slots in use.
	 */
	private int length;	
	
	/**
	 * Construct an empty ledger with a given initial capacity.
	 * 
	 * @param size
	 */
	public ByteArrayLedger(int capacity) {
		this.items = new Item[capacity];
		this.length = 0;
	}
	
	@Override
	public long size() {
		return length;
	}

	@Override
	public Transaction<String, ByteArrayValue> get(long txn) {
		throw new IllegalArgumentException("GOT HERE");
	}

	@Override
	public void add(Transaction<String, ByteArrayValue> txn) throws Failure {
		append(toBytes(txn));				
	}
	
	public interface Item {
		
	}
	
	public final static class KeyItem implements Item {
		private final String value;

		private KeyItem(String value) {
			this.value = value;
		}

		public String toString() {
			return value;
		}
	}

	/**
	 * A base object which is simply a sequence of bytes.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private static class RawItem implements Item {
		private final byte[] bytes;
		
		public RawItem(byte[] bytes) {
			this.bytes = bytes;
		}
		
		public int size() {
			return bytes.length;
		}
	}

	private class DeltaItem implements Item {
		private final int parent;
		private final int offset;
		private final int length;
		private final byte[] bytes;

		public DeltaItem(int parent, int offset, int length, byte[] bytes) {
			this.parent = parent;
			this.offset = offset;
			this.length = length;
			this.bytes = bytes;
		}
	}
	
	private static enum OperationKind {
		ASSIGN, MOVE, COPY, DELETE
	}
	
	private class OperationItem implements Item {
		private final OperationKind kind;
		private final int lhs;
		private final int rhs;

		public OperationItem(OperationKind kind, int lhs, int rhs) {
			this.kind = kind;
			this.lhs = lhs;
			this.rhs = rhs;
		}
	}

	private class TransactionItem implements Item {
		private final int[] operations;

		public TransactionItem(int[] operations) {
			this.operations = operations;
		}
	}
}
