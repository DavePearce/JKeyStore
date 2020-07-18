package jledger.core;

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
public final class Transaction<K, V> implements Iterable<Operation<K, V>> {
	private static final Transaction EMPTY = new Transaction();
	
	private final Transaction<K,V> parent;
	private final Operation<K,V> operation;
	
	private Transaction() {
		this.parent = null;
		this.operation = null;
	}
	
	/**
	 * Get the number of operations in this transaction.
	 * 
	 * @return
	 */
	public int size() {
		if (this == EMPTY) {
			return 0;
		} else {
			return 1 + parent.size();
		}
	}

	/**
	 * Get the ith operation in this transaction.
	 * @param i
	 * @return
	 */
	public Operation<K,V> get(int i) {
		throw new IllegalArgumentException("GPT HERE");
	}

	/**
	 * An exception used for indicating a transaction failure has occurred.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Failure extends Exception {

	}
	
	/**
	 * A transaction is made up of multiple operations which assign values to keys.
	 *
	 * @author David J. Pearce
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static class Operation<K, V> {
		private final K target;
		
		private Operation(K target) {
			this.target = target;
		}
		
		/**
		 * Every operation has a unique target key which is affected. Observe that, in
		 * all cases, this key may or may not already exist.
		 *
		 * @return
		 */
		public K getTarget() {
			return target;
		}

		/**
		 * Represents the assignment of a given value to a given key. If the target key
		 * does not exist, it is created. If it already existed, then its value is
		 * overwritten. If the source key does not exist, then the enclosing transaction
		 * fails.
		 *
		 * @author David J. Pearce
		 *
		 * @param <K>
		 * @param <V>
		 */
		public static final class Assign<K, V> implements Operation<K, V> {
			/**
			 * Get the value being assigned to the target key.
			 *
			 * @return
			 */
			public V getValue();
		}

		/**
		 * Represents the deleting of a given key. If the key did not already exist,
		 * then this is a no-operation. Otherwise, the given key and its associated
		 * value is deleted.
		 *
		 * @author David J. Pearce
		 *
		 * @param <K>
		 * @param <V>
		 */
		public static final class Delete<K, V> implements Operation<K, V> {

		}
		
		/**
		 * Represents the copying of one keys' value to another key.
		 *
		 * @author David J. Pearce
		 *
		 * @param <K>
		 * @param <V>
		 */
		public static final class Copy<K, V> implements Operation<K, V> {
			/**
			 * Get the source key whose value is being copied into the target key.  If the
			 * target key does not exist, it is created. If it already existed, then its
			 * value is overwritten. If the source key does not exist, then the enclosing
			 * transaction fails.
			 *
			 * @return
			 */
			public K getSource();
		}

		/**
		 * Represents the moving (or renaming) of one keys value to another key. If the
		 * target key does not exist, it is created. If it already existed, then its
		 * value is overwritten. If the source key does not exist, then the enclosing
		 * transaction fails.
		 *
		 * @author David J. Pearce
		 *
		 * @param <K>
		 * @param <V>
		 */
		public static final class Move<K, V> implements Operation<K, V> {
			/**
			 * Get the source key whose value is being moved into the target key.
			 *
			 * @return
			 */
			public K getSource();
		}
	}
}
