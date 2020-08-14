package jledger.core;

public class Content {
	/**
	 * A proxy essentially provides a nice API for interacting with object interned
	 * on a given ledger.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Proxy {

	}

	/**
	 * A mechanism for translating between proxy objects and ledger values.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public interface Type<T extends Proxy> {
		/**
		 * Get the layout of this content type.
		 *
		 * @return
		 */
		Layout layout();

		/**
		 * Decode a given binary value into a given structured proxy object.
		 *
		 * @param v
		 * @return
		 */
		T decode(Value v);
	}

	/**
	 * Provides a generic mechanism for describing the data layout of an object
	 * interned on a ledger.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Layout {
		/**
		 * Identify the blocks within this layout.
		 *
		 * @return
		 */
		public Block[] blocks();

		/**
		 * Represents a block of data within a layout which may be repeating.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Block extends Layout {

		}
	}

}
