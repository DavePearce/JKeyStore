package jledger.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * <p>
 * Represents a sequence of one or more updates to a given byte array. For
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
public class Diff implements Iterable<Diff.Delta> {
	private Delta[] deltas;

	/**
	 * Construct a diff from a given sequence of one or more non-overlapping deltas
	 * in sorted order.
	 * 
	 * @param deltas
	 */
	private Diff(Delta... deltas) {
		if (deltas == null) {
			throw new IllegalArgumentException("null deltas");
		} else if(!ArrayUtils.isSorted(deltas)) {
			throw new IllegalArgumentException("unsorted deltas");
		} else if(!disjoint(deltas)) {
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
	public Delta get(int ith) {
		return deltas[ith];
	}
	
	@Override
	public Iterator<Delta> iterator() {
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

	public String apply(String str) {
		return new String(apply(str.getBytes()));
	}
	
	public byte[] apply(byte[] bytes) {
		// Determine size of final array
		final int size = bytes.length + diff();
		// Construct final array
		byte[] result = new byte[size];
		int opos = 0;
		int rpos = 0;
		// Apply delta's one-by-one
		for (int i = 0; i < deltas.length; ++i) {
			final Delta ith = deltas[i];
			final byte[] ithbytes = ith.bytes;
			// Calculate gap
			int gap = ith.offset - opos;
			// Copy section up to delta start
			System.arraycopy(bytes, opos, result, rpos, gap);
			// move pointer along
			rpos = rpos + gap;
			// Copy delta replacement itself
			System.arraycopy(ithbytes, 0, result, rpos, ithbytes.length);			
			// move pointers along			
			opos = opos + gap + ith.length;
			rpos = rpos + ithbytes.length;
		}
		// Finally, copy over any remaining bytes from original sequence.
		System.arraycopy(bytes, opos, result, rpos, bytes.length - opos);
		//
		return result;
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
	public static class Delta implements Comparable<Delta> {
		/**
		 * Offset in the original sequence where region being replaced begins.
		 */
		private final int offset;
		/**
		 * Length of region in the original sequence being replaced.
		 */
		private final int length;
		/**
		 * Sequence which replaced the region from the original sequence.
		 */
		private final byte[] bytes;

		public Delta(int offset, int length, byte[] bytes) {
			if(offset < 0) {
				throw new IllegalArgumentException("negative offset");
			} else if(length < 0) {
				throw new IllegalArgumentException("negative length");
			} else if(bytes == null) {
				throw new IllegalArgumentException("null bytes");
			}
			this.offset = offset;
			this.length = length;
			this.bytes = bytes;
		}
		
		/**
		 * Calculate the overall change in length of the original sequence resulting
		 * from this delta.  Thus, 
		 * 
		 * @return
		 */
		public int diff() {
			return bytes.length - length;
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
		 * Get the bytes which replace the region in the original array.
		 * 
		 * @return
		 */
		public byte[] bytes() {
			return bytes;
		}
		
		/**
		 * Check this delta is disjoint with another (i.e. they don't overlap).
		 * 
		 * @param other
		 * @return
		 */
		public boolean disjoint(Delta other) {
			int end = (offset + length) - 1;
			int oend = (other.offset + other.length) - 1;
			return (end < other.offset) || (oend < offset);
		}

		@Override
		public int compareTo(Delta o) {
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
		
		public String toString() {
			return "(" + offset + ";" + length + ";" + Arrays.toString(bytes) + ")";
		}
	}
	
	/**
	 * Construct a diff between two strings using the <i>longest common
	 * subsequence</i> algorithm.
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static Diff construct(String before, String after) {
		System.out.println("CONSTRUCTING: " + before + " ---> " + after);
		return construct(before.getBytes(), after.getBytes());
	}
	
	/**
	 * Construct a diff between two arrays of bytes using the <i>longest common
	 * subsequence</i> algorithm.
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static Diff construct(byte[] before, byte[] after) {
		// Apply the LCS algorithm
		int[] mapping = longestCommonSubsequence(before, after);
		// FIXME: be good to improve readability of what follows!
		System.out.println("MAPPING: " + Arrays.toString(mapping));
		// Convert mapping to deltas
		ArrayList<Delta> deltas = new ArrayList<>();
		// Initialise after markers
		int aStart = 0, aPos = 0;
		// Initialise before markers
		int bStart = 0, bPos = 0;
		// Proceed extracting delta's
		while (bStart < mapping.length || aStart < after.length) {
			System.out.println("[" + bStart + ".." + bPos + "] => [" + aStart + ".." + aPos + "]");
			if (bPos >= mapping.length) {
				byte[] additions = Arrays.copyOfRange(after, aStart, after.length);
				System.out.println("ADDING DELTA(1): " + new Delta(bStart, bPos - bStart, additions));
				deltas.add(new Delta(bStart, bPos - bStart, additions));
				break;
			} else if (mapping[bPos] > aPos) {
				byte[] additions = Arrays.copyOfRange(after, aStart, mapping[bPos]);
				System.out.println("ADDING DELTA(2): " + new Delta(bStart, bPos - bStart, additions));
				deltas.add(new Delta(bStart, bPos - bStart, additions));
				aStart = aPos = mapping[bPos] + 1;
				bStart = bPos + 1;
			} else if (mapping[bPos] == aPos) {
				aStart = aPos = aPos + 1;
				bStart = bPos + 1;
			}  
			bPos = bPos + 1;
		}		
		// Contruct final diff.
		return new Diff(deltas.toArray(new Delta[deltas.size()]));
	}

	/**
	 * Check whether a given array of delta's are overlapping or not.
	 * 
	 * @param deltas
	 * @return
	 */
	private static boolean disjoint(Delta[] deltas) {
		for (int i = 1; i < deltas.length; ++i) {
			Delta ithm1 = deltas[i-1];
			Delta ith = deltas[i];
			if (!ithm1.disjoint(ith)) {
				return false;
			}
			ithm1 = ith;
		}
		return true;
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
	private static int[] longestCommonSubsequence(byte[] X, byte[] Y) {
		final int m = X.length + 1;
		final int n = Y.length + 1;
		final int[] C = new int[m * n];
		// Calculate the lengths
		for (int i = 1; i < m; ++i) {
			for (int j = 1; j < n; ++j) {
				int ij = i + (j * m);
				if (X[i-1] == Y[j-1]) {
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

	private static void extractSubsequence(int[] C, int[] Z, int i, int j) {
		final int m = Z.length + 1;
		if(i > 0 && j > 0) {
			int Cij = C[i + (j * m)];
			final int Cim1j = C[(i - 1) + (j * m)];
			final int Cijm1 = C[i + ((j - 1) * m)];
			if(Cij == Cim1j) {
				Z[i-1] = -1;
				extractSubsequence(C, Z, i - 1, j);
			} else if(Cij == Cijm1) {
				Z[i-1] = -1;
				extractSubsequence(C, Z, i, j - 1);
			} else {
				extractSubsequence(C, Z, i - 1, j - 1);
				Z[i-1] = j-1;
			}
		} 
	}

	/**
	 * Useful for debugging.
	 * 
	 * @param matrix
	 * @param m
	 * @param n
	 */
	private static void printMatrix(int[] matrix, int m, int n) {
		System.out.print("   ");
		for(int i=0;i<m;++i) {
			System.out.print(String.format("%02d",i) + " ");
		}
		System.out.println();
		System.out.print("  +");
		for(int k=0;k<m;++k) {
			System.out.print("--+");
		}
		System.out.println();
		for(int j=0;j<n;++j) {
			System.out.print(String.format("%02d|",j));
			for(int i=0;i<m;++i) {
				int ij = i + (j * m);
				System.out.print(String.format("%02d|",matrix[ij]));
			}
			System.out.println();
			System.out.print("  +");
			for(int k=0;k<m;++k) {
				System.out.print("--+");
			}
			System.out.println();
		}
	}
	
	private static void printDiff(byte[] X, byte[] Y) {
		int[] Z = longestCommonSubsequence(X,Y);
		int j = 0;
		for (int i = 0; i != X.length; ++i) {
			int k = Z[i];
			while(j < k) {
				System.out.print(" +" + Y[j]);
				j++;
			}
			if (k >= 0) {
				System.out.print(" " + Y[k]);
				j = k + 1;
			} else {
				System.out.print(" -" + X[i]);
			}
		}
		System.out.println();
	}

	public static void main(String[] args) {
		String before = "hello";
		String after = "lo";
		Diff d = construct(before, after);
		System.out.println("DIFF: " + d);
		System.out.println(before + " ==> " + after + " ... " + d.apply(before));
	}	
}