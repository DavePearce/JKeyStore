package jledger.core;

import jledger.util.Pair;

/**
 * Represents an immutable transaction ledger. That is a sequence of zero or
 * more transactions which can be appended.
 *
 * @author David J. Pearce
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
public interface Ledger<K,V extends Value.Interned<K, V>> {

	/**
	 * Get the current value associated with a given key.
	 * @return
	 */
	public V get(K key);

	/**
	 * Get the current value associated with a given key at a given timestamp.
	 * 
	 * @return
	 */
	public V get(int timestamp, K key);
	
	/**
	 * Check whether a given key exists in the ledger or not. If it does exist, then
	 * return that.
	 * 
	 * @param key String representation of the key.
	 * @return The key itself, or <code>null</code> if the key doesn't exist.
	 */
	public K lookup(String key);
	
	/**
	 * Add a new key to the ledger corresponding to a given string.
	 * 
	 * @param key
	 * @return
	 */
	public K add(String key);
	
	/**
	 * Add a new value to the ledger producing an interned value which can be used
	 * for transactions on this ledger.
	 * 
	 * @param value
	 * @return
	 */
	public V add(Value value);
	
	/**
	 * Apply a sequence of key/value assignments as a single atomic transaction.
	 *
	 * @param t
	 */
	public void add(Pair<K, V>... txn);
}
