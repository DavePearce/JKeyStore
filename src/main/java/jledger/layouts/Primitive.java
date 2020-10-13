package jledger.layouts;

import jledger.core.Content;
import jledger.core.Content.Blob;

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

	public static class ByteArrayLayout implements Content.Layout<byte[]> {

		@Override
		public int sizeOf(Blob blob, int offset) {
			return 4 + blob.readInt(offset);
		}

		@Override
		public byte[] read(Blob blob, int offset) {
			final int n = blob.readInt(offset);
			return blob.readBytes(offset + 4, n);
		}

		@Override
		public Blob write(byte[] bytes, Blob blob, int offset) {
			final int n = blob.readInt(offset);
			// Replace existing bytes
			Blob b = blob.replaceBytes(offset + 4, n, bytes);
			// Update length
			return b.writeInt(offset, bytes.length);
		}

		@Override
		public Blob insert(byte[] proxy, Blob blob, int offset) {
			blob = blob.insertInt(offset, proxy.length);
			return blob.insertBytes(offset + 4, proxy);
		}
	}
}
