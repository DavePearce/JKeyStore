package jledger.core;

public class Content {

	/**
	 * An interface for a particular version of an object on a ledger.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Proxy {
		/**
		 * Get the offset of this proxy object within the blob containing it.
		 *
		 * @return
		 */
		public int getOffset();
		/**
		 * Get the underlying blob containing this proxy object.
		 *
		 * @return
		 */
		public Content.Blob getBlob();

		/**
		 * Convert proxy into a byte sequence.
		 * @return
		 */
		public byte[] toBytes();
	}

	/**
	 * Provides a generic mechanism for describing the data layout of an object
	 * interned on a ledger.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Layout<T> {

		/**
		 * Return the size (in bytes) of this layout instantiation in a given blob.
		 *
		 * @param blob   The blob in which this layout is instantiated.
		 * @param offset The starting offset where this layout is instantiated.
		 * @return
		 */
		public int sizeOf(Content.Blob blob, int offset);

		/**
		 * Initialise this layout at a given position within a blob. This will
		 * initialise appropriate values for fields. For example, an integer may default
		 * to zero, whilst an array may default to being empty, etc.
		 *
		 * @return
		 */
		public Content.Blob initialise(Content.Blob blob, int offset);

		/**
		 * Read a value from a given position within an instantiation of this layout in
		 * a blob.
		 *
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public T read(Content.Blob blob, int offset);

		/**
		 * Write a proxy object to a given position within this layout.
		 *
		 * @param proxy
		 * @param position
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob write(T proxy, Content.Blob blob, int offset);
	}

	/**
	 * Represents a layout whose size is statically known. Knowing that a child
	 * layout is static can help in some cases. Unfortunately, not all layouts can
	 * be static by definition.
	 *
	 * @author David J. Pearec
	 *
	 */
	public interface StaticLayout<T> extends Layout<T> {
		/**
		 * Return the size of this layout in bytes. Since this is a static layout, there
		 * is no need for any parameters!
		 *
		 * @return
		 */
		public int sizeOf();
	}

	/**
	 * A specialised interface which can be used to construct an object proxy for a
	 * given position within a blob.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public interface Constructor<T> {
		public T read(Content.Blob blob, int offset);
	}

	/**
	 * Represents an immutable binary blob of data which can be written into a
	 * ledger. Blobs are immutable data structures which, when written, construct
	 * new blobs. Blobs are also elastic in that they automatically resize to
	 * accommodate writing beyond their current bounds.
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
		public byte[] readAll();

		/**
		 * Read a given byte from a given position in the blob. The index must be
		 * within bounds.
		 *
		 * @param index
		 * @return
		 */
		public byte readByte(int index);

		/**
		 * Read a 16-bit signed integer starting at a given position in the blob
		 * assuming big endian orientation. All indices must be entirely within bounds.
		 *
		 * @param index
		 * @return
		 */
		public short readShort(int index);

		/**
		 * Read a 32-bit signed integer starting at a given position in the blob
		 * assuming big endian orientation. All indices must be entirely within bounds.
		 *
		 * @param index
		 * @return
		 */
		public int readInt(int index);

		/**
		 * Read a given sequence of bytes from a given position in the blob. The entire
		 * region must be within bounds.
		 *
		 * @param index
		 * @param length
		 * @return
		 */
		public byte[] readBytes(int index, int length);

		/**
		 * Read a given sequence of bytes from a given position in the blob into a
		 * prexisting array. The entire region must be within bounds.
		 *
		 * @param index     The index within this blob to start reading from
		 * @param length    The number of bytes to read
		 * @param dest      The destination byte array
		 * @param destStart starting position within destination
		 * @return
		 */
		public void readBytes(int index, int length, byte[] dest, int destStart);

		/**
		 * Write a given byte to a given position within this value. The index does not
		 * need to be in bounds since blobs are elastic. Thus, writing beyond bounds
		 * increases the size of the blob accordingly.
		 *
		 * @param index Position to overwrite
		 * @param b     data byte to written
		 * @return
		 */
		public Diff writeByte(int index, byte b);

		/**
		 * Write a given 16-bit signed integer byte to a given position within this blob
		 * assuming a big endian orientation. The index does not need to be in bounds
		 * since blobs are elastic. Thus, writing beyond bounds increases the size of
		 * the blob accordingly.
		 *
		 * @param index Position to overwrite
		 * @param b     data byte to written
		 * @return
		 */
		public Diff writeShort(int index, short i16);

		/**
		 * Write a given 32-bit signed integer byte to a given position within this blob
		 * assuming a big endian orientation. The index does not need to be in bounds
		 * since blobs are elastic. Thus, writing beyond bounds increases the size of
		 * the blob accordingly.
		 *
		 * @param index Position to overwrite
		 * @param b     data byte to written
		 * @return
		 */
		public Diff writeInt(int index, int i32);

		/**
		 * Replace a given section of this value with a new sequence of bytes. The index
		 * does not need to be in bounds since blobs are elastic. Thus, writing beyond
		 * bounds increases the size of the blob accordingly.
		 *
		 * @param index Position to overwrite
		 * @param b     data byte to written
		 * @return
		 */
		public Diff writeBytes(int index, byte... bytes);

		/**
		 * Replace a given section of this value with a new sequence of bytes. The new
		 * byte sequence does not need to be the same length as the section replaced.
		 * The index does not need to be in bounds since blobs are elastic. Thus,
		 * writing beyond bounds increases the size of the blob accordingly.
		 *
		 * @param index  starting offset of section being replaced
		 * @param length size of section being replaced
		 * @param b      data byte to written
		 * @return
		 */
		public Diff replaceBytes(int index, int length, byte... bytes);

		/**
		 * Insert a given byte to a given position within this value. The index does not
		 * need to be in bounds since blobs are elastic. Thus, inserting beyond bounds
		 * increases the size of the blob accordingly.
		 *
		 * @param index Position to insert before
		 * @param b     data byte to written
		 * @return
		 */
		public Diff insertByte(int index, byte b);

		/**
		 * Insert a given 16-bit signed integer byte to a given position within this blob
		 * assuming a big endian orientation. The index does not need to be in bounds
		 * since blobs are elastic. Thus, insert beyond bounds increases the size of
		 * the blob accordingly.
		 *
		 * @param index Position to insert before
		 * @param b     data byte to written
		 * @return
		 */
		public Diff insertShort(int index, short i16);

		/**
		 * Insert a given 32-bit signed integer byte to a given position within this blob
		 * assuming a big endian orientation. The index does not need to be in bounds
		 * since blobs are elastic. Thus, insert beyond bounds increases the size of
		 * the blob accordingly.
		 *
		 * @param index Position to insert before
		 * @param b     data byte to written
		 * @return
		 */
		public Diff insertInt(int index, int i32);

		/**
		 * Insert a given section of this value with a new sequence of bytes. The index
		 * does not need to be in bounds since blobs are elastic. Thus, insert beyond
		 * bounds increases the size of the blob accordingly.
		 *
		 * @param index Position to insert before
		 * @param b     data byte to written
		 * @return
		 */
		public Diff insertBytes(int index, byte... bytes);
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
