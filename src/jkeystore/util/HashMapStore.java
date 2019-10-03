package jkeystore.util;

import java.util.HashMap;

import jkeystore.core.Key;
import jkeystore.core.Query;
import jkeystore.core.Store;
import jkeystore.core.Value;

/**
 * A lightweight implementation of Store which is not effecient. In particular,
 * queries are linear in the size of the store.
 *
 * @author David J. Pearce
 *
 * @param <K>
 * @param <V>
 */
public class HashMapStore<K extends Key, V extends Value> extends HashMap<K, V> implements Store<K, V> {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public V get(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<jkeystore.core.Store.Entry<K, V>> select(Query q) {
		// This operation will be time linear in the number of elements in the hash map.
		return null;
	}

}
