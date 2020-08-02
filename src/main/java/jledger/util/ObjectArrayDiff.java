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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * Represents a sequence of one or more updates to a given object array. For
 * example, consider the following update to a given array:
 * </p>
 * 
 * <pre>
 *  0 1 2 3 4 5 6 7 8 9 A
 * +-+-+-+-+-+-+-+-+-+-+-+
 * |H|e|l|l|o| |W|o|r|l|d|
 * +-+-+-+-+-+-+-+-+-+-+-+
 * 
 *           \/
 *           \/
 *           
 * +-+-+-+-+-+-+-+-+-+-+
 * |h|e|l|l|o|w|o|r|l|d|
 * +-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * 
 * <p>
 * This change can be encoded using a <code>Diff</code> containing three deltas
 * as follows: <code>(0,1,"h");(5,1,"");(6,1,"w")</code>. For example,
 * <code>(0,1,"h")</code> means replace the region from <code>0</code> to
 * <code>1</code> (exclusive)
 * 
 * </p>
 * 
 * @author David J. Pearce
 *
 */
public class ObjectArrayDiff<T> extends AbstractDiff<ObjectArrayDiff.Delta<T>> {

	/**
	 * Construct a diff from a given sequence of one or more non-overlapping deltas
	 * in sorted order.
	 * 
	 * @param deltas
	 */
	private ObjectArrayDiff(Delta<T>... deltas) {
		super(deltas);		
	}

	public T[] apply(T[] items) {
		// Determine size of final array
		final int size = items.length + diff();
		// Construct final array
		T[] result = Arrays.copyOf(items, size);
		int opos = 0;
		int rpos = 0;
		// Apply delta's one-by-one
		for (int i = 0; i < deltas.length; ++i) {
			final Delta<T> ith = deltas[i];
			final T[] ithobjects = ith.objects;
			// Calculate gap
			int gap = ith.offset - opos;
			// Copy section up to delta start
			System.arraycopy(items, opos, result, rpos, gap);
			// move pointer along
			rpos = rpos + gap;
			// Copy delta replacement itself
			System.arraycopy(ithobjects, 0, result, rpos, ithobjects.length);			
			// move pointers along			
			opos = opos + gap + ith.length;
			rpos = rpos + ithobjects.length;
		}
		// Finally, copy over any remaining bytes from original sequence.
		System.arraycopy(items, opos, result, rpos, items.length - opos);
		//
		return result;
	}

	/**
	 * <p>
	 * Represents an atomic replacement in the original array. Specifically, a
	 * region in the original array is replaced by a new sequence of bytes. Observe
	 * that this region may be larger or smaller than the original region. The
	 * following illustrates:
	 * </p>
	 * 
	 * <pre>
	 *  0 1 2 3 4 5 6 7 8 9 A B
	 * +-+-+-+-+-+-+-+-+-+-+-+-+
	 * |H|e|L|L|L|O| |W|o|r|l|d|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+
	 *     | : : : |
	 *     +-------+
	 *         |
	 *        \|/
	 *      +-+-+-+
	 *      |l|l|o|
	 *      +-+-+-+
	 * </pre>
	 * 
	 * <p>
	 * The above illustrates the region "LLLO" from the original sequence being
	 * replaced by "llo". Thus, after applying the delta, we have the string "Hello
	 * World".
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Delta<T> extends AbstractDiff.AbstractDelta implements Comparable<Delta<T>> {
		/**
		 * Sequence which replaced the region from the original sequence.
		 */
		private final T[] objects;

		public Delta(int offset, int length, T[] objects) {
			super(offset,length);
			if(objects == null) {
				throw new IllegalArgumentException("null bytes");
			}
			this.objects = objects;
		}
		
		@Override
		public int diff() {
			return objects.length - length;
		}

		/**
		 * Get the bytes which replace the region in the original array.
		 * 
		 * @return
		 */
		public T[] objects() {
			return objects;
		}
		
		public String toString() {
			return "(" + offset + ";" + length + ";" + Arrays.toString(objects) + ")";
		}

		@Override
		public int compareTo(Delta<T> o) {
			return super.compareTo(o);
		}
	}
	
	/**
	 * Construct a diff between two arrays of bytes using the <i>longest common
	 * subsequence</i> algorithm.
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static <T> ObjectArrayDiff<T> construct(T[] before, T[] after) {
		// Apply the LCS algorithm
		int[] mapping = longestCommonSubsequence(before, after);
		// Convert mapping to deltas
		List<Delta<T>> deltas = extractDeltas(mapping, after);		
		// Contruct final diff.
		return new ObjectArrayDiff<T>(deltas.toArray(new Delta[deltas.size()]));
	}

	/**
	 * <p>
	 * Extract delta's using a given mapping from the before sequence to the after
	 * sequence, as generated from the <i>least common subsequence</i> algorithm.
	 * For example:
	 * </p>
	 * 
	 * <pre>
	 *  0 1 2
	 * +-+-+-+-+-+
	 * |a|b|c|d|e| (before)
	 * +-+-+-+-+-+
	 *    | |
	 *   / /
	 *  | |
	 * +-+-+-+-+
	 * |b|c|f|g| (after)
	 * +-+-+-+-+
	 * </pre>
	 * 
	 * <p>
	 * In this case, the mapping would be <code>[-1,0,1,-1,-1]</code> which
	 * indicates positions <code>0</code>, <code>3</code> and <code>4</code> are
	 * removed whilst positions <code>1</code> and <code>2</code> correspond to
	 * positions <code>0</code> and <code>1</code> in the final sequence.
	 * </p>
	 * 
	 * <p>
	 * The current extraction mechanism could still be improved in that it can
	 * generate lots of small delta's when a single large one would be more
	 * sensible. Potentially, some form of post processing could coalesce delta's
	 * as necessary.
	 * </p>
	 * 
	 * @param mapping
	 * @param after
	 * @return
	 */
	private static <T> List<Delta<T>> extractDeltas(int[] mapping, T[] after) {
		ArrayList<Delta<T>> deltas = new ArrayList<>();
		// Initialise after markers
		int aStart = 0, aPos = 0;
		// Initialise before markers
		int bStart = 0, bPos = 0;
		// Proceed extracting delta's
		while (bPos < mapping.length && aPos < after.length) {
			if (mapping[bPos] > aPos) {
				// Uneven case. Increase after buffer
				aPos = mapping[bPos];
			} else if (mapping[bPos] < aPos) {
				// Uneven case. Increase before buffer
				bPos = bPos + 1;
			} else {
				// Matching case. Flush buffers and advance
				if (bStart < bPos || aStart < aPos) {
					T[] additions = Arrays.copyOfRange(after, aStart, aPos);
					deltas.add(new Delta<T>(bStart, bPos - bStart, additions));
				}
				aStart = aPos = aPos + 1;
				bStart = bPos = bPos + 1;
			}
		}
		// Flush remaining buffers
		if (bStart < mapping.length || aStart < after.length) {
			// Terminating case. Flush buffers and end.
			T[] additions = Arrays.copyOfRange(after, aStart, after.length);
			deltas.add(new Delta<T>(bStart, mapping.length - bStart, additions));
		}
		//
		return deltas;
	}

	/**
	 * <p>
	 * Determine the longest common subsequence of two sequences. For example,
	 * suppose <code>X=[1,2,2,3,2,3,4]</code> and <code>Y=[2,2,5,3,4,5]</code> then
	 * a <i>common subsequence</code> is <code>[2,2]</code> and another is
	 * <code>[2,3,4]</code>. However, the <i>longest common subsequence</code> is
	 * <code>[2,2,3,4]</code>.
	 * </p>
	 *
	 * <p>
	 * This algorithm produces a mapping from elements in X to elements in Y. For
	 * the above example, it might produce <code>[-1,0,1,3,-1,-1]</code>. Here,
	 * <code>-1</code> indicates the element in <code>X</code> does not match an
	 * element in <code>Y</code>. In contrast, non-negative values are the matching
	 * indices in <code>Y</code>.
	 * </p>
	 *
	 * <p>
	 * <b>NOTE:</b> Whilst the above example had only one longest common
	 * subsequence, it's not always the case there is only one. This algorithm
	 * simply returns <i>a</i> longest common subsequence.
	 * </p>
	 *
	 *
	 * @param X The left sequence
	 * @param Y The right sequence
	 * @return The resulting mapping
	 */
	private static <T> int[] longestCommonSubsequence(T[] X, T[] Y) {
		final int m = X.length + 1;
		final int n = Y.length + 1;
		final int[] C = new int[m * n];
		// Calculate the lengths
		for (int i = 1; i < m; ++i) {
			for (int j = 1; j < n; ++j) {
				int ij = i + (j * m);
				if (X[i - 1].equals(Y[j - 1])) {
					C[ij] = C[(i - 1) + ((j - 1) * m)] + 1;
				} else {
					final int Cim1j = C[(i - 1) + (j * m)];
					final int Cijm1 = C[i + ((j - 1) * m)];
					C[ij] = (Cim1j >= Cijm1) ? Cim1j : Cijm1;
				}
			}
		}
		// Finally, extract the LCS
		int[] Z = new int[X.length];
		Arrays.fill(Z, -1);
		extractSubsequence(C, Z, m - 1, n - 1);
		return Z;
	}
}