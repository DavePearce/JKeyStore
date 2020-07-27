package jledger.core;

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
public interface Ledger<K,V> {

	/**
	 * Get the number of transactions in this ledger.
	 *
	 * @return
	 */
	public long size();

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
	 * Add a new transaction to the ledger. This may fail if the underlying
	 * transaction fails for some reason.
	 *
	 * @param t
	 */
	public void add(Transaction<K, V> t) throws Transaction.Failure;
}
