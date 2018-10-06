/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import junit.framework.TestCase;

import java.util.*;

/**
 * Generic test case for {@linkplain java.util.Set} implementations.
 * @author Thomas A Beck
 * @version $Id: GenericSetTst.java,v 1.2 2005/10/29 16:33:57 tabeck Exp $
 */
public abstract class GenericSetTst extends TestCase {

	static final int SIZE = 4;
		
	private Set set = null;
	private Object[] values = null;
	private boolean allowsAdd;
	private boolean allowsRemove;

	/**
	 * Provides a new instance of Set to be tested.  
	 * @return the values specified in {@linkplain #getValues()}
	 */
	public abstract Set getNewSet();

	/**
	 * _values an array containing the 4 values supposed to be in the set
	 * @return the values contained by the {@linkplain #getNewSet()}
	 */
	public abstract Object[] getValues();

	 /**
	  * _allowsAdd Determines whether the Set is supposed to accept new additions.
	  */
	public abstract boolean allowsAdd();
	
	 /**
	  * Determines whether the Set is supposed to accept removals.
	  */
	public abstract boolean allowsRemove();
	
	public GenericSetTst(String name) {
	    super(name);
	}
	

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		this.set = this.getNewSet();
		assertNotNull (set);
		
		this.values = this.getValues();
		assertNotNull (this.values);
		
		this.allowsAdd = this.allowsAdd();
		this.allowsRemove = this.allowsRemove();
		
		assertTrue ("If it allows add, it must allow remove",
			allowsAdd ? allowsRemove : true
		);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public final void testSize() {
		assertTrue(set.size() == SIZE);
	}

	public void testClear() {
		
		if (allowsRemove()) {
			assertTrue(set.size() == SIZE);

			assertTrue(! set.isEmpty());

			set.clear();
			assertTrue(set.size() == 0);
			assertTrue(! set.contains(values[0]));
			assertTrue(! set.contains(values[1]));
			assertTrue(! set.contains(values[2]));
			assertTrue(! set.contains(values[3]));
		
			assertTrue(  set.isEmpty());
		}
		else {
			assertTrue(set.size() == SIZE);

			assertTrue(! set.isEmpty());

			try {
				set.clear();
				fail("Missing exception");
			}
			catch (UnsupportedOperationException e) {
				// this is fine
			}
			assertTrue(set.size() == SIZE);
			assertTrue(  set.contains(values[0]));
			assertTrue(  set.contains(values[1]));
			assertTrue(  set.contains(values[2]));
			assertTrue(  set.contains(values[3]));
		
			assertTrue(! set.isEmpty());
			
		}
	}
	
	public void testIsEmpty() {
		assertTrue(! set.isEmpty());
	}

	public void testAddAndRemove() {
		boolean ok;
		if (allowsAdd) {
			set.clear();
			assertTrue(!set.contains(values[1]));			
			assertTrue( set.size() == 0);
			ok = set.add(values[3]);
			assertTrue (ok);
			assertTrue( set.size() == 1);
			assertTrue( set.contains(values[3]));			
			assertTrue(!set.contains(values[1]));			

			ok = set.add(values[1]);
			assertTrue (ok);
			assertTrue( set.size() == 2);
			assertTrue( set.contains(values[3]));			
			assertTrue( set.contains(values[1]));			

			ok = set.add(values[3]);
			assertTrue (!ok);
			assertTrue( set.size() == 2);
			assertTrue( set.contains(values[3]));			
			assertTrue( set.contains(values[1]));
			
			ok = set.remove(values[3]);
			assertTrue (ok);
			assertTrue( set.size() == 1);
			assertTrue(!set.contains(values[3]));			
			assertTrue( set.contains(values[1]));
									
			ok = set.remove(values[0]);
			assertTrue (!ok);
			assertTrue( set.size() == 1);
			assertTrue(!set.contains(values[3]));			
			assertTrue( set.contains(values[1]));
									
		}
	}

	public void testRemove() {
		boolean ok;
		assertTrue( set.contains(values[0]));
		assertTrue( set.contains(values[1]));
		assertTrue( set.contains(values[2]));
		assertTrue( set.contains(values[3]));

		if (allowsRemove) {
			ok = set.remove(values[2]);
			assertTrue ( ok);
			assertTrue( set.contains(values[0]));
			assertTrue( set.contains(values[1]));
			assertTrue(!set.contains(values[2]));
			assertTrue( set.contains(values[3]));
			assertTrue( set.size() == 3);
			
			ok = set.remove(values[2]);
			assertTrue (!ok);
			assertTrue( set.contains(values[0]));
			assertTrue( set.contains(values[1]));
			assertTrue(!set.contains(values[2]));
			assertTrue( set.contains(values[3]));
			assertTrue( set.size() == 3);
						
			ok = set.remove(values[0]);
			assertTrue ( ok);
			assertTrue(!set.contains(values[0]));
			assertTrue( set.contains(values[1]));
			assertTrue(!set.contains(values[2]));
			assertTrue( set.contains(values[3]));
			assertTrue( set.size() == 2);

			ok = set.remove(values[0]);
			assertTrue (!ok);
			assertTrue(!set.contains(values[0]));
			assertTrue( set.contains(values[1]));
			assertTrue(!set.contains(values[2]));
			assertTrue( set.contains(values[3]));
			assertTrue( set.size() == 2);

			
			ok = set.remove(values[1]);
			assertTrue ( ok);
			assertTrue(!set.contains(values[0]));
			assertTrue(!set.contains(values[1]));
			assertTrue(!set.contains(values[2]));
			assertTrue( set.contains(values[3]));
			assertTrue( set.size() == 1);
			assertTrue(!set.isEmpty());
			
			ok = set.remove(values[3]);
			assertTrue ( ok);
			assertTrue(!set.contains(values[0]));
			assertTrue(!set.contains(values[1]));
			assertTrue(!set.contains(values[2]));
			assertTrue(!set.contains(values[3]));
			assertTrue( set.size() == 0);
			assertTrue( set.isEmpty());			
		}
	}

	public void testReadingIterator() {
		ArrayList list = new ArrayList(Arrays.asList(values));
		Iterator i = set.iterator();
		Object value;
		for (int j=0; j<SIZE; j++) {
			assertTrue(i.hasNext());
			value = i.next();
			assertTrue(value != null);
			boolean ok = list.remove(value);
			assertTrue (ok);			
		}
		assertTrue(!i.hasNext());
		assertTrue(list.isEmpty());
		
		try {
			value = i.next();
			fail("Should throw NoSuchElementException");
		}
		catch (NoSuchElementException e) {
			// that's fine
		}
	}

	public void testDeletingIterator() {
		Object value;
		Iterator i = set.iterator();
		value = i.next();
		value = i.next();

		if (allowsRemove) {
			ArrayList list = new ArrayList(Arrays.asList(values));
			i.remove();
			list.remove(value);
			
			try {
				i.remove();
				fail("Should throw IllegalStateException");
			}
			catch (IllegalStateException e) {
				// that's fine
			}

			value = i.next();
			i.remove();
			list.remove(value);

			assertTrue( set.size() == 2);
			assertTrue( list.size() == 2);
			assertTrue( set.containsAll(list));
			assertTrue( list.containsAll(set));
		}
		else {
			try {
				i.remove();
				fail();
			}
			catch (UnsupportedOperationException e) {
				// that's fine
			}
			value = i.next();
		}
		
		value = i.next();
		try {
			value = i.next();
			fail("Should throw NoSuchElementException");
		}
		catch (NoSuchElementException e) {
			// that's fine
		}
	}

	public void testConcurrentDeletingiterator() {
		if (!allowsRemove)	return;
		
		ArrayList list = new ArrayList(Arrays.asList(values));
		Object value;
		Iterator i = set.iterator();
		value = i.next();
		i.remove();
		list.remove(value);
		
		set.remove(list.get(0));
		list.remove(0);
		try {
			value = i.next();
			fail("Should throw ConcurrentModificationException");
		}
		catch (ConcurrentModificationException e) {
			// that's fine
		}

		assertTrue( set.containsAll(list));
		assertTrue( list.containsAll(set));		
	}


	/*
	public void testHashCode() {
	}

	public void testEquals() {
	}

	public void testRemoveAll() {
	}

	public void testToArray() {
	}

	public void testAddAll() {
	}

	public void testRetainAll() {
	}

	public void testToArrayObjectArray() {
	}
	*/
}
