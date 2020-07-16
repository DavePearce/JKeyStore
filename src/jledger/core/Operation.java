package jledger.core;


/**
 * A transaction is made up of multiple operations which assign values to keys.
 *
 * @author David J. Pearce
 *
 * @param <K>
 * @param <V>
 */
public interface Operation<K, V> {

	/**
	 * Every operation has a unique target key which is affected. Observe that, in
	 * all cases, this key may or may not already exist.
	 *
	 * @return
	 */
	public K getTarget();

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
	public interface Assign<K, V> extends Operation<K, V> {
		/**
		 * Get the value being assigned to the target key.
		 *
		 * @return
		 */
		public V getValue();
	}

	/**
	 * Represents the copying of one keys' value to another key.
	 *
	 * @author David J. Pearce
	 *
	 * @param <K>
	 * @param <V>
	 */
	public interface Copy<K, V> extends Operation<K, V> {
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
	public interface Move<K, V> extends Operation<K, V> {
		/**
		 * Get the source key whose value is being moved into the target key.
		 *
		 * @return
		 */
		public K getSource();
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
	public interface Delete<K, V> extends Operation<K, V> {

	}
}