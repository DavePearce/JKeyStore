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
 * A generic mechanism for identifying one or more keys.
 *
 * @author David J. Pearce
 *
 */
public interface Query {
	/**
	 * Check whether a given entry is matched by this filter.
	 *
	 * @param id --- id to test.
	 * @return --- true if it matches, otherwise false.
	 */
	public boolean matches(Key id);

	/**
	 * Check whether a given subkey is matched by this filter. A matching subpath
	 * does not necessarily identify an exact match; rather, it may be an enclosing
	 * folder.
	 *
	 * @param id
	 * @return
	 */
	public boolean matchesSubpath(Key id);
}
