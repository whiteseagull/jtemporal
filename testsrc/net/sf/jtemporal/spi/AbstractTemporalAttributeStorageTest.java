/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.NoSuchElementException;
import java.util.TreeMap;

import net.sf.jtemporal.SimpleTimedObject;
import net.sf.jtemporal.TestConstants;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;
import junit.framework.TestCase;

/**
 * Generic test case, running a series of test against any implementation
 * of {@linkplain TemporalAttributeStorage}, to check whether it fulfils 
 * the interface contract. 
 * 
 * @author Thomas A Beck
 * @version $Id: AbstractTemporalAttributeStorageTest.java,v 1.8 2008/05/24 08:44:41 tabeck Exp $
 */
public abstract class AbstractTemporalAttributeStorageTest extends TestCase implements TestConstants
{
    /**
     * Provides instances of {@linkplain TemporalAttributeStorage} to be tested.
     * @return the instances to be tested
     */
    protected abstract TemporalAttributeStorage newStorage();
	
	private TemporalAttributeStorage map;

	/**
	 * Constructor for TreeTemporalAttributeStorageTest.
	 * @param name
	 */
	public AbstractTemporalAttributeStorageTest(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		TemporalAttributeStorage map2;
		//map  = new TreeTemporalAttributeStorage();
		
		map  = this.newStorage();
		
		//map2  = this.newStorage();

		assertNotNull (map);
		//assertNotNull (map2);
		//assertTrue (map != map2);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		this.map = null;
	}


	public void testVarious() {
	  map.put(p24, s1);

	  assertTrue( map.getPeriod(i1) == null);
	  assertTrue( map.getPeriod(i2) != null);
	  assertTrue( map.getPeriod(i3) != null);
	  assertTrue( map.getPeriod(i4) == null);
	  
	  assertTrue(map.getValue(i2) == s1);
	  assertTrue(map.getValue(i3) == s1);
	  assertNull(map.getValue(i1));
	  assertNull(map.getValue(i4));
	  assertTrue(map.getPeriod(i2) == p24);
	  assertTrue(map.getPeriod(i3) == p24);

	  //assertTrue( map.containsValue(s1));
	  //assertTrue(!map.containsValue(s2));

	  assertTrue( map.firstPeriod()  == p24);
	  assertTrue( map.lastPeriod()   == p24);
	}
	
	/*
	 * Test for Period lastPeriod()
	 */
	public void testFirstAndLastPeriod() {
		map.put(p23, s1);
		map.put(p45, s2);
		assertTrue( map.firstPeriod() == p23);
		assertTrue( map.lastPeriod()  == p45);
	}

	/*
	 * Test for Period  lastPeriod(Period)
	 * and      Period firstPeriod(Period)
	 */
	public void testFirstAndLastPeriodPeriod() {
		map.put(p24, s1);
		map.put(p46, s2);
		assertEquals( map.firstPeriod(p13), p24 );
		assertEquals( map.firstPeriod(p23), p24 );
		assertEquals( map.firstPeriod(p15), p24 );
		assertEquals( map.firstPeriod(p35), p24 );
		assertEquals( map.firstPeriod(p18), p24 );

		assertEquals( map.lastPeriod(p35), p46);
		assertEquals( map.lastPeriod(p18), p46);
		assertEquals( map.lastPeriod(p34), p24);
		assertEquals( map.lastPeriod(p13), p24);		
	}


	/*
	 * Test for boolean isEmpty()
	 */
	public void testIsEmpty() {
		assertTrue(map.isEmpty());
		map.put(p24, s1);
		assertTrue(!map.isEmpty());
		map.removeEntry(p24);
		assertTrue( map.isEmpty());
	}

	public void testGetValue() {
		map.put(p24, s1);
		map.put(p46, s2);
		
		assertTrue( map.getValue(i1) == null);
		assertTrue( map.getValue(i2) == s1);
		assertTrue( map.getValue(i3) == s1);
		assertTrue( map.getValue(i4) == s2);
		assertTrue( map.getValue(i5) == s2);
		assertTrue( map.getValue(i6) == null);
	}

	public void testGetPeriod() {
		map.put(p24, s1);
		map.put(p46, s2);

		assertEquals( map.getPeriod(i1), null);
		assertEquals( map.getPeriod(i2), p24);
		assertEquals( map.getPeriod(i3), p24);
		assertEquals( map.getPeriod(i4), p46);
		assertEquals( map.getPeriod(i5), p46);
		assertEquals( map.getPeriod(i6), null);
		assertEquals( map.getPeriod(i7), null);
	}

	public void testGetEntryMeetingBeforeInstant() {
		TimedObject t24 = new SimpleTimedObject(p24, s1);
		TimedObject t46 = new SimpleTimedObject(p46, s2);

		map.put(p24, s1);
		map.put(p46, s2);
		
		assertNull  ( map.getEntryEndingAt(i1) );
		assertNull  ( map.getEntryEndingAt(i2) );
		assertNull  ( map.getEntryEndingAt(i3) );
		assertEquals( map.getEntryEndingAt(i4), t24 );
		assertNull  ( map.getEntryEndingAt(i5) );
		assertEquals( map.getEntryEndingAt(i6), t46 );
		assertNull  ( map.getEntryEndingAt(i7) );
	}

	public void testGetEntryMeetingAfterInstant() {
		TimedObject t24 = new SimpleTimedObject(p24, s1);
		TimedObject t46 = new SimpleTimedObject(p46, s2);

		map.put(p24, s1);
		map.put(p46, s2);
		
		assertNull  ( map.getEntryStartingAt(i1) );
		assertEquals( map.getEntryStartingAt(i2), t24 );
		assertNull  ( map.getEntryStartingAt(i3) );
		assertEquals( map.getEntryStartingAt(i4), t46 );
		assertNull  ( map.getEntryStartingAt(i5) );
		assertNull  ( map.getEntryStartingAt(i6) );
		assertNull  ( map.getEntryStartingAt(i7) );
	}


	public void testGetEntry() {
		TimedObject t24 = new SimpleTimedObject(p24, s1);
		TimedObject t46 = new SimpleTimedObject(p46, s2);

		map.put(p24, s1);
		map.put(p46, s2);
		
		assertTrue( map.getEntry(i1) == null);
		assertEquals( map.getEntry(i2), t24);
		assertEquals( map.getEntry(i3), t24);
		assertEquals( map.getEntry(i4), t46);
		assertEquals( map.getEntry(i5), t46);
		assertEquals( map.getEntry(i6), null);
		assertEquals( map.getEntry(i7), null);
	}

	public void testBeginAtomicOperation() {
		this.map.beginAtomicOperation();
	}

	public void testEndAtomicOperation() {
		this.map.endAtomicOperation();
	}

	public void testClear() {
		map.put(p24, s1);
		map.put(p46, s2);
		map.clear();
		assertTrue(map.size() == 0);
		map.clear();
		assertTrue(map.size() == 0);
	}

	public void testSize() {
		assertTrue(map.size() == 0);
		map.put(p24, s1);
		assertTrue(map.size() == 1);
		map.put(p46, s2);
		assertTrue(map.size() == 2);
	}

	/*
	 * Test for boolean isEmpty(Period)
	 */
	public void testIsEmptyPeriod() {
		map.put(p23, s1);
		map.put(p46, s2);
		
		assertTrue( map.isEmpty(p12));
		assertTrue(!map.isEmpty(p23));
		assertTrue( map.isEmpty(p34));
		assertTrue(!map.isEmpty(p45));
		assertTrue(!map.isEmpty(p56));
		assertTrue( map.isEmpty(p68));

		assertTrue(!map.isEmpty(p18));
	}

	public void testContainsValue() {
		map.put(p23, s1);
		map.put(p46, s2);

		assertTrue( map.containsValue(s1));
		assertTrue( map.containsValue(s2));
		assertTrue(!map.containsValue(s3));
	}

	final public void testPut() {
	}

	/*
	 * Test for CloseableIterator entryIterator()
	 */
	public void testEntryIterator() {
		TimedObject t24 = new SimpleTimedObject(p24, s1);
		TimedObject t46 = new SimpleTimedObject(p46, s2);

		map.put(p24, s1);
		map.put(p46, s2);
		
		CloseableIterator i = map.entryIterator();
		assertTrue	( i.hasNext() );
		assertEquals( i.next(), t24);
		assertTrue	( i.hasNext() );
		assertEquals( i.next(), t46);
		assertTrue	(!i.hasNext() );
		i.close();
		
	}

	/*
	 * Test for CloseableIterator entryIterator(Period)
	 */
	public void testEntryIteratorPeriod() {
		TimedObject to;
		CloseableIterator i;
		TimedObject t24 = new SimpleTimedObject(p24, s1);
		TimedObject t46 = new SimpleTimedObject(p46, s2);

		map.put(p24, s1);
		map.put(p46, s2);
		
		i = map.entryIterator(p35);
		assertTrue	( i.hasNext() );
		Object next = i.next();
		assertTrue(
		    "next is actually instance of "+next.getClass().getName()
		    , next instanceof TimedObject
		);
		to = (TimedObject) next;
		assertEquals(to.getPeriod(), p24);
		assertEquals(to.getValue(), s1);
		
		to = (TimedObject) i.next();
		assertEquals(to.getPeriod(), p46);
		assertEquals(to.getValue(), s2);
		
		assertTrue	(!i.hasNext() );
		i.close();
		
		///////////////////////////////////////////////
		
		i = map.entryIterator(p18);
		assertTrue	( i.hasNext() );
		to = (TimedObject) i.next();
		assertEquals(to.getPeriod(), p24);
		assertEquals(to.getValue(), s1);
		
		to = (TimedObject) i.next();
		assertEquals(to.getPeriod(), p46);
		assertEquals(to.getValue(), s2);
		
		assertTrue	(!i.hasNext() );
		i.close();

		///////////////////////////////////////////////
		
		i = map.entryIterator(p14);
		assertTrue	( i.hasNext() );
		to = (TimedObject) i.next();
		assertEquals(to.getPeriod(), p24);
		assertEquals(to.getValue(), s1);
		
		assertTrue	(!i.hasNext() );
		i.close();
		

		///////////////////////////////////////////////
		
		i = map.entryIterator(p46);
		assertTrue	( i.hasNext() );
		to = (TimedObject) i.next();
		assertEquals(to.getPeriod(), p46);
		assertEquals(to.getValue(), s2);
		
		assertTrue	(!i.hasNext() );
		i.close();
		
		///////////////////////////////////////////////
		
		i = map.entryIterator(p12);
		
		assertTrue	(!i.hasNext() );
		i.close();

	}

	/*
	 * Test for CloseableIterator periodIterator()
	 */
	public void testPeriodIterator() {
		map.put(p24, s1);
		map.put(p46, s2);
		
		CloseableIterator i = map.periodIterator();
		assertTrue	( i.hasNext() );
		assertEquals( i.next(), p24);
		assertTrue	( i.hasNext() );
		assertEquals( i.next(), p46);
		assertTrue	(!i.hasNext() );
		i.close();
	}

	/*
	 * Test for CloseableIterator periodIterator(Period)
	 */
	public void testPeriodIteratorPeriod() {
		map.put(p24, s1);
		map.put(p46, s2);

		Period p;
		
		CloseableIterator i = map.periodIterator(p35);
		assertTrue	( i.hasNext() );
		p = (Period) i.next();
		assertEquals(p, p24);
		
		p = (Period) i.next();
		assertEquals(p, p46);
		
		assertTrue	(!i.hasNext() );
		i.close();
		
		///////////////////////////////////////////////
		
		i = map.periodIterator(p18);
		assertTrue	( i.hasNext() );
		p = (Period) i.next();
		assertEquals(p, p24);
		
		p = (Period) i.next();
		assertEquals(p, p46);
		
		assertTrue	(!i.hasNext() );
		i.close();

		///////////////////////////////////////////////
		
		i = map.periodIterator(p14);
		assertTrue	( i.hasNext() );
		p = (Period) i.next();
		assertEquals(p, p24);
		
		assertTrue	(!i.hasNext() );
		i.close();
		

		///////////////////////////////////////////////
		
		i = map.periodIterator(p46);
		assertTrue	( i.hasNext() );
		p = (Period) i.next();
		assertEquals(p, p46);
		
		assertTrue	(!i.hasNext() );
		i.close();
		
		///////////////////////////////////////////////
		
		i = map.periodIterator(p12);
		
		assertTrue	(!i.hasNext() );
		i.close();

	}

	public void testRemoveEntry() {
		map.put(p24, s1);
		map.put(p46, s2);
		
		try {
			map.removeEntry(p35);
			fail();
		}
		catch (NoSuchElementException e) {
			// correct			
		}

		try {
			map.removeEntry(p18);
			fail();
		}
		catch (NoSuchElementException e) {
			// correct			
		}

		map.removeEntry(p24bis);		
		assertTrue(map.size() == 1);
		map.removeEntry(p46bis);		
		assertTrue(map.size() == 0);
	}

	public void testRemoveRange() {
		int cnt=0;
		
		map.put(p24, s1);
		map.put(p46, s2);
		
		cnt = map.removeRange(p35);
		assertTrue(cnt == 2);
		assertTrue(map.isEmpty());
		
		map.put(p24, s1);
		map.put(p46, s2);
		
		cnt = map.removeRange(p18);
		assertTrue(cnt == 2);
		assertTrue(map.isEmpty());
		

		map.put(p12, s1);
		map.put(p23, s2);
		map.put(p34, s3);
		map.put(p45, s4);
		assertTrue(map.size() == 4);

		cnt = map.removeRange(p24bis);		
		assertTrue(cnt == 2);
		assertTrue(map.size() == 2);
		assertTrue(map.getPeriod(i1).equals(p12));
		assertTrue(map.getPeriod(i4).equals(p45));
		
		cnt = map.removeRange(p24bis);		
		assertTrue(cnt == 0);
		assertTrue(map.size() == 2);

	}

	public void testConstructor_SortedMap() {
		new TreeTemporalAttributeStorage(
			new TreeMap(StartComparator.COMPARATOR)
		);

		new TreeTemporalAttributeStorage(
			new TreeMap(
				new StartComparator()
			)
		);
		
		try {
			new TreeTemporalAttributeStorage(new TreeMap());
			fail();
		}
		catch (IllegalArgumentException e) {
		}

		try {
			new TreeTemporalAttributeStorage(null);
			fail();
		}
		catch (IllegalArgumentException e) {
		}

	}

}
