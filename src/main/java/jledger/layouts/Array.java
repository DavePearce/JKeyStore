package jledger.layouts;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Constructor;
import jledger.util.AbstractProxy;

public class Array {

	/**
	 * A proxy for an array of dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static class Proxy<T, U extends Proxy<T, U>> implements Content.Proxy {
		protected final Layout<T, U> layout;
		protected final Content.Blob blob;
		protected final int offset;

		public Proxy(Layout<T,U> layout, Content.Blob blob, int offset) {
			this.layout = layout;
			this.blob = blob;
			this.offset = offset;
		}

		/**
		 * Get the number of elements in the array.
		 *
		 * @return
		 */
		public int length() {
			return blob.readInt(offset);
		}

		@Override
		public Blob getBlob() {
			return blob;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public Content.Layout<?> getLayout() {
			return layout;
		}

		@Override
		public int sizeOf() {
			return layout.sizeOf(blob, offset);
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
		public byte[] toBytes() {
			int size = layout.sizeOf(blob, offset);
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

	/**
	 * An array of arbitrarily many dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static abstract class Layout<T, U extends Proxy<T,U>> implements Content.Layout<U> {
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
