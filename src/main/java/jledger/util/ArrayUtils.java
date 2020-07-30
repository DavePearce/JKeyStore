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
package jledger.util;

import java.util.Arrays;

/**
 * Provides a collection of useful array manipulation methods.
 * 
 * @author David J. Pearce
 *
 */
public class ArrayUtils {
	/**
	 * Replace a sequence of bytes in a given array with another sequence (which may
	 * have a different size).
	 * 
	 * @param bytes       The original sequence of bytes
	 * @param offset      The offset in the original sequence where replacement
	 *                    begins
	 * @param length      The length of the region in the original sequence being
	 *                    replaced
	 * @param replacement The bytes which are replacing the given region in the
	 *                    original sequence
	 * @return
	 */
	public  static byte[] replace(byte[] bytes, int offset, int length, byte[] replacement) {
		// Calculate size of the updated sequence
		final int size = (bytes.length - length) + replacement.length;
		byte[] nbytes = new byte[size];
		// Copy untouched region from beginning of original array up to start of region
		// being replaced.
		System.arraycopy(bytes, 0, nbytes, 0, offset);
		// Copy over replacement sequence.
		System.arraycopy(replacement, 0, nbytes, offset, replacement.length);
		// Copy untouched region from end of replaced region in original array.
		final int n = offset + replacement.length;
		System.arraycopy(bytes, offset + length, nbytes, n, nbytes.length - n);
		// Done
		return nbytes;
	}
}
