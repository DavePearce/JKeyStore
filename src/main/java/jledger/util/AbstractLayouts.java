package jledger.util;

import java.util.Arrays;
import java.util.function.Function;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Constructor;
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
	// Proxy
	// ========================================================================

	public static class Proxy<T> implements Content.Proxy<T> {
		protected final Content.Layout<T> layout;
		protected final Content.Blob blob;
		protected final int offset;

		public Proxy(Content.Layout<T> layout) {
			this.layout = layout;
			this.blob = layout.initialise(ByteBlob.EMPTY, 0);
			this.offset = 0;

		}

		public Proxy(Content.Layout<T> layout, Content.Blob blob, int offset) {
			this.layout = layout;
			this.blob = blob;
			this.offset = offset;
		}

		@Override
		public Blob getBlob() {
			return blob;
		}

		@Override
		public int getOffset() {
			return offset;
		}

		@Override
		public Layout<T> getLayout() {
			return layout;
		}

		@Override
		public byte[] toBytes() {
			int size = layout.size(blob, offset);
			return blob.read(offset, size);
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
	public static abstract class Terminal<T> implements Content.Layout<T> {

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
		public <S> S read(Class<S> kind, Position pos, Content.Blob blob, int offset) {
			throw new IllegalArgumentException("invalid position");
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

		@Override
		public Content.Blob insert_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return insert_bit(value,blob,offset);
			}
		}

		@Override
		public Content.Blob insert_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return insert_i8(value,blob,offset);
			}
		}

		@Override
		public Content.Blob insert_i16(short value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return insert_i16(value,blob,offset);
			}
		}

		@Override
		public Content.Blob insert_i32(int value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return insert_i32(value,blob,offset);
			}
		}

		@Override
		public Content.Blob insert_i64(long value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return insert_i64(value,blob,offset);
			}
		}

		@Override
		public Content.Blob insert_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return insert_bytes(value,blob,offset);
			}
		}

		@Override
		public Content.Blob insert(Content.Proxy proxy, Position pos, Content.Blob blob, int offset) {
			return blob.replace(offset, 0, proxy.toBytes());
		}


		@Override
		public Content.Blob append_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return append_bit(value,blob,offset);
			}
		}

		@Override
		public Content.Blob append_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return append_i8(value,blob,offset);
			}
		}

		@Override
		public Content.Blob append_i16(short value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return append_i16(value,blob,offset);
			}
		}

		@Override
		public Content.Blob append_i32(int value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return append_i32(value,blob,offset);
			}
		}

		@Override
		public Content.Blob append_i64(long value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return append_i64(value,blob,offset);
			}
		}

		@Override
		public Content.Blob append_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				throw new IllegalArgumentException("invalid position");
			} else {
				return append_bytes(value,blob,offset);
			}
		}

		@Override
		public Content.Blob append(Content.Proxy proxy, Position pos, Content.Blob blob, int offset) {
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

		protected Content.Blob insert_bit(boolean value, Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob insert_i8(byte value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob insert_i16(short value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob insert_i32(int value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob insert_i64(long value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob insert_bytes(byte[] value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob append_bit(boolean value, Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob append_i8(byte value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob append_i16(short value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob append_i32(int value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob append_i64(long value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}

		protected Content.Blob append_bytes(byte[] value,Content.Blob blob, int offset) {
			throw new UnsupportedOperationException();
		}
	}

	public static abstract class StaticTerminal<T> extends Terminal<T>
			implements Content.StaticLayout<T> {

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
	public static abstract class NonTerminal<T> implements Content.Layout<T> {
		private final Content.Constructor<T> constructor;

		public NonTerminal(Content.Constructor<T> constructor) {
			this.constructor = constructor;
		}

		@Override
		public Content.Constructor<T> constructor() {
			return constructor;
		}

		@Override
		public Content.Blob initialise(Content.Blob blob, int offset) {
			for(int i=0;i!=numberOfChildren(blob,offset);++i) {
				// Extract the given child from the position
				Content.Layout<?> child = getChild(i,blob,offset);
				// Determine the offset of the child within enclosing blob
				int childOffset = getChildOffset(i, blob, offset);
				// Initialise the child
				blob = child.initialise(blob, childOffset);
			}
			return blob;
		}

		@Override
		public boolean read_bit(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.read_bit(pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public byte read_i8(Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.read_i8(pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public short read_i16(Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.read_i16(pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public int read_i32(Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.read_i32(pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public long read_i64(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.read_i64(pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public byte[] read_bytes(Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.read_bytes(pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public <S> S read(Class<S> kind, Position pos, Content.Blob blob,
				int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.read(kind, pos.child(), blob, childOffset);
				}
			} else {
				T item = constructor.read(blob, offset);
				// Dynamic cast
				if(kind.isInstance(item)) {
					return (S) item;
				} else {
					throw new IllegalArgumentException("invalid proxy kind");
				}
			}
		}

		@Override
		public T read(Content.Blob blob, int offset) {
			return constructor.read(blob, offset);
		}

		@Override
		public Content.Blob write_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.write_bit(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob write_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.write_i8(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob write_i16(short value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.write_i16(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob write_i32(int value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.write_i32(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob write_i64(long value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.write_i64(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob write_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.write_bytes(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob write(Content.Proxy proxy, Position pos, Content.Blob blob, int offset) {
			// Determine next index within position
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n >= numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the proxy down
					return child.write(proxy, pos.child(), blob, childOffset);
				}
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

		@Override
		public Content.Blob insert_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.insert_bit(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob insert_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.insert_i8(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob insert_i16(short value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.insert_i16(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob insert_i32(int value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.insert_i32(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob insert_i64(long value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.insert_i64(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob insert_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.insert_bytes(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob insert(Content.Proxy proxy, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the proxy down
					return child.insert(proxy, pos.child(), blob, childOffset);
				}
			} else {
				return blob.replace(offset, 0, proxy.toBytes());
			}
		}

		@Override
		public Content.Blob append(Content.Proxy proxy, Position pos, Content.Blob blob, int offset) {
			if(pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the proxy down
					return child.append(proxy, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob append_bit(boolean value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.insert_bit(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob append_i8(byte value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.append_i8(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob append_i16(short value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.append_i16(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob append_i32(int value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.append_i32(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob append_i64(long value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.append_i64(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public Content.Blob append_bytes(byte[] value, Position pos, Content.Blob blob, int offset) {
			if (pos != null) {
				int n = pos.index();
				// Sanity check bounds
				if (n < 0 || n > numberOfChildren(blob, offset)) {
					throw new IllegalArgumentException("invalid child");
				} else {
					// Extract the given child from the position
					Content.Layout<?> child = getChild(n, blob, offset);
					// Determine the offset of the child within enclosing blob
					int childOffset = getChildOffset(n, blob, offset);
					// Write the child at the given position
					return child.append_bytes(value, pos.child(), blob, childOffset);
				}
			} else {
				throw new UnsupportedOperationException();
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


	public static abstract class StaticNonTerminal<T> extends NonTerminal<T>
			implements Content.StaticLayout<T> {

		public StaticNonTerminal(Constructor<T> constructor) {
			super(constructor);
		}

	}

	private static class Test extends Proxy {
		private static final Content.Layout<Test> LAYOUT = RecordLayouts.RECORD(Test::new,
				PrimitiveLayouts.INT32(0), PrimitiveLayouts.INT32(1));

		public Test() {
			super(LAYOUT);
		}

		public Test(Content.Blob blob, int offset) {
			super(LAYOUT,blob,offset);
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
		Test t = new Test();
		System.out.println("GOT: " + t.getX() + ", " + t.getY());
	}
}
