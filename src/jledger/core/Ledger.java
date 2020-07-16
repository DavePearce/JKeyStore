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
public interface Ledger<K,V,T extends Transaction<K,V>> {

	/**
	 * Get the number of transactions in this ledger.
	 *
	 * @return
	 */
	public long size();

	/**
	 * Get a given transaction from the ledger.
	 *
	 * @param txn
	 * @return
	 */
	public Transaction<K,V> get(long txn);

	/**
	 * Add a new transaction to the ledger. This may fail if the underlying
	 * transaction fails for some reason.
	 *
	 * @param t
	 */
	public void add(Transaction<K, V> t) throws Transaction.Failure;
}
