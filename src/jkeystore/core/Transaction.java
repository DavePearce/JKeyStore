package jkeystore.core;

/**
 * A transaction is a sequence of operations which modify a key-value store in
 * some way. Each operation updates a given target key. Furthermore, operations
 * are executed in the order declared within this transaction. Hence, operations
 * affecting the same key can interfere.
 *
 * @author David J. Pearce
 *
 * @param <K>
 * @param <V>
 */
public interface Transaction<K, V> extends Iterable<Operation<K, V>> {

	/**
	 * Get the number of operations in this transaction.
	 * @return
	 */
	public int size();

	/**
	 * Get the ith operation in this transaction.
	 * @param i
	 * @return
	 */
	public Operation<K,V> get(int i);

	/**
	 * An exception used for indicating a transaction failure has occurred.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class Failure extends Exception {

	}
}
