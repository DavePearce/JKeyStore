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

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import jledger.core.Value;
import jledger.util.ByteArrayValue;

public class ByteArrayValueTests {

	@Test
	public void test_01() {
		byte[] bs1 = "Hello World".getBytes();
		ByteArrayValue v1 = new ByteArrayValue(bs1);
		assertEquals(v1.size(),11);
		assertEquals(v1.get(),bs1);
		for (int i = 0; i != bs1.length; ++i) {
			assertEquals(v1.read(i), bs1[i]);
		}
	}

	@Test
	public void test_02() {
		byte[] bs1 = "hello world".getBytes();
		Value v = new ByteArrayValue(bs1);
		Value[] vs = new Value[11];
		// A bunch of writes
		vs[0] = v.write(0, (byte) 'H');
		vs[1] = v.write(1, (byte) 'E');
		vs[2] = v.write(2, (byte) 'L');
		vs[3] = v.write(3, (byte) 'L');
		vs[4] = v.write(4, (byte) 'O');
		vs[5] = v.write(5, (byte) '_');
		vs[6] = v.write(6, (byte) 'W');
		vs[7] = v.write(7, (byte) 'O');
		vs[8] = v.write(8, (byte) 'R');
		vs[9] = v.write(9, (byte) 'L');
		vs[10] = v.write(10, (byte) 'D');
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
		assertArrayEquals("Hello world".getBytes(),vs[0].get());
		assertArrayEquals("hEllo world".getBytes(),vs[1].get());
		assertArrayEquals("heLlo world".getBytes(),vs[2].get());
		assertArrayEquals("helLo world".getBytes(),vs[3].get());
		assertArrayEquals("hellO world".getBytes(),vs[4].get());
		assertArrayEquals("hello_world".getBytes(),vs[5].get());
		assertArrayEquals("hello World".getBytes(),vs[6].get());
		assertArrayEquals("hello wOrld".getBytes(),vs[7].get());
		assertArrayEquals("hello woRld".getBytes(),vs[8].get());
		assertArrayEquals("hello worLd".getBytes(),vs[9].get());
		assertArrayEquals("hello worlD".getBytes(),vs[10].get());		
	}
	
	@Test
	public void test_03() {
		byte[] bs1 = "hello world".getBytes();
		Value v = new ByteArrayValue(bs1);
		Value[] vs = new Value[11];
		// A bunch of replacements
		vs[0] = v.replace(0, 1, "H".getBytes());
		vs[1] = v.replace(1, 1, "E".getBytes());
		vs[2] = v.replace(2, 1, "L".getBytes());
		vs[3] = v.replace(3, 1, "L".getBytes());
		vs[4] = v.replace(4, 1, "O".getBytes());
		vs[5] = v.replace(5, 1, "_".getBytes());
		vs[6] = v.replace(6, 1, "W".getBytes());
		vs[7] = v.replace(7, 1, "O".getBytes());
		vs[8] = v.replace(8, 1, "R".getBytes());
		vs[9] = v.replace(9, 1, "L".getBytes());
		vs[10] = v.replace(10, 1, "D".getBytes());
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
		assertArrayEquals("Hello world".getBytes(),vs[0].get());
		assertArrayEquals("hEllo world".getBytes(),vs[1].get());
		assertArrayEquals("heLlo world".getBytes(),vs[2].get());
		assertArrayEquals("helLo world".getBytes(),vs[3].get());
		assertArrayEquals("hellO world".getBytes(),vs[4].get());
		assertArrayEquals("hello_world".getBytes(),vs[5].get());
		assertArrayEquals("hello World".getBytes(),vs[6].get());
		assertArrayEquals("hello wOrld".getBytes(),vs[7].get());
		assertArrayEquals("hello woRld".getBytes(),vs[8].get());
		assertArrayEquals("hello worLd".getBytes(),vs[9].get());
		assertArrayEquals("hello worlD".getBytes(),vs[10].get());		
	}
	
	@Test
	public void test_04() {
		byte[] bs1 = "hello world".getBytes();
		Value v = new ByteArrayValue(bs1);
		Value[] vs = new Value[11];
		// A bunch of replacements
		vs[0] = v.replace(0, 1, "Hh".getBytes());
		vs[1] = v.replace(1, 1, "Ee".getBytes());
		vs[2] = v.replace(2, 1, "Ll".getBytes());
		vs[3] = v.replace(3, 1, "Ll".getBytes());
		vs[4] = v.replace(4, 1, "Oo".getBytes());
		vs[5] = v.replace(5, 1, " _".getBytes());
		vs[6] = v.replace(6, 1, "Ww".getBytes());
		vs[7] = v.replace(7, 1, "Oo".getBytes());
		vs[8] = v.replace(8, 1, "Rr".getBytes());
		vs[9] = v.replace(9, 1, "Ll".getBytes());
		vs[10] = v.replace(10, 1, "Dd".getBytes());
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
		assertArrayEquals("Hhello world".getBytes(),vs[0].get());
		assertArrayEquals("hEello world".getBytes(),vs[1].get());
		assertArrayEquals("heLllo world".getBytes(),vs[2].get());
		assertArrayEquals("helLlo world".getBytes(),vs[3].get());
		assertArrayEquals("hellOo world".getBytes(),vs[4].get());
		assertArrayEquals("hello _world".getBytes(),vs[5].get());
		assertArrayEquals("hello Wworld".getBytes(),vs[6].get());
		assertArrayEquals("hello wOorld".getBytes(),vs[7].get());
		assertArrayEquals("hello woRrld".getBytes(),vs[8].get());
		assertArrayEquals("hello worLld".getBytes(),vs[9].get());
		assertArrayEquals("hello worlDd".getBytes(),vs[10].get());		
	}
	
	@Test
	public void test_05() {
		byte[] bs1 = "hello world".getBytes();
		Value v = new ByteArrayValue(bs1);
		Value[] vs = new Value[11];
		// A bunch of replacements
		vs[0] = v.replace(0, 1, "HhH".getBytes());
		vs[1] = v.replace(1, 1, "EeE".getBytes());
		vs[2] = v.replace(2, 1, "LlL".getBytes());
		vs[3] = v.replace(3, 1, "LlL".getBytes());
		vs[4] = v.replace(4, 1, "OoO".getBytes());
		vs[5] = v.replace(5, 1, " _ ".getBytes());
		vs[6] = v.replace(6, 1, "WwW".getBytes());
		vs[7] = v.replace(7, 1, "OoO".getBytes());
		vs[8] = v.replace(8, 1, "RrR".getBytes());
		vs[9] = v.replace(9, 1, "LlL".getBytes());
		vs[10] = v.replace(10, 1, "DdD".getBytes());
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
		assertArrayEquals("HhHello world".getBytes(),vs[0].get());
		assertArrayEquals("hEeEllo world".getBytes(),vs[1].get());
		assertArrayEquals("heLlLlo world".getBytes(),vs[2].get());
		assertArrayEquals("helLlLo world".getBytes(),vs[3].get());
		assertArrayEquals("hellOoO world".getBytes(),vs[4].get());
		assertArrayEquals("hello _ world".getBytes(),vs[5].get());
		assertArrayEquals("hello WwWorld".getBytes(),vs[6].get());
		assertArrayEquals("hello wOoOrld".getBytes(),vs[7].get());
		assertArrayEquals("hello woRrRld".getBytes(),vs[8].get());
		assertArrayEquals("hello worLlLd".getBytes(),vs[9].get());
		assertArrayEquals("hello worlDdD".getBytes(),vs[10].get());		
	}
	
	@Test
	public void test_06() {
		byte[] bs1 = "hello world".getBytes();
		Value v = new ByteArrayValue(bs1);
		Value[] vs = new Value[10];
		// A bunch of replacements
		vs[0] = v.replace(0, 2, "H".getBytes());
		vs[1] = v.replace(1, 2, "E".getBytes());
		vs[2] = v.replace(2, 2, "L".getBytes());
		vs[3] = v.replace(3, 2, "L".getBytes());
		vs[4] = v.replace(4, 2, "O".getBytes());
		vs[5] = v.replace(5, 2, "_".getBytes());
		vs[6] = v.replace(6, 2, "W".getBytes());
		vs[7] = v.replace(7, 2, "O".getBytes());
		vs[8] = v.replace(8, 2, "R".getBytes());
		vs[9] = v.replace(9, 2, "L".getBytes());
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
		assertArrayEquals("Hllo world".getBytes(),vs[0].get());
		assertArrayEquals("hElo world".getBytes(),vs[1].get());
		assertArrayEquals("heLo world".getBytes(),vs[2].get());
		assertArrayEquals("helL world".getBytes(),vs[3].get());
		assertArrayEquals("hellOworld".getBytes(),vs[4].get());
		assertArrayEquals("hello_orld".getBytes(),vs[5].get());
		assertArrayEquals("hello Wrld".getBytes(),vs[6].get());
		assertArrayEquals("hello wOld".getBytes(),vs[7].get());
		assertArrayEquals("hello woRd".getBytes(),vs[8].get());
		assertArrayEquals("hello worL".getBytes(),vs[9].get());		
	}
}
