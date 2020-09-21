package jledger.util;

import java.util.Arrays;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Position;

public class ArrayLayouts {

	/**
	 * Represents a fixed-size repeating sequence of a given layout. Since the array
	 * has a known fixed size, there is no need to store the array length.
	 * Furthermore, the size of the layout is static when the child layout is
	 * static.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static <T> Content.Layout<T[]> STATIC_ARRAY(int n, Content.Layout<T> child) {
		return new StaticArrayLayout<>(n,child);
	}

	/**
	 * Represents a fixed-size repeating sequence of a given layout. Since the array
	 * has a known fixed size, there is no need to store the array length.
	 * Furthermore, the size of the layout is static when the child layout is
	 * static.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static <T> Content.Layout<T[]> STATIC_ARRAY(int n, Content.StaticLayout<T> child) {
		return new StaticArrayStaticLayout<>(n, child);
	}

	/**
	 * Represents a dynamic-size repeating sequence of a given layout. Since the
	 * array has a unknown size, this is stored as an <code>int32</code>.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static <T> Content.Layout<T[]> DYNAMIC_ARRAY(Content.Layout<T> child) {
		return new DynamicArrayLayout<>(child);
	}

	// =================================================================
	// Helpers
	// =================================================================


	private static class StaticArrayLayout<T> extends AbstractLayouts.NonTerminal<T[]> {
		protected final int n;
		protected final Content.Layout<T> child;

		public StaticArrayLayout(int n, Content.Layout<T> child) {
			super((b, i) -> construct(b, i, n, child));
			this.n = n;
			this.child = child;
		}

		@Override
		public int numberOfChildren(Content.Blob blob, int offset) {
			return n;
		}

		@Override
		public int size(Content.Blob blob, int offset) {
			int start = offset;
			for (int i = 0; i != n; ++i) {
				offset += child.size(blob, offset);
			}
			return offset - start;
		}

		@Override
		protected Content.Layout<T> getChild(int index, Content.Blob blob, int offset) {
			if(index < 0 || index >= n) {
				throw new IndexOutOfBoundsException();
			} else {
				return child;
			}
		}

		@Override
		protected int getChildOffset(int c, Content.Blob blob, int offset) {
			for (int i = 0; i < c; ++i) {
				offset += child.size(blob, offset);
			}
			return offset;
		}

		protected static <S> S[] construct(Content.Blob blob, int offset, int n, Content.Layout<S> child) {
			if(n == 0) {
				return (S[]) new Object[0];
			}
			throw new IllegalArgumentException("IMPLEMENT ME");
		}
	}

	private static class StaticArrayStaticLayout<T>
			extends AbstractLayouts.StaticNonTerminal<T[]> {
		protected final int n;
		protected final Content.StaticLayout<T> child;

		public StaticArrayStaticLayout(int n, Content.StaticLayout<T> child) {
			super((b, i) -> construct(b, i, n, child));
			this.n = n;
			this.child = child;
		}

		@Override
		public int size() {
			return n * child.size();
		}

		@Override
		public int numberOfChildren(Content.Blob blob, int offset) {
			return n;
		}

		@Override
		public int size(Content.Blob blob, int offset) {
			return n * child.size();
		}

		@Override
		protected Content.Layout<?> getChild(int index, Content.Blob blob, int offset) {
			if (index < 0 || index >= n) {
				throw new IndexOutOfBoundsException();
			} else {
				return child;
			}
		}

		@Override
		protected int getChildOffset(int c, Content.Blob blob, int offset) {
			return c * child.size();
		}

		protected static <S> S[] construct(Content.Blob blob, int offset, int n, Content.Layout<S> child) {
			if(n == 0) {
				return (S[]) new Object[0];
			}
			throw new IllegalArgumentException("IMPLEMENT ME");
		}
	}

	private static class DynamicArrayLayout<T> extends AbstractLayouts.NonTerminal<T[]> {
		protected final Content.Layout<T> child;

		public DynamicArrayLayout(Content.Layout<T> child) {
			super((b, i) -> construct(b, i, child));
			this.child = child;
		}

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			// Arrays initialised as empty
			return PrimitiveLayouts.INT32.initialise(blob, offset);
		}

		@Override
		public int numberOfChildren(Content.Blob blob, int offset) {
			return 1 + PrimitiveLayouts.INT32.read_i32(null, blob, offset);
		}

		@Override
		public int size(Content.Blob blob, int offset) {
			int start = offset;
			int n = numberOfChildren(blob,offset);
			offset += 4;
			for (int i = 1; i < n; ++i) {
				offset += child.size(blob, offset);
			}
			return offset - start;
		}

		@Override
		public Content.Blob insert_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if (pos == null) {
				throw new IllegalArgumentException("cannot overwrite array with primitive");
			} else if (pos.index() == 0) {
				throw new IllegalArgumentException("cannot write directly to array length");
			}
			// NOTE: must insert before updating length to catch out-of-bounds errors.
			blob = super.insert_bit(value, pos, blob, offset);
			// Update length field (if appropriate)
			if (pos.child() == null) {
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				blob = PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
			// Done
			return blob;
		}

		@Override
		public Content.Blob insert_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if (pos == null) {
				throw new IllegalArgumentException("cannot overwrite array with primitive");
			} else if(pos.index() == 0) {
				throw new IllegalArgumentException("cannot write directly to array length");
			}
			// NOTE: must insert before updating length to catch out-of-bounds errors.
			blob = super.insert_i8(value, pos, blob, offset);
			// Update length field (if appropriate)
			if (pos.child() == null) {
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				blob = PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
			// Done
			return blob;
		}

		@Override
		public Content.Blob insert_i16(short value, Position pos, Content.Blob blob, int offset) {
			if (pos == null) {
				throw new IllegalArgumentException("cannot overwrite array with primitive");
			} else if(pos.index() == 0) {
				throw new IllegalArgumentException("cannot write directly to array length");
			}
			// NOTE: must insert before updating length to catch out-of-bounds errors.
			blob = super.insert_i16(value, pos, blob, offset);
			// Update length field (if appropriate)
			if (pos.child() == null) {
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				blob = PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
			// Done
			return blob;
		}

		@Override
		public Content.Blob insert_i32(int value, Position pos, Content.Blob blob, int offset) {
			if (pos == null) {
				throw new IllegalArgumentException("cannot overwrite array with primitive");
			} else if(pos.index() == 0) {
				throw new IllegalArgumentException("cannot write directly to array length");
			}
			// NOTE: must insert before updating length to catch out-of-bounds errors.
			blob = super.insert_i32(value, pos, blob, offset);
			// Update length field (if appropriate)
			if (pos.child() == null) {
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				blob = PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
			// Done
			return blob;
		}

		@Override
		public Content.Blob insert_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if (pos == null) {
				throw new IllegalArgumentException("cannot overwrite array with primitive");
			} else if (pos.index() == 0) {
				throw new IllegalArgumentException("cannot write directly to array length");
			}
			// NOTE: must insert before updating length to catch out-of-bounds errors.
			blob = super.insert_bytes(value, pos, blob, offset);
			// Update length field (if appropriate)
			if (pos.child() == null) {
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				blob = PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
			// Done
			return blob;
		}

		@Override
		public Content.Blob append_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				return super.append_bit(value, pos, blob, offset);
			} else {
				int size = size(blob,offset);
				// Insert new value
				blob = child.insert_bit(value,null,blob,offset + size);
				// Update length field (if appropriate)
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				return PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
		}

		@Override
		public Content.Blob append_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				return super.append_i8(value, pos, blob, offset);
			} else {
				int size = size(blob,offset);
				// Insert new value
				blob = child.insert_i8(value,null,blob,offset + size);
				// Update length field (if appropriate)
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				return PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
		}

		@Override
		public Content.Blob append_i16(short value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				return super.append_i16(value, pos, blob, offset);
			} else {
				int size = size(blob,offset);
				// Insert new value
				blob = child.insert_i16(value,null,blob,offset + size);
				// Update length field (if appropriate)
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				return PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
		}

		@Override
		public Content.Blob append_i32(int value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				return super.append_i32(value, pos, blob, offset);
			} else {
				int size = size(blob,offset);
				// Insert new value
				blob = child.insert_i32(value,null,blob,offset + size);
				// Update length field (if appropriate)
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				return PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
		}

		@Override
		public Content.Blob append_i64(long value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				return super.append_i64(value, pos, blob, offset);
			} else {
				int size = size(blob,offset);
				// Insert new value
				blob = child.insert_i64(value,null,blob,offset + size);
				// Update length field (if appropriate)
				int n = numberOfChildren(blob, offset);
				// NOTE: makes sense since number of children already includes length field.
				return PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
		}

		@Override
		public Content.Blob append(Content.Proxy value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				return super.append(value, pos, blob, offset);
			} else {
				int size = size(blob,offset);
				// Insert new value
				blob = child.insert(value, null, blob, offset + size);
				// Update length field (if appropriate)
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				return PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
		}

		@Override
		public Content.Blob append_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				return super.append_bytes(value, pos, blob, offset);
			} else {
				int size = size(blob,offset);
				// Insert new value
				blob = child.append_bytes(value,null,blob,offset + size);
				// Update length field (if appropriate)
				int n = numberOfChildren(blob, offset);
				// NOTE: makse sense since number of children already includes length field.
				return PrimitiveLayouts.INT32.write_i32(n, null, blob, offset);
			}
		}

		@Override
		protected Content.Layout<?> getChild(int index, Content.Blob blob, int offset) {
			if(index == 0) {
				return PrimitiveLayouts.INT32;
			} else {
				return child;
			}
		}

		@Override
		protected int getChildOffset(int c, Content.Blob blob, int offset) {
			if(c > 0) {
				offset += 4;
				for (int i = 1; i < c; ++i) {
					offset += child.size(blob, offset);
				}
			}
			return offset;
		}

		protected static <S> S[] construct(Content.Blob blob, int offset, Content.Layout<S> child) {
			throw new IllegalArgumentException("IMPLEMENT ME");
		}
	}
}
