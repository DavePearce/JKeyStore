package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;

public abstract class AbstractLayout<T extends Content.Proxy> implements Content.Layout<T> {

	@Override
	public Blob write(T proxy, Blob blob, int offset) {
		byte[] bytes = proxy.toBytes();
		return blob.replaceBytes(offset, sizeOf(blob, offset), bytes);
	}

	@Override
	public Blob insert(T proxy, Blob blob, int offset) {
		byte[] bytes = proxy.toBytes();
		return blob.replaceBytes(offset, 0, bytes);
	}
}
