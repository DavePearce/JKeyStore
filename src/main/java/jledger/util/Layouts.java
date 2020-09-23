package jledger.util;

import java.util.function.Function;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;

public class Layouts {

	// =================================================================================
	// Bridge
	// =================================================================================

	public static <S, T> BridgeLayout<S,T> BRIDGE(Content.Layout<T> child, Function<T,S> northBridge) {
		return new BridgeLayout<>(child, northBridge);
	}

	// =================================================================================
	// Primitives
	// =================================================================================

	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Int32Layout INT32 = INT32(0);

	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Int32Layout INT32(int v) {
		return new Int32Layout(v);
	}

	public static class Int32Layout implements Content.StaticLayout<Integer> {
		private final int n;

		public Int32Layout(int n) {
			this.n = n;
		}

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			return insertInt(n, blob, offset);
		}

		@Override
		public int sizeOf(Content.Blob blob, int offset) {
			return 4;
		}

		@Override
		public int sizeOf() {
			return 4;
		}

		public int readInt(Content.Blob blob, int offset) {
			return blob.readInt(offset);
		}

		public Content.Blob writeInt(int value, Content.Blob blob, int offset) {
			return blob.writeInt(offset, value);
		}

		public Content.Blob insertInt(int value, Content.Blob blob, int offset) {
			return blob.insertInt(offset, value);
		}

		@Override
		public Integer read(Blob blob, int offset) {
			return readInt(blob,offset);
		}

		@Override
		public Content.Blob write(Integer i, Blob blob, int offset) {
			return writeInt(i, blob, offset);
		}

		@Override
		public Content.Blob insert(Integer i, Blob blob, int offset) {
			return insertInt(i, blob, offset);
		}
	}

	// =================================================================================
	// Tuples
	// =================================================================================

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
	public static <S, T> Content.Layout<Pair<S, T>> PAIR(Content.Layout<S> first, Content.Layout<T> second) {
		return new PairLayout<>(first, second);
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
	public static <S, T> Content.StaticLayout<Pair<S, T>> PAIR(Content.StaticLayout<S> first, Content.StaticLayout<T> second) {
		return new StaticPairLayout<>(first, second);
	}

	/**
	 * Represents a layout which consists of three fields. The following
	 * illustrates:
	 *
	 * <pre>
	 *   |00|01|02|03|04|05|06|07|08|09|0A|
	 *   +----------+----------+----------+
	 * 0 |          |          |          |
	 *   +----+--+--+----+--+--+----+--+--+
	 * 1 |    |  |  |    |  |  |    |  |  |
	 *   +----+--+--+----+--+--+----+--+--+
	 * 2 |00ff|01|01|0000|cd|dd|af00|00|01|
	 * </pre>
	 *
	 * This layout consists of repeating sequence (level 0) of static layouts (level
	 * 1). The static layout consists of a two byte field followed by two one byte
	 * fields.
	 *
	 * @param <S>
	 * @param <T>
	 * @param first  Layout for the first field.
	 * @param second Layout for the second field.
	 * @param third  Layout for the third field.
	 * @return
	 */
	public static <S, T, U> Content.Layout<Triple<S, T,U>> TRIPLE(Content.Layout<S> first, Content.Layout<T> second, Content.Layout<U> third) {
		return new TripleLayout<>(first, second, third);
	}

	/**
	 * Represents a static layout which consists of three fields. The following
	 * illustrates:
	 *
	 * <pre>
	 *   |00|01|02|03|04|05|06|07|08|09|0A|
	 *   +----------+----------+----------+
	 * 0 |          |          |          |
	 *   +----+--+--+----+--+--+----+--+--+
	 * 1 |    |  |  |    |  |  |    |  |  |
	 *   +----+--+--+----+--+--+----+--+--+
	 * 2 |00ff|01|01|0000|cd|dd|af00|00|01|
	 * </pre>
	 *
	 * This layout consists of repeating sequence (level 0) of static layouts (level
	 * 1). The static layout consists of a two byte field followed by two one byte
	 * fields.
	 *
	 * @param <S>
	 * @param <T>
	 * @param first  Static layout for the first field.
	 * @param second Static layout for the second field.
	 * @param third  Static layout for the third field.
	 * @return
	 */
	public static <S, T, U> Content.StaticLayout<Triple<S, T,U>> TRIPLE(Content.StaticLayout<S> first, Content.StaticLayout<T> second, Content.StaticLayout<U> third) {
		return new StaticTripleLayout<>(first, second, third);
	}


	/**
	 * Represents a pair of items in a given layout.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 * @param <T>
	 */
	public static class Pair<S, T> extends AbstractProxy<Pair<S, T>, PairLayout<S, T, ?, ?>> {
		public Pair(PairLayout<S, T, ?, ?> layout, Content.Blob blob, int offset) {
			super(layout, blob, offset);
		}

		public S getFirst() {
			return layout.first.read(blob, offset);
		}

		public T getSecond() {
			int n = layout.first.sizeOf(blob, offset);
			return layout.second.read(blob, offset + n);
		}

		public Blob setFirst(S value) {
			return layout.first.write(value, blob, offset);
		}

		public Blob setSecond(T value) {
			int n = layout.first.sizeOf(blob, offset);
			return layout.second.write(value, blob, offset + n);
		}

		@Override
		public String toString() {
			return "(" + getFirst() + "," + getSecond() + ")";
		}
	}

	/**
	 * Represents a triple of items in a given layout.
	 *
	 * @author David J. Pearce
	 *
	 * @param <S>
	 * @param <T>
	 */
	public static class Triple<S, T, U> extends AbstractProxy<Triple<S, T, U>, TripleLayout<S, T, U, ?, ?, ?>> {
		public Triple(TripleLayout<S, T, U, ?, ?, ?> layout, Content.Blob blob, int offset) {
			super(layout, blob, offset);
		}

		public S getFirst() {
			return layout.first.read(blob, offset);
		}

		public T getSecond() {
			int n = layout.first.sizeOf(blob, offset);
			return layout.second.read(blob, offset + n);
		}

		public U getThird() {
			int n = layout.first.sizeOf(blob, offset);
			n = n + layout.second.sizeOf(blob, offset + n);
			return layout.third.read(blob, offset + n);
		}

		public Blob setFirst(S value) {
			return layout.first.write(value, blob, offset);
		}

		public Blob setSecond(T value) {
			int n = layout.first.sizeOf(blob, offset);
			return layout.second.write(value, blob, offset + n);
		}

		public Blob setThird(U value) {
			int n = layout.first.sizeOf(blob, offset);
			n = n + layout.second.sizeOf(blob, offset + n);
			return layout.third.write(value, blob, offset + n);
		}

		@Override
		public String toString() {
			return "(" + getFirst() + "," + getSecond() + "," + getThird() + ")";
		}
	}

	// =================================================================================
	// Arrays
	// =================================================================================

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
	// Helpers (Generic)
	// =================================================================

	private static abstract class AbstractLayout<T extends Content.Proxy> implements Content.Layout<T> {

		@Override
		public Blob write(T proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}

		@Override
		public Blob insert(T proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, 0, bytes);
		}
	}

	private static class BridgeLayout<S, T> implements Content.Layout<S> {
		private final Content.Layout<T> child;
		private final Function<T,S> bridge;

		public BridgeLayout(Content.Layout<T> child, Function<T,S> bridge) {
			this.child = child;
			this.bridge = bridge;
		}

		@Override
		public int sizeOf(Blob blob, int offset) {
			return child.sizeOf(blob, offset);
		}

		@Override
		public Blob initialise(Blob blob, int offset) {
			return child.initialise(blob, offset);
		}

		@Override
		public S read(Blob blob, int offset) {
			T item = child.read(blob, offset);
			return bridge.apply(item);
		}

		@Override
		public Blob write(S item, Blob blob, int offset) {
//			return child.write(item, blob, offset);
			throw new IllegalArgumentException();
		}

		@Override
		public Blob insert(S item, Blob blob, int offset) {
//			return child.insert(item, blob, offset);
			throw new IllegalArgumentException();
		}
	}

	// =================================================================
	// Helpers (Tuples)
	// =================================================================

	private static class PairLayout<S,T, CS extends Content.Layout<S>, CT extends Content.Layout<T>> extends AbstractLayout<Pair<S,T>> {
		protected final CS first;
		protected final CT second;

		public PairLayout(CS first, CT second) {
			this.first = first;
			this.second = second;
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
		public Pair<S, T> read(Blob blob, int offset) {
			return new Pair<>(this,blob,offset);
		}
	}

	private static class StaticPairLayout<S, T>
			extends PairLayout<S, T, Content.StaticLayout<S>, Content.StaticLayout<T>>
			implements Content.StaticLayout<Pair<S, T>> {
		public StaticPairLayout(Content.StaticLayout<S> first, Content.StaticLayout<T> second) {
			super(first, second);
		}

		@Override
		public int sizeOf() {
			return first.sizeOf() + second.sizeOf();
		}
	}

	private static class TripleLayout<S, T, U, CS extends Content.Layout<S>, CT extends Content.Layout<T>, CU extends Content.Layout<U>>
			extends AbstractLayout<Triple<S, T, U>> {
		protected final CS first;
		protected final CT second;
		protected final CU third;

		public TripleLayout(CS first, CT second, CU third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		@Override
		public int sizeOf(Blob blob, int offset) {
			int n = first.sizeOf(blob, offset);
			return n + second.sizeOf(blob, offset + n);
		}

		@Override
		public Blob initialise(Blob blob, int offset) {
			// Initialise first item
			blob = first.initialise(blob, offset);
			// Determine it's size
			int n = first.sizeOf(blob, offset);
			// Initialise second item
			blob = second.initialise(blob, offset + n);
			// Add it's size
			n += first.sizeOf(blob, offset);
			// Initialise final item
			return this.initialise(blob, offset + n);
		}

		@Override
		public Triple<S, T, U> read(Blob blob, int offset) {
			return new Triple<>(this,blob,offset);
		}
	}

	private static class StaticTripleLayout<S, T, U>
			extends TripleLayout<S, T, U, Content.StaticLayout<S>, Content.StaticLayout<T>, Content.StaticLayout<U>>
			implements Content.StaticLayout<Triple<S, T, U>> {

		public StaticTripleLayout(Content.StaticLayout<S> first, Content.StaticLayout<T> second,
				Content.StaticLayout<U> third) {
			super(first, second, third);
		}

		@Override
		public int sizeOf() {
			return first.sizeOf() + second.sizeOf() + third.sizeOf();
		}
	}

	// =================================================================
	// Helpers (Arrays)
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
		public Array<T> read(Blob blob, int offset) {
			return new DynamicArray<>(this, blob, offset);
		}

		@Override
		public Blob write(Array<T> proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}

		@Override
		public Blob insert(Array<T> proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, 0, bytes);
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
			return new StaticArray<>(this, blob, offset);
		}

		@Override
		public Blob write(Array<T> proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
		}

		@Override
		public Blob insert(Array<T> proxy, Blob blob, int offset) {
			byte[] bytes = proxy.toBytes();
			return blob.replaceBytes(offset, 0, bytes);
		}
	}

	/**
	 * A proxy for an array of dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	private static class DynamicArray<T> extends AbstractProxy<Array<T>, DynamicArrayLayout<T>>
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
	 * A proxy for an array of dynamically-sized elements.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	private static class StaticArray<T> extends AbstractProxy<Array<T>, StaticArrayLayout<T>>
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
			final Content.StaticLayout<T> child = layout.child;
			//
			if(index < 0 || index >= length()) {
				throw new IllegalArgumentException();
			} else {
				int coffset = offset + 4 + (index * child.sizeOf(blob, index));
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

		@Override
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
				return child.insert(value, b, coffset);
			}
		}

		@Override
		public Content.Blob append(T value) {
			final Content.StaticLayout<T> child = layout.child;
			final int n = length();
			// Determine location of insert element
			int coffset = offset + 4 + (n * child.sizeOf());
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

	public static class Test extends AbstractProxy.Bridge<Layouts.Pair<Integer, Integer>> {
		public static final Content.Layout<Test> LAYOUT = BRIDGE(PAIR(INT32(1), INT32(2)), Test::new);

		public Test(Layouts.Pair<Integer, Integer> fields) {
			super(fields);
		}

		@Override
		public Layout<Test> getLayout() {
			return LAYOUT;
		}

		@Override
		public String toString() {
			return "(" + target.getFirst() + "," + target.getSecond() + ")";
		}
	}

	public static void main(String[] args) {
		Content.Blob blob = ByteBlob.EMPTY;
		blob = Test.LAYOUT.initialise(blob, 0);
		//
		Test t = Test.LAYOUT.read(blob, 0);
		System.out.println("PAIR: " + t);
	}
}
