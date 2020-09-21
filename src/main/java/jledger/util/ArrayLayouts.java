package jledger.util;

import java.util.Arrays;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Constructor;
import jledger.core.Content.Position;

public class ArrayLayouts {

	public interface Array<T> extends Content.Proxy<Array<T>> {
		/**
		 * Get size of the underlying array
		 *
		 * @return
		 */
		public int size();

		/**
		 * Set a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public T get(int index);

		/**
		 * Set a given index within this array proxy.
		 *
		 * @param index
		 * @param value
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob set(int index, T value);
	}

//	/**
//	 * Represents a fixed-size repeating sequence of a given layout. Since the array
//	 * has a known fixed size, there is no need to store the array length.
//	 * Furthermore, the size of the layout is static when the child layout is
//	 * static.
//	 *
//	 * @param n
//	 * @param child
//	 * @return
//	 */
//	public static <T> Content.Layout<T[]> STATIC_ARRAY(int n, Content.Layout<T> child) {
//		return new StaticArrayLayout<>(n,child);
//	}
//
//	/**
//	 * Represents a fixed-size repeating sequence of a given layout. Since the array
//	 * has a known fixed size, there is no need to store the array length.
//	 * Furthermore, the size of the layout is static when the child layout is
//	 * static.
//	 *
//	 * @param n
//	 * @param child
//	 * @return
//	 */
//	public static <T> Content.Layout<T[]> STATIC_ARRAY(int n, Content.StaticLayout<T> child) {
//		return new StaticArrayStaticLayout<>(n, child);
//	}

	/**
	 * Represents a dynamic-size repeating sequence of a given layout. Since the
	 * array has a unknown size, this is stored as an <code>int32</code>.
	 *
	 * @param n
	 * @param child
	 * @return
	 */
	public static <T> Content.Layout<Array<T>> DYNAMIC_ARRAY(Content.Layout<T> child) {
		return new DynamicArrayLayout<>(child);
	}

	// =================================================================
	// Helpers
	// =================================================================

	private static class DynamicArrayLayout<T> implements Content.Layout<Array<T>> {
		protected final Content.Layout<T> child;

		public DynamicArrayLayout(Content.Layout<T> child) {
			this.child = child;
		}

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			// Arrays initialised as empty
			return PrimitiveLayouts.INT32.initialise(blob, offset);
		}

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
		public Array<T> read(Blob blob, int offset) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Blob write(Array<T> proxy, Blob blob, int offset) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
