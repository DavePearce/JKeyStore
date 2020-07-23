package jledger.util;

import java.util.ArrayList;
import java.util.Arrays;

import jledger.core.Transaction;

/**
 * Provides an in-memory ledger implementation which stores
 * <code>ByteArrayValue</code>s and provides an implementation of
 * <code>Store</code> for the head.
 * 
 * @author David J. Pearce
 *
 */
public class ByteArrayLedger {
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
	
	public long size() {
		return length;
	}
	
	/**
	 * Lookup a given key in this ledger. If the key is not registered, an
	 * <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param key
	 * @return
	 */
	public Key lookup(String key) {
		for (int i = 0; i != length; ++i) {
			Item ith = items[i];
			if (ith instanceof KeyItem) {
				KeyItem k = (KeyItem) ith;
				if (k.value.equals(key)) {
					return new Key(i);
				}
			}
		}
		throw new IllegalArgumentException("Invalid key");
	}
	
	/**
	 * Lookup a given key in this ledger. If the key is not registered, then
	 * register it.
	 * 
	 * @param key
	 * @return
	 */
	public Key lookupOrRegister(String key) {
		for (int i = 0; i != length; ++i) {
			Item ith = items[i];
			if (ith instanceof KeyItem) {
				KeyItem k = (KeyItem) ith;
				if (k.value.equals(key)) {
					return new Key(i);
				}
			}
		}
		return new Key(append(new KeyItem(key)));
	}
	
	/**
	 * Get the value associated with a given key.
	 * 
	 * @param key
	 * @return
	 */
	public Value get(Key key) {
		int kid = key.oid;
		// Traverse ledger looking for item
		for (int i = length - 1; i >= 0; i = i - 1) {
			Item ith = items[i];
			if (ith instanceof TransactionItem) {
				TransactionItem t = (TransactionItem) ith;
				for (int j = 0; j != t.operations.length; ++j) {
					OperationItem jth = (OperationItem) items[t.operations[j]];
					// FIXME: this is simplistic
					if (jth.lhs == kid) {
						return new FixedValue(jth.rhs);
					}
				}
			}
		}
		// Nothing assigned
		return null;
	}
	
	/**
	 * Add a new transaction.
	 * 
	 * @param txn
	 * @return
	 */
	public int add(Transaction<Key, Value> txn) {
		int[] operations = new int[txn.size()];
		int index = 0;
		for (Transaction.Operation<Key, Value> op : txn) {
			Item item = toItem(op);
			operations[index++] = append(item);
		}
		return append(new TransactionItem(operations));
	}
	
	private int append(Item item) {
		if(items.length == length) {
			items = Arrays.copyOf(items, items.length * 2);
		}
		int id = length;
		items[length++] = item;
		return id;
	}
	
	private Item toItem(Transaction.Operation<Key, Value> _op) {
		// FIXME: assuming only assignments at this stage.
		Transaction.Operation.Assign<Key, Value> op = (Transaction.Operation.Assign<Key, Value>) _op;
		int kid = op.getTarget().oid;
		int vid = flattern(op.getValue());
		return new OperationItem(OperationKind.ASSIGN,kid,vid);
	}	
	
	private int flattern(Value value) {
		if (value instanceof FixedValue) {
			// FIXME: probme if this value is on a different chain!
			return ((FixedValue) value).oid;
		} else if (value instanceof RawValue) {
			RawValue rv = (RawValue) value;
			return append(new RawItem(rv.bytes));
		} else {
			DeltaValue dv = (DeltaValue) value;
			int p = flattern(dv.parent);
			return append(new DeltaItem(p, dv.offset, dv.length, dv.bytes));
		}
	}
	
	public class Key {
		private final int oid;

		public Key(int oid) {
			this.oid = oid;
		}

		public String get() {
			KeyItem k = (KeyItem) items[oid];
			return k.value;
		}
	}
	
	public interface Value {
		int size();

		byte read(int offset);

		Value write(int offset, int length, byte[] bytes);
	}
	
	private class FixedValue implements Value {
		private final int oid;

		private FixedValue(int oid) {
			this.oid = oid;
		}

		@Override
		public int size() {
			Item item = items[oid];
			int delta = 0;
			while (item instanceof DeltaItem) {
				DeltaItem d = (DeltaItem) item;
				delta += d.bytes.length - d.length;
				item = items[d.parent];
			}
			RawItem base = (RawItem) item;
			return base.size() + delta;
		}
		
		public Value write(int offset, int length, byte[] bytes) {
			return new DeltaValue(this, offset, length, bytes);
		}
		
		public byte read(int offset) {
			throw new IllegalArgumentException("GOT HERE");
		}
		
		public String toString() {
			return "FixedValue(" + oid + ")";
		}
	}
	
	private static class RawValue implements Value {
		private final byte[] bytes;
		
		public RawValue(String string) {
			this.bytes = string.getBytes();
		}
		
		public RawValue(byte[] bytes) {
			this.bytes = bytes;
		}

		@Override
		public int size() {
			return bytes.length;
		}

		@Override
		public byte read(int offset) {
			return bytes[offset];
		}

		@Override
		public Value write(int offset, int length, byte[] bytes) {
			return new DeltaValue(this, offset, length, bytes);
		}
	}
	
	private static class DeltaValue implements Value {
		private final Value parent;
		private final int offset;
		private final int length;
		private final byte[] bytes;
		
		public DeltaValue(Value parent, int offset, int length, byte[] bytes) {
			this.parent = parent;
			this.offset = offset;
			this.length = length;
			this.bytes = bytes;
		}
		
		public int size() {
			return parent.size() + (bytes.length - length);
		}
		
		public Value write(int offset, int length, byte[] bytes) {
			return new DeltaValue(this, offset, length, bytes);
		}
		
		public byte read(int index) {
			if (index < offset) {
				return parent.read(index);
			} else if (index >= (offset + bytes.length)) {
				return parent.read(index + (bytes.length - length));
			} else {
				return bytes[index - offset];
			}
		}
	}
	
	// =======================================================
	//
	// =======================================================
	
	private interface Item {
		
	}
	
	/**
	 * Internal representation of a registered key in the ledger.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private final static class KeyItem implements Item {
		private final String value;

		private KeyItem(String value) {
			this.value = value;
		}

		public String get() {
			return value;
		}
		
		public String toString() {
			return "key(\"" + value + "\")";
		}
	}

	/**
	 * Internal representation of an obect as a sequence of bytes.
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
		
		public String toString() {
			return "raw(" + Arrays.toString(bytes) + ")";
		}
	}
	
	/**
	 * Internal representation of an object as a delta of another object.
	 * 
	 * @author David J. Pearce
	 *
	 */
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
		
		public String toString() {
			return "delta(parent=" + parent + ",offset=" + offset + ",length=" + length + ",data="
					+ Arrays.toString(bytes);
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
		
		public String toString() {
			return kind + "(" + lhs + "," + rhs + ")"; 
		}
	}

	private class TransactionItem implements Item {
		private final int[] operations;

		public TransactionItem(int[] operations) {
			this.operations = operations;
		}
		
		public String toString() {
			return "transaction(" + Arrays.toString(operations) + ")";
		}
	}
	
	public static void print(ByteArrayLedger ledger) {
		for(int i=0;i!=ledger.length;++i) {
			System.out.println("[" + i + "] " + ledger.items[i]);
		}
	}
	
	public static void main(String[] args) {
		ByteArrayLedger ledger = new ByteArrayLedger(10);
		Key key = ledger.lookupOrRegister("main.whiley");
		Transaction<Key,Value> txn = Transaction.EMPTY;
		txn = txn.assign(key,new RawValue("hello"));
		ledger.add(txn);
		print(ledger);
		Value v = ledger.get(key);
		System.out.println("GOT: " + v);
	}
}
