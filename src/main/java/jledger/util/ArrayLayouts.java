package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;

public class ArrayLayouts {

	/**
	 * Represents a dynamic-size repeating sequence of a given layout. Since the
	 * array has a unknown size, this is stored as an <code>int32</code>.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static <T> Content.Layout<Array<T>> ARRAY(Content.Layout<T> child, T... values) {
		return new DynamicArrayLayout<>(child, values);
	}

	/**
	 * Represents a dynamic-size repeating sequence of a given layout. Since the
	 * array has a unknown size, this is stored as an <code>int32</code>.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static <T> Content.Layout<Array<T>> ARRAY(Content.StaticLayout<T> child, T... values) {
		return new StaticArrayLayout<>(child, values);
	}
	
	/**
	 * A proxy for an array of elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public interface Array<T> extends Content.Proxy {
		
		/**
		 * Get the number of elements in the array.
		 * @return
		 */
		public int length();

		/**
		 * Read a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public T get(int index);

		/**
		 * Update a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob set(int index, T value);
		
		/**
		 * Insert a new value into this array, increasing its size by one.
		 * 
		 * @param index The index to insert the new element before.
		 * @param value
		 * @return
		 */
		public Content.Blob insert(int index, T value);
		
		/**
		 * 
		 * Append a value onto the end of this array, increasing its size by one.
		 * 
		 * @param value
		 * @return
		 */
		public Content.Blob append(T value);
	}

	// =================================================================
	// Helpers
	// =================================================================
	
	/**
	 * An array of arbitrarily many dynamically-sized elements.
	 * 
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	private static class DynamicArrayLayout<T> implements Content.Layout<Array<T>> {
		protected final Content.Layout<T> child;
		protected final T[] values;

		@SafeVarargs
		public DynamicArrayLayout(Content.Layout<T> child, T... values) {
			this.child = child;
			this.values = values;
		}

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			// FIXME: can we make this more efficient?
			blob = blob.insertInt(offset, values.length);
			//
			offset = offset + 4;
			//
			for(int i=0;i!=values.length;++i) {
				// FIXME: technically this is not correct because we could be overwriting some
				// other initialised data, but it should work assuming that nothing beyond index
				// has been initialised yet.
				blob = child.write(values[i], blob, offset);
				// Advance to next child
				offset = offset + child.sizeOf(blob, offset);
			}
			//
			return blob;
		}

		@Override
		public int sizeOf(Content.Blob blob, int offset) {
			int start = offset;
			int n = blob.readInt(offset);
			offset += 4;
			for (int i = 1; i < n; ++i) {
				offset += child.sizeOf(blob, offset);
			}
			return offset - start;
		}

		@Override
		public Array<T> read(Blob blob, int offset) {
			return new DynamicArray<T>(this, blob, offset);
		}

		@Override
		public Blob write(Array<T> proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}
	}

	/**
	 * An array of arbitrarily many statically-sized elements. Since all elements
	 * have the same fixed size, calculating their offsets is relatively easy.
	 * 
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	private static class StaticArrayLayout<T> implements Content.Layout<Array<T>> {
		protected final Content.StaticLayout<T> child;
		protected final T[] values;

		@SafeVarargs
		public StaticArrayLayout(Content.StaticLayout<T> child, T... values) {
			this.child = child;
			this.values = values;
		}

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			// FIXME: can we make this more efficient?
			blob = blob.insertInt(offset, values.length);
			//
			offset = offset + 4;
			//
			for(int i=0;i!=values.length;++i) {
				// FIXME: technically this is not correct because we could be overwriting some
				// other initialised data, but it should work assuming that nothing beyond index
				// has been initialised yet.
				blob = child.write(values[i], blob, offset);
				// Advance to next child
				offset = offset + child.sizeOf();
			}
			//
			return blob;
		}

		@Override
		public int sizeOf(Content.Blob blob, int offset) {
			int n = blob.readInt(offset);
			return 4 + (child.sizeOf() * n);
		}

		@Override
		public Array<T> read(Blob blob, int offset) {
			return new StaticArray<T>(this, blob, offset);
		}

		@Override
		public Blob write(Array<T> proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}
	}
	
	/**
	 * A proxy for an array of dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	private static class DynamicArray<T> extends AbstractLayouts.Proxy<Array<T>, DynamicArrayLayout<T>>
			implements Array<T> {
		
		public DynamicArray(DynamicArrayLayout<T> layout) {
			super(layout);
		}

		public DynamicArray(DynamicArrayLayout<T> layout, Content.Blob blob, int offset) {
			super(layout, blob, offset);
		}

		/**
		 * Get the number of elements in the array.
		 * 
		 * @return
		 */
		public int length() {
			return blob.readInt(offset);
		}
		/**
		 * Read a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public T get(int index) {
			final Content.Layout<T> child = layout.child;
			//
			if(index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset;
				// Skip over length
				coffset += 4;
				// Locate child
				for (int i = 1; i < index; ++i) {
					coffset += child.sizeOf(blob, offset);
				}
				// Read out child
				return child.read(blob, coffset);
			}
		}

		/**
		 * Update a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob set(int index, T value) {
			final Content.Layout<T> child = layout.child;
			//
			if(index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset;
				// Skip over length
				coffset += 4;
				// Locate child
				for (int i = 1; i < index; ++i) {
					coffset += child.sizeOf(blob, offset);
				}
				// Overwrite existing child
				return child.write(value, blob, coffset);
			}
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

	/**
	 * A proxy for an array of dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	private static class StaticArray<T> extends AbstractLayouts.Proxy<Array<T>, StaticArrayLayout<T>>
			implements Array<T> {
		public StaticArray(StaticArrayLayout<T> layout) {
			super(layout);
		}

		public StaticArray(StaticArrayLayout<T> layout, Content.Blob blob, int offset) {
			super(layout, blob, offset);
		}
		/**
		 * Get the number of elements in the array.
		 * 
		 * @return
		 */
		public int length() {
			return blob.readInt(offset);
		}
		/**
		 * Read a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public T get(int index) {
			final Content.StaticLayout<T> child = layout.child;
			//
			if(index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset + (index * child.sizeOf(blob, index));				
				// Read out child
				return child.read(blob, coffset);
			}
		}

		/**
		 * Update a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob set(int index, T value) {
			final Content.StaticLayout<T> child = layout.child;
			//
			if (index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset + 4 + (index * child.sizeOf());
				// Overwrite existing child
				return child.write(value, blob, coffset);
			}
		}
		
		public Content.Blob insert(int index, T value) {
			final Content.StaticLayout<T> child = layout.child;
			final int n = length();
			//
			if (index < 0 || index >= n) {
				throw new IllegalArgumentException();
			} else {
				// Determine location of insert element
				int coffset = offset + 4 + (index * child.sizeOf());
				// Update length of array
				Content.Blob b = blob.writeInt(offset, n + 1);
				// Overwrite existing child
				return child.insert(value, blob, coffset);
			}
		}
		
		public Content.Blob append(T value) {
			final Content.StaticLayout<T> child = layout.child;
			final int n = length();
			// Determine location of insert element
			int coffset = offset + 4 (n * child.sizeOf());
			// Update length of array
			Content.Blob b = blob.writeInt(offset, n + 1);
			// Overwrite existing child
			return child.insert(value, b, coffset);
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
	
	public static void main(String[] args) {
		Content.Blob blob = ByteBlob.EMPTY;
		Content.Layout<Array<Integer>> layout = ARRAY(PrimitiveLayouts.INT32, 0);
		blob = layout.initialise(blob, 0);
		Array<Integer> arr = layout.read(blob, 0);
		//
		System.out.println("ARRAY: " + arr);
		//
		blob = arr.set(1, 1);
		arr = layout.read(blob, 0);
		//
		System.out.println("ARRAY: " + arr);
	}
}
