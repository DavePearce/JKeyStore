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
	public static <T> Content.Layout<Array<T>> ARRAY(Content.Layout<T> child) {
		return new ArrayLayout<>(child);
	}

	/**
	 * A proxy for an array of elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static class Array<T> extends AbstractLayouts.Proxy<Array<T>, ArrayLayout<T>> {
		public Array(ArrayLayout<T> layout) {
			super(layout);
		}

		public Array(ArrayLayout<T> layout, Content.Blob blob, int offset) {
			super(layout, blob, offset);
		}

		/**
		 * Get the number of elements in the array.
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
			if(index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset;
				// Skip over length
				coffset += 4;
				// Locate child
				for (int i = 1; i < index; ++i) {
					coffset += layout.child.sizeOf(blob, offset);
				}
				// Read out child
				return layout.child.read(blob, coffset);
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
			if(index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset;
				// Skip over length
				coffset += 4;
				// Locate child
				for (int i = 1; i < index; ++i) {
					coffset += layout.child.sizeOf(blob, offset);
				}
				// Overwrite existing child
				return layout.child.write(value, blob, coffset);
			}
		}

		@Override
		public String toString() {
			String r = "[";
			final int n = length();
			for(int i=0;i<n;++i) {
				if(i!=0) {
					r += ",";
				}
				r += get(i);
			}
			return r + "]";
		}
	}

	// =================================================================
	// Helpers
	// =================================================================

	private static class ArrayLayout<T> implements Content.Layout<Array<T>> {
		protected final Content.Layout<T> child;
		protected final T[] values;

		@SafeVarargs
		public ArrayLayout(Content.Layout<T> child, T... values) {
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
			return new Array<T>(this, blob, offset);
		}

		@Override
		public Blob write(Array<T> proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}
	}

	public static void main(String[] args) {
		Content.Blob blob = ByteBlob.EMPTY;
		Content.Layout<Array<Integer>> layout = new ArrayLayout<>(PrimitiveLayouts.INT32, 0);
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
