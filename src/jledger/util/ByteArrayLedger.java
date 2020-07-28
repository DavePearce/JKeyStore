package jledger.util;

import java.util.Arrays;

import jledger.core.Ledger;
import jledger.core.Value;

/**
 * Provides a simple in-memory ledger implementation which stores values using
 * byte arrays. This is not thread-safe and, hence, is not intended for
 * concurrent modification.
 * 
 * @author David J. Pearce
 *
 */
public class ByteArrayLedger implements Ledger<ByteArrayLedger.Key, ByteArrayLedger.Data> {
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

	public ByteArrayLedger(int capacity) {
		this.bytes = new byte[capacity];
		this.offsets = new int[20];
		this.size = 0;
	}

	@Override
	public Key lookup(String key) {
		byte[] bs = key.getBytes();
		int offset = internalFind(KEY, bs, size, offsets, bytes);
		return (offset < 0) ? null : new Key(this, offset); 
	}
	
	@Override
	public Key add(String key) {
		byte[] bs = key.getBytes();		
		return new Key(this, append(KEY, bs));
	}

	/**
	 * Add an arbitrary value into this ledger producing an interned value which can
	 * then be used within a transaction. This will traverse the value adding
	 * delta's as necessary and interning anything not already interned on this
	 * ledger.
	 * 
	 * @param value
	 * @return
	 */
	@Override
	public Data add(jledger.core.Value value) {
		//
		if (value instanceof Data && ((Data) value).ledger == this) {
			// Easy, already interned on this ledger
			return (Data) value;
		} else if (value instanceof Data.Delta) {
			Data.Delta d = (Data.Delta) value;
			// Intern the parent
			Data parent = add(d.parent());
			//
			return new Data(this, append(DIFF, parent.id, d.offset(), d.length(), d.bytes()));
		} else {
			// FIXME: could be more efficient!
			byte[] bytes = new byte[value.size()];
			//
			for (int i = 0; i != bytes.length; ++i) {
				bytes[i] = value.read(i);
			}
			return new Data(this, append(DATA, bytes));
		}
	}

	/**
	 * Append a transaction onto this ledger.
	 * 
	 * @param txn
	 * @return
	 */
	@Override
	public void add(Pair<Key, Data>... txn) {
		int index = 0;
		byte[] bytes = new byte[txn.length * 2];
		for (int i = 0; i != txn.length; ++i) {
			Pair<Key, Data> a = txn[i];
			Key k = a.first();
			Data v = a.second();
			// FIXME: clearly a bug here for larger identifiers.
			bytes[index++] = (byte) k.id;
			bytes[index++] = (byte) v.id;
		}
		append(TRANSACTION, bytes);
	}

	@Override
	public Data get(Key key) {
		return get(size,key);
	}

	@Override
	public Data get(int timestamp, Key key) {
		final int id = key.id;
		// Traverse backwards in time looking for matching value.
		for (int i = timestamp; i >= 0; --i) {
			// Determine start of packet
			int offset = (i == 0) ? 0 : offsets[i - 1];
			// Find any transaction
			if (bytes[offset] == TRANSACTION) {
				int n = bytes[offset + 1];
				for (int j = 0; j < n; j = j + 2) {
					byte l = bytes[offset + 2 + j];
					byte r = bytes[offset + 3 + j];
					//
					if (l == id) {
						return new Data(this, r);
					}
				}
			}
		}
		// No value found.
		return null;
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

	private int append(byte header, int parent, int offset, int length, byte[] payload) {
		// FIXME: could be more efficient!
		byte[] bytes = new byte[payload.length + 3];
		// FIXME: unsound for larger integers
		bytes[0] = (byte) parent;
		bytes[1] = (byte) offset;
		bytes[2] = (byte) length;
		// Copy payload
		System.arraycopy(payload, 0, bytes, 3, payload.length);
		return append(header, bytes);
	}

	/**
	 * Represents a key within the ledger which can be associated with a value.
	 * Every key is associated with a specific ledger and can only be used with that
	 * ledger.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Key {
		/**
		 * The enclosing ledger.
		 */
		private final ByteArrayLedger ledger;
		/**
		 * The object identifier within the given ledger.
		 */
		private final int id;

		private Key(ByteArrayLedger ledger, int id) {
			this.ledger = ledger;
			this.id = id;
		}

		public int id() {
			return id;
		}
	}
	
	/**
	 * An interned value on this ledger, which corresponds to either a raw byte
	 * sequence or a diff against some parent value.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Data implements Value.Interned<Key, Data> {
		/**
		 * The enclosing ledger.
		 */
		private final ByteArrayLedger ledger;
		/**
		 * The object identifier within the given ledger.
		 */
		private final int id;

		private Data(ByteArrayLedger ledger, int id) {
			this.ledger = ledger;
			this.id = id;
		}

		public int id() {
			return id;
		}

		@Override
		public int size() {
			return internalSize(id, ledger.bytes, ledger.size, ledger.offsets);
		}

		@Override
		public byte read(int index) {
			return internalRead(id, index, ledger.bytes, ledger.size, ledger.offsets);
		}

		@Override
		public Delta write(int index, byte b) {
			return new ByteArrayValue.Delta(this,index,1,b);
		}

		@Override
		public Delta replace(int index, int length, byte[] bytes) {
			return new ByteArrayValue.Delta(this, index, length, bytes);
		}
		
		@Override
		public jledger.core.Ledger<Key, Data> getLedger() {
			return ledger;
		}
		
		public String toString() {
			return internalToString(id,ledger.bytes,ledger.size,ledger.offsets);
		}
	}

	// =================================================================
	// Byte Utils
	// =================================================================

	private static final byte KEY = 0b000;
	private static final byte DATA = 0b001;
	private static final byte DIFF = 0b010;
	private static final byte TRANSACTION = 0b011;

	private static int internalFind(byte kind, byte[] bytes, int n, int[] offsets, byte[] ledger) {
		// Check first item
		if (ledger[0] == kind) {
			if (internalEquals(bytes, ledger, 0)) {
				return 0;
			}
		}
		// Check remaining items
		for (int i = 1; i < n; ++i) {
			int offset = offsets[i - 1];
			// Check object kind
			if (ledger[offset] == kind) {
				if (internalEquals(bytes, ledger, offset)) {
					return i;
				}
			}
		}
		return -1;
	}

	private static String internalToString(int id, byte[] bytes, int n, int[] offsets) {
		// Calculate offset of this packet
		int offset = id == 0 ? 0 : offsets[id - 1];
		//
		byte header = bytes[offset];
		int size = bytes[offset + 1];
		//
		if(header == DATA) {
			String r = "";
			for (int i = 0; i < size; ++i) {
				if(i != 0) {
					r += ";";
				}
				r += String.format("%02X", bytes[offset + 2 + i]);
			}
			return r;
		} else {
			int p = bytes[offset + 2];
			int o = bytes[offset + 3];
			int l = bytes[offset + 4];
			String str = internalToString(p,bytes,n,offsets);
			String[] bs = str.split(";");
			String r  = "";
			boolean first=true;
			for (int i = 0; i < o; ++i) {
				if (!first) {
					r += ";";
				}
				first = false;
				r += bs[i];
			}
			for (int i = 3; i < size; ++i) {
				if (!first) {
					r += ";";
				}
				first = false;
				r += String.format("%02X", bytes[offset + 2 + i]);
			}
			for (int i = (o+l); i < bs.length; ++i) {
				if (!first) {
					r += ";";
				}
				first = false;
				r += bs[i];
			}
			return r;
		}
	}
	
	private static int internalSize(int id, byte[] bytes, int n, int[] offsets) {
		// Calculate offset of this packet
		int offset = id == 0 ? 0 : offsets[id - 1];
		// Extract key fields
		byte header = bytes[offset];
		int size = bytes[offset + 1];
		//
		if (header == DATA) {
			return size;
		} else {
			size = size - 3;
			int p = bytes[offset + 2];
			int l = bytes[offset + 4];
			return (internalSize(p, bytes, n, offsets) - l) + size;
		}
	}

	private static byte internalRead(int id, int index, byte[] bytes, int n, int[] offsets) {
		int offset = offsets[id];
		byte header = bytes[offset];
		if (header == DATA) {
			return bytes[offset + 2 + index];
		} else {
			throw new IllegalArgumentException("GOT HERE");
		}
	}

	private static boolean internalEquals(byte[] bytes, byte[] ledger, int offset) {
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
		for (int i = 0, offset = 0; i != n; ++i) {
			byte header = ledger[offset];
			byte length = ledger[offset + 1];
			byte[] bytes = new byte[length];
			System.arraycopy(ledger, offset + 2, bytes, 0, length);
			switch (header) {
			case KEY:
				System.out
						.println("[" + i + "]\t" + toString(header) + ":" + length + ":\"" + new String(bytes) + "\"");
				break;
			case DATA:
				System.out.println("[" + i + "]\t" + toString(header) + ":" + length + ":" + Arrays.toString(bytes));
				break;
			case DIFF:
				System.out.println("[" + i + "]\t" + toString(header) + ":" + length + ":" + bytes[0] + ":" + bytes[1]
						+ ":" + bytes[2] + ":" + Arrays.toString(Arrays.copyOfRange(bytes, 3, bytes.length)));
				break;
			case TRANSACTION:
				System.out.println("[" + i + "]\t" + toString(header) + ":" + length + ":" + Arrays.toString(bytes));
			}
			offset += (length + 2);
		}
	}

	private static String toString(byte header) {
		switch (header) {
		case KEY:
			return "key";
		case DATA:
			return "data";
		case DIFF:
			return "diff";
		case TRANSACTION:
			return "txn";
		}
		throw new IllegalArgumentException(Integer.toBinaryString(header));
	}

	public static void main(String[] args) {
		Value v1 = new ByteArrayValue("dave".getBytes());
		Value v2 = v1.write(0, (byte) 'D');
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		Key k1 = ledger.add("dave");
		Key k2 = ledger.add("src/main.whiley");
		Data d1 = ledger.add(v1);
		ledger.add(new Pair<>(k1, d1));
		System.out.println("get(dave)=" + ledger.get(k1));
		Data d2 = ledger.add(d1.replace(3, 1, "id".getBytes()));
		ledger.add(new Pair<>(k1, d2));
		System.out.println("get(dave)=" + ledger.get(k1));
		print(ledger.bytes, ledger.size);
	}
}
