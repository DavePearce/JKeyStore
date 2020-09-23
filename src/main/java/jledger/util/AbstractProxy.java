package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Layout;

public class AbstractProxy<S extends Content.Proxy, T extends Content.Layout<S>> implements Content.Proxy {
	protected final T layout;
	protected final Content.Blob blob;
	protected final int offset;

	public AbstractProxy(T layout) {
		this.layout = layout;
		this.blob = layout.initialise(ByteBlob.EMPTY, 0);
		this.offset = 0;
	}

	public AbstractProxy(T layout, Content.Blob blob, int offset) {
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
	public Content.Layout<?> getLayout() {
		return layout;
	}

	@Override
	public int sizeOf() {
		return layout.sizeOf(blob, offset);
	}

	@Override
	public boolean equals(Object o) {
		// NOTE: Equality is defined in a structural fashion based on a bitwise
		// equality.
		if (o instanceof Content.Proxy) {
			Content.Proxy p = (Content.Proxy) o;
			final int n = sizeOf();
			if (layout.equals(p.getLayout()) && n == p.sizeOf()) {
				Content.Blob pBlob = p.getBlob();
				int pOffset = p.getOffset();
				for (int i = 0; i < n; ++i) {
					if (blob.readByte(offset + i) != pBlob.readByte(pOffset + i)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int n = sizeOf();
		// FIXME: this could be improved!
		int hc = 0;
		for (int i = 0; i != n; ++i) {
			hc ^= blob.readByte(i);
		}
		return hc;
	}

	@Override
	public byte[] toBytes() {
		int size = layout.sizeOf(blob, offset);
		return blob.readBytes(offset, size);
	}
}
