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
	public static Content.Layout STATIC_ARRAY(int n, Content.Layout child) {
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
	public static Content.Layout STATIC_ARRAY(int n, Content.StaticLayout child) {
		return new StaticArrayStaticLayout<>(n, child);
	}

	/**
	 * Represents a fixed-size repeating sequence of a given layout. Since the array
	 * has a known fixed size, there is no need to store the array length.
	 * Furthermore, the size of the layout is static when the child layout is
	 * static.
	 *
	 * @param n
	 * @param child
	 * @param dummy This array is cloned to construct the new array.
	 * @return
	 */
	public static <T> Content.ConstructorLayout<T[]> STATIC_ARRAY(int n, Content.ConstructorLayout<T> child,
			T... dummy) {
		return new StaticArrayConstructorLayout<>(n, child, dummy);
	}

	/**
	 * Represents a fixed-size repeating sequence of a given layout. Since the array
	 * has a known fixed size, there is no need to store the array length.
	 * Furthermore, the size of the layout is static when the child layout is
	 * static.
	 *
	 * @param n
	 * @param child
	 * @param dummy This array is cloned to construct the new array.
	 * @return
	 */
	public static <T> Content.StaticConstructorLayout<T[]> STATIC_ARRAY(int n, Content.StaticConstructorLayout<T> child,
			T... dummy) {
		return new StaticArrayStaticConstructorLayout<>(n, child, dummy);
	}

	/**
	 * Represents a dynamic-size repeating sequence of a given layout. Since the
	 * array has a unknown size, this is stored as an <code>int32</code>.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static Content.Layout DYNAMIC_ARRAY(Content.Layout child) {
		return new DynamicArrayLayout<>(child);
	}


	/**
	 * Represents a dynamic-size repeating sequence of a given layout. Since the
	 * array has a unknown size, this is stored as an <code>int32</code>.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static <T> Content.ConstructorLayout<T[]> DYNAMIC_ARRAY(Content.ConstructorLayout<T> child, T... dummy) {
		return new DynamicArrayConstructorLayout<>(child, dummy);
	}

	// =================================================================
	// Helpers
	// =================================================================

	private static class StaticArrayLayout<T extends Content.Layout> extends AbstractLayouts.NonTerminal {
		protected final int n;
		protected final T child;

		public StaticArrayLayout(int n, T child) {
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
		protected Content.Layout getChild(int index, Content.Blob blob, int offset) {
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
	}

	private static class StaticArrayConstructorLayout<T> extends StaticArrayLayout<Content.ConstructorLayout<T>>
			implements Content.ConstructorLayout<T[]> {
		private final T[] dummy;

		public StaticArrayConstructorLayout(int n, Content.ConstructorLayout<T> child, T... dummy) {
			super(n, child);
			this.dummy = dummy;
		}

		@Override
		public T[] read(Blob blob, int offset) {
			T[] arr = Arrays.copyOf(dummy, n);
			//
			for (int i = 0; i != arr.length; ++i) {
				// Construct ith item
				arr[i] = child.read(blob, offset);
				// Advance offset
				offset += child.size(blob, offset);
			}
			return arr;
		}
	}

	private static class StaticArrayStaticLayout<T extends Content.StaticLayout>
			extends AbstractLayouts.StaticNonTerminal {
		protected final int n;
		protected final T child;

		public StaticArrayStaticLayout(int n, T child) {
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
		protected Content.Layout getChild(int index, Content.Blob blob, int offset) {
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
	}

	private static class StaticArrayStaticConstructorLayout<T> extends StaticArrayStaticLayout<Content.StaticConstructorLayout<T>>
			implements Content.StaticConstructorLayout<T[]> {
		private final T[] dummy;

		public StaticArrayStaticConstructorLayout(int n, Content.StaticConstructorLayout<T> child, T... dummy) {
			super(n, child);
			this.dummy = dummy;
		}

		@Override
		public T[] read(Blob blob, int offset) {
			// Determine child size
			final int size = child.size();
			// Construct array
			T[] arr = Arrays.copyOf(dummy, n);
			//
			for (int i = 0; i != arr.length; ++i) {
				// Construct ith item
				arr[i] = child.read(blob, offset);
				// Advance offset
				offset += size;
			}
			return arr;
		}
	}

	private static class DynamicArrayLayout<T extends Content.Layout>
			extends AbstractLayouts.NonTerminal {
		protected final T child;

		public DynamicArrayLayout(T child) {
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
		protected Content.Layout getChild(int index, Content.Blob blob, int offset) {
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
	}


	private static class DynamicArrayConstructorLayout<T> extends DynamicArrayLayout<Content.ConstructorLayout<T>>
			implements Content.ConstructorLayout<T[]> {
		private final T[] dummy;

		public DynamicArrayConstructorLayout(Content.ConstructorLayout<T> child, T... dummy) {
			super(child);
			this.dummy = dummy;
		}

		@Override
		public T[] read(Blob blob, int offset) {
			// Determine number of elements
			int n = numberOfChildren(blob, offset);
			// Advance past length pointer
			offset = offset + 4;
			// Construct array from dummy
			T[] arr = Arrays.copyOf(dummy, n);
			//
			for (int i = 0; i != arr.length; ++i) {
				// Construct ith item
				arr[i] = child.read(blob, offset);
				// Advance offset
				offset += child.size(blob, offset);
			}
			return arr;
		}
	}
}
