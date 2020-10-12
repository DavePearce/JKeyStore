package jledger.layouts;

import jledger.core.Content;
import jledger.core.Content.Blob;

public class Primitive {

	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Int32Layout INT32 = INT32(0);

	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Int32Layout INT32(int v) {
		return new Int32Layout(v);
	}

	public static class Int32Layout implements Content.StaticLayout<Integer> {
		private final int n;

		public Int32Layout(int n) {
			this.n = n;
		}

		@Override
		public int sizeOf(Content.Blob blob, int offset) {
			return 4;
		}

		@Override
		public int sizeOf() {
			return 4;
		}

		public int readInt(Content.Blob blob, int offset) {
			return blob.readInt(offset);
		}

		public Content.Blob writeInt(int value, Content.Blob blob, int offset) {
			return blob.writeInt(offset, value);
		}

		public Content.Blob insertInt(int value, Content.Blob blob, int offset) {
			return blob.insertInt(offset, value);
		}

		@Override
		public Integer read(Blob blob, int offset) {
			return readInt(blob,offset);
		}

		@Override
		public Content.Blob write(Integer i, Blob blob, int offset) {
			return writeInt(i, blob, offset);
		}

		@Override
		public Content.Blob insert(Integer i, Blob blob, int offset) {
			return insertInt(i, blob, offset);
		}
	}

}
