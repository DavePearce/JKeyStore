package jledger.util;

import jledger.core.Content;
import jledger.core.Content.Blob;
import jledger.core.Content.Proxy;

public class TreeLayout implements Content.Layout {
	private TreeLayout INT = new TreeLayout(1, 1);
	private TreeLayout BOOL = new TreeLayout(1, 1);
	private TreeLayout STRING = new TreeLayout(1, 1);
	
	private final int min; 
	private final int max;
	private final Content.Layout[] children;

	public TreeLayout(int min, int max,  Content.Layout... children) {
		this.min = min;
		this.max = max;
		this.children = children;
	}

	public int minimumCount() {
		return 1;
	}

	public int maximumCount() {
		return 1;
	}

	public int size() {
		return children.length;
	}

	public Content.Layout get(int i) {
		return children[i];
	}
	
	@Override
	public Proxy decode(Blob blob) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob encode(Proxy object) {
		// TODO Auto-generated method stub
		return null;
	}

}
