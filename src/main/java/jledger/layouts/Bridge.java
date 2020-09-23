package jledger.layouts;

import java.util.function.Function;

import jledger.core.Content;
import jledger.core.Content.Blob;

public class Bridge {

	public static <S, T> Layout<S,T> BRIDGE(Content.Layout<T> child, Function<T,S> northBridge) {
		return new Layout<>(child, northBridge);
	}

	private static class Layout<S, T> implements Content.Layout<S> {
		private final Content.Layout<T> child;
		private final Function<T,S> bridge;

		public Layout(Content.Layout<T> child, Function<T,S> bridge) {
			this.child = child;
			this.bridge = bridge;
		}

		@Override
		public int sizeOf(Blob blob, int offset) {
			return child.sizeOf(blob, offset);
		}

		@Override
		public Blob initialise(Blob blob, int offset) {
			return child.initialise(blob, offset);
		}

		@Override
		public S read(Blob blob, int offset) {
			T item = child.read(blob, offset);
			return bridge.apply(item);
		}

		@Override
		public Blob write(S item, Blob blob, int offset) {
//			return child.write(item, blob, offset);
			throw new IllegalArgumentException();
		}

		@Override
		public Blob insert(S item, Blob blob, int offset) {
//			return child.insert(item, blob, offset);
			throw new IllegalArgumentException();
		}
	}

//
//	public static abstract class Bridge<T extends Content.Proxy> implements Content.Proxy {
//		protected final T target;
//
//		public Bridge(T target) {
//			this.target = target;
//		}
//
//		@Override
//		public int getOffset() {
//			return target.getOffset();
//		}
//
//		@Override
//		public Blob getBlob() {
//			return target.getBlob();
//		}
//
//		@Override
//		public int sizeOf() {
//			return target.getOffset();
//		}
//
//		@Override
//		public byte[] toBytes() {
//			return target.toBytes();
//		}
//
//	}
}
