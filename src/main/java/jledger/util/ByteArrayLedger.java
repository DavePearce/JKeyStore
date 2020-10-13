package jledger.util;

import java.util.Arrays;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Ledger;
import jledger.layouts.Array;
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
		offsets[0] = append(0, value.getBlob().readAll());
	}

	@Override
	public int versions() {
		return versions;
	}

	public int size() {
		return offsets[versions - 1];
	}

	@Override
	public T get(int v) {
		int offset = (v == 0) ? 0 : offsets[v - 1];
		// FIXME: following is broken for two reasons. Firstly, for anything other than
		// initial version, it's not doing the right thing since the data at the given
		// offset represents an encoded transaction, not a snapshot. Secondly, unclear
		// whether is sensible for initial blob to include data for all subsequent
		// transactions.
		return layout.read(new ByteBlob(bytes), offset);
	}

	@Override
	public void put(T object) {
		int offset = offsets[versions - 1];
		// Determine enclosing blob
		Content.Blob blob = object.getBlob();
		// Ensure enough space for transaction
		ensureVersions(versions + 1);
		// Record transaction end-point
		offsets[versions++] = append(blob, offset);
	}

	private int append(Content.Blob b, int offset) {
		if (b instanceof Content.Diff) {
			Content.Diff d = (Content.Diff) b;
			// FIXME: this is broken because doesn't include count of replacements. This is
			// challenging because need to extract this from parent somehow I guess?

			// Append parent replacements (if any)
			offset = append(d.parent(),offset);
			// Recursive case
			for (int i = 0; i != d.count(); ++i) {
				offset = append(d.getReplacement(i), offset);
			}
			// done;
			return offset;
		} else if (b instanceof ByteBlob) {
			ByteBlob bb = (ByteBlob) b;
			byte[] bs = bb.readAll();
			if (bs == bytes) {
				// Terminating case
				return offset;
			}
		}
		throw new IllegalArgumentException("invalid blob for ledger");
	}

	private int append(Content.Replacement b, int offset) {
		byte[] bs = b.bytes();
		int o = b.offset();
		int s = b.size();
		System.out.println("OFFSET = " + offset + ", |BS|=" + bs.length);
		// Determine space required
		final int n = offset + 2 + bs.length;
		ensureCapacity(n);
		// FIXME: this is broken as it doesn't handle large enough replacements.
		if(o > 127 || s > 127) {
			throw new UnsupportedOperationException("implement me!");
		}
		bytes[offset++] = (byte) o;
		bytes[offset++] = (byte) s;
		System.arraycopy(bs, 0, bytes, offset, bs.length);
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

	private static class Test extends Array.Proxy<Integer, Test> {
		public static final Array.Layout<Integer, Test> LAYOUT = Array.LAYOUT(INT, Test::new);

		public Test() {
			super(LAYOUT, ByteBlob.EMPTY.insertInt(0, 123), 0);
		}

		public Test(Blob blob, int offset) {
			super(LAYOUT, blob, offset);
		}

		public Test increment() {
			int value = INT.read(blob, offset);
			Content.Blob b = INT.write(value + 1, blob, offset);
			return new Test(b, offset);
		}
	}

	public static void main(String[] args) {
		ByteArrayLedger<Test> l = new ByteArrayLedger<>(new Test());
		System.out.println("VERSIONS: " + l.versions + ", SIZE: " + l.size() + " bytes.");
		l.put(l.get(0).increment());
		System.out.println("VERSIONS: " + l.versions + ", SIZE: " + l.size() + " bytes.");
	}
}
