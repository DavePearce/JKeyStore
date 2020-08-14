package jledger.core;

import jledger.core.Value.Delta;

public class Content {

	/**
	 * An interface for a particular version of an object on a ledger.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Proxy {
		/**
		 * Get the value which underpins this proxy object.
		 *
		 * @return
		 */
		Blob getContents();
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

	}

	/**
	 * Represents an immutable data blob which can be written into a ledger. Values
	 * are immutable data structures which, when written, construct new values.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Blob {
		/**
		 * Get the total number of bytes in this blob.
		 *
		 * @return
		 */
		public int size();

		/**
		 * Get the complete contents of this value as a sequence of bytes.
		 *
		 * @return
		 */
		public byte[] get();

		/**
		 * Read a given byte from a given position in the value. The index must be
		 * within bounds.
		 *
		 * @param index
		 * @return
		 */
		public byte read(int index);

		/**
		 * Write a given byte to a given position within this value. The index must be
		 * within bounds.
		 *
		 * @param index Position to overwrite
		 * @param b data byte to written
		 * @return
		 */
		public Diff write(int index, byte b);

		/**
		 * Replace a given section of this value with a new sequence of bytes. The new
		 * byte sequence does not need to be the same length as the section replaced.
		 * The section bing replaced must be entirely within bounds.
		 *
		 * @param index  starting offset of section being replaced
		 * @param length size of section being replaced
		 * @param b      data byte to written
		 * @return
		 */
		public Diff replace(int index, int length, byte... bytes);
	}

	/**
	 * Replacements cannot overlap.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Diff extends Blob {
		/**
		 * Get the parent of this blob, if one exists.
		 *
		 * @return
		 */
		public Blob parent();

		/**
		 * Get the number of replacements in this diff.
		 *
		 * @return
		 */
		public int count();

		/**
		 * Get a specific replacement.
		 * @return
		 */
		public Replacement getReplacement(int i);
	}

	public interface Replacement {
		/**
		 * Get the starting offset of this replacement.
		 * @return
		 */
		public int offset();

		/**
		 * Get the length of this replacement.
		 *
		 * @return
		 */
		public int size();

		/**
		 * Get the array of replacement bytes.
		 *
		 * @return
		 */
		public byte[] bytes();
	}
}
