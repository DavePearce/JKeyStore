package jledger.util;

import java.util.Arrays;

import jledger.core.Content;

/**
 * <p>
 * Represents an atomic replacement in some source array. Specifically, a region
 * in the source array is replaced by a new sequence of bytes. Observe that this
 * region may be larger or smaller than the original region. The following
 * illustrates:
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
 * World". This replacement is denoted as the triple <code>(2;4;"llo")</code>
 * which indicates the replacement begins at position <code>2</code>, replaces
 * <code>4</code> bytes from the original array with a given sequence of zero or
 * more bytes.
 * </p>
 *
 * <p>
 * A <i>replacement sequence</i> is used to implement <i>diffs</i> and have a
 * specific interpretation. Consider the following:
 * </p>
 * 
 * <pre>
 *  0 1 2 3 4 5 6 7 8 9 A B
 * +-+-+-+-+-+-+-+-+-+-+-+-+
 * |H|e|L|L|L|O| |W|o|r|l|d|
 * +-+-+-+-+-+-+-+-+-+-+-+-+
 *     | : : : |    | |
 *     +-------+    +-+
 *         |        |
 *        \|/      \|/
 *     +-+-+-+-+-+-+-+
 *  ...|l|l|o|...|O|R|...
 *     +-+-+-+-+-+-+-+
 *  0 1 2 3 4 5 6 7 8 9 A B
 * </pre>
 * 
 * <p>
 * Here, we have a sequence of two replacements. We assume replacements in a
 * sequence do not overlap (including not adjacent) and are sorted. We also
 * assume that the starting offset for each replacement is in terms of the
 * <i>final</i> array. Thus, the above is encoded as the sequence
 * <code>(2;4;"llo"),(7;2;"OR")</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public final class Replacement implements Content.Replacement, Comparable<Replacement> {
	/**
	 * Offset in the original sequence where region being replaced begins.
	 */
	final int offset;
	/**
	 * Length of region in the original sequence being replaced.
	 */
	final int length;

	/**
	 * Sequence which replaced the region from the original sequence.
	 */
	final byte[] bytes;

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

	public int last() {
		return offset + bytes.length;
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
		int end = (offset + bytes.length) - 1;
		int oend = (other.offset + other.bytes.length) - 1;
		return (end < other.offset) || (oend < offset);
	}

	/**
	 * Check this delta is disjoint with another (i.e. they don't overlap).
	 *
	 * @param other
	 * @return
	 */
	public boolean disjoint(int other_offset, int other_length) {
		int end = (offset + length) - 1;
		int oend = (other_offset + other_length) - 1;
		return (end < other_offset) || (oend < offset);
	}

	@Override
	public int compareTo(Replacement o) {
		if (offset < o.offset) {
			return -1;
		} else if (offset > o.offset) {
			return 1;
		}
		int end = (offset + bytes.length) - 1;
		int oend = (o.offset + o.bytes.length) - 1;
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
	
	/**
	 * Apply a new write onto this diff.
	 * 
	 * The key challenge is that the offset of the write is in terms of the
	 * resulting layout from this diff. This contrasts with the replacements making
	 * up this diff, which are in terms of the layout of the original blob.
	 * Therefore, we must account for this.
	 *
	 * @param offset
	 * @param length
	 * @param bytes
	 * @return
	 */
	public static Replacement[] write(Replacement[] replacements, int offset, int length, byte... bytes) {
		System.out.println(
				"REPLACE(" + offset + "," + length + "," + bytes.length + ") <= " + Arrays.toString(replacements));
		// Determine lower affected range
		int i = findLowestAffected(replacements, offset,length,bytes);
		int j = findGreatestAffected(i, replacements, offset, length, bytes);
		//
		if (i == j) {
			// Insert
			return ArrayUtils.insert(i, new Replacement(offset, length, bytes), replacements);
		} else if (i == (j - 1)) {
			// single merge
			return write(i, replacements, new Replacement(offset, length, bytes));
		} else {
			// multi merge
			return replace(i, j, replacements, new Replacement(offset, length, bytes));
		}
	}

	private static int findLowestAffected(Replacement[] replacements, int offset, int length, byte[] bytes) {
		// TODO: this could employ binary search
		int i = 0;
		while(i < replacements.length && replacements[i].last() < offset) {
			i = i + 1;
		}
		return i;
	}

	private static int findGreatestAffected(int i, Replacement[] replacements, int offset, int length, byte[] bytes) {
		int last = offset + length;
		while(i < replacements.length && replacements[i].offset() <= last) {
			i = i + 1;
		}
		return i;
	}

	/**
	 * <p>
	 * Write a given replacement at a given position within a replacement sequence.
	 * The assumption is that they either in <i>conflict</i> or are <i>adjacent</i>.
	 * We're also assuming the given replacement is overwriting the existing
	 * replacements. There are several cases to consider:
	 * </p>
	 *
	 * <ol>
	 * <li>Replacement within:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | |X|X|X|X|X| |  | |X|X|X|X|X| |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | |Y|Y|Y| | |  | | |Y|Y|Y|Y| |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * 
	 * 
	 * <li>Replacement overwrites:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | | |X|X| | |  | | |X|X|X| | |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | |Y|Y|Y|Y| |  | | |Y|Y|Y|Y| |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * 
	 * <li>Partial conflict:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | |X|X|X| | |  | | |X|X|X|X|X|
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | | |Y|Y|Y| |  | |Y|Y|Y|Y| | |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * 
	 * <li>Adjacent writes:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | |X|X| | | |  | | | | |X|X|X|
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | | | |Y|Y| |  | | |Y|Y| | | |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param index        Index of replacement being overwritten in replacements
	 *                     array.
	 * @param replacements Array of replacements
	 * @param r            Replacement overwriting replacement and index in
	 *                     replacements array
	 * @return
	 */
	private static Replacement[] write(int index, Replacement[] replacements, Replacement r) {
		Replacement ith = replacements[index];
		Replacement[] rs = Arrays.copyOf(replacements, replacements.length);
		// Combine both replacements together.
		int off = Math.min(ith.offset, r.offset);
		int last = Math.max(ith.offset + ith.bytes.length, r.offset + r.bytes.length);
		int len = ith.length + (ith.offset - off) + (last - ith.last());
		byte[] bs = new byte[last - off];
		// TODO: this copy is inefficient in some cases
		System.arraycopy(ith.bytes, 0, bs, ith.offset - off, ith.bytes.length);
		System.arraycopy(r.bytes, 0, bs, r.offset - off, r.bytes.length);
		rs[index] = new Replacement(off, len, bs);
		// Done
		return rs;
	}

	/**
	 * <p>
	 * Write a given replacement over two or more "affected" replacements within a
	 * replacement sequence. The assumption is that the given replacement is either
	 * in <i>conflict</i> with or is <i>adjacent</i> to each of the affected exist
	 * replacements. We're also assuming the given replacement is overwriting the
	 * existing replacements. The key difference from unit case is that we must
	 * consider multiple existing replacements here. There are several cases to
	 * consider:
	 * </p>
	 *
	 * <ol>
	 * <li>Replacement within:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+    +-+-+-+-+-+-+-+
	 *   | |X|X| |X|X| |    | |X| |X| |X|X|
	 *   +-+-+-+-+-+-+-+    +-+-+-+-+-+-+-+
	 *   | | |Y|Y|Y| | |    | | |Y|Y|Y|Y|Y|
	 *   +-+-+-+-+-+-+-+    +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * 
	 * 
	 * <li>Replacement overwrites:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | |X|X| |X| |  | |X| |X|X| | |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | |Y|Y|Y|Y|Y|Y|  | |Y|Y|Y|Y|Y| |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * 
	 * <li>Partial conflict:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | |X|X| |X|X| |  | | |X|X| | |X|
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | |Y|Y|Y|Y| |  | |Y|Y|Y|Y|Y| |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * 
	 * <li>Adjacent writes:
	 * 
	 * <pre>
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | |X|X|X| | |X|  |X|X| | |X|X|X|
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 *   | | | | |Y|Y| |  | |Y|Y|Y| | | |
	 *   +-+-+-+-+-+-+-+  +-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * </li>
	 * </ol>
	 * 
	 * @param i            Starting index of affected region of replacements.
	 * @param j            First index outside affected region of replacements.
	 * @param replacements Array of replacements
	 * @param r            Replacement overwriting replacement and index in
	 *                     replacements array
	 * @return
	 */
	public static Replacement[] replace(int i, int j, Replacement[] replacements, Replacement r) {
		Replacement ith = replacements[i];
		Replacement jth = replacements[j];
		// Determine starting offset
		int offset = Math.min(ith.offset, r.offset);
		// Determine last
		int last = Math.max(jth.offset + jth.bytes.length, r.offset + r.bytes.length);
		// Determine length of affect region in original array
		int len = ;
		// Construct new replacement
		byte[] bs = new byte[last - offset];
		// TODO: following two copies inefficient in many cases
		// Write first affected replacement
		System.arraycopy(ith.bytes, 0, bs, ith.offset - offset, ith.bytes.length);
		// Write second affected replacement
		System.arraycopy(jth.bytes, 0, bs, jth.offset - offset, jth.bytes.length);
		// Write replacement data
		System.arraycopy(r.bytes, 0, bs, r.offset - offset, r.bytes.length);
		// Check invariant which must hold to prevent original array being returned from remove.
		assert (i+1) != j;
		// Replace all elements between i and j with single replacement
		Replacement[] rs = ArrayUtils.remove(i+1,j,replacements);
		// Put merged replacement in place 
		rs[i] = new Replacement(offset, len, bs);
		// Done
		return rs;
	}

}
