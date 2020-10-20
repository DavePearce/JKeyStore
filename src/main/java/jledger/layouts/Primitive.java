package jledger.layouts;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;
import jledger.util.Byte;

public class Primitive {

	// =======================================================================
	// Interface
	// =======================================================================

	/**
	 * Describes a fixed-width 16bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final ShortLayout SHORT = new ShortLayout();

	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final IntLayout INT = new IntLayout();

	/**
	 * Describes a dynamically sized array of bytes.
	 */
	public static final ByteArrayLayout BYTE_ARRAY = new ByteArrayLayout();

	public static final class ByteArray implements Content.Proxy {
		private final Content.Blob blob;
		private final int offset;

		public ByteArray(byte... bytes) {
			this.blob = Byte.Blob.EMPTY.insertInt(0, bytes.length).insertBytes(4, bytes);
			this.offset = 0;
		}

		public ByteArray(Content.Blob blob, int offset) {
			this.blob = blob;
			this.offset = offset;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public Blob getBlob() {
			return blob;
		}

		@Override
		public Layout<?> getLayout() {
			return BYTE_ARRAY;
		}

		public int length() {
			return blob.readInt(offset);
		}

		public byte get(int index) {
			return blob.readByte(offset + 4 + index);
		}

		public byte[] getAll() {
			int n = blob.readInt(offset);
			return blob.readBytes(offset + 4,n);
		}

		@Override
		public int sizeOf() {
			return BYTE_ARRAY.sizeOf(blob, offset);
		}

		@Override
		public byte[] toBytes() {
			int size = BYTE_ARRAY.sizeOf(blob, offset);
			return blob.readBytes(offset, size);
		}

		@Override
		public String toString() {
			String r = "[";
			final int n = length();
			for (int i = 0; i < n; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += get(i);
			}
			return r + "]";
		}
	}

	// =======================================================================
	// Implementations
	// =======================================================================

	public static class ShortLayout implements Content.StaticLayout<Short> {
		@Override
		public int sizeOf(Content.Blob blob, int offset) {
			return 2;
		}

		@Override
		public int sizeOf() {
			return 2;
		}

		@Override
		public Short read(Blob blob, int offset) {
			return blob.readShort(offset);
		}

		@Override
		public Content.Blob write(Short i, Blob blob, int offset) {
			return blob.writeShort(offset, i);
		}

		@Override
		public Content.Blob insert(Short i, Blob blob, int offset) {
			return blob.insertShort(offset, i);
		}
	}

	public static class IntLayout implements Content.StaticLayout<Integer> {
		@Override
		public int sizeOf(Content.Blob blob, int offset) {
			return 4;
		}

		@Override
		public int sizeOf() {
			return 4;
		}

		@Override
		public Integer read(Blob blob, int offset) {
			return blob.readInt(offset);
		}

		@Override
		public Content.Blob write(Integer i, Blob blob, int offset) {
			return blob.writeInt(offset, i);
		}

		@Override
		public Content.Blob insert(Integer i, Blob blob, int offset) {
			return blob.insertInt(offset, i);
		}
	}

	public static class ByteArrayLayout implements Content.Layout<ByteArray> {

		@Override
		public int sizeOf(Blob blob, int offset) {
			return 4 + blob.readInt(offset);
		}

		@Override
		public ByteArray read(Blob blob, int offset) {
			return new ByteArray(blob, offset);
		}

		@Override
		public Blob write(ByteArray arr, Blob blob, int offset) {
			final byte[] bytes = arr.toBytes();
			// Replace existing bytes
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}

		@Override
		public Blob insert(ByteArray arr, Blob blob, int offset) {
			final byte[] bytes = arr.toBytes();
			return blob.replaceBytes(offset, 0, bytes);
		}
	}
}
