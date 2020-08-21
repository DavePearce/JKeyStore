package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;

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
	public static <T> Content.ConstructorLayout<T[]> STATIC_ARRAY(int n, Content.ConstructorLayout<T> child) {
		return new StaticArrayConstructorLayout<T>(n,child);
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
	public static Content.Layout STATIC_ARRAY(int n, Content.Layout child) {
		return new StaticArrayLayout<Content.Layout>(n,child);
	}

	private static class StaticArrayLayout<T extends Content.Layout> extends AbstractLayouts.AbstractNonTerminalLayout {
		protected final int n;
		protected final T child;

		public StaticArrayLayout(int n, T child) {
			this.n = n;
			this.child = child;
		}

		@Override
		public int numberOfChildren() {
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

	private static class StaticArrayConstructorLayout<T>
			extends StaticArrayLayout<Content.ConstructorLayout<T>> implements Content.ConstructorLayout<T[]>{

		public StaticArrayConstructorLayout(int n, Content.ConstructorLayout<T> child) {
			super(n, child);
		}

		@Override
		public T[] construct(Blob blob, int offset) {
			throw new IllegalArgumentException("got here");
		}

	}
}
