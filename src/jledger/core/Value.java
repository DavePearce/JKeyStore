package jledger.core;

import java.io.InputStream;

/**
 * Represents an immutable data blob which can be written into a ledger. Values
 * are immutable data structures which, when written, construct new values.
 *
 * @author David J. Pearce
 *
 */
public interface Value {
	/**
	 * Get the number of bytes in this value.
	 *
	 * @return
	 */
	public int size();

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
	public Delta write(int index, byte b);
	
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
	public Delta replace(int index, int length, byte[] bytes);
	
	/**
	 * Starting from a given position, read the contents of this value into a given
	 * byte array. If the array is shorter than the length of this value, then the
	 * result is truncated. If the array is longer then the extra bytes are left
	 * untouched.
	 *
	 * @param index     starting position to read from.
	 * @param bytes array to write data into.
	 * @return
	 */
	//public void read(int index, byte[] bytes);

	/**
	 * Write a given byte to a given position within this value. The index must be
	 * within bounds.
	 *
	 * @param index
	 * @param bytes
	 * @return
	 */
	//public Delta write(int index, byte[] bytes);

	/**
	 * Append a given number of (zeroed) bytes on to the end of this value.
	 *
	 * @param count
	 * @return
	 */
	//public Delta append(byte[] bytes);

	/**
	 * Get an input stream representation of this value.
	 *
	 * @return
	 */
	//public InputStream getInputStream();

	/**
	 * Indicates a delta over a previous value. This is a sequence of bytes which
	 * replace a sequence of bytes in the original sequence. The replacement
	 * sequence can be larger or smaller than the original sequence.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Delta extends Value {
		/**
		 * Get original sequence which this delta is modifying.
		 * 
		 * @return
		 */
		public Value parent();

		/**
		 * Get the length of the replaced sequence.
		 * 
		 * @return
		 */
		public int length();

		/**
		 * Get the start of the updated sequence
		 * 
		 * @return
		 */
		public int offset();

		/**
		 * Get the replacement bytes.
		 * 
		 * @return
		 */
		public byte[] bytes();
	}
	
	/**
	 * An interned value is one which physically stored in a given ledger.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface Interned<K, V> extends Value {
		public Ledger<K, V> getLedger();
	}
}
