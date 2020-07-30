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

import org.junit.jupiter.api.Test;

import jledger.util.ByteArrayLedger;
import jledger.util.ByteArrayLedger.Key;

/**
 * Perform a range of tests on small domains. We use
 * <code>AbstractBigDomain</code> rather than going through the
 * <code>Domains</code> API to ensure we are checking what we are intending.
 *
 * @author David J. Pearce
 *
 */
public class ByteArrayLedgerTests {

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
		// Test adding keys of increasingle large sizes
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 64 digits
		String key = createKey("key",0,64);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_04() {
		// Test adding keys of increasingle large sizes
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 128 digits
		String key = createKey("key",0,128);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_05() {
		// Test adding keys of increasingle large sizes
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 256 digits
		String key = createKey("key",0,256);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_06() {
		// Test adding keys of increasingly large sizes
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 32768 digits
		String key = createKey("key",0,32768);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_07() {
		// Test adding keys of increasingly large sizes
		ByteArrayLedger ledger = new ByteArrayLedger(100);		
		// Create a key with 65546 digits
		String key = createKey("key",0,65536);
		// Add single key
		test_add_key(key,ledger);
		test_check_key(key,ledger);
	}
	
	@Test
	public void test_keys_08() {
		// Test adding keys of increasingle large sizes
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		for (int i = 5; i < 256; i = i + 1) {
			String key = createKey("key", 0, i);
			// Add single key
			test_add_key(key, ledger);
		}
		// Check all keys
		for (int i = 5; i < 256; i = i + 1) {
			String key = createKey("key", 0, i);
			test_check_key(key, ledger);
		}
	}
	
	@Test
	public void test_keys_09() {
		// Test adding keys of increasingle large sizes
		ByteArrayLedger ledger = new ByteArrayLedger(100);
		for (int i = 256; i < 65536; i = i + 1) {
			String key = createKey("key", 0, i);
			// Add single key
			test_add_key(key, ledger);
		}
		// Check all keys
		for (int i = 256; i < 65536; i = i + 1) {
			String key = createKey("key", 0, i);
			test_check_key(key, ledger);
		}
	}
	
	private static void test_add_key(String key, ByteArrayLedger ledger) {
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
	}
	
	private static void test_check_key(String key, ByteArrayLedger ledger) {
		Key k1 = ledger.lookup(key);
		// Sanity checks
		assertTrue(k1 != null);
		assertTrue(k1.get().equals(key));
	}
	
	private static String createKey(String preamble, int n, int digits) {
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
