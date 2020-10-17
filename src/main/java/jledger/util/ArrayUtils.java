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
	 * Check whether or not the elements of this array are sorted according to their
	 * underlying order or not.
	 *
	 * @param children
	 * @return
	 */
	public static <T extends Comparable<T>> boolean isSorted(T[] children) {
		for (int i = 1; i < children.length; ++i) {
			int c = children[i - 1].compareTo(children[i]);
			if (c > 0) {
				// NOT in sorted order
				return false;
			}
		}
		// All good
		return true;
	}

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
	public static byte[] replace(byte[] bytes, int offset, int length, byte[] replacement) {
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

	/**
	 * Append an items onto the end of an array of unknown type together, producing
	 * a fresh array whose length equals that of the first and second added
	 * together.
	 *
	 * @param lhs The left-hand side. Elements of this array will be copied first
	 *            into the resulting array.
	 * @param rhs The right-hand side which will form the last element of the
	 *            resulting array
	 * @return
	 */
	public static <T> T[] append(T[] lhs, T rhs) {
		final int n = lhs.length;
		T[] rs = java.util.Arrays.copyOf(lhs, n + 1);
		rs[n] = rhs;
		return rs;
	}

	/**
	 * Append two byte arrays type together, producing a fresh array whose length
	 * equals that of the first and second added together.
	 *
	 * @param lhs The left-hand side. Elements of this array will be copied first
	 *            into the resulting array.
	 * @param rhs The right-hand side. Elements of this array will be copied last
	 *            into the resulting array.
	 * @return
	 */
	public static byte[] append(byte[] lhs, byte[] rhs) {
		byte[] rs = java.util.Arrays.copyOf(lhs, lhs.length + rhs.length);
		System.arraycopy(rhs, 0, rs, lhs.length, rhs.length);
		return rs;
	}

	/**
	 * Insert a given item into an array of items at the given position producing an
	 * updated array where everything else remains unchanged.
	 *
	 * @param <T>
	 * @param index
	 * @param item
	 * @param items
	 */
	public static <T> T[] insert(int index, T item, T[] items) {
		T[] nitems = Arrays.copyOf(items, items.length + 1);
		System.arraycopy(items, index, nitems, index + 1, items.length - index);
		nitems[index] = item;
		return nitems;
	}

	/**
	 * Remove any occurrence of a given value from an array. The resulting array
	 * may be shorter in length, but the relative position of all other items
	 * will remain unchanged. This algorithm is robust to <code>null</code>. The
	 * <code>items</code> array may contain <code>null</code> values and the
	 * <code>item</code> may itself be <code>null</code> (in which case, all
	 * <code>null</code> values are removed).
	 *
	 * @param items
	 * @return
	 */
	public static <T> T[] removeAll(T[] items, T item) {
		int count = 0;
		// First, determine the number of elements which will be removed
		for (int i = 0; i != items.length; ++i) {
			T ith = items[i];
			if (ith == item || (item != null && item.equals(ith))) {
				count++;
			}
		}
		// Second, eliminate duplicates (if any)
		if (count == 0) {
			// nothing actually needs to be removed
			return items;
		} else {
			T[] nItems = Arrays.copyOf(items, items.length - count);
			for(int i=0, j = 0;i!=items.length;++i) {
				T ith = items[i];
				if (ith == item || (item != null && item.equals(ith))) {
					// skip item
				} else {
					nItems[j++] = ith;
				}
			}
			return nItems;
		}
	}
	
	/**
	 * Remove all items between <code>i</code> (inclusive) and <code>j</code>
	 * (exclusive) from a given array. This is guaranteed to return a new array when
	 * <code>i!=j</code>.
	 * 
	 * @param <T>
	 * @param i
	 * @param j
	 * @param items
	 * @return
	 */
	public static <T> T[] remove(int i, int j, T[] items) {
		if(i == j) {
			return items;
		} else {
			final int n = j - i;
			T[] nitems = Arrays.copyOf(items, items.length - n);
			System.arraycopy(items, j, nitems, i, items.length - j);
			return nitems;
		}
	}
}
