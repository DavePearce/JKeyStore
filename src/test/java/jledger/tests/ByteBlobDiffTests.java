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
package jledger.tests;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import jledger.core.Content;
import jledger.util.ByteBlob;

/**
 * Tests for which <code>jledger.util.Diff</code> which are constructed from
 * exhaustive enumerations of replacements over given strings.
 *
 * @author David J. Pearce
 *
 */
public class ByteBlobDiffTests {

	// ===================================================================
	// Core Tests
	// ===================================================================

	@Test
	public void test_01() {
		byte[] bs1 = "Hello World".getBytes();
		ByteBlob v1 = new ByteBlob(bs1);
		assertEquals(v1.size(),11);
		assertEquals(v1.readAll(),bs1);
		for (int i = 0; i != bs1.length; ++i) {
			assertEquals(v1.readByte(i), bs1[i]);
		}
	}

	@Test
	public void test_02() {
		byte[] bs1 = "hello world".getBytes();
		Content.Blob v = new ByteBlob(bs1);
		Content.Blob[] vs = new Content.Blob[11];
		// A bunch of writes
		vs[0] = v.writeByte(0, (byte) 'H');
		vs[1] = v.writeByte(1, (byte) 'E');
		vs[2] = v.writeByte(2, (byte) 'L');
		vs[3] = v.writeByte(3, (byte) 'L');
		vs[4] = v.writeByte(4, (byte) 'O');
		vs[5] = v.writeByte(5, (byte) '_');
		vs[6] = v.writeByte(6, (byte) 'W');
		vs[7] = v.writeByte(7, (byte) 'O');
		vs[8] = v.writeByte(8, (byte) 'R');
		vs[9] = v.writeByte(9, (byte) 'L');
		vs[10] = v.writeByte(10, (byte) 'D');
		// Check sizes invariant
		assertEquals(v.size(),vs[0].size());
		assertEquals(v.size(),vs[1].size());
		assertEquals(v.size(),vs[2].size());
		assertEquals(v.size(),vs[3].size());
		assertEquals(v.size(),vs[4].size());
		assertEquals(v.size(),vs[5].size());
		assertEquals(v.size(),vs[6].size());
		assertEquals(v.size(),vs[7].size());
		assertEquals(v.size(),vs[8].size());
		assertEquals(v.size(),vs[9].size());
		assertEquals(v.size(),vs[10].size());
		// Check character change as expected
		assertArrayEquals("Hello world".getBytes(),vs[0].readAll());
		assertArrayEquals("hEllo world".getBytes(),vs[1].readAll());
		assertArrayEquals("heLlo world".getBytes(),vs[2].readAll());
		assertArrayEquals("helLo world".getBytes(),vs[3].readAll());
		assertArrayEquals("hellO world".getBytes(),vs[4].readAll());
		assertArrayEquals("hello_world".getBytes(),vs[5].readAll());
		assertArrayEquals("hello World".getBytes(),vs[6].readAll());
		assertArrayEquals("hello wOrld".getBytes(),vs[7].readAll());
		assertArrayEquals("hello woRld".getBytes(),vs[8].readAll());
		assertArrayEquals("hello worLd".getBytes(),vs[9].readAll());
		assertArrayEquals("hello worlD".getBytes(),vs[10].readAll());
	}

	@Test
	public void test_03() {
		byte[] bs1 = "hello world".getBytes();
		Content.Blob v = new ByteBlob(bs1);
		Content.Blob[] vs = new Content.Blob[11];
		// A bunch of replacements
		vs[0] = v.replaceBytes(0, 1, "H".getBytes());
		vs[1] = v.replaceBytes(1, 1, "E".getBytes());
		vs[2] = v.replaceBytes(2, 1, "L".getBytes());
		vs[3] = v.replaceBytes(3, 1, "L".getBytes());
		vs[4] = v.replaceBytes(4, 1, "O".getBytes());
		vs[5] = v.replaceBytes(5, 1, "_".getBytes());
		vs[6] = v.replaceBytes(6, 1, "W".getBytes());
		vs[7] = v.replaceBytes(7, 1, "O".getBytes());
		vs[8] = v.replaceBytes(8, 1, "R".getBytes());
		vs[9] = v.replaceBytes(9, 1, "L".getBytes());
		vs[10] = v.replaceBytes(10, 1, "D".getBytes());
		// Check sizes invariant
		assertEquals(v.size(),vs[0].size());
		assertEquals(v.size(),vs[1].size());
		assertEquals(v.size(),vs[2].size());
		assertEquals(v.size(),vs[3].size());
		assertEquals(v.size(),vs[4].size());
		assertEquals(v.size(),vs[5].size());
		assertEquals(v.size(),vs[6].size());
		assertEquals(v.size(),vs[7].size());
		assertEquals(v.size(),vs[8].size());
		assertEquals(v.size(),vs[9].size());
		assertEquals(v.size(),vs[10].size());
		// Check character change as expected
		assertArrayEquals("Hello world".getBytes(),vs[0].readAll());
		assertArrayEquals("hEllo world".getBytes(),vs[1].readAll());
		assertArrayEquals("heLlo world".getBytes(),vs[2].readAll());
		assertArrayEquals("helLo world".getBytes(),vs[3].readAll());
		assertArrayEquals("hellO world".getBytes(),vs[4].readAll());
		assertArrayEquals("hello_world".getBytes(),vs[5].readAll());
		assertArrayEquals("hello World".getBytes(),vs[6].readAll());
		assertArrayEquals("hello wOrld".getBytes(),vs[7].readAll());
		assertArrayEquals("hello woRld".getBytes(),vs[8].readAll());
		assertArrayEquals("hello worLd".getBytes(),vs[9].readAll());
		assertArrayEquals("hello worlD".getBytes(),vs[10].readAll());
	}

	@Test
	public void test_04() {
		byte[] bs1 = "hello world".getBytes();
		Content.Blob v = new ByteBlob(bs1);
		Content.Blob[] vs = new Content.Blob[11];
		// A bunch of replacements
		vs[0] = v.replaceBytes(0, 1, "Hh".getBytes());
		vs[1] = v.replaceBytes(1, 1, "Ee".getBytes());
		vs[2] = v.replaceBytes(2, 1, "Ll".getBytes());
		vs[3] = v.replaceBytes(3, 1, "Ll".getBytes());
		vs[4] = v.replaceBytes(4, 1, "Oo".getBytes());
		vs[5] = v.replaceBytes(5, 1, " _".getBytes());
		vs[6] = v.replaceBytes(6, 1, "Ww".getBytes());
		vs[7] = v.replaceBytes(7, 1, "Oo".getBytes());
		vs[8] = v.replaceBytes(8, 1, "Rr".getBytes());
		vs[9] = v.replaceBytes(9, 1, "Ll".getBytes());
		vs[10] = v.replaceBytes(10, 1, "Dd".getBytes());
		// Check sizes invariant
		assertEquals(v.size()+1,vs[0].size());
		assertEquals(v.size()+1,vs[1].size());
		assertEquals(v.size()+1,vs[2].size());
		assertEquals(v.size()+1,vs[3].size());
		assertEquals(v.size()+1,vs[4].size());
		assertEquals(v.size()+1,vs[5].size());
		assertEquals(v.size()+1,vs[6].size());
		assertEquals(v.size()+1,vs[7].size());
		assertEquals(v.size()+1,vs[8].size());
		assertEquals(v.size()+1,vs[9].size());
		assertEquals(v.size()+1,vs[10].size());
		// Check character change as expected
		assertArrayEquals("Hhello world".getBytes(),vs[0].readAll());
		assertArrayEquals("hEello world".getBytes(),vs[1].readAll());
		assertArrayEquals("heLllo world".getBytes(),vs[2].readAll());
		assertArrayEquals("helLlo world".getBytes(),vs[3].readAll());
		assertArrayEquals("hellOo world".getBytes(),vs[4].readAll());
		assertArrayEquals("hello _world".getBytes(),vs[5].readAll());
		assertArrayEquals("hello Wworld".getBytes(),vs[6].readAll());
		assertArrayEquals("hello wOorld".getBytes(),vs[7].readAll());
		assertArrayEquals("hello woRrld".getBytes(),vs[8].readAll());
		assertArrayEquals("hello worLld".getBytes(),vs[9].readAll());
		assertArrayEquals("hello worlDd".getBytes(),vs[10].readAll());
	}

	@Test
	public void test_05() {
		byte[] bs1 = "hello world".getBytes();
		Content.Blob v = new ByteBlob(bs1);
		Content.Blob[] vs = new Content.Blob[11];
		// A bunch of replacements
		vs[0] = v.replaceBytes(0, 1, "HhH".getBytes());
		vs[1] = v.replaceBytes(1, 1, "EeE".getBytes());
		vs[2] = v.replaceBytes(2, 1, "LlL".getBytes());
		vs[3] = v.replaceBytes(3, 1, "LlL".getBytes());
		vs[4] = v.replaceBytes(4, 1, "OoO".getBytes());
		vs[5] = v.replaceBytes(5, 1, " _ ".getBytes());
		vs[6] = v.replaceBytes(6, 1, "WwW".getBytes());
		vs[7] = v.replaceBytes(7, 1, "OoO".getBytes());
		vs[8] = v.replaceBytes(8, 1, "RrR".getBytes());
		vs[9] = v.replaceBytes(9, 1, "LlL".getBytes());
		vs[10] = v.replaceBytes(10, 1, "DdD".getBytes());
		// Check sizes invariant
		assertEquals(v.size()+2,vs[0].size());
		assertEquals(v.size()+2,vs[1].size());
		assertEquals(v.size()+2,vs[2].size());
		assertEquals(v.size()+2,vs[3].size());
		assertEquals(v.size()+2,vs[4].size());
		assertEquals(v.size()+2,vs[5].size());
		assertEquals(v.size()+2,vs[6].size());
		assertEquals(v.size()+2,vs[7].size());
		assertEquals(v.size()+2,vs[8].size());
		assertEquals(v.size()+2,vs[9].size());
		assertEquals(v.size()+2,vs[10].size());
		// Check character change as expected
		assertArrayEquals("HhHello world".getBytes(),vs[0].readAll());
		assertArrayEquals("hEeEllo world".getBytes(),vs[1].readAll());
		assertArrayEquals("heLlLlo world".getBytes(),vs[2].readAll());
		assertArrayEquals("helLlLo world".getBytes(),vs[3].readAll());
		assertArrayEquals("hellOoO world".getBytes(),vs[4].readAll());
		assertArrayEquals("hello _ world".getBytes(),vs[5].readAll());
		assertArrayEquals("hello WwWorld".getBytes(),vs[6].readAll());
		assertArrayEquals("hello wOoOrld".getBytes(),vs[7].readAll());
		assertArrayEquals("hello woRrRld".getBytes(),vs[8].readAll());
		assertArrayEquals("hello worLlLd".getBytes(),vs[9].readAll());
		assertArrayEquals("hello worlDdD".getBytes(),vs[10].readAll());
	}

	@Test
	public void test_06() {
		byte[] bs1 = "hello world".getBytes();
		Content.Blob v = new ByteBlob(bs1);
		Content.Blob[] vs = new Content.Blob[10];
		// A bunch of replacements
		vs[0] = v.replaceBytes(0, 2, "H".getBytes());
		vs[1] = v.replaceBytes(1, 2, "E".getBytes());
		vs[2] = v.replaceBytes(2, 2, "L".getBytes());
		vs[3] = v.replaceBytes(3, 2, "L".getBytes());
		vs[4] = v.replaceBytes(4, 2, "O".getBytes());
		vs[5] = v.replaceBytes(5, 2, "_".getBytes());
		vs[6] = v.replaceBytes(6, 2, "W".getBytes());
		vs[7] = v.replaceBytes(7, 2, "O".getBytes());
		vs[8] = v.replaceBytes(8, 2, "R".getBytes());
		vs[9] = v.replaceBytes(9, 2, "L".getBytes());
		// Check sizes invariant
		assertEquals(v.size()-1,vs[0].size());
		assertEquals(v.size()-1,vs[1].size());
		assertEquals(v.size()-1,vs[2].size());
		assertEquals(v.size()-1,vs[3].size());
		assertEquals(v.size()-1,vs[4].size());
		assertEquals(v.size()-1,vs[5].size());
		assertEquals(v.size()-1,vs[6].size());
		assertEquals(v.size()-1,vs[7].size());
		assertEquals(v.size()-1,vs[8].size());
		assertEquals(v.size()-1,vs[9].size());
		// Check character change as expected
		assertArrayEquals("Hllo world".getBytes(),vs[0].readAll());
		assertArrayEquals("hElo world".getBytes(),vs[1].readAll());
		assertArrayEquals("heLo world".getBytes(),vs[2].readAll());
		assertArrayEquals("helL world".getBytes(),vs[3].readAll());
		assertArrayEquals("hellOworld".getBytes(),vs[4].readAll());
		assertArrayEquals("hello_orld".getBytes(),vs[5].readAll());
		assertArrayEquals("hello Wrld".getBytes(),vs[6].readAll());
		assertArrayEquals("hello wOld".getBytes(),vs[7].readAll());
		assertArrayEquals("hello woRd".getBytes(),vs[8].readAll());
		assertArrayEquals("hello worL".getBytes(),vs[9].readAll());
	}

	// ===================================================================
	// Multi Tests
	// ===================================================================

	@Test
	public void test_multi_01() {
		byte[] bs1 = "hello world".getBytes();
		byte[] bs2 = "HELLO WORLD".getBytes();
		checkMutants(bs1, bs2, 1);
	}

	@Test
	public void test_multi_02() {
		byte[] bs1 = "hello world".getBytes();
		byte[] bs2 = "HELLO WORLD".getBytes();
		checkMutants(bs1, bs2, 2);
	}

	@Test
	public void test_multi_03() {
		byte[] bs1 = "hello world".getBytes();
		byte[] bs2 = "HELLO WORLD".getBytes();
		checkMutants(bs1, bs2, 2);
	}

	private static void checkMutants(byte[] source, byte[] target, int n) {
		Content.Blob[] mutants = new Content.Blob[] { new ByteBlob(source) };
		//
		for (int i = 0; i != n; ++i) {
			mutants = mutate(target, mutants);
		}
		// Sanity check mutants
		for (Content.Blob b : mutants) {
			byte[] bs = b.readAll();
			assertEquals(bs.length, source.length);
			int count = 0;
			for (int i = 0; i != bs.length; ++i) {
				if (bs[i] != source[i]) {
					count++;
					assertEquals(target[i], bs[i]);
				}
			}
			assertEquals(n, count);
		}
	}

	private static Content.Blob[] mutate(byte[] target, Content.Blob[] blobs) {
		ArrayList<Content.Blob> tmp = new ArrayList<>();
		for(int i=0;i!=blobs.length;++i) {
			Content.Blob ith = blobs[i];
			for(int j=0;j!=target.length;++j) {
				byte b = ith.readByte(j);
				if(b != target[j]) {
					tmp.add(ith.writeByte(j, target[j]));
				}
			}
		}
		return tmp.toArray(new Content.Blob[tmp.size()]);
	}

	// ===================================================================
	// Diff Tests
	// ===================================================================

	@Test
	public void test_diff_01() {
		check("a","");
	}

	@Test
	public void test_diff_02() {
		check("a","1");
	}

	@Test
	public void test_diff_03() {
		check("a","12");
	}

	@Test
	public void test_diff_04() {
		check("ab","");
	}

	@Test
	public void test_diff_05() {
		check("ab","1");
	}

	@Test
	public void test_diff_06() {
		check("ab","12");
	}

	@Test
	public void test_diff_07() {
		check("abcde","");
	}

	@Test
	public void test_diff_08() {
		check("abcde","1");
	}

	@Test
	public void test_diff_09() {
		check("abcde","12");
	}

	@Test
	public void test_diff_10() {
		check("abcde","","");
	}

	@Test
	public void test_diff_11() {
		check("abcde","1","");
	}

	@Test
	public void test_diff_12() {
		check("abcde","","1");
	}

	@Test
	public void test_diff_13() {
		check("abcde","1","1");
	}

	@Test
	public void test_diff_14() {
		check("abcde","12","");
	}

	@Test
	public void test_diff_15() {
		check("abcde","12","1");
	}

	@Test
	public void test_diff_16() {
		check("abcde","","12");
	}

	@Test
	public void test_diff_17() {
		check("abcde","1","12");
	}

	@Test
	public void test_diff_18() {
		check("abcde","12","12");
	}

	private static void check(String before, String... replacements) {
		final int n = replacements.length;
		for (String after : permute(before, replacements)) {
			// Construct corresponding diff
			ByteBlob.Diff diff = ByteBlob.diff(before.getBytes(), after.getBytes());
			// Check number of replacements matches
			assertTrue(diff.count() <= n);
			// Check replacement matches
			assertEquals(after, new String(diff.readAll()));
		}
	}

	/**
	 * Generate all possible <i>nary</i> replacements for a given string using a
	 * given set of replacements.
	 *
	 * @param text         The base string on which replacements are made.
	 * @param replacements The sequence of replacement strings to use.
	 * @return
	 */
	private static List<String> permute(String text, String... replacements) {
		final int n = replacements.length;
		ArrayList<String> results = new ArrayList<>();
		permute(new Replacement[n], 0, text, replacements, results);
		return results;
	}

	private static void permute(Replacement[] root, int i, String text, String[] replacements,
			List<String> results) {
		if (i == root.length) {
			results.add(replace(text, root));
		} else {
			// Determine starting position
			int start = (i == 0) ? 0 : root[i - 1].next();
			//
			for (Replacement r : permuteSingle(text, start, replacements[i])) {
				root[i] = r;
				permute(root, i + 1, text, replacements, results);
			}
		}
	}

	/**
	 * Generate all possible unit replacements for a given string and a given
	 * replacement string.
	 *
	 * @param text
	 * @return
	 */
	private static List<Replacement> permuteSingle(String text, int start, String replacement) {
		ArrayList<Replacement> rs = new ArrayList<>();
		for (; start <= text.length(); ++start) {
			// Calculate maximum size of region
			int rem = text.length() - start;
			// Enumerate all possible replacement regions
			for (int length = 0; length <= rem; ++length) {
				if(length != 0 || replacement.length() != 0) {
					rs.add(new Replacement(start, length, replacement));
				}
			}
		}
		return rs;
	}


	/**
	 * Apply a given sequence of replacements to a given text string.
	 *
	 * @param text
	 * @param root
	 * @return
	 */
	private static String replace(String text, Replacement... root) {
		int delta = 0;
		//
		for (int i = 0; i != root.length; ++i) {
			Replacement r = root[i];
			int start = r.start + delta;
			// NOTE: could be way more efficient!!
			String before = text.substring(0,start);
			String after = text.substring(start + r.length);
			text = before + r.text + after;
			// JUpdate delta
			delta += r.text.length() - r.length;
		}
		//
		return text;
	}

	/**
	 * Represents a unit replacement for a given text sequence.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static final class Replacement {
		public final int start;
		public final int length;
		public final String text;

		public Replacement(int start, int length, String text) {
			// Sanity check what is generated
			if(text.length() == 0 && length == 0) {
				throw new IllegalArgumentException("invalid replacement");
			}
			this.start = start;
			this.length = length;
			this.text = text;
		}

		public int next() {
			return (length == 0) ? (start + 1) : start + length;
		}

		@Override
		public String toString() {
			return start + ":" + length + ":\"" + text + "\"";
		}
	}
}
