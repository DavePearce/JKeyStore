package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Constructor;
import jledger.core.Content.Position;

public class PrimitiveLayouts {
	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout<Integer> INT32(int v) {
		return new INT32(v);
	}

	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout<Integer> INT32 = INT32(0);

	public static class INT32 implements Content.StaticLayout<Integer> {
		private final int n;

		public INT32(int n) {
			this.n = n;
		}

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			return write_i32(n, blob, offset);
		}

		@Override
		public int size(Content.Blob blob, int offset) {
			return 4;
		}

		@Override
		public int size() {
			return 4;
		}

		public int read_i32(Content.Blob blob, int offset) {
			// FIXME: faster API would be nice
			byte b1 = blob.read(offset);
			byte b2 = blob.read(offset + 1);
			byte b3 = blob.read(offset + 2);
			byte b4 = blob.read(offset + 3);
			// Recombine bytes
			return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		}

		public Content.Blob write_i32(int value, Content.Blob blob, int offset) {
			// Convert value into bytes
			byte b1 = (byte) ((value >> 24) & 0xFF);
			byte b2 = (byte) ((value >> 16) & 0xFF);
			byte b3 = (byte) ((value >> 8) & 0xFF);
			byte b4 = (byte) (value & 0xFF);
			// FIXME: faster API would be nice
			return blob.replace(offset, 4, new byte[] { b1, b2, b3, b4 });
		}

		@Override
		public Integer read(Blob blob, int offset) {
			return read_i32(blob,offset);
		}

		@Override
		public Content.Blob write(Integer i, Blob blob, int offset) {
			return write_i32(i, blob, offset);
		}
	}
}
