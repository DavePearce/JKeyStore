package jledger.core;

public class Content {

	/**
	 * An interface for a particular version of an object on a ledger.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Proxy<T> {
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
		 * Get the underlying layout describing this proxy object.
		 *
		 * @return
		 */
		public Content.Layout<T> getLayout();

		/**
		 * Convert proxy into a byte sequence.
		 * @return
		 */
		public byte[] toBytes();
	}

	/**
	 * Identifies a specific position within a given layout. The following
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
	 * This layout consists of a repeating sequence of two fields. The first field
	 * occupies two bytes, whilst the second occupies one. The position held by
	 * value <code>01</code> is <code>(0,1)</code>, whilst that for the value
	 * <code>af 00</code> is <code>(2,0)</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Position {
		/**
		 * The index point at this level of recursion.
		 *
		 * @return
		 */
		public int index();

		/**
		 * Return the subposition contained within this position.
		 *
		 * @return
		 */
		public Position child();
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
		 * Return the size (in bytes) of this layouts instantiation in a given blob.
		 *
		 * @param blob   The blob in which this layout is instantiated.
		 * @param offset The starting offset where this layout is instantiated.
		 * @return
		 */
		public int size(Content.Blob blob, int offset);

		/**
		 * Initialise this layout at a given position within a blob. This will
		 * initialise appropriate default values for fields. For example, an integer may
		 * default to zero, whilst an array may default to being empty, etc.
		 *
		 * @return
		 */
		public Content.Blob initialise(Content.Blob blob, int offset);

		/**
		 * Return a constructor for this layout.
		 *
		 * @return
		 */
		public Content.Constructor<T> constructor();

		/**
		 * Read a boolean value from a given position within an instantiation of this
		 * layout in a blob.
		 *
		 * @param position The position to be read.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public boolean read_bit(Position position, Content.Blob blob, int offset);

		/**
		 * Read a signed 8-bit integer value from a given position within an
		 * instantiation of this layout in a blob.
		 *
		 * @param position The position to be read.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public byte read_i8(Position position, Content.Blob blob, int offset);

		/**
		 * Read a signed 16-bit integer value from a given position within an
		 * instantiation of this layout in a blob.
		 *
		 * @param position The position to be read.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public short read_i16(Position position, Content.Blob blob, int offset);

		/**
		 * Read a signed 32-bit integer value from a given position within an
		 * instantiation of this layout in a blob.
		 *
		 * @param position The position to be read.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public int read_i32(Position position, Content.Blob blob, int offset);

		/**
		 * Read a signed 64-bit integer value from a given position within an
		 * instantiation of this layout in a blob.
		 *
		 * @param position The position to be read.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public long read_i64(Position position, Content.Blob blob, int offset);

		/**
		 * Read a sequence of zero or more bytes from a given position within an
		 * instantiation of this layout in a blob.
		 *
		 * @param position The position to be read.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public byte[] read_bytes(Position position, Content.Blob blob, int offset);

		public <S> S read(Class<S> kind, Position position, Content.Blob blob, int offset);

		public T read(Content.Blob blob, int offset);

		/**
		 * Write a boolean value at a given position within an instantiation of this
		 * layout in a blob, returning the updated blob.
		 *
		 * @param value    The boolean value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob write_bit(boolean value, Position position, Content.Blob blob, int offset);

		/**
		 * Write a signed 8-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob write_i8(byte value, Position position, Content.Blob blob, int offset);

		/**
		 * Write a signed 16-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob write_i16(short value, Position position, Content.Blob blob, int offset);

		/**
		 * Write a signed 32-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob write_i32(int value, Position position, Content.Blob blob, int offset);

		/**
		 * Write a signed 64-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob write_i64(long value, Position position, Content.Blob blob, int offset);

		/**
		 * Write sequence of zero or more bytes value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob write_bytes(byte[] bytes, Position position, Content.Blob blob, int offset);


		/**
		 * Write a proxy object to a given position within this layout.
		 *
		 * @param proxy
		 * @param position
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob write(Proxy proxy, Position position, Content.Blob blob, int offset);

		/**
		 * Insert a boolean value at a given position within an instantiation of this
		 * layout in a blob, returning the updated blob.
		 *
		 * @param value    The boolean value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob insert_bit(boolean value, Position position, Content.Blob blob, int offset);

		/**
		 * Insert a signed 8-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob insert_i8(byte value, Position position, Content.Blob blob, int offset);

		/**
		 * Insert a signed 16-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob insert_i16(short value, Position position, Content.Blob blob, int offset);

		/**
		 * Insert a signed 32-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob insert_i32(int value, Position position, Content.Blob blob, int offset);

		/**
		 * Insert a signed 64-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob insert_i64(long value, Position position, Content.Blob blob, int offset);

		/**
		 * Insert sequence of zero or more bytes value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob insert_bytes(byte[] bytes, Position position, Content.Blob blob, int offset);

		/**
		 * Insert a given proxy object at a given position, whilst moving any existing
		 * children at that offset down. When the position is passed the last child,
		 * then this is effectively appending one (or more) new children.
		 *
		 * @param proxy
		 * @param position
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob insert(Proxy proxy, Position position, Content.Blob blob, int offset);


		/**
		 * Append a boolean value at a given position within an instantiation of this
		 * layout in a blob, returning the updated blob.
		 *
		 * @param value    The boolean value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob append_bit(boolean value, Position position, Content.Blob blob, int offset);

		/**
		 * Insert a signed 8-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob append_i8(byte value, Position position, Content.Blob blob, int offset);

		/**
		 * Append a signed 16-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob append_i16(short value, Position position, Content.Blob blob, int offset);

		/**
		 * Append a signed 32-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob append_i32(int value, Position position, Content.Blob blob, int offset);

		/**
		 * Append a signed 64-bit integer value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob append_i64(long value, Position position, Content.Blob blob, int offset);

		/**
		 * Append a sequence of zero or more bytes value at a given position within an
		 * instantiation of this layout in a blob, returning the updated blob.
		 *
		 * @param value    The signed integer value to be written.
		 * @param position The position to be written.
		 * @param blob     The blob containing the instantiation of this layout.
		 * @param offset   The offset within the blob where the instantiation of this
		 *                 layout begins.
		 * @return
		 */
		public Content.Blob append_bytes(byte[] bytes, Position position, Content.Blob blob, int offset);

		/**
		 * Append a given proxy object at a given position, whilst moving any existing
		 * children at that offset down. When the position is passed the last child,
		 * then this is effectively appending one (or more) new children.
		 *
		 * @param proxy
		 * @param position
		 * @param blob
		 * @param offset
		 * @return
		 */
		public Content.Blob append(Proxy proxy, Position position, Content.Blob blob, int offset);
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
		 * Return the size of this layout in bytes.
		 *
		 * @return
		 */
		public int size();
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
		public byte[] read();

		/**
		 * Read a given byte from a given position in the blob. The index must be
		 * within bounds.
		 *
		 * @param index
		 * @return
		 */
		public byte read(int index);

		/**
		 * Read a given sequence of bytes from a given position in the blob. The entire
		 * region must be within bounds.
		 *
		 * @param index
		 * @param length
		 * @return
		 */
		public byte[] read(int index, int length);

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
		public void read(int index, int length, byte[] dest, int destStart);

		/**
		 * Write a given byte to a given position within this value. The index does not
		 * need to be in bounds since blobs are elastic. Thus, writing beyond bounds
		 * increases the size of the blob accordingly.
		 *
		 * @param index Position to overwrite
		 * @param b     data byte to written
		 * @return
		 */
		public Diff write(int index, byte b);

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
