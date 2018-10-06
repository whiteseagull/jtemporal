/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import junit.framework.TestCase;

/**
 * @author Thomas A Beck
 * @version $Id: LRUCacheTest.java,v 1.4 2005/05/14 16:37:21 tabeck Exp $
 */
public class LRUCacheTest extends TestCase {

	private LightLRUCache cache;

	/**
	 * Constructor for LRUCacheTest.
	 * @param name
	 */
	public LRUCacheTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(LRUCacheTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.cache = new LightLRUCache(3);
		this.cache.put("A", "a");
		this.cache.put("B", "b");
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*public void testLinkedList() {
		System.out.println("Testing it...");
		LightLRUCache.testLinkedList();
	}*/
	
	public void testUpdate() {
		Object prev = this.cache.put("B", "bbb");
		assertEquals(prev, "b");
		Object o = this.cache.get("B");
		assertEquals(o, "bbb");
	}

	public void testLRUCycle() {
		this.cache.put("C", "c");
		this.cache.put("D", "d");
		assertTrue( cache.get("A") == null );
		assertTrue( cache.get("B").equals("b") );
		assertTrue( cache.get("C").equals("c") );
		assertTrue( cache.get("D").equals("d") ); // B,C,D
		assertTrue( cache.get("B").equals("b") ); // C,D,B
		assertTrue( cache.get("B").equals("b") ); // C,D,B
		assertTrue( cache.get("C").equals("c") ); // D,B,C
		this.cache.put("E", "e");
		assertTrue( cache.get("D") == null ); 
		assertTrue( cache.get("B").equals("b") ); 
		assertTrue( cache.get("C").equals("c") ); 
		assertTrue( cache.get("E").equals("e") ); 
	}

	public void testRemove() {
		this.cache.put("C", "c");
		assertTrue (this.cache.remove("X") == null);
		assertTrue (this.cache.remove("B").equals("b"));
		assertTrue( cache.currentSize() == 2);  // A,C
		assertTrue (this.cache.remove("C").equals("c"));
		assertTrue (this.cache.remove("A").equals("a"));
		assertTrue( cache.currentSize() == 0);  // A,C
	}

	public void testClear() {
		cache.clear();
		assertTrue( cache.currentSize() == 0 );
		assertTrue( cache.get("A") == null );
		assertTrue( cache.get("B") == null );
	}

	public void testCurrentSize() {
		assertTrue( cache.currentSize() == 2 );
	}

	public void testGet() {
		assertTrue( cache.get("A").equals("a") );
		assertTrue( cache.get("B").equals("b") );
		assertTrue( cache.get("C") == null );
	}

	public void testMaxSize() {
		assertTrue( cache.maxSize() == 3 );
		this.cache.put("B", "bbb");
		this.cache.put("C", "c");
		this.cache.put("D", "d");
		assertTrue( cache.currentSize() == 3);
	}

}
