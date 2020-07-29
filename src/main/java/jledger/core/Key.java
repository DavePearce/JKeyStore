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

public interface Key extends Iterable<String>, Comparable<Key> {

	/**
	 * Get the number of components that make up this ID.
	 * @return
	 */
	public int size();

	/**
	 * Return the component at a given index.
	 * @param index
	 * @return
	 */
	public String get(int index);

	/**
	 * A convenience function that gets the last component of this path.
	 *
	 * @return
	 */
	public String last();

	/**
	 * Get the parent of this path.
	 *
	 * @return
	 */
	public Key parent();

	/**
	 * Get a sub Key from this Key, which consists of those components between
	 * start and end (exclusive).
	 *
	 * @param start
	 *            --- starting component index
	 * @param start
	 *            --- one past last component index
	 * @return
	 */
	public Key subpath(int start, int end);

	/**
	 * Append a component onto the end of this Key.
	 *
	 * @param component
	 *            --- to be appended
	 * @return
	 */
	public Key append(String component);

	/**
	 * Append all components from an Key onto the end of this Key.
	 *
	 * @param Key
	 * @return
	 */
	public Key append(Key id);
}
