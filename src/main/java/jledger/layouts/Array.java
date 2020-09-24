package jledger.layouts;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Constructor;
import jledger.util.AbstractProxy;

public class Array {

	/**
	 * A proxy for an array of elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public interface Proxy<T> extends Content.Proxy {

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
	// Helpers (Arrays)
	// =================================================================

	/**
	 * A proxy for an array of dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	private static class DynamicProxy<T, U extends Proxy<T>> extends AbstractProxy<U, Layout<T, U>>
			implements Proxy<T> {

		public DynamicProxy(Layout<T,U> layout) {
			super(layout);
		}

		public DynamicProxy(Layout<T,U> layout, Content.Blob blob, int offset) {
			super(layout, blob, offset);
		}

		/**
		 * Get the number of elements in the array.
		 *
		 * @return
		 */
		@Override
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
		@Override
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
				for (int i = 0; i < index; ++i) {
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
		@Override
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
				for (int i = 0; i < index; ++i) {
					coffset += child.sizeOf(blob, offset);
				}
				// Overwrite existing child
				return child.write(value, blob, coffset);
			}
		}

		@Override
		public Content.Blob insert(int index, T value) {
			final Content.Layout<T> child = layout.child;
			//
			if (index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset;
				// Skip over length
				coffset += 4;
				// Locate child
				for (int i = 0; i < index; ++i) {
					coffset += child.sizeOf(blob, offset);
				}
				// Overwrite existing child
				return child.insert(value, blob, coffset);
			}
		}

		@Override
		public Content.Blob append(T value) {
			final int n = length();
			final Content.Layout<T> child = layout.child;
			//
			int coffset = offset;
			// Skip over length
			coffset += 4;
			// Locate child
			for (int i = 0; i < n; ++i) {
				coffset += child.sizeOf(blob, offset);
			}
			// Overwrite existing child
			return child.insert(value, blob, coffset);
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
	 * An array of arbitrarily many dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static abstract class Layout<T, U extends Proxy<T>> implements Content.Layout<U> {
		protected final Content.Layout<T> child;
		protected final T[] values;

		@SafeVarargs
		public Layout(Content.Layout<T> child, T... values) {
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
				// Insert the initial element value
				blob = child.insert(values[i], blob, offset);
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
		public abstract U read(Blob blob, int offset);

		@Override
		public Blob write(U proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}

		@Override
		public Blob insert(U proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, 0, bytes);
		}
	}
}
