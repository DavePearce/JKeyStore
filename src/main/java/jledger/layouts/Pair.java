package jledger.layouts;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.util.AbstractLayout;
import jledger.util.AbstractProxy;
import jledger.util.ByteBlob;

import static jledger.layouts.Primitive.*;

public class Pair {
	public interface Constructor<L,T> {
		public T apply(L layout, Content.Blob blob, int offset);
	}

	/**
	 * Represents a layout which consists of a two fields. The following
	 * illustrates:
	 *
	 * <pre>
	 *   |00|01|02|03|04|05|06|07|08|
	 *   +--------+--------+--------+
	 * 0 |        |        |        |
	 *   +-----+--+-----+--+-----+--+
	 * 1 |     |  |     |  |     |  |
	 *   +-----+--+-----+--+-----+--+
	 * 2 |00 ff|01|00 00|00|af 00|00|
	 * </pre>
	 *
	 * This layout consists of repeating sequence (level 0) of static layouts (level
	 * 1). The static layout consists of a two byte field followed by a one byte
	 * field.
	 *
	 * @param <S>
	 * @param <T>
	 * @param first  Layout for the first field.
	 * @param second Layout for the second field.
	 * @return
	 */
	public static <S, T, U extends Proxy<S, T, U>> Content.Layout<U> create(Content.Layout<S> first,
			Content.Layout<T> second, Constructor<Layout<S, T, U>, U> constructor) {
		return new DynamicLayout<>(first, second, constructor);
	}

	/**
	 * Represents a static layout which consists of a two fields. The
	 * following illustrates:
	 *
	 * <pre>
	 *   |00|01|02|03|04|05|06|07|08|
	 *   +--------+--------+--------+
	 * 0 |        |        |        |
	 *   +-----+--+-----+--+-----+--+
	 * 1 |     |  |     |  |     |  |
	 *   +-----+--+-----+--+-----+--+
	 * 2 |00 ff|01|00 00|00|af 00|00|
	 * </pre>
	 *
	 * This layout consists of repeating sequence (level 0) of static layouts (level
	 * 1). The static layout consists of a two byte field followed by a one byte
	 * field.
	 *
	 * @param <S>
	 * @param <T>
	 * @param first Static layout for the first field.
	 * @param second Static layout for the second field.
	 * @return
	 */
	public static <S, T, U extends Proxy<S, T, U>> Content.StaticLayout<U> create(Content.StaticLayout<S> first,
			Content.StaticLayout<T> second, Constructor<Layout<S, T, U>, U> constructor) {
		return new StaticLayout<>(first, second, constructor);
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

	public abstract static class Layout<S, T, U extends Proxy<S, T, U>> extends AbstractLayout<U> {
		protected abstract S getFirst(Content.Blob blob, int offset);

		protected abstract T getSecond(Content.Blob blob, int offset);

		protected abstract Content.Blob setFirst(S value, Content.Blob blob, int offset);

		protected abstract Content.Blob setSecond(T value, Content.Blob blob, int offset);
	}

	private static class DynamicLayout<S, T, U extends Proxy<S, T, U>> extends Layout<S, T, U> {
		protected final Content.Layout<S> first;
		protected final Content.Layout<T> second;
		protected final Constructor<Layout<S, T, U>, U> constructor;

		public DynamicLayout(Content.Layout<S> first, Content.Layout<T> second,
				Constructor<Layout<S, T, U>, U> constructor) {
			this.first = first;
			this.second = second;
			this.constructor = constructor;
		}

		@Override
		public S getFirst(Content.Blob blob, int offset) {
			return first.read(blob, offset);
		}

		@Override
		public T getSecond(Content.Blob blob, int offset) {
			int n = first.sizeOf(blob, offset);
			return second.read(blob, offset + n);
		}

		@Override
		public Content.Blob setFirst(S value, Content.Blob blob, int offset) {
			return first.write(value, blob, offset);
		}

		@Override
		public Content.Blob setSecond(T value, Content.Blob blob, int offset) {
			int n = first.sizeOf(blob, offset);
			return second.write(value, blob, offset);
		}

		@Override
		public int sizeOf(Blob blob, int offset) {
			int f = first.sizeOf(blob, offset);
			return f + second.sizeOf(blob, offset + f);
		}

		@Override
		public Blob initialise(Blob blob, int offset) {
			// Initialise first item
			blob = first.initialise(blob, offset);
			// Determine it's size
			int f = first.sizeOf(blob, offset);
			// Initialise second item
			return second.initialise(blob, offset + f);
		}

		@Override
		public U read(Blob blob, int offset) {
			return constructor.apply(this, blob, offset);
		}
	}

	private static class StaticLayout<S, T, U extends Proxy<S, T, U>> extends Layout<S, T, U>
			implements Content.StaticLayout<U> {
		protected final Content.StaticLayout<S> first;
		protected final Content.StaticLayout<T> second;
		protected final Constructor<Layout<S, T, U>, U> constructor;

		public StaticLayout(Content.StaticLayout<S> first, Content.StaticLayout<T> second,
				Constructor<Layout<S, T, U>, U> constructor) {
			this.first = first;
			this.second = second;
			this.constructor = constructor;
		}

		@Override
		public S getFirst(Content.Blob blob, int offset) {
			return first.read(blob, offset);
		}

		@Override
		public T getSecond(Content.Blob blob, int offset) {
			int n = first.sizeOf();
			return second.read(blob, offset + n);
		}

		@Override
		public Content.Blob setFirst(S value, Content.Blob blob, int offset) {
			return first.write(value, blob, offset);
		}

		@Override
		public Content.Blob setSecond(T value, Content.Blob blob, int offset) {
			int n = first.sizeOf();
			return second.write(value, blob, offset);
		}

		@Override
		public int sizeOf() {
			return first.sizeOf() + second.sizeOf();
		}

		@Override
		public int sizeOf(Blob blob, int offset) {
			int f = first.sizeOf(blob, offset);
			return f + second.sizeOf(blob, offset + f);
		}

		@Override
		public Blob initialise(Blob blob, int offset) {
			// Initialise first item
			blob = first.initialise(blob, offset);
			// Determine it's size
			int f = first.sizeOf(blob, offset);
			// Initialise second item
			return second.initialise(blob, offset + f);
		}

		@Override
		public U read(Blob blob, int offset) {
			return constructor.apply(this, blob, offset);
		}
	}

	public static class TestProxy extends Proxy<Integer, Integer, TestProxy> {
		public static final Content.Layout<TestProxy> LAYOUT = create(INT32(1), INT32(2), TestProxy::new);

		public TestProxy(Layout<Integer, Integer, TestProxy> layout, Blob blob, int offset) {
			super(layout, blob, offset);
		}
	}

	public static void main(String[] args) {
		Content.Blob blob = ByteBlob.EMPTY;
		// Initialise proxy
		blob = TestProxy.LAYOUT.initialise(blob, 0);
		// Access proxy
		TestProxy tp = TestProxy.LAYOUT.read(blob, 0);
		//
		System.out.println("GOT: " + tp.getFirst() + ", " + tp.getSecond());
	}
}
