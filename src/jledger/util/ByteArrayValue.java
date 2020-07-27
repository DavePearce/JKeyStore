package jledger.util;

import jledger.core.Value;

/**
 * Represents a simple standalone implementation of <code>Value</code> which
 * stores the value using an internal byte array.
 * 
 * @author David J. Pearce
 *
 */
public class ByteArrayValue implements Value {
	protected byte[] bytes;

	public ByteArrayValue(byte[] data) {
		this.bytes = data;
	}

	@Override
	public int size() {
		return bytes.length;
	}

	@Override
	public byte read(int index) {
		return bytes[index];
	}

	@Override
	public Delta write(int index, byte b) {
		return new Delta(this, index, 1, b);
	}
	
	@Override
	public Delta replace(int index, int length, byte[] bytes) {
		return new Delta(this, index, length, bytes);
	}

	public static class Delta implements Value.Delta {
		private final Value parent;
		private final int offset;
		private final int length;
		private final byte[] bytes;

		public Delta(Value parent, int offset, int length, byte... bytes) {
			this.parent = parent;
			this.offset = offset;
			this.length = length;
			this.bytes = bytes;
		}

		@Override
		public jledger.core.Value parent() {
			return parent;
		}
		
		@Override
		public int size() {
			return parent.size() + (bytes.length - length);
		}

		@Override
		public int length() {
			return length;
		}
		
		@Override
		public int offset() {
			return offset;
		}
		
		@Override
		public byte[] bytes() {
			return bytes;
		}
		
		@Override
		public byte read(int index) {
			if (index < offset) {
				return parent.read(index);
			} else if (index >= (offset + bytes.length)) {
				return parent.read(index + (bytes.length - length));
			} else {
				return bytes[index - offset];
			}
		}

		@Override
		public Delta write(int index, byte b) {
			return new ByteArrayValue.Delta(this, index, 1, b);
		}
		
		@Override
		public Delta replace(int index, int length, byte[] bytes) {
			return new ByteArrayValue.Delta(this, index, length, bytes);
		}
	}
}
