package jledger.layouts;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Constructor;
import jledger.util.AbstractLayout;
import jledger.util.AbstractProxy;

import static jledger.layouts.Primitive.*;

import java.util.function.BiFunction;

public class Pair {

	public static <S, T, U extends Proxy<S, T, U>> Pair.Layout<S, T, U> LAYOUT(Content.Layout<S> first,
			Content.Layout<T> second, BiFunction<Content.Blob, Integer, U> constructor) {
		return new Layout<S, T, U>(first, second) {

			@Override
			public U read(Blob blob, int offset) {
				return constructor.apply(blob, offset);
			}

		};
	}

	/**
	 * Represents a pair of items in a given layout.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 * @param <T>
	 */
	public static class Proxy<S, T, U extends Proxy<S, T, U>> extends AbstractProxy<U, Layout<S, T, U>> {
		public Proxy(Layout<S, T, U> layout, Content.Blob blob, int offset) {
			super(layout, blob, offset);
		}

		public S getFirst() {
			return layout.getFirst(blob,offset);
		}

		public T getSecond() {
			return layout.getSecond(blob,offset);
		}

		public Blob setFirst(S value) {
			return layout.setFirst(value, blob, offset);
		}

		public Blob setSecond(T value) {
			return layout.setSecond(value, blob, offset);
		}

		@Override
		public String toString() {
			return "(" + getFirst() + "," + getSecond() + ")";
		}
	}

	public abstract static class Layout<S, T, U extends Proxy<S, T, U>> implements Content.Layout<U> {
		protected final Content.Layout<S> first;
		protected final Content.Layout<T> second;

		public Layout(Content.Layout<S> first, Content.Layout<T> second) {
			this.first = first;
			this.second = second;
		}

		public S getFirst(Content.Blob blob, int offset) {
			return first.read(blob, offset);
		}

		public T getSecond(Content.Blob blob, int offset) {
			int n = first.sizeOf(blob, offset);
			return second.read(blob, offset + n);
		}

		public Content.Blob setFirst(S value, Content.Blob blob, int offset) {
			return first.write(value, blob, offset);
		}

		public Content.Blob setSecond(T value, Content.Blob blob, int offset) {
			int n = first.sizeOf(blob, offset);
			return second.write(value, blob, offset + n);
		}

		public Blob initialise(Blob blob, int offset, S lhs, T rhs) {
			// Initialise first item
			blob = first.insert(lhs, blob, offset);
			// Determine it's size
			int n = first.sizeOf(blob, offset);
			// Initialise second item
			return second.insert(rhs, blob, offset + n);
		}

		@Override
		public int sizeOf(Blob blob, int offset) {
			int f = first.sizeOf(blob, offset);
			return f + second.sizeOf(blob, offset + f);
		}

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
