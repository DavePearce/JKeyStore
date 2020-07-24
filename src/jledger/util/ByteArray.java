package jledger.util;

import java.util.Arrays;

import jledger.core.Transaction;
import jledger.util.ByteArrayLedger.Key;
import jledger.util.ByteArrayLedger.Value;

public class ByteArray  {
	
	public static class Value implements jledger.core.Value {
		protected byte[] bytes;

		public Value(byte[] data) {
			this.bytes = data;
		}

		@Override
		public int size() {
			return bytes.length;
		}

		@Override
		public byte read(int index) {
			return bytes[index];
		}

		@Override
		public Delta write(int index, byte b) {
			return new ByteArray.Delta(this, index, 1, b);
		}
	}

	public static class Delta implements jledger.core.Value.Delta {
		private final jledger.core.Value parent;
		private final int offset;
		private final int length;
		private final byte[] bytes;

		public Delta(jledger.core.Value parent, int offset, int length, byte... bytes) {
			this.parent = parent;
			this.offset = offset;
			this.length = length;
			this.bytes = bytes;
		}

		public int size() {
			return parent.size() + (bytes.length - length);
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

		@Override
		public Value.Delta write(int index, byte b) {
			return new ByteArray.Delta(this, index, 1, b);
		}

		@Override
		public jledger.core.Value getParent() {
			return parent;
		}
	}
	
	
	/**
	 * Provides an in-memory ledger implementation which stores
	 * <code>ByteArray</code> values.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Ledger {
		/**
		 * The raw bytes making up the ledger.
		 */
		private byte[] bytes;
		/**
		 * The offsets within the <code>bytes</code> array identifying, for each item,
		 * the start of the following item. Thus, <code>offsets[size-1]</code> always
		 * identifies the first inactive byte in the ledger.
		 */
		private int[] offsets;
		/**
		 * The count of active packets in the current ledger.
		 */
		private int size;
		
		public Ledger(int capacity) {
			this.bytes = new byte[capacity];
			this.offsets = new int[20];
			this.size = 0;
		}
		
		/**
		 * Lookup a given key in this ledger. If the key is not registered, an
		 * <code>IllegalArgumentException</code> is thrown.
		 * 
		 * @param key
		 * @return
		 */
		public Key lookup(String key) {
			byte[] bs = key.getBytes();
			int offset = find(KEY, bs, size, offsets, bytes);
			if (offset >= 0) {
				return new Key(this, offset);
			} else {
				throw new IllegalArgumentException("key not found");
			}
		}
		
		/**
		 * Lookup a given key in this ledger. If the key is not registered, then
		 * register it.
		 * 
		 * @param key
		 * @return
		 */
		public Key lookupOrAdd(String key) {
			byte[] bs = key.getBytes();
			int offset = find(KEY, bs, size, offsets, bytes);
			if (offset >= 0) {
				return new Key(this, offset);
			} else {
				return new Key(this,append(KEY,bs));
			}
		}
		
		public Static add(Value value) {
			// FIXME: add a value to the ledger
		}
		
		public int append(Transaction<Key, Static> txn) {
			int index = 0;
			byte[] bytes = new byte[txn.size() * 2];
			for (Transaction.Operation<Key, Static> op : txn) {
				if (op instanceof Transaction.Operation.Assign) {
					Transaction.Operation.Assign<Key, Static> a = (Transaction.Operation.Assign) op;
					Key k = a.getTarget();
					Static v = a.getValue();
					// FIXME: clearly a bug here for larger identifiers.
					bytes[index++] = (byte) k.id;
					bytes[index++] = (byte) v.id;
				}
			}
			return append(TRANSACTION, bytes);
		}
		
		/**
		 * Append a given item onto the ledger whilst preserving the offsets array.
		 * 
		 * @param header
		 * @param payload
		 * @return
		 */
		private int append(byte header, byte[] payload) {
			if (payload.length > 255) {
				throw new IllegalArgumentException("invalid payload");
			}
			int length = payload.length + 2;
			// Identifier first inactive byte			
			int offset = (size == 0) ? 0 : offsets[size - 1];
			// Ensure sufficient capacity
			while (bytes.length < (offset + length)) {
				bytes = Arrays.copyOf(bytes, bytes.length * 2);
			}
			// Copy over header and payload
			bytes[offset] = header;
			bytes[offset + 1] = (byte) payload.length;
			System.arraycopy(payload, 0, bytes, offset + 2, length - 2);
			// Update offsets array
			if (offsets.length == size) {
				offsets = Arrays.copyOf(offsets, size * 2);
			}
			offsets[size++] = offset + length;
			return (size - 1);
		}
	}
	
	public static class Key {
		/**
		 * The enclosing ledger.
		 */
		private final ByteArray.Ledger ledger;
		/**
		 * The object identifier within the given ledger.
		 */
		private final int id;
		
		private Key(ByteArray.Ledger ledger, int id) {
			this.ledger = ledger;
			this.id = id;
		}
	}
	
	public static class Static {
		/**
		 * The enclosing ledger.
		 */
		private final ByteArray.Ledger ledger;
		/**
		 * The object identifier within the given ledger.
		 */
		private final int id;
		
		private Static(ByteArray.Ledger ledger, int id) {
			this.ledger = ledger;
			this.id = id;
		}
	}
	
	
	// =================================================================
	// Byte Utils
	// =================================================================
	
	private static final byte KEY = 0b000;
	private static final byte DATA = 0b001;
	private static final byte DELTA = 0b010;
	private static final byte TRANSACTION = 0b011;	
	
	private static int find(byte kind, byte[] bytes, int n, int[] offsets, byte[] ledger) {
		// Check first item
		if (ledger[0] == kind) {
			if(equals(bytes, ledger, 0)) {
				return 0;
			}
		}
		// Check remaining items
		for (int i = 1; i < n; ++i) {
			int offset = offsets[i - 1];
			// Check object kind
			if (ledger[offset] == kind) {
				if (equals(bytes, ledger, offset)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private static boolean equals(byte[] bytes, byte[] ledger, int offset) {
		if ((offset + bytes.length) > ledger.length) {
			return false;
		} else {
			for (int i = 0; i != bytes.length; ++i) {
				if (bytes[i] != ledger[offset + i]) {
					return false;
				}
			}
			return true;
		}
	}
	
	private static void print(byte[] ledger, int n) {
		for(int i=0,offset=0;i!=n;++i) {
			byte header = ledger[offset];
			byte length = ledger[offset+1];
			byte[] bytes = new byte[length];
			System.arraycopy(ledger, offset+2, bytes, 0, length);
			switch(header) {
			case KEY:
				System.out.println("[" + i + "]\t" + toString(header) + ":" + length + ":\"" + new String(bytes) + "\"");
				break;
			}
			offset += (length + 2);
		}
	}
	
	private static String toString(byte header) {
		switch(header) {
		case KEY:
			return "key";
		case DATA:
			return "data";
		case DELTA:
			return "delta";
		}
		throw new IllegalArgumentException(Integer.toBinaryString(header));
	}
	
	public static void main(String[] args) {
		ByteArray.Ledger ledger = new ByteArray.Ledger(100);
		ledger.lookupOrAdd("dave");
		ledger.lookupOrAdd("src/main.whiley");
		print(ledger.bytes, ledger.size);
	}
}
