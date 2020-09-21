package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Constructor;
import jledger.core.Content.Layout;
import jledger.core.Content.Position;

public class AbstractLayouts {

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
//
//	private static class Test extends Proxy {
//		private static final Content.Layout<Test> LAYOUT = RecordLayouts.RECORD(Test::new,
//				PrimitiveLayouts.INT32(0), PrimitiveLayouts.INT32(1));
//
//		public Test() {
//			super(LAYOUT);
//		}
//
//		public Test(Content.Blob blob, int offset) {
//			super(LAYOUT,blob,offset);
//		}
//
//		public int getX() {
//			return LAYOUT.read_i32(POSITION(0), blob, offset);
//		}
//
//		public int getY() {
//			return LAYOUT.read_i32(POSITION(1), blob, offset);
//		}
//
//		@Override
//		public int getOffset() {
//			return offset;
//		}
//
//		@Override
//		public Blob getBlob() {
//			return blob;
//		}
//
//		@Override
//		public Layout getLayout() {
//			return LAYOUT;
//		}
//
//		@Override
//		public String toString() {
//			return "(" + getX() + "," + getY() + ")";
//		}
//	}

//	public static void main(String[] args) {
//		Test t = new Test();
//		System.out.println("GOT: " + t.getX() + ", " + t.getY());
//	}
}
