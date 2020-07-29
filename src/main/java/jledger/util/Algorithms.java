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

public class Algorithms {

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
	public static int[] longestCommonSubsequence(int[] X, int[] Y) {
		final int m = X.length;
		final int n = Y.length;
		final int[] C = new int[m * n];
		// Calculate the lengths
		for (int i = 1; i != m; ++i) {
			for (int j = 1; j != n; ++j) {
				int ij = i + (j * m);
				if (X[i] == Y[j]) {
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
		extractSubsequence(C, Z, m - 1, n - 1);
		return Z;
	}

	public static void extractSubsequence(int[] C, int[] Z, int i, int j) {
		final int m = Z.length;
		if(i > 0 && j > 0) {
			int Cij = C[i + (j * m)];
			final int Cim1j = C[(i - 1) + (j * m)];
			final int Cijm1 = C[i + ((j - 1) * m)];
			if(Cij == Cim1j) {
				Z[i] = -1;
				extractSubsequence(C, Z, i - 1, j);
			} else if(Cij == Cijm1) {
				Z[i] = -1;
				extractSubsequence(C, Z, i, j - 1);
			} else {
				extractSubsequence(C, Z, i - 1, j - 1);
				Z[i] = j;
			}
		}
	}

	static void printDiff(int[] X, int[] Y) {
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
		int[] X = {1,2,8,2,4,3};
		int[] Y = {1,2,4,5,3,2,3};
		printDiff(X,Y);
	}
}
