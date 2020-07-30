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

import org.junit.jupiter.api.Test;

import jledger.core.Value;
import jledger.util.ByteArrayLedger;
import jledger.util.ByteArrayLedger.Data;
import jledger.util.ByteArrayLedger.Key;
import jledger.util.ByteArrayValue;
import jledger.util.Pair;

/**
 * Perform a range of tests on small domains. We use
 * <code>AbstractBigDomain</code> rather than going through the
 * <code>Domains</code> API to ensure we are checking what we are intending.
 *
 * @author David J. Pearce
 *
 */
public class ByteArrayLedgerTests {
	// ================================================================================
	// Keys
	// ================================================================================

	@Test
	public void test_keys_01() {
		// Test adding keys
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		// Add single key
		test_add_key("key",ledger);
	}

	@Test
	public void test_keys_02() {
		// Test adding keys
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		// Add single key
		test_add_key("key1",ledger);
		test_add_key("key2",ledger);
		test_check_key("key1",ledger);
		test_check_key("key2",ledger);
	}
	
	@Test
	public void test_keys_03() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 64 digits
		String key = createString("key",0,64);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_04() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 128 digits
		String key = createString("key",0,128);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_05() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 256 digits
		String key = createString("key",0,256);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_06() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 32768 digits
		String key = createString("key",0,32768);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_07() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 65546 digits
		String key = createString("key",0,65536);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_08() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		for (int i = 5; i < 256; i = i + 1) {
			String key = createString("key", 0, i);
			// Add single key
			test_add_key(key, ledger);
		}
		// Check all keys
		for (int i = 5; i < 256; i = i + 1) {
			String key = createString("key", 0, i);
			test_check_key(key, ledger);
		}
	}
	
	@Test
	public void test_keys_09() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		for (int i = 256; i < 65536; i = i + 1) {
			String key = createString("key", 0, i);
			// Add single key
			test_add_key(key, ledger);
		}
		// Check all keys
		for (int i = 256; i < 65536; i = i + 1) {
			String key = createString("key", 0, i);
			test_check_key(key, ledger);
		}
	}
	
	// ================================================================================
	// Values
	// ================================================================================

	@Test
	public void test_values_01() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = "Hello World".getBytes();
		test_add_value(bs,ledger);
	}
	
	@Test
	public void test_values_02() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs1 = "Hello World".getBytes();
		byte[] bs2 = "Another World".getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs1,ledger);
		ByteArrayLedger.Data d2 = test_add_value(bs2,ledger);
		test_check_value(d1,bs1);
		test_check_value(d2,bs2);
	}
	
	@Test
	public void test_values_03() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = createString("data",0,64).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs,ledger);
		test_check_value(d1,bs);
	}
	
	@Test
	public void test_values_04() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs1 = createString("data", 0, 64).getBytes();
		byte[] bs2 = createString("data", 123, 64).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs1,ledger);
		ByteArrayLedger.Data d2 = test_add_value(bs2,ledger);
		test_check_value(d1,bs1);
		test_check_value(d2,bs2);
	}
	
	@Test
	public void test_values_05() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = createString("data",0,128).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs,ledger);
		test_check_value(d1,bs);
	}
	
	@Test
	public void test_values_06() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs1 = createString("data", 0, 128).getBytes();
		byte[] bs2 = createString("data", 123, 128).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs1,ledger);
		ByteArrayLedger.Data d2 = test_add_value(bs2,ledger);
		test_check_value(d1,bs1);
		test_check_value(d2,bs2);
	}
	
	@Test
	public void test_values_07() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = createString("data",0,256).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs,ledger);
		test_check_value(d1,bs);
	}
	
	@Test
	public void test_values_08() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs1 = createString("data", 0, 256).getBytes();
		byte[] bs2 = createString("data", 123, 256).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs1,ledger);
		ByteArrayLedger.Data d2 = test_add_value(bs2,ledger);
		test_check_value(d1,bs1);
		test_check_value(d2,bs2);
	}
		
	
	@Test
	public void test_values_09() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = createString("data",0,16384).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs,ledger);
		test_check_value(d1,bs);
	}
	
	@Test
	public void test_values_10() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs1 = createString("data", 0, 16384).getBytes();
		byte[] bs2 = createString("data", 123, 16384).getBytes();
		ByteArrayLedger.Data d1 = test_add_value(bs1,ledger);
		ByteArrayLedger.Data d2 = test_add_value(bs2,ledger);
		test_check_value(d1,bs1);
		test_check_value(d2,bs2);
	}	

	@Test
	public void test_values_11() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		ArrayList<Pair<ByteArrayLedger.Data, byte[]>> items = new ArrayList<>();
		//
		for (int i = 5; i < 256; i = i + 1) {
			byte[] bs = createString("data", 0, i).getBytes();
			// Add single value
			ByteArrayLedger.Data d = test_add_value(bs, ledger);
			// Save for later
			items.add(new Pair<>(d, bs));
		}
		// Check all values
		for (Pair<ByteArrayLedger.Data, byte[]> item : items) {
			test_check_value(item.first(), item.second());
		}
	}
	
	@Test
	public void test_values_12() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		ArrayList<Pair<ByteArrayLedger.Data, byte[]>> items = new ArrayList<>();
		//
		for (int i = 256; i < 65536; i = i + 1) {
			byte[] bs = createString("data", 0, i).getBytes();
			// Add single value
			ByteArrayLedger.Data d = test_add_value(bs, ledger);
			// Save for later
			items.add(new Pair<>(d, bs));
		}
		// Check all values
		for (Pair<ByteArrayLedger.Data, byte[]> item : items) {
			test_check_value(item.first(), item.second());
		}
	}
	
	// ================================================================================
	// Diff
	// ================================================================================
	
	@Test
	public void test_diffs_01() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = "Hello World".getBytes();		
		ByteArrayLedger.Data d1 = test_add_value(bs,ledger);
		ByteArrayLedger.Data d2 = test_add_value(d1.write(0, (byte) 'h'), ledger);
		//
		test_check_value(d2, "hello World".getBytes());
	}
	
	@Test
	public void test_diffs_02() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = "Hello World".getBytes();		
		ByteArrayLedger.Data d1 = test_add_value(bs,ledger);
		ByteArrayLedger.Data d2 = test_add_value(d1.replace(0, 1, (byte) 'h'), ledger);
		//
		test_check_value(d2, "hello World".getBytes());
	}
	
	@Test
	public void test_diffs_03() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = "Hello World".getBytes();		
		ByteArrayLedger.Data d1 = test_add_value(bs,ledger);
		ByteArrayLedger.Data d2 = test_add_value(d1.replace(0, 2, (byte) 'h'), ledger);
		//
		test_check_value(d2, "hllo World".getBytes());
	}
	
	// ================================================================================
	// Transactions
	// ================================================================================
	
	@Test
	public void test_txns_01() {
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		byte[] bs = "Hello World".getBytes();
		ByteArrayLedger.Key k1 = test_add_key("key", ledger);
		assertEquals(null, ledger.get(k1));
		//
		ByteArrayLedger.Data d1 = test_add_value(bs, ledger);
		test_apply_txn(ledger, new Pair<>(k1, d1));
	}
	
	// ================================================================================
	// Helpers
	// ================================================================================
		
	private static ByteArrayLedger.Key test_add_key(String key, ByteArrayLedger ledger) {
		// Store size
		int size = ledger.size();
		// Add new key
		Key k1 = ledger.add(key);
		Key k2 = ledger.lookup(key);
		// Sanity checks
		assertTrue(k1 != null);
		assertTrue(k2 != null);
		assertTrue(k1.equals(k2));
		assertTrue(k1.hashCode() == k2.hashCode());
		assertTrue(k1.get().equals(key));
		assertEquals(ledger.size(), size + 1);
		//
		return k1;
	}
	
	private static void test_check_key(String key, ByteArrayLedger ledger) {
		Key k1 = ledger.lookup(key);
		// Sanity checks
		assertTrue(k1 != null);
		assertTrue(k1.get().equals(key));
	}
	
	private static ByteArrayLedger.Data test_add_value(byte[] bytes, ByteArrayLedger ledger) {
		return test_add_value(new ByteArrayValue(bytes),ledger);
	}
	
	private static ByteArrayLedger.Data test_add_value(Value val, ByteArrayLedger ledger) {
		byte[] bytes = val.get();
		// Store size
		int size = ledger.size();
		// Add a new value to the ledger
		ByteArrayLedger.Data d = ledger.add(val);
		// Sanity check what was added
		assertTrue(d != null);
		assertArrayEquals(d.get(),bytes);
		assertEquals(ledger.size(), size + 1);
		//
		return d;
	}
	
	private static void test_apply_txn(ByteArrayLedger ledger, Pair<Key, Data>... pairs) {
		ledger.add(pairs);
		// Sanity check
		for (Pair<Key, Data> p : pairs) {
			byte[] bs1 = p.second().get();
			byte[] bs2 = ledger.get(p.first()).get();
			assertArrayEquals(bs1, bs2);
		}
	}
	
	
	private static void test_check_value(ByteArrayLedger.Data d, byte[] bytes) {		
		// Sanity check what was added
		assertTrue(d != null);
		assertArrayEquals(d.get(),bytes);
	}
	
	private static String createString(String preamble, int n, int digits) {
		String id = Integer.toHexString(n);
		int diff = digits - (preamble.length() + id.length());
		if(diff < 0) {
			throw new IllegalArgumentException("insufficient number of digits");
		} else {
			for (int i = 0; i < diff; ++i) {
				preamble += "0";
			}
			return preamble + id;
		}
	}
}
