package jledger.util;

import java.util.Arrays;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Diff;
import jledger.core.Ledger;
import jledger.layouts.Array;
import jledger.util.Byte.Replacement;

import static jledger.layouts.Primitive.INT;

public class ByteArrayLedger<T extends Content.Proxy> implements Ledger<T> {
	/**
	 * Layout each transaction in this ledger conforms to.
	 */
	private final Content.Layout<T> layout;
	/**
	 * Array of raw bytes.
	 */
	private byte[] bytes;
	/**
	 * Offset of "following" transaction. For example, for the ith transaction this
	 * identifies first byte of ith+1 transaction.
	 */
	private int[] offsets;
	/**
	 * The number of transactions in the ledger
	 */
	private int versions;

	/**
	 * Construct an empty ledger for a given layout.
	 *
	 * @param layout
	 */
	public ByteArrayLedger(T value) {
		this(value,256);
	}

	public ByteArrayLedger(T value, int capacity) {
		this.layout = (Content.Layout) value.getLayout();
		this.bytes = new byte[capacity];
		this.offsets = new int[10];
		// Always at least one version
		this.versions = 1;
		// write initial value
		byte[] bs = value.getBlob().readAll();
		int off = append(0,bs.length);
		offsets[0] = append(off, bs);
	}

	@Override
	public int versions() {
		return versions;
	}

	public int size() {
		return offsets[versions - 1];
	}

	@Override
	public T last() {
		return get(versions - 1);
	}

	@Override
	public T get(int v) {
		return layout.read(new Blob<>(this, v), 0);
	}

	@Override
	public void put(T object) {
		int offset = offsets[versions - 1];
		// Determine enclosing blob
		Content.Blob blob = object.getBlob();
		// Ensure enough space for transaction
		ensureVersions(versions + 1);
		// Record transaction end-point
		offsets[versions] = append(blob, offset);
		// Increment version number
		versions = versions + 1;
	}

	@Override
	public String toString() {
		String r = "{";
		int n = read_i32(bytes,0);
		for(int i=0;i!=n;++i) {
			r += Integer.toHexString(bytes[i + 4]);
		}
		r += "}";
		for(int i=1;i<=versions;++i) {
			r += toTransactionString(i);
		}
		return r;
	}

	private String toTransactionString(int v) {
		int offset = offsets[v - 1];
		String r = "{";
		int m = read_i32(bytes,offset);
		offset = offset + 4;
		for(int j=0;j<m;++j) {
			int o = read_i32(bytes,offset);
			int s = read_i32(bytes,offset+4);
			int l = read_i32(bytes,offset+8);
			r += "<" + o + ";" + s + ";" + l + ">";
			offset = offset + 12;
		}
		return r + "}";
	}

	private int append(Content.Blob b, int offset) {
		if (b instanceof Content.Diff) {
			Content.Diff d = (Content.Diff) b;
			Content.Blob p = d.parent();
			if (p instanceof Blob) {
				Blob<T> pb = (Blob<T>) p;
				if (pb.parent == this) {
					if (pb.version == (versions - 1)) {
						// Store replacement count
						offset = append(offset,d.count());
						// Store replacement headers
						for (int i = 0; i != d.count(); ++i) {
							Content.Replacement r = d.getReplacement(i);
							offset = append(offset,r.offset());
							offset = append(offset,r.size());
							offset = append(offset,r.bytes().length);
						}
						// Store replacement contents
						for (int i = 0; i != d.count(); ++i) {
							Content.Replacement r = d.getReplacement(i);
							offset = append(offset,r.bytes());
						}
						// done;
						return offset;
					} else {
						throw new IllegalArgumentException("non-sequential put (" + pb.version + " vs " + versions + ")");
					}
				} else {
					throw new IllegalArgumentException("blob not from this ledger");
				}
			}
		}
		throw new IllegalArgumentException("invalid blob for ledger (" + b.getClass().getName() + ")");
	}

	private int append(int offset, int value) {
		// FIXME: This is inefficient as always requires 4 bytes. However, to enable
		// binary search we want fixed size entries.

		// Extract bytes
		final byte b1 = (byte) ((value >> 24) & 0xFF);
		final byte b2 = (byte) ((value >> 16) & 0xFF);
		final byte b3 = (byte) ((value >> 8) & 0xFF);
		final byte b4 = (byte) (value & 0xFF);
		// Determine updated length
		final int n = offset + 4;
		// Ensure sufficient capacity
		ensureCapacity(n);
		// Copy bytes
		bytes[offset] = b1;
		bytes[offset+1] = b2;
		bytes[offset+2] = b3;
		bytes[offset+3] = b4;
		// Done
		return n;
	}

	private int append(int offset, byte[] bs) {
		// Determine space required
		final int n = offset + bs.length;
		// Ensure sufficient capacity
		ensureCapacity(n);
		// Copy bytes
		System.arraycopy(bs, 0, bytes, offset, bs.length);
		// Done
		return n;
	}

	private void ensureCapacity(int m) {
		final int n = bytes.length;
		//
		if(n < m) {
			bytes = Arrays.copyOf(bytes, m * 2);
		}
	}

	private void ensureVersions(int m) {
		final int n = offsets.length;
		//
		if(n < m) {
			offsets = Arrays.copyOf(offsets, m * 2);
		}
	}

	/**
	 * A proxy representing the blob in this ledger at a given version.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 */
	private static class Blob<S extends Content.Proxy> implements Content.Blob {
		/**
		 * Identify ledger for which this is a proxy.
		 */
		private final ByteArrayLedger<S> parent;
		/**
		 * Identify ledger transaction to which this corresponds.
		 */
		private final int version;

		public Blob(ByteArrayLedger<S> parent, int version) {
			this.parent = parent;
			this.version = version;
		}

		@Override
		public int size() {
			return blob_size(parent,version);
		}

		@Override
		public byte[] readAll() {
			return blob_readall(parent,version);
		}

		@Override
		public byte readByte(int index) {
			return blob_read(index, parent, version);
		}

		@Override
		public short readShort(int index) {
			// FIXME: performance could be improved!!
			byte b1 = readByte(index);
			byte b2 = readByte(index + 1);
			// Recombine bytes
			return (short) ((b1 << 8) | b2);
		}

		@Override
		public int readInt(int index) {
			// FIXME: performance could be improved!!
			byte b1 = readByte(index);
			byte b2 = readByte(index + 1);
			byte b3 = readByte(index + 2);
			byte b4 = readByte(index + 3);
			// Recombine bytes
			return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		}

		@Override
		public byte[] readBytes(int index, int length) {
			// FIXME: performance could be improved!!
			byte[] bs = new byte[length];
			for (int i = 0; i < length; ++i) {
				bs[i] = readByte(index + i);
			}
			return bs;
		}

		@Override
		public void readBytes(int index, int length, byte[] dest, int destStart) {
			// FIXME: performance could be improved using binary search!!
			for (int i = 0; i < length; ++i) {
				dest[destStart + i] = readByte(index + i);
			}
		}

		@Override
		public Diff writeByte(int index, byte b) {
			return new Byte.Diff(this, new Replacement(index, 1, b));
		}

		@Override
		public Diff writeShort(int offset, short value) {
			// Convert value into bytes
			// FIXME: replacement can be smaller in common cases. For example, when either
			// first byte is identical, or last byte is.
			byte b1 = (byte) ((value >> 8) & 0xFF);
			byte b2 = (byte) (value & 0xFF);
			return new Byte.Diff(this, new Replacement(offset, 2, b1, b2));
		}

		@Override
		public Diff writeInt(int offset, int value) {
			// Convert value into bytes
			// FIXME: replacement can be smaller in common cases. For example, when either
			// first n bytes are identical, or last n bytes.
			byte b1 = (byte) ((value >> 24) & 0xFF);
			byte b2 = (byte) ((value >> 16) & 0xFF);
			byte b3 = (byte) ((value >> 8) & 0xFF);
			byte b4 = (byte) (value & 0xFF);
			return new Byte.Diff(this, new Replacement(offset, 4, b1, b2, b3, b4));
		}

		@Override
		public Diff writeBytes(int offset, byte... bytes) {
			return new Byte.Diff(this, new Replacement(offset, bytes.length, bytes));
		}

		@Override
		public Diff replaceBytes(int offset, int length, byte... bytes) {
			return new Byte.Diff(this, new Replacement(offset, length, bytes));
		}

		@Override
		public Diff insertByte(int offset, byte b) {
			return new Byte.Diff(this, new Replacement(offset, 0, b));
		}

		@Override
		public Diff insertShort(int offset, short value) {
			// Convert value into bytes
			byte b1 = (byte) ((value >> 8) & 0xFF);
			byte b2 = (byte) (value & 0xFF);
			return new Byte.Diff(this, new Replacement(offset, 0, b1, b2));
		}

		@Override
		public Diff insertInt(int offset, int value) {
			// Convert value into bytes
			byte b1 = (byte) ((value >> 24) & 0xFF);
			byte b2 = (byte) ((value >> 16) & 0xFF);
			byte b3 = (byte) ((value >> 8) & 0xFF);
			byte b4 = (byte) (value & 0xFF);
			return new Byte.Diff(this, new Replacement(offset, 0, b1, b2, b3, b4));
		}

		@Override
		public Diff insertBytes(int offset, byte... bytes) {
			return new Byte.Diff(this, new Replacement(offset, 0, bytes));
		}

		@Override
		public jledger.core.Content.Blob merge(jledger.core.Content.Blob sibling) {
			throw new UnsupportedOperationException();
		}
	}

	private static int blob_size(ByteArrayLedger<?> parent, int version) {
		// Determine transaction offset
		if(version == 0) {
			// base case
			return read_i32(parent.bytes,0);
		} else {
			// recursive case
			int offset = parent.offsets[version-1];
			// Determine parent size
			int size = blob_size(parent,version-1);
			// Determine replacement count
			int n = read_i32(parent.bytes,offset);
			offset = offset + 4;
			// Apply replacement delta's
			for(int i=0;i!=n;++i) {
				int s = read_i32(parent.bytes, offset + 4);
				int l = read_i32(parent.bytes, offset + 8);
				size += (l - s);
				offset += 12;
			}
			return size;
		}
	}

	private static byte[] blob_readall(ByteArrayLedger<?> parent, int version) {
		// FIXME: highly inefficient
		int n = blob_size(parent, version);
		byte[] bytes = new byte[n];
		for (int i = 0; i != n; ++i) {
			bytes[i] = blob_read(i, parent, version);
		}
		return bytes;
	}

	private static byte blob_read(int index, ByteArrayLedger<?> parent, int version) {
		if(version == 0) {
			// base case
			int n = read_i32(parent.bytes,0);
			if(index < 0 || index >= n) {
				throw new IllegalArgumentException("invalid index (index=" + index + ", version=" + version + ")");
			}
			return parent.bytes[index + 4];
		} else {
			// TODO: could be more efficient with binary search.
			// recursive case
			int offset = parent.offsets[version-1];
			int delta = 0;
			// Determine replacement count
			int n = read_i32(parent.bytes,offset);
			//
			offset = offset + 4;
			//
			int payload = (n * 12) + offset;
			// Apply replacement delta's
			for(int i=0;i!=n;++i) {
				int o = read_i32(parent.bytes, offset);
				int s = read_i32(parent.bytes, offset + 4);
				int l = read_i32(parent.bytes, offset + 8);
				if(index < o) {
					return blob_read(index - delta, parent, version - 1);
				} else if(index < (o + l)) {
					return parent.bytes[payload + (index-o)];
				} else {
					delta += (l-s);
					payload += l;
				}
				offset += 12;
			}
			return blob_read(index - delta, parent, version - 1);
		}
	}

	private static int read_i32(byte[] bytes, int offset) {
		byte b1 = bytes[offset];
		byte b2 = bytes[offset + 1];
		byte b3 = bytes[offset + 2];
		byte b4 = bytes[offset + 3];
		// Recombine bytes
		return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
	}
}
