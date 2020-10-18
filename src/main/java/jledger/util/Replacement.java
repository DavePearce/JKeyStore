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
 * <i>final</i> array (reading left-to-right). Thus, the above is encoded as the
 * sequence <code>(2;4;"llo"),(7;2;"OR")</code>.
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

	/**
	 * Get the first index beyond this replacement in the final array.
	 *
	 * @return
	 */
	public int end() {
		return offset + bytes.length;
	}

	/**
	 * Calculate the overall change in length (i.e. delta) of the original array
	 * resulting from this delta. If the replaced sequence is larger than the
	 * original, this delta is positive; Whilst, if it is smaller, then it is
	 * negative, etc.
	 *
	 * @return
	 */
	public int delta() {
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
	 * <p>
	 * Write a new replacement on top of an existing replacement sequence. This may
	 * result in bytes from the original sequence being overwritten. It may also
	 * result in one or more replacements from the original sequence being replaced.
	 * As an example, consider the following scenario:
	 * </p>
	 *
	 * <pre>
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |X|X| |X|X|X| |X|X| |X|X|X|
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   | | |Y|Y|Y|Y|Y|Y| | | | | |
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    0 1 2 3 4 5 6 7 8 9 A B C
	 * </pre>
	 *
	 * <p>
	 * Here, we have four replacements in the original sequence (indicated by
	 * <code>X</code>) of which three are affected by the new write(indicated by
	 * <code>Y</code>). The results in the following updated replacement sequence:
	 * </p>
	 *
	 * <pre>
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   |X|X|Y|Y|Y|Y|Y|Y|X| |X|X|X|
	 *   +-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *    0 1 2 3 4 5 6 7 8 9 A B C
	 * </pre>
	 *
	 * <p>
	 * This is now a sequence of only <i>two</i> replacements as the new write
	 * resulted in the first three of the original sequence being merged together
	 * into a single replacement. Furthermore, we see that some data from the
	 * original sequence is lost completely as it was overwritten (i.e. bytes
	 * 3,4,5,7 from the original sequence).
	 * </p>
	 *
	 * @param replacements The Replacement sequence.
	 * @param offset       Starting offset of the replacement.
	 * @param length       Length of the replacement.
	 * @param bytes        Bytes used for replacement.
	 * @return
	 */
	public static Replacement[] write(Replacement[] replacements, int offset, int length, byte... bytes) {
		final int last = offset + length;
		// Determine lowest affected replacement
		int i = findLowestAffected(replacements,offset);
		// Scan forward to greatest effected replacement
		int j = i;
		while(j < replacements.length && replacements[j].offset() <= last) {
			j = j + 1;
		}
		j = j - 1;
		//
		if (j < i) {
			// Insert
			return ArrayUtils.insert(i, new Replacement(offset, length, bytes), replacements);
		} else if (i == j) {
			// single merge
			return write(i, replacements, new Replacement(offset, length, bytes));
		} else {
			// multi merge
			return write(i, j, replacements, new Replacement(offset, length, bytes));
		}
	}

	/**
	 * <p>
	 * Find the lowest affected replacement in a replacement sequence. This then
	 * identifies the "affect region" of replacements. An existing replacement is
	 * affected if either: it overlaps with the new replacement; or it is adjacent
	 * to it. Since replacements are stored in sorted order, we can employ a binary
	 * search here for efficiency.
	 * </p>
	 *
	 * @param replacements
	 * @param offset
	 * @param length
	 * @param bytes
	 * @return
	 */
	private static int findLowestAffected(Replacement[] replacements, int offset) {
		int i = 0;
		int j = replacements.length - 1;
		//
		while (i <= j) {
			// Determine center position
			final int pivot = (i + j) >>> 1;
			// Extract corresponding replacement
			Replacement p = replacements[pivot];
			// Do the binary chop
			if (offset < p.offset) {
				// Replacement above
				j = pivot - 1;
			} else if (p.end() < offset) {
				// Replacement below
				i = pivot + 1;
			} else {
				// Match!
				return pivot;
			}
		}
		// done
		return i;
	}

	/**
	 * Find the replacement (if any) in a replacement sequence containing the given
	 * offset. If no such replacement exists, a negative value is returned whose
	 * absolute value indicates the point within the replacement sequence where such
	 * a replacement would be found. Otherwise, the index of the enclosing
	 * replacement is returned. This is implemented using a binary search for
	 * efficiency, which is made possible because replacements in a sequence are
	 * always stored in sorted order.
	 *
	 * @param replacements
	 * @param offset
	 * @return
	 */
	public static int findEnclosing(Replacement[] replacements, int offset) {
		int i = 0;
		int j = replacements.length - 1;
		//
		while (i <= j) {
			// Determine center position
			final int pivot = (i + j) >>> 1;
			// Extract corresponding replacement
			Replacement p = replacements[pivot];
			// Do the binary chop
			if (offset < p.offset) {
				// Replacement above
				j = pivot - 1;
			} else if (p.end() <= offset) {
				// Replacement below
				i = pivot + 1;
			} else {
				// Match!
				return pivot;
			}
		}
		// failure
		return -(i+1);
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
		final Replacement ith = replacements[index];
		// Do some precalculations
		final int r_end = r.offset + r.bytes.length;
		final int ith_end = ith.offset + ith.bytes.length;
		// Construct new replacement sequence (as copy of original)
		final Replacement[] rs = Arrays.copyOf(replacements, replacements.length);
		// Determine start of replaced region (in final layout).
		final int offset = Math.min(ith.offset, r.offset);
		// Determine end of replaced region (in final layout).
		final int end = Math.max(ith_end, r_end);
		// Determine length of affected region (in final layout).
		final int nlength = end - offset;
		// Determine length of affected region (in original layout). This includes the
		// entire length of the original replacement, plus any additional bytes from the
		// new replacement which occur either side of the original.
		int length = nlength - ith.delta();
		byte[] bs = new byte[nlength];
		// Determine lower and upper overhangs
		final int lower = r.offset - offset;
		final int upper = end - r_end;
		// Copy any bytes from original array which are below overwritten region.\
		System.arraycopy(ith.bytes, 0, bs, 0, lower);
		// Copy any bytes from original array which are above overwritten region.
		System.arraycopy(ith.bytes, ith.bytes.length - upper, bs, bs.length - upper, upper);
		// Copy bytes from the write itself.
		System.arraycopy(r.bytes, 0, bs, lower, r.bytes.length);
		// Put in the new replacement.
		rs[index] = new Replacement(offset, length, bs);
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
	 * )</li>
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
	 * @param j            Final index of affected region of replacements.
	 * @param replacements Array of replacements
	 * @param r            Replacement overwriting replacement and index in
	 *                     replacements array
	 * @return
	 */
	private static Replacement[] write(int i, int j, Replacement[] replacements, Replacement r) {
		Replacement ith = replacements[i];
		Replacement jth = replacements[j];
		final int r_end = r.offset + r.bytes.length;
		final int jth_end = jth.offset + jth.bytes.length;
		// Determine starting offset
		int offset = Math.min(ith.offset, r.offset);
		// Determine last
		int end = Math.max(jth_end, r_end);
		// Determine length of affected region in original array
		int len = (end - offset) - delta(i, j, replacements);
		// Construct new replacement
		byte[] bs = new byte[end - offset];
		// Determine lower and upper overhangs
		final int lower = r.offset - offset;
		final int upper = end - r_end;
		// Write first affected replacement
		System.arraycopy(ith.bytes, 0, bs, 0, lower);
		// Write second affected replacement
		System.arraycopy(jth.bytes, jth.bytes.length - upper, bs, bs.length - upper, upper);
		// Write replacement data
		System.arraycopy(r.bytes, 0, bs, r.offset - offset, r.bytes.length);
		// Check invariant holds to prevent original array being returned.
		assert (i + 1) >= j;
		// Replace all elements between i and j with single replacement
		Replacement[] rs = ArrayUtils.remove(i + 1, j, replacements);
		// Put merged replacement in place
		rs[i] = new Replacement(offset, len, bs);
		// Done
		return rs;
	}

	/**
	 * Compute the "delta" between the original array size and the final array size,
	 * as caused by a given range of replacements.
	 *
	 * @param i
	 * @param j
	 * @param replacements
	 * @return
	 */
	public static int delta(int i, int j, Replacement[] replacements) {
		final int n = replacements.length;
		final int m = j < n ? j : n - 1;
		int d = 0;
		for (; i <= m; ++i) {
			d += replacements[i].delta();
		}
		return d;
	}
}
