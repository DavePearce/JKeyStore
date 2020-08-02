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
import java.util.Iterator;

public class AbstractDiff<T extends AbstractDiff.AbstractDelta & Comparable<T>> implements Iterable<T> {
	protected final T[] deltas;
	
	public AbstractDiff(T[] deltas) {
		if (deltas == null) {
			throw new IllegalArgumentException("null deltas");
		} else if (!ArrayUtils.isSorted(deltas)) {
			throw new IllegalArgumentException("unsorted deltas");
		} else if (!disjoint(deltas)) {
			throw new IllegalArgumentException("overlapping deltas");
		}
		this.deltas = deltas;
	}

	/**
	 * Get the number of delta's in this diff.
	 * 
	 * @return
	 */
	public int size() {
		return deltas.length;
	}
	
	/**
	 * Get the ith delta associated with this diff.
	 * 
	 * @param ith
	 * @return
	 */
	public T get(int ith) {
		return deltas[ith];
	}
	
	@Override
	public Iterator<T> iterator() {
		return Arrays.stream(deltas).iterator();
	}
	
	/**
	 * Get the change in size this diff represents.
	 * 
	 * @return
	 */
	public int diff() {
		int diff = 0;
		for (int i = 0; i != deltas.length; ++i) {
			diff += deltas[i].diff();
		}
		return diff;
	}

	public String toString() {		
		String r = "";
		if(deltas.length > 0) {
			r += deltas[0].toString();
			for(int i=1;i!=deltas.length;++i) {
				r += "," + deltas[i].toString();
			}
		}
		return "{" + r + "}";
	}	

	protected static abstract class AbstractDelta {
		/**
		 * Offset in the original sequence where region being replaced begins.
		 */
		protected final int offset;
		/**
		 * Length of region in the original sequence being replaced.
		 */
		protected final int length;
		
		public AbstractDelta(int offset, int length) {
			if(offset < 0) {
				throw new IllegalArgumentException("negative offset");
			} else if(length < 0) {
				throw new IllegalArgumentException("negative length");
			} 
			this.offset = offset;
			this.length = length;
		}
				
		/**
		 * Get the offset in the original array where the region replaced by this delta
		 * begins.
		 * 
		 * @return
		 */
		public int offset() {
			return offset;
		}
		
		/**
		 * Get the size of the region in the original array which is replaced by this
		 * delta.
		 * 
		 * @return
		 */
		public int length() {
			return length;
		}
		
		/**
		 * Calculate the overall change in length of the original sequence resulting
		 * from this delta.  Thus, 
		 * 
		 * @return
		 */
		abstract protected int diff();
		
		/**
		 * Check this delta is disjoint with another (i.e. they don't overlap).
		 * 
		 * @param other
		 * @return
		 */
		public boolean disjoint(AbstractDelta other) {
			int end = (offset + length) - 1;
			int oend = (other.offset + other.length) - 1;
			return (end < other.offset) || (oend < offset);
		}
		
		public int compareTo(AbstractDelta o) {
			if (offset < o.offset) {
				return -1;
			} else if (offset > o.offset) {
				return 1;
			}
			int end = (offset + length) - 1;
			int oend = (o.offset + o.length) - 1;
			if (end < oend) {
				return -1;
			} else if (end > oend) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	

	protected static void extractSubsequence(int[] C, int[] Z, int i, int j) {
		final int m = Z.length + 1;
		if (i > 0 && j > 0) {
			int Cij = C[i + (j * m)];
			final int Cim1j = C[(i - 1) + (j * m)];
			final int Cijm1 = C[i + ((j - 1) * m)];
			if (Cij == Cim1j) {
				Z[i - 1] = -1;
				extractSubsequence(C, Z, i - 1, j);
			} else if (Cij == Cijm1) {
				Z[i - 1] = -1;
				extractSubsequence(C, Z, i, j - 1);
			} else {
				extractSubsequence(C, Z, i - 1, j - 1);
				Z[i - 1] = j - 1;
			}
		}
	}

	/**
	 * Check whether a given array of delta's are overlapping or not.
	 * 
	 * @param deltas
	 * @return
	 */
	protected static boolean disjoint(AbstractDelta[] deltas) {
		for (int i = 1; i < deltas.length; ++i) {
			AbstractDelta ithm1 = deltas[i - 1];
			AbstractDelta ith = deltas[i];
			if (!ithm1.disjoint(ith)) {
				return false;
			}
			ithm1 = ith;
		}
		return true;
	}

	/**
	 * Useful for debugging.
	 * 
	 * @param matrix
	 * @param m
	 * @param n
	 */
	protected static void printMatrix(int[] matrix, int m, int n) {
		System.out.print("   ");
		for (int i = 0; i < m; ++i) {
			System.out.print(String.format("%02d", i) + " ");
		}
		System.out.println();
		System.out.print("  +");
		for (int k = 0; k < m; ++k) {
			System.out.print("--+");
		}
		System.out.println();
		for (int j = 0; j < n; ++j) {
			System.out.print(String.format("%02d|", j));
			for (int i = 0; i < m; ++i) {
				int ij = i + (j * m);
				System.out.print(String.format("%02d|", matrix[ij]));
			}
			System.out.println();
			System.out.print("  +");
			for (int k = 0; k < m; ++k) {
				System.out.print("--+");
			}
			System.out.println();
		}
	}
}
