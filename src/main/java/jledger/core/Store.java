// Copyright 2020 David J. Pearce
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package jledger.core;

/**
 * Provides a convenient interface to a ledge which allows easy and efficient
 * access to the key/value pairs stored within. In effect, it's a snapshot of
 * the ledger at a given point.
 * 
 * @author David J. Pearce
 *
 * @param <K>
 * @param <V>
 */
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
