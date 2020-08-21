package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;
import jledger.util.AbstractLayouts.NonTerminal;
import jledger.util.AbstractLayouts.StaticNonTerminal;

public class RecordLayouts {

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
	public static Content.Layout RECORD(Content.Layout... children) {
		return new RecordLayout(children);
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
	public static Content.Layout RECORD(Content.StaticLayout... children) {
		return new RecordStaticLayout(children);
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
	public static <T extends Content.Proxy> Content.ConstructorLayout<T> RECORD(Content.Constructor<T> constructor,
			Content.Layout... children) {
		return new RecordConstructorLayout<T>(constructor, children);
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
	public static <T extends Content.Proxy> Content.StaticConstructorLayout<T> RECORD(Content.Constructor<T> constructor,
			Content.StaticLayout... children) {
		return new RecordConstructorStaticLayout<T>(constructor, children);
	}
	
	// =======================================================================
	// Helpers
	// =======================================================================
	
	private static class RecordLayout extends NonTerminal {
		private final Content.Layout[] children;
		
		public RecordLayout(Content.Layout[] children) {
			this.children = children;
		}
		
		@Override
		public int numberOfChildren(Content.Blob blob,int offset) {
			return children.length;
		}

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
	}

	private static class RecordConstructorLayout<T extends Content.Proxy> extends RecordLayout
			implements Content.ConstructorLayout<T> {
		private final Content.Constructor<T> constructor;
		
		public RecordConstructorLayout(Content.Constructor<T> constructor, Layout[] children) {
			super(children);
			this.constructor = constructor;
		}

		@Override
		public T read(Blob blob, int offset) {
			return constructor.read(blob, offset);
		}
		
	}
	
	private static class RecordStaticLayout extends StaticNonTerminal {
		private final int size;
		private final int[] offsets;
		private final Content.StaticLayout[] children;
		
		public RecordStaticLayout(Content.StaticLayout[] children) {
			this.children = children;
			this.offsets = new int[children.length];
			int offset = 0;
			for (int i = 0; i != children.length; ++i) {
				offsets[i] = offset;
				offset += children[i].size();
			}
			this.size = offset;
		}
		
		@Override
		public int numberOfChildren(Content.Blob blob,int offset) {
			return children.length;
		}

		public int size() {
			return size;
		}
		
		@Override
		public int size(Blob blob, int offset) {
			return size;
		}

		@Override
		protected Content.Layout getChild(int index, Content.Blob blob, int offset) {
			return children[index];
		}

		@Override
		protected int getChildOffset(int child, Blob blob, int offset) {
			return offsets[child];
		}
	}
	
	private static class RecordConstructorStaticLayout<T extends Content.Proxy> extends RecordStaticLayout
			implements Content.StaticConstructorLayout<T> {
		private final Content.Constructor<T> constructor;

		public RecordConstructorStaticLayout(Content.Constructor<T> constructor,
				Content.StaticLayout[] children) {
			super(children);
			this.constructor = constructor;
		}

		@Override
		public T read(Blob blob, int offset) {
			return constructor.read(blob, offset);
		}

	}
}
