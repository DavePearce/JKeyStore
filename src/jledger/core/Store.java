package jledger.core;

public interface Store<K extends Key, V extends Value> {

	/**
	 * Associate a given value with a given given in the store. If the key already
	 * existed, then its value is overwritten. Otherwise, a new key is created.
	 *
	 * @param key
	 * @param value
	 * @return The old value associated with this key, or <code>null</code> if no
	 *         such value existed.
	 */
	public V put(K key, V value);

	/**
	 * Get the value associated with a given key.
	 *
	 * @param key
	 * @return
	 */
	public V get(K key);

	/**
	 * Select the results of a given query from this store.
	 *
	 * @param q
	 * @return
	 */
	public Iterable<Entry<K,V>> select(Query q);

	/**
	 * Represents a given entry in a key value store.
	 *
	 * @author David J. Pearce
	 *
	 * @param <K>
	 * @param <V>
	 */
	public interface Entry<K extends Key, V extends Value> {
		/**
		 * Get the key associated with this entry.
		 *
		 * @return
		 */
		public K getKey();

		/**
		 * Get the value associated with this entry.
		 *
		 * @return
		 */
		public V getValue();
	}
}
