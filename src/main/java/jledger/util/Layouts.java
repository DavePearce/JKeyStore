package jledger.util;

import java.util.Arrays;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Position;

public class Layouts {

	// ========================================================================
	// Positions
	// ========================================================================

	public static Content.Position POSITION(int... indices) {
		return POSITION_HELPER(0,indices);
	}

	private static Content.Position POSITION_HELPER(int start, int... indices) {
		if(start == (indices.length-1)) {
			return new PositionNode(indices[start], null);
		} else {
			return new PositionNode(indices[start],POSITION_HELPER(start+1,indices));
		}
	}

	private static class PositionNode implements Content.Position {
		private final int index;
		private final Content.Position child;

		public PositionNode(int index, Content.Position child) {
			this.index = index;
			this.child = child;
		}

		@Override
		public int index() {
			return index;
		}

		@Override
		public Position child() {
			return child;
		}
	}

	// ========================================================================
	// Terminal Layouts
	// ========================================================================

	/**
	 * Represents a terminal layout which is responsible for reading concrete
	 * values.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static abstract class AbstractTerminalLayout implements Content.Layout {

		@Override
		public boolean read_bit(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return read_bit(blob,offset);
			}
		}

		@Override
		public byte read_i8(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return read_i8(blob,offset);
			}
		}

		@Override
		public short read_i16(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return read_i16(blob,offset);
			}
		}

		@Override
		public int read_i32(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return read_i32(blob,offset);
			}
		}

		@Override
		public long read_i64(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return read_i64(blob,offset);
			}
		}

		@Override
		public byte[] read_bytes(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return read_bytes(blob,offset);
			}
		}

		@Override
		public Content.Blob write_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return write_bit(value,blob,offset);
			}
		}

		@Override
		public Content.Blob write_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return write_i8(value,blob,offset);
			}
		}

		@Override
		public Content.Blob write_i16(short value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return write_i16(value,blob,offset);
			}
		}

		@Override
		public Content.Blob write_i32(int value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return write_i32(value,blob,offset);
			}
		}

		@Override
		public Content.Blob write_i64(long value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return write_i64(value,blob,offset);
			}
		}

		@Override
		public Content.Blob write_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return write_bytes(value,blob,offset);
			}
		}

		protected boolean read_bit(Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected byte read_i8(Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected short read_i16(Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected int read_i32(Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected long read_i64(Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected byte[] read_bytes(Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob write_bit(boolean value, Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob write_i8(byte value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob write_i16(short value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob write_i32(int value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob write_i64(long value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob write_bytes(byte[] value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Describes a fixed-width 8bit signed integer.
	 *
	 * @return
	 */
	public static final Content.Layout INT8 = new AbstractTerminalLayout() {

		@Override
		public int size(Blob blob, int offset) {
			return 1;
		}

		@Override
		public byte read_i8(Content.Blob blob, int offset) {
			return blob.read(offset);
		}

		@Override
		public Content.Blob write_i8(byte value, Content.Blob blob, int offset) {
			return blob.write(offset, value);
		}
	};

	/**
	 * Describes a fixed-width 16bit signed integer with a big-endian orientation.
	 *
	 * @return
	 */
	public static final Content.Layout INT16 = new AbstractTerminalLayout() {

		@Override
		public int size(Blob blob, int offset) {
			return 2;
		}

		@Override
		public short read_i16(Content.Blob blob, int offset) {
			// FIXME: faster API would be nice
			byte b1 = blob.read(offset);
			byte b2 = blob.read(offset + 1);
			// Recombine bytes
			return (short) ((b1 << 8) | b2);
		}

		@Override
		public Content.Blob write_i16(short value, Content.Blob blob, int offset) {
			// Convert value into bytes
			byte b1 = (byte) ((value >> 8) & 0xFF);
			byte b2 = (byte) (value & 0xFF);
			// FIXME: faster API would be nice
			return blob.replace(offset, 2, new byte[] { b1, b2 });
		}
	};

	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation.
	 *
	 * @return
	 */
	public static final Content.Layout INT32 = new AbstractTerminalLayout() {

		@Override
		public int size(Blob blob, int offset) {
			return 4;
		}

		@Override
		public int read_i32(Content.Blob blob, int offset) {
			// FIXME: faster API would be nice
			byte b1 = blob.read(offset);
			byte b2 = blob.read(offset + 1);
			byte b3 = blob.read(offset + 2);
			byte b4 = blob.read(offset + 3);
			// Recombine bytes
			return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		}

		@Override
		public Content.Blob write_i32(int value, Content.Blob blob, int offset) {
			// Convert value into bytes
			byte b1 = (byte) ((value >> 24) & 0xFF);
			byte b2 = (byte) ((value >> 16) & 0xFF);
			byte b3 = (byte) ((value >> 8) & 0xFF);
			byte b4 = (byte) (value & 0xFF);
			// FIXME: faster API would be nice
			return blob.replace(offset, 4, new byte[] { b1, b2, b3, b4 });
		}

	};

	// ========================================================================
	// Non-Terminal Layouts
	// ========================================================================

	/**
	 * Represents an internal node of a given layout. That is, a non-terminal layout
	 * which contains some number of subcomponents.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static abstract class AbstractNonTerminalLayout implements Content.Layout {

		@Override
		public boolean read_bit(Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.read_bit(pos.child(), blob, childOffset);
		}

		@Override
		public byte read_i8(Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.read_i8(pos.child(), blob, childOffset);
		}

		@Override
		public short read_i16(Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.read_i16(pos.child(), blob, childOffset);
		}

		@Override
		public int read_i32(Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.read_i32(pos.child(), blob, childOffset);
		}

		@Override
		public long read_i64(Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.read_i64(pos.child(), blob, childOffset);
		}

		@Override
		public byte[] read_bytes(Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.read_bytes(pos.child(), blob, childOffset);
		}

		@Override
		public Content.Blob write_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.write_bit(value, pos.child(), blob, childOffset);
		}

		@Override
		public Content.Blob write_i8(byte value, Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.write_i8(value, pos.child(), blob, childOffset);
		}

		@Override
		public Content.Blob write_i16(short value, Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.write_i16(value, pos.child(), blob, childOffset);
		}

		@Override
		public Content.Blob write_i32(int value, Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.write_i32(value, pos.child(), blob, childOffset);
		}

		@Override
		public Content.Blob write_i64(long value, Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(),blob,offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(),blob,offset);
			// Write the child at the given position
			return child.write_i64(value, pos.child(), blob, childOffset);
		}

		@Override
		public Content.Blob write_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			// Extract the given child from the position
			Content.Layout child = getChild(pos.index(), blob, offset);
			// Determine the offset of the child within enclosing blob
			int childOffset = getChildOffset(pos.index(), blob, offset);
			// Write the child at the given position
			return child.write_bytes(value, pos.child(), blob, childOffset);
		}

		/**
		 * Generic method for extracting the layout of a given child.
		 *
		 * @param child  The child of this layout
		 * @param blob   The blob containing the instantiation of this layout
		 * @param offset The offset within the enclosing blob of the instantiation of
		 *               this layout
		 * @return
		 */
		protected abstract Content.Layout getChild(int child, Content.Blob blob, int offset);

		/**
		 * Generic method for extracting the offset within the enclosing blob of a given
		 * child.
		 *
		 * @param child  The child of this layout
		 * @param blob   The blob containing the instantiation of this layout
		 * @param offset The offset within the enclosing blob of the instantiation of
		 *               this layout
		 * @return
		 */
		protected abstract int getChildOffset(int child, Content.Blob blob, int offset);
	}


	/**
	 * Represents a static layout which consists of a fixed number of children. The
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
	 * @param children
	 * @return
	 */
	public static Content.Layout STATIC(Content.Layout... children) {
		return new AbstractNonTerminalLayout() {

			@Override
			public int size(Blob blob, int offset) {
				int start = offset;
				for(int i=0;i!=children.length;++i) {
					offset += children[i].size(blob, offset);
				}
				return offset - start;
			}

			@Override
			protected Content.Layout getChild(int index, Content.Blob blob, int offset) {
				return children[index];
			}

			@Override
			protected int getChildOffset(int child, Blob blob, int offset) {
				for (int i = 0; i < child; ++i) {
					offset += children[i].size(blob, offset);
				}
				return offset;
			}
		};
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
		return new AbstractNonTerminalLayout() {

			@Override
			public int size(Blob blob, int offset) {
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
			protected int getChildOffset(int c, Blob blob, int offset) {
				for (int i = 0; i < c; ++i) {
					offset += child.size(blob, offset);
				}
				return offset;
			}
		};
	}

	public static void main(String[] args) {
		Content.Blob blob = new ByteBlob(new byte[5]);
		Content.Layout layout = STATIC(INT8,INT32);
		blob = layout.write_i32(-2, POSITION(1), blob, 0);
		blob = layout.write_i8((byte) 127, POSITION(0), blob, 0);
		System.out.println("BLOB: " + Arrays.toString(blob.get()));
		System.out.println("GOT: " + layout.read_i8(POSITION(0), blob, 0));
	}
}
