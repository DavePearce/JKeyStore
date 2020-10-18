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
 * Represents a versioned object implemented using an immutable transaction
 * ledger.
 *
 * @author David J. Pearce
 *
 * @param <T>
 */
public interface Ledger<T extends Content.Proxy> {
	/**
	 * Get the number of versions currently stored in this ledger.
	 *
	 * @return
	 */
	public int versions();

	/**
	 * Get the latest version of the ledger's contents.
	 *
	 * @return
	 */
	public T last();

	/**
	 * Get the current state at a given timestamp.
	 *
	 * @return
	 */
	public T get(int timestamp);

	/**
	 * Write a new version of the object into this ledger. This increases the number
	 * of versions by one. Observe that this could lead to a concurrent modification
	 * error if objects are written out of sequence.
	 *
	 * @param object
	 */
	public void put(T object);
}
