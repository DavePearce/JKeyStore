package jledger.core;

import java.io.InputStream;

/**
 * Represents an immutable data blob which can be written into a key value
 * store. Values are immutable data structures which, when written, construct
 * new values.
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
	 * Starting from a given position, read the contents of this value into a given
	 * byte array. If the array is shorted than the length of this value, then the
	 * result is truncated. If the array is longer then the extra bytes are left
	 * untouched.
	 *
	 * @param index     starting position to read from.
	 * @param bytes array to write data into.
	 * @return
	 */
	public void read(int index, byte[] bytes);

	/**
	 * Write a given byte to a given position within this value. The index must be
	 * within bounds.
	 *
	 * @param index
	 * @param bytes
	 * @return
	 */
	public Delta write(int index, byte[] bytes);

	/**
	 * Append a given number of (zeroed) bytes on to the end of this value.
	 *
	 * @param count
	 * @return
	 */
	public Delta append(byte[] bytes);

	/**
	 * Get an input stream representation of this value.
	 *
	 * @return
	 */
	public InputStream getInputStream();

	/**
	 * Indicates a delta over a previous value.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Delta extends Value {
		public Value getParent();
	}
}
