package jledger.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import jledger.core.Value;

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
	public void read(int i, byte[] bytes) {
		int len = Math.min((bytes.length-i),bytes.length);
		System.arraycopy(bytes, i, bytes, 0, len);
	}

	@Override
	public Delta write(int index, byte[] bytes) {
		return new Write(this, index, bytes);
	}

	@Override
	public Delta append(byte[] bytes) {
		return new Append(this, bytes);
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	private static class Write extends ByteArrayValue implements Delta {
		private final Value parent;
		private final int index;

		public Write(Value parent, int index, byte[] bytes) {
			super(bytes);
			this.parent = parent;
			this.index = index;
		}

		@Override
		public Value getParent() {
			return parent;
		}

		@Override
		public int size() {
			return parent.size();
		}

		@Override
		public void read(int i, byte[] bs) {
			int indexend = index + bytes.length;
			int iend = i+bs.length;

			if (indexend < i || iend < index) {
				parent.read(i, bs);
			} else {
				throw new IllegalArgumentException("GOT HERE");
			}
		}

		@Override
		public InputStream getInputStream() {
			throw new IllegalArgumentException("GOT HERE");
		}

	}

	private static class Append extends ByteArrayValue implements Delta {
		private final Value parent;

		public Append(Value parent, byte[] bytes) {
			super(bytes);
			this.parent = parent;
		}

		@Override
		public Value getParent() {
			return parent;
		}

		@Override
		public int size() {
			return parent.size() + bytes.length;
		}

		@Override
		public void read(int i, byte[] bs) {
			int psize = parent.size();
			int iend = i + bs.length;
			if (i >= psize) {
				// Read entirely past end of parent.
				super.read(i - psize, bs);
			} else if (iend <= psize) {
				// Read entirely within parent
				parent.read(i, bs);
			} else if (i >= psize) {
				// Read split
				throw new IllegalArgumentException("GOT HERE");
			} else {
				throw new IllegalArgumentException("GOT HERE");
			}
		}
	}
}
