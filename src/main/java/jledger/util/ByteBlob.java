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
import java.util.List;

import jledger.core.Content;
import jledger.core.Content.Blob;

/**
 * A straightforward implementation of <code>Content.Blob</code> which is backed
 * by an array,
 *
 * @author David J. Pearce
 *
 */
public final class ByteBlob implements Content.Blob {
	/**
	 * An empty blob which is useful in all situations where there is no initial
	 * data.
	 */
	public static final ByteBlob EMPTY = new ByteBlob();

	protected final byte[] bytes;

	public ByteBlob(byte... data) {
		this.bytes = data;
	}

	@Override
	public int size() {
		return bytes.length;
	}

	@Override
	public byte[] readAll() {
		return bytes;
	}

	@Override
	public byte readByte(int index) {
		return bytes[index];
	}

	@Override
	public short readShort(int index) {
		byte b1 = bytes[index];
		byte b2 = bytes[index + 1];
		// Recombine bytes
		return (short) ((b1 << 8) | b2);
	}

	@Override
	public int readInt(int index) {
		byte b1 = bytes[index];
		byte b2 = bytes[index + 1];
		byte b3 = bytes[index + 2];
		byte b4 = bytes[index + 3];
		// Recombine bytes
		return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
	}

	@Override
	public byte[] readBytes(int index, int length) {
		byte[] bs = new byte[length];
		System.arraycopy(bytes, index, bs, 0, length);
		return bs;
	}

	@Override
	public void readBytes(int index, int length, byte[] dest, int destStart) {
		System.arraycopy(bytes, index, dest, destStart, length);
	}


	@Override
	public Diff writeByte(int index, byte b) {
		return new Diff(this, new Replacement(index, 1, b));
	}

	@Override
	public Diff writeShort(int offset, short value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 8) & 0xFF);
		byte b2 = (byte) (value & 0xFF);
		return new Diff(this, new Replacement(offset, 2, b1, b2));
	}

	@Override
	public Diff writeInt(int offset, int value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 24) & 0xFF);
		byte b2 = (byte) ((value >> 16) & 0xFF);
		byte b3 = (byte) ((value >> 8) & 0xFF);
		byte b4 = (byte) (value & 0xFF);
		return new Diff(this, new Replacement(offset, 4, b1, b2, b3, b4));
	}

	@Override
	public Diff writeBytes(int offset, byte... bytes) {
		return new Diff(this, new Replacement(offset, bytes.length, bytes));
	}

	@Override
	public Diff replaceBytes(int offset, int length, byte... bytes) {
		return new Diff(this, new Replacement(offset, length, bytes));
	}

	@Override
	public Diff insertByte(int offset, byte b) {
		return new Diff(this, new Replacement(offset, 0, b));
	}

	@Override
	public Diff insertShort(int offset, short value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 8) & 0xFF);
		byte b2 = (byte) (value & 0xFF);
		return new Diff(this, new Replacement(offset, 0, b1, b2));
	}

	@Override
	public Diff insertInt(int offset, int value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 24) & 0xFF);
		byte b2 = (byte) ((value >> 16) & 0xFF);
		byte b3 = (byte) ((value >> 8) & 0xFF);
		byte b4 = (byte) (value & 0xFF);
		return new Diff(this, new Replacement(offset, 0, b1, b2, b3, b4));
	}

	@Override
	public Diff insertBytes(int offset, byte... bytes) {
		return new Diff(this, new Replacement(offset, 0, bytes));
	}

	@Override
	public Content.Blob merge(Content.Blob blob) {
		if(blob == this) {
			return this;
		} else if(blob instanceof Content.Diff) {
			Content.Diff d = (Content.Diff) blob;
			if(d.parent() == this) {
				return blob;
			}
		}
		throw new IllegalArgumentException("cannot merge blob");
	}

	@Override
	public String toString() {
		return Arrays.toString(bytes);
	}

	/**
	 * Construct a diff between two arrays of bytes using the <i>longest common
	 * subsequence</i> algorithm.
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static Diff diff(byte[] before, byte[] after) {
		// Apply the LCS algorithm
		int[] mapping = longestCommonSubsequence(before, after);
		// Convert mapping to deltas
		List<Replacement> deltas = extractDeltas(mapping, after);
		// Contruct final diff.
		return new Diff(new ByteBlob(before), deltas.toArray(new Replacement[deltas.size()]));
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
	 * sensible. Potentially, some form of post processing could coalesce delta's as
	 * necessary.
	 * </p>
	 *
	 * @param mapping
	 * @param after
	 * @return
	 */
	private static List<Replacement> extractDeltas(int[] mapping, byte[] after) {
		ArrayList<Replacement> deltas = new ArrayList<>();
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
					byte[] additions = Arrays.copyOfRange(after, aStart, aPos);
					deltas.add(new Replacement(bStart, bPos - bStart, additions));
				}
				aStart = aPos = aPos + 1;
				bStart = bPos = bPos + 1;
			}
		}
		// Flush remaining buffers
		if (bStart < mapping.length || aStart < after.length) {
			// Terminating case. Flush buffers and end.
			byte[] additions = Arrays.copyOfRange(after, aStart, after.length);
			deltas.add(new Replacement(bStart, mapping.length - bStart, additions));
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
	private static int[] longestCommonSubsequence(byte[] X, byte[] Y) {
		final int m = X.length + 1;
		final int n = Y.length + 1;
		final int[] C = new int[m * n];
		// Calculate the lengths
		for (int i = 1; i < m; ++i) {
			for (int j = 1; j < n; ++j) {
				int ij = i + (j * m);
				if (X[i - 1] == Y[j - 1]) {
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

	public static void main(String[] args) {
		ByteBlob b1 = new ByteBlob("hello".getBytes());
		Content.Blob b2 = b1.writeBytes(1, (byte) 'E', (byte) '?');
		Content.Blob b3 = b2.writeBytes(0, (byte) 'H',(byte) '_',(byte) '_',(byte) '_');
		//
		System.out.println(b1 + " => " + b2 + " => " + b3);

		//
		for (int i = 0; i != b3.size(); ++i) {
			System.out.print(Character.toString((char) b3.readByte(i)));
		}
	}
}

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
final class Diff implements Content.Diff {
	private final Content.Blob parent;
	private final Replacement[] replacements;

	/**
	 * Construct a diff from a given sequence of one or more non-overlapping deltas
	 * in sorted order.
	 *
	 * @param deltas
	 */
	public Diff(Content.Blob parent, Replacement... deltas) {
		if (deltas == null) {
			throw new IllegalArgumentException("null deltas");
		} else if (!ArrayUtils.isSorted(deltas)) {
			throw new IllegalArgumentException("unsorted deltas");
		} else if (!areDisjoint(deltas)) {
			throw new IllegalArgumentException("overlapping deltas");
		}
		this.parent = parent;
		this.replacements = deltas;
	}

	@Override
	public int size() {
		// Determine parent's initial size
		int size = parent.size();
		//
		int diff = 0;
		// Examine replacements
		for (int i = 0; i != replacements.length; ++i) {
			Replacement ith = replacements[i];
			// Account for delta changes
			diff += ith.diff();
			// Account for elastiticity of parent
			size = Math.max(size, ith.offset + ith.length);
		}
		//
		return size + diff;
	}

	@Override
	public Blob parent() {
		return parent;
	}

	@Override
	public byte readByte(int index) {
		// TODO: could be more efficient using a binary search.
		for (int i = 0; i != replacements.length; ++i) {
			Replacement ith = replacements[i];
			if (ith.offset > index) {
				return parent.readByte(index);
			} else if (index < (ith.offset + ith.bytes.length)) {
				return ith.bytes[index - ith.offset];
			} else {
				index = index - ith.diff();
			}
		}
		return parent.readByte(index);
	}

	@Override
	public short readShort(int index) {
		// FIXME: performance could be improved!!
		byte b1 = readByte(index);
		byte b2 = readByte(index + 1);
		// Recombine bytes
		return (short) ((b1 << 8) | b2);
	}

	@Override
	public int readInt(int index) {
		// FIXME: performance could be improved!!
		byte b1 = readByte(index);
		byte b2 = readByte(index + 1);
		byte b3 = readByte(index + 2);
		byte b4 = readByte(index + 3);
		// Recombine bytes
		return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
	}

	@Override
	public byte[] readBytes(int index, int length) {
		// FIXME: performance could be improved!!
		byte[] bs = new byte[length];
		for (int i = 0; i < length; ++i) {
			bs[i] = readByte(index + i);
		}
		return bs;
	}

	@Override
	public void readBytes(int index, int length, byte[] dest, int destStart) {
		// FIXME: performance could be improved!!
		for (int i = 0; i < length; ++i) {
			dest[destStart + i] = readByte(index + i);
		}
	}

	@Override
	public int count() {
		return replacements.length;
	}

	@Override
	public Replacement getReplacement(int i) {
		return replacements[i];
	}

	@Override
	public byte[] readAll() {
		// NOTE: a more efficient way of doing this might be to create an array of the
		// right size and then write parent data into it?
		//
		// Extract the parent bytes
		final byte[] bytes = parent.readAll();
		// Determine size of final array
		final int size = size();
		// Construct final array
		byte[] result = new byte[size];
		int opos = 0;
		int rpos = 0;
		// Apply delta's one-by-one
		for (int i = 0; i < replacements.length; ++i) {
			final Replacement ith = replacements[i];
			final byte[] ithbytes = ith.bytes;
			// Calculate gap
			int gap = ith.offset - opos;
			// Determine remainder
			int remainder = bytes.length - opos;
			// Copy section up to delta start (if any)
			System.arraycopy(bytes, opos, result, rpos, Math.min(remainder, gap));
			// move pointer along
			rpos = rpos + gap;
			// Copy delta replacement itself
			System.arraycopy(ithbytes, 0, result, rpos, ithbytes.length);
			// move pointers along
			opos = opos + gap + ith.length;
			rpos = rpos + ithbytes.length;
		}
		// Finally, copy over any remaining bytes from original sequence.
		int remainder = bytes.length - opos;
		// Copy remaining bytes (if any)
		if (remainder > 0) {
			System.arraycopy(bytes, opos, result, rpos, remainder);
		}
		//
		return result;
	}


	@Override
	public Diff writeByte(int index, byte b) {
		return replace(index, 1, b);
	}

	@Override
	public Diff writeShort(int offset, short value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 8) & 0xFF);
		byte b2 = (byte) (value & 0xFF);
		return replace(offset, 2, b1, b2);
	}

	@Override
	public Diff writeInt(int offset, int value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 24) & 0xFF);
		byte b2 = (byte) ((value >> 16) & 0xFF);
		byte b3 = (byte) ((value >> 8) & 0xFF);
		byte b4 = (byte) (value & 0xFF);
		return replace(offset, 4, b1, b2, b3, b4);
	}

	@Override
	public Diff writeBytes(int offset, byte... bytes) {
		return replace(offset, bytes.length, bytes);
	}

	@Override
	public Diff replaceBytes(int offset, int length, byte... bytes) {
		return replace(offset, length, bytes);
	}

	@Override
	public Diff insertByte(int offset, byte b) {
		return replace(offset, 0, b);
	}

	@Override
	public Diff insertShort(int offset, short value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 8) & 0xFF);
		byte b2 = (byte) (value & 0xFF);
		return replace(offset, 0, b1, b2);
	}

	@Override
	public Diff insertInt(int offset, int value) {
		// Convert value into bytes
		byte b1 = (byte) ((value >> 24) & 0xFF);
		byte b2 = (byte) ((value >> 16) & 0xFF);
		byte b3 = (byte) ((value >> 8) & 0xFF);
		byte b4 = (byte) (value & 0xFF);
		return replace(offset, 0, b1, b2, b3, b4);
	}

	@Override
	public Diff insertBytes(int offset, byte... bytes) {
		return replace(offset, 0, bytes);
	}

	@Override
	public Content.Blob merge(Content.Blob blob) {
		if (blob == this || parent == blob) {
			return this;
		} else if (blob instanceof Diff) {
			Diff d = (Diff) blob;
			if (d.parent() == parent) {
				Replacement[] rs = merge(replacements, d.replacements);
				return new Diff(parent, rs);
			}
		}
		throw new IllegalArgumentException("cannot merge blob");
	}

	@Override
	public String toString() {
		String r = "";
		if (replacements.length > 0) {
			r += replacements[0].toString();
			for (int i = 1; i != replacements.length; ++i) {
				r += "," + replacements[i].toString();
			}
		}
		return "{" + r + "}";
	}

	/**
	 * Apply a new write onto this diff. The key challenge is that the offset of the
	 * write is in terms of the resulting layout from this diff. This contrasts with
	 * the replacements making up this diff, which are in terms of the layout of the
	 * original blob. Therefore, we must account for this.
	 *
	 * @param offset
	 * @param length
	 * @param bytes
	 * @return
	 */
	private Diff replace(int offset, int length, byte... bytes) {
		// TODO: performance could be improved using a binary search. However, this
		// requires altering the way in which replacements are represented to be in
		// terms of the resulting layout rather than the original layout.
		// Determine first element past write
		int ith_offset = 0;
		int ith_length = 0;
		int ith_last = 0;
		byte[] ith_bytes = null;
		//
		for(int i=0;i!=replacements.length;++i) {
			Replacement ith = replacements[i];
			// Determine replacement coordinates
			ith_offset = ith.offset;
			ith_length = ith.length;
			ith_last = ith_offset + ith_length;
			ith_bytes = ith.bytes;
			//
			final int last = offset + length;
			// Check for overlaps
			if(last <= ith_offset) {
				if(last == ith_offset) {
					// Merge
					Replacement[] rs = Arrays.copyOf(replacements, replacements.length);
					// Construct combined array
					byte[] bs = ArrayUtils.append(bytes,ith_bytes);
					// Combine both replacements together.
					rs[i] = new Replacement(offset, length + ith_length, bs);
					// Done
					return new Diff(parent, rs);
				} else {
					Replacement r = new Replacement(offset, length, bytes);
					// Insert
					Replacement[] rs = ArrayUtils.insert(i, r, replacements);
					// Done
					return new Diff(parent, rs);
				}
			} else if(ith_last <= offset) {
				// Continue
			} else {
				// At this point, conflict detected. First, determine how many replacements
				// affected.
				int j = i + 1;
				if(j < replacements.length && last >= replacements[j].offset) {
					// Multiple affected replacements which need to be merged into one.
					j = j + 1;
					while(j < replacements.length && last >= replacements[j].offset) {
						j = j + 1;
					}
					//
					throw new UnsupportedOperationException("IMPLEMENT ME!");
				} else {
					// Only one affected replacement, so existing one can be replaced.
					int o = Math.min(offset, ith_offset);
					int l = Math.max(last, ith_last);
					int n = l - o;
					byte[] bs = new byte[n];
					// Apply first
					System.arraycopy(ith_bytes, 0, bs, ith_offset - o, ith_bytes.length);
					// Apply second
					System.arraycopy(bytes, 0, bs, offset - o, bytes.length);
					// Construct final replacement set.
					Replacement[] rs = Arrays.copyOf(replacements, replacements.length);
					rs[i] = new Replacement(o, n, bs);
					// Done
					return new Diff(parent, rs);
				}
			}
			// Account for differences between before/after layouts
			offset = offset - ith.diff();
		}
		// Append to end
		if(ith_last == offset) {
			// Merge
			Replacement[] rs = Arrays.copyOf(replacements, replacements.length);
			// Construct combined array
			byte[] bs = ArrayUtils.append(ith_bytes,bytes);
			// Combine both replacements together.
			rs[replacements.length-1] = new Replacement(ith_offset, length + ith_length, bs);
			// Done
			return new Diff(parent, rs);
		} else {
			Replacement r = new Replacement(offset, length, bytes);
			// Doesn't conflict so append on the end
			Replacement[] rs = ArrayUtils.append(replacements, r);
			// Done
			return new Diff(parent, rs);
		}
	}

	/**
	 * Check whether a given array of delta's are overlapping or not.
	 *
	 * @param deltas
	 * @return
	 */
	private static boolean areDisjoint(Replacement[] deltas) {
		for (int i = 1; i < deltas.length; ++i) {
			Replacement ithm1 = deltas[i - 1];
			Replacement ith = deltas[i];
			if (!ithm1.disjoint(ith)) {
				return false;
			}
			ithm1 = ith;
		}
		return true;
	}

	/**
	 * Combine two sets of (disjoint) replacements together to form one. All
	 * replacements are assumed to be disjoint, otherwise we have an error.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private static Replacement[] merge(Replacement[] lhs, Replacement[] rhs) {
		// Optimistically assume no merging of replacements will occurr between
		// replacements. If there is some, then we'll need to trim this array at the
		// end.
		Replacement[] results = new Replacement[lhs.length + rhs.length];
		//
		int l = 0, r = 0, k = 0;
		//
		while(l < lhs.length && r < rhs.length) {
			Replacement lth = lhs[l];
			Replacement rth = rhs[r];
			//
			int lth_offset = lth.offset;
			int rth_offset = rth.offset;
			int lth_last = lth_offset + lth.length;
			int rth_last = rth_offset + rth.length;
			//
			if(lth_last <= rth_offset) {
				if(lth_last == rth_offset) {
					// Can merge!
					byte[] bytes = ArrayUtils.append(lth.bytes, rth.bytes);
					results[k++] = new Replacement(lth_offset,lth.length + rth.length,bytes);
					l = l + 1;
					r = r + 1;
				} else {
					// lth first
					results[k++] = lth;
					l = l + 1;
				}
			} else if(rth_last <= lth_offset) {
				if(rth_last == lth_offset) {
					// Can merge!
					byte[] bytes = ArrayUtils.append(rth.bytes, lth.bytes);
					results[k++] = new Replacement(rth_offset,lth.length + rth.length,bytes);
					l = l + 1;
					r = r + 1;
				} else {
					// rth first
					results[k++] = rth;
					r = r + 1;
				}
			} else {
				// Merge!
				throw new IllegalArgumentException("Cannot merge conflicting replacements");
			}
		}
		// Copy any remaining segments
		if(l < lhs.length) {
			System.arraycopy(lhs, l, results, k, lhs.length - l);
		} else if(r < rhs.length) {
			System.arraycopy(rhs, r, results, k, rhs.length - r);
		}
		// Finally, sanity check for merged elements
		if(k < results.length) {
			results = Arrays.copyOf(results, k);
		}
		//
		return results;
	}
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
class Replacement implements Content.Replacement, Comparable<Replacement> {
	/**
	 * Offset in the original sequence where region being replaced begins.
	 */
	protected final int offset;
	/**
	 * Length of region in the original sequence being replaced.
	 */
	protected final int length;

	/**
	 * Sequence which replaced the region from the original sequence.
	 */
	protected final byte[] bytes;

	public Replacement(int offset, int length, byte... bytes) {
		if (offset < 0) {
			throw new IllegalArgumentException("negative offset");
		} else if (length < 0) {
			throw new IllegalArgumentException("negative length");
		} else if (bytes == null) {
			throw new IllegalArgumentException("null bytes");
		}
		this.offset = offset;
		this.length = length;
		this.bytes = bytes;
	}

	@Override
	public int offset() {
		return offset;
	}

	@Override
	public int size() {
		return length;
	}

	/**
	 * Calculate the overall change in length of the original sequence resulting
	 * from this delta. Thus,
	 *
	 * @return
	 */
	public int diff() {
		return bytes.length - length;
	}

	/**
	 * Get the bytes which replace the region in the original array.
	 *
	 * @return
	 */
	@Override
	public byte[] bytes() {
		return bytes;
	}

	/**
	 * Check this delta is disjoint with another (i.e. they don't overlap).
	 *
	 * @param other
	 * @return
	 */
	public boolean disjoint(Replacement other) {
		int end = (offset + length) - 1;
		int oend = (other.offset + other.length) - 1;
		return (end < other.offset) || (oend < offset);
	}

	@Override
	public int compareTo(Replacement o) {
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

	@Override
	public String toString() {
		return "(" + offset + ";" + length + ";" + Arrays.toString(bytes) + ")";
	}
}
