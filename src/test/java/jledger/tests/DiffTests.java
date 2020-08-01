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
import java.util.List;

import org.junit.jupiter.api.Test;

import jledger.util.Diff;

/**
 * Tests for which <code>jledger.util.Diff</code> which are constructed from
 * exhaustive enumerations of replacements over given strings.
 * 
 * @author David J. Pearce
 *
 */
public class DiffTests {

	@Test
	public void test_01() {
		check("h","");
	}
	
	@Test
	public void test_02() {
		check("h","1");
	}
	
	@Test
	public void test_03() {
		check("h","12");
	}
	
	@Test
	public void test_04() {
		check("he","");
	}
	
	@Test
	public void test_05() {
		check("he","1");
	}
	
	@Test
	public void test_06() {
		check("he","12");
	}
	
	@Test
	public void test_07() {
		check("hello","");
	}
	
	@Test
	public void test_08() {
		check("hello","1");
	}
	
	@Test
	public void test_09() {
		check("hello","12");
	}
	
	@Test
	public void test_10() {
		check("hello","","");
	}
	
	@Test
	public void test_11() {
		check("hello","1","");
	}
	
	@Test
	public void test_12() {
		check("hello","","1");
	}
	
	@Test
	public void test_13() {
		check("hello","1","1");
	}
	
	@Test
	public void test_14() {
		check("hello","12","");
	}
	
	@Test
	public void test_15() {
		check("hello","12","1");
	}
	
	@Test
	public void test_16() {
		check("hello","","12");
	}
	
	@Test
	public void test_17() {
		check("hello","1","12");
	}
	
	@Test
	public void test_18() {
		check("hello","12","12");
	}
	
	private static void check(String before, String... replacements) {
		final int n = replacements.length;
		for (String after : permute(before, replacements)) {
			// Construct corresponding diff
			Diff diff = Diff.construct(before, after);
			//
			// Check number of replacements matches
			assertEquals(n, diff.size());
			// Check replacement matches
			assertEquals(after, diff.apply(before));
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
		
		public String toString() {
			return start + ":" + length + ":\"" + text + "\"";
		}
	}	
}
