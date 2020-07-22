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
public final class Transaction<K, V> implements Iterable<Transaction.Operation<K, V>> {
	@SuppressWarnings("rawtypes")
	private static final Transaction EMPTY = new Transaction<>(null,null);
	
	private final Transaction<K,V> parent;
	private final Operation<K,V> operation;
		
	private Transaction(Transaction<K,V> parent, Operation<K,V> operation) {
		this.parent = parent;
		this.operation = operation;
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

	@Override
	public java.util.Iterator<Operation<K, V>> iterator() {
		return new Iterator<>(this);
	}
	
	public Transaction<K,V> assign(K target, V value) {
		return new Transaction<>(this,new Operation.Assign<K,V>(target,value));
	}
	
	public Transaction<K,V> copy(K source, K target) {
		return new Transaction<>(this,new Operation.Copy<K,V>(source,target));
	}
	
	public Transaction<K,V> move(K source, K target) {
		return new Transaction<>(this,new Operation.Move<K,V>(source,target));
	}
	
	public Transaction<K,V> delete(K target) {
		return new Transaction<>(this,new Operation.Delete<K,V>(target));
	}
	
	/**
	 * Get the ith operation in this transaction.
	 * @param i
	 * @return
	 */
	public Operation<K,V> get(int i) {
		throw new IllegalArgumentException("GOT HERE");
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
		public static final class Assign<K, V> extends Operation<K, V> {
			private final V value;
			
			public Assign(K target, V value) {
				super(target);
				this.value = value;
			}
			/**
			 * Get the value being assigned to the target key.
			 *
			 * @return
			 */
			public V getValue() {
				return value;
			}
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
		public static final class Delete<K, V> extends Operation<K, V> {
			public Delete(K target) {
				super(target);				
			}
		}
		
		/**
		 * Represents the copying of one keys' value to another key.
		 *
		 * @author David J. Pearce
		 *
		 * @param <K>
		 * @param <V>
		 */
		public static final class Copy<K, V> extends Operation<K, V> {
			private final K source;

			public Copy(K source, K target) {
				super(target);
				this.source = source;
			}

			/**
			 * Get the source key whose value is being copied into the target key. If the
			 * target key does not exist, it is created. If it already existed, then its
			 * value is overwritten. If the source key does not exist, then the enclosing
			 * transaction fails.
			 *
			 * @return
			 */
			public K getSource() {
				return source;
			}
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
		public static final class Move<K, V> extends Operation<K, V> {
			private final K source;

			public Move(K source, K target) {
				super(target);
				this.source = source;
			}

			/**
			 * Get the source key whose value is being moved into the target key.
			 *
			 * @return
			 */
			public K getSource() {
				return source;
			}
		}
	}
	
	private static class Iterator<K,V> implements java.util.Iterator<Operation<K,V>> {
		private Transaction<K,V> tx;
		
		public Iterator(Transaction<K,V> root) {
			this.tx = root;
		}
		
		@Override
		public boolean hasNext() {
			return tx != EMPTY;
		}

		@Override
		public Operation<K,V> next() {
			Operation<K,V> op = tx.operation;
			tx = tx.parent;
			return op;
		}
		
	}
}
