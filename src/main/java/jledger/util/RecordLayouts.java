package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;

public class RecordLayouts {

//	/**
//	 * Represents a static layout which consists of a fixed number of children. The
//	 * following illustrates:
//	 *
//	 * <pre>
//	 *   |00|01|02|03|04|05|06|07|08|
//	 *   +--------+--------+--------+
//	 * 0 |        |        |        |
//	 *   +-----+--+-----+--+-----+--+
//	 * 1 |     |  |     |  |     |  |
//	 *   +-----+--+-----+--+-----+--+
//	 * 2 |00 ff|01|00 00|00|af 00|00|
//	 * </pre>
//	 *
//	 * This layout consists of repeating sequence (level 0) of static layouts (level
//	 * 1). The static layout consists of a two byte field followed by a one byte
//	 * field.
//	 *
//	 * @param children
//	 * @return
//	 */
//	public static <T> Content.Layout<T> RECORD(Content.Constructor<T> constructor, Content.Layout<?>... children) {
//		return new RecordLayout<>(constructor,children);
//	}
//	/**
//	 * Represents a static layout which consists of a fixed number of children. The
//	 * following illustrates:
//	 *
//	 * <pre>
//	 *   |00|01|02|03|04|05|06|07|08|
//	 *   +--------+--------+--------+
//	 * 0 |        |        |        |
//	 *   +-----+--+-----+--+-----+--+
//	 * 1 |     |  |     |  |     |  |
//	 *   +-----+--+-----+--+-----+--+
//	 * 2 |00 ff|01|00 00|00|af 00|00|
//	 * </pre>
//	 *
//	 * This layout consists of repeating sequence (level 0) of static layouts (level
//	 * 1). The static layout consists of a two byte field followed by a one byte
//	 * field.
//	 *
//	 * @param children
//	 * @return
//	 */
//	public static <T> Content.Layout<T> RECORD(Content.Constructor<T> constructor,Content.StaticLayout<?>... children) {
//		return new RecordStaticLayout<>(constructor, children);
//	}
//
//	// =======================================================================
//	// Helpers
//	// =======================================================================
//
//	private static class RecordLayout<T> extends NonTerminal<T> {
//		private final Content.Layout<?>[] children;
//
//		public RecordLayout(Content.Constructor<T> constructor, Content.Layout<?>[] children) {
//			super(constructor);
//			this.children = children;
//		}
//
//		@Override
//		public int numberOfChildren(Content.Blob blob,int offset) {
//			return children.length;
//		}
//
//		@Override
//		public int size(Blob blob, int offset) {
//			int start = offset;
//			for(int i=0;i!=children.length;++i) {
//				offset += children[i].size(blob, offset);
//			}
//			return offset - start;
//		}
//
//		@Override
//		protected Content.Layout<?> getChild(int index, Content.Blob blob, int offset) {
//			return children[index];
//		}
//
//		@Override
//		protected int getChildOffset(int child, Blob blob, int offset) {
//			for (int i = 0; i < child; ++i) {
//				offset += children[i].size(blob, offset);
//			}
//			return offset;
//		}
//	}
//
//	private static class RecordStaticLayout<T> extends StaticNonTerminal<T> {
//		private final int size;
//		private final int[] offsets;
//		private final Content.StaticLayout<?>[] children;
//
//		public RecordStaticLayout(Content.Constructor<T> constructor, Content.StaticLayout<?>[] children) {
//			super(constructor);
//			this.children = children;
//			this.offsets = new int[children.length];
//			int offset = 0;
//			for (int i = 0; i != children.length; ++i) {
//				offsets[i] = offset;
//				offset += children[i].size();
//			}
//			this.size = offset;
//		}
//
//		@Override
//		public int numberOfChildren(Content.Blob blob,int offset) {
//			return children.length;
//		}
//
//		@Override
//		public int size() {
//			return size;
//		}
//
//		@Override
//		public int size(Blob blob, int offset) {
//			return size;
//		}
//
//		@Override
//		protected Content.Layout<?> getChild(int index, Content.Blob blob, int offset) {
//			return children[index];
//		}
//
//		@Override
//		protected int getChildOffset(int child, Blob blob, int offset) {
//			return offsets[child];
//		}
//	}
}
