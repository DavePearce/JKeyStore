package jledger.util;

import java.util.Arrays;
import java.util.function.Function;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;
import jledger.core.Content.Position;

public class AbstractLayouts {

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
	public static abstract class Terminal implements Content.Layout {

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

		@Override
		public Content.Blob write(Content.Proxy proxy, Position position, Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
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

	public static abstract class StaticTerminal extends Terminal
			implements Content.StaticLayout {

	}

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
	public static abstract class NonTerminal implements Content.Layout {

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			for(int i=0;i!=numberOfChildren(blob,offset);++i) {
				// Extract the given child from the position
				Content.Layout child = getChild(i,blob,offset);
				// Determine the offset of the child within enclosing blob
				int childOffset = getChildOffset(i, blob, offset);
				// Initialise the child
				blob = child.initialise(blob, childOffset);
			}
			return blob;
		}

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

		@Override
		public Content.Blob write(Content.Proxy proxy, Position pos, Content.Blob blob, int offset) {
			// Determine next index within position
			if(pos != null) {
				// Extract the given child from the position
				Content.Layout child = getChild(pos.index(), blob, offset);
				// Determine the offset of the child within enclosing blob
				int childOffset = getChildOffset(pos.index(), blob, offset);
				// Write the proxy down
				return child.write(proxy, pos.child(), blob, childOffset);
			} else if(proxy.getLayout() != this) {
				throw new IllegalArgumentException("incompatible layout");
			} else {
				// Determine size of the child
				int mySize = size(blob, offset);
				// Extract underlying blob
				Content.Blob proxyBlob = proxy.getBlob();
				// Readout the bytes representing the object in question
				byte[] bytes = proxyBlob.read(offset,mySize);
				// Destruct the object and replace the existing one
				return blob.replace(offset, mySize, bytes);
			}
		}


		/**
		 * Generic method for determining the number of children.
		 *
		 * @param blob   The blob containing the instantiation of this layout
		 * @param offset The offset within the enclosing blob of the instantiation of
		 *               this layout
		 *
		 * @return
		 */
		protected abstract int numberOfChildren(Content.Blob blob, int offset);

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


	public static abstract class StaticNonTerminal extends NonTerminal
			implements Content.StaticLayout {

	}

	private static class Test implements Content.Proxy {
		private static final Content.ConstructorLayout<Test> LAYOUT = RecordLayouts.RECORD(Test::new,
				PrimitiveLayouts.INT32, PrimitiveLayouts.INT32);
		private final int offset;
		private final Content.Blob blob;

		public Test(int x, int y) {
			Content.Blob b = new ByteBlob();
			this.offset = 0;
			b = LAYOUT.initialise(b, 0);
			b = LAYOUT.write_i32(x,POSITION(0),b,0);
			b = LAYOUT.write_i32(y,POSITION(1),b,0);
			this.blob = b;
		}

		public Test(Content.Blob blob, int offset) {
			this.offset = offset;
			this.blob = blob;
		}

		public int getX() {
			return LAYOUT.read_i32(POSITION(0), blob, offset);
		}

		public int getY() {
			return LAYOUT.read_i32(POSITION(1), blob, offset);
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public Blob getBlob() {
			return blob;
		}

		@Override
		public Layout getLayout() {
			return LAYOUT;
		}

		@Override
		public String toString() {
			return "(" + getX() + "," + getY() + ")";
		}
	}

	public static void main(String[] args) {
		Content.Layout layout = ArrayLayouts.DYNAMIC_ARRAY(PrimitiveLayouts.INT32);
		Content.Blob blob = ByteBlob.EMPTY;
		blob = layout.initialise(blob, 0);
		blob = layout.write_i32(0, POSITION(1), blob, 0);
		//
		int n = layout.read_i32(POSITION(0), blob, 0);
		System.out.println("LENGTH: " + n);
		for(int i=0;i!=n;++i) {
			System.out.println("[" + i + "] = " + layout.read_i32(POSITION(1 + i), blob, 0));
		}
	}
}
