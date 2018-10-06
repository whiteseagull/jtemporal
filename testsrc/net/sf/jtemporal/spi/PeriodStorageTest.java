/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.NoSuchElementException;

import net.sf.jtemporal.Period;
import net.sf.jtemporal.TestConstants;
import net.sf.jtemporal.util.CloseableIterator;
import junit.framework.TestCase;

/**
 * @author Thomas A Beck
 * @version $Id: PeriodStorageTest.java,v 1.1 2005/06/05 21:49:30 tabeck Exp $
 */
public class PeriodStorageTest extends TestCase implements TestConstants {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PeriodStorageTest.class);
	}

	PeriodStorage s = new CompactPeriodStorage();

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
	    // will contain p12, p23, p45, p56
		super.setUp();
		assertTrue (s.isEmpty());
		s.put(p23);
		assertTrue (!s.isEmpty());
		s.put(p12);
		s.put(p45);
		try {
			s.put(p12);
			fail();
		}
		catch (IllegalArgumentException e) {
			// ok
		}
		s.put(p56);
		assertTrue ("is instead:"+s.size(), s.size()  == 4);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	/*
	 * Class under test for Period lastPeriod()
	 */
	public void testLastPeriod() {
		assertEquals(s.lastPeriod(), p56);
	}

	/*
	 * Class under test for Period lastPeriod(Period)
	 */
	public void testLastPeriodPeriod() {
	}

	/*
	 * Class under test for Period firstPeriod()
	 */
	public void testFirstPeriod() {
		assertEquals(s.firstPeriod(), p12);
	}

	/*
	 * Class under test for Period firstPeriod(Period)
	 */
	public void testFirstPeriodPeriod() {
	}

	/*
	 * Class under test for boolean isEmpty()
	 */
	public void testIsEmpty() {
	    assertFalse(s.isEmpty());
	    s.clear();
	    assertTrue(s.isEmpty());
	}

	/*
	 * Class under test for boolean isEmpty(Period)
	 */
	public void testIsEmptyPeriod() {
	}

	public void testGetPeriod() {
	}

	public void testGetPeriodEndingAt() {
		assertNull(s.getPeriodEndingAt(i0));
		assertNull(s.getPeriodEndingAt(i1));
		assertEquals(s.getPeriodEndingAt(i2), p12);
		assertEquals(s.getPeriodEndingAt(i3), p23);
		assertNull(s.getPeriodEndingAt(i4));
		assertEquals(s.getPeriodEndingAt(i5), p45);
		assertEquals(s.getPeriodEndingAt(i6), p56);
		assertNull(s.getPeriodEndingAt(i7));		
	}

	public void testGetPeriodStartingAt() {
		assertNull(s.getPeriodStartingAt(i0));
		assertEquals(s.getPeriodStartingAt(i1), p12);
		assertEquals(s.getPeriodStartingAt(i2), p23);
		assertNull(s.getPeriodStartingAt(i3));
		assertEquals(s.getPeriodStartingAt(i4), p45);
		assertEquals(s.getPeriodStartingAt(i5), p56);
		assertNull(s.getPeriodStartingAt(i6));
		assertNull(s.getPeriodStartingAt(i7));
	}

	public void testClear() {
		s.clear();
		assertTrue(s.isEmpty());
	}

	public void testSize() {
	}

	public void testRemove() {
		try {
			s.remove(p34);
			fail();
		}
		catch (NoSuchElementException e) {
			//ok
		}
		assertEquals(s.getPeriod(i1), p12);
		assertEquals(s.getPeriod(i2), p23);
		assertNull(s.getPeriod(i3));
		assertEquals(s.getPeriod(i4), p45);
		assertEquals(s.getPeriod(i5), p56);
		assertNull(s.getPeriod(i6));

		s.remove(p12);
		
		assertNull(s.getPeriod(i1));
		assertEquals(s.getPeriod(i2), p23);
		assertNull(s.getPeriod(i3));
		assertEquals(s.getPeriod(i4), p45);
		assertEquals(s.getPeriod(i5), p56);
		assertNull(s.getPeriod(i6));

		s.remove(p56);

		assertNull(s.getPeriod(i1));
		assertEquals(s.getPeriod(i2), p23);
		assertNull(s.getPeriod(i3));
		assertEquals(s.getPeriod(i4), p45);
		assertNull(s.getPeriod(i5));
		assertNull(s.getPeriod(i6));

		s.remove(p23);

		assertNull(s.getPeriod(i1));
		assertNull(s.getPeriod(i2));
		assertNull(s.getPeriod(i3));
		assertEquals(s.getPeriod(i4), p45);
		assertNull(s.getPeriod(i5));
		assertNull(s.getPeriod(i6));

		s.remove(p45);

		assertNull(s.getPeriod(i1));
		assertNull(s.getPeriod(i2));
		assertNull(s.getPeriod(i3));
		assertNull(s.getPeriod(i4));
		assertNull(s.getPeriod(i5));
		assertNull(s.getPeriod(i6));
		
		assertTrue(s.isEmpty());
		
		s.put(p25);
		
		assertNull(s.getPeriod(i1));
		assertEquals(s.getPeriod(i2), p25);
		assertEquals(s.getPeriod(i3), p25);
		assertEquals(s.getPeriod(i4), p25);
		assertNull(s.getPeriod(i5));
		assertNull(s.getPeriod(i6));

	}

	/*
	 * Class under test for CloseableIterator periodIterator(Period)
	 */
	public void testPeriodIteratorPeriod() {
	    //	  will contain p12, p23, p45, p56
	    Period p;
	    CloseableIterator i;
	    i = s.periodIterator(p25);
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p23);
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p45);
	    assertFalse(i.hasNext());
	    assertFalse(i.isOpen());
	    /////////////////////////////////////////////
	    i = s.periodIterator(p25);
	    assertTrue (i.hasNext());
	    i.close();
        assertFalse(i.hasNext());
	    assertFalse(i.isOpen());
	    /////////////////////////////////////////////
	    i = s.periodIterator(p25);
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p23);
	    i.remove();
	    //assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p45);
	    i.remove();
	    assertFalse(i.hasNext());
	    assertFalse(i.isOpen());

	    i = s.periodIterator(p25);
	    assertFalse(i.hasNext());
	    /////////////////////////////////////////////

	}

	
	/*
	 * Class under test for CloseableIterator periodIterator(Period)
	 */
	public void testPeriodIteratorPeriod2() {
	    //	  contains p12, p23, p45, p56
	    //   try without hasNext()
	    Period p;
	    CloseableIterator i;
	    i = s.periodIterator(p15);
	    p = (Period) i.next();
	    assertEquals(p, p12);
	    p = (Period) i.next();
	    assertEquals(p, p23);
	    p = (Period) i.next();
	    assertEquals(p, p45);
	    try {
	        p = (Period) i.next();
	        fail();
	    }
	    catch (NoSuchElementException e) {
	        // ok
	    }
	}
	
	/*
	 * Class under test for CloseableIterator periodIterator(Period)
	 */
	public void testPeriodIteratorPeriod3() {
	    //	  contains p12, p23, p45, p56
	    Period p;
	    CloseableIterator i;
	    i = s.periodIterator(p12);
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p12);
	    i.remove();
	    assertFalse(i.hasNext());
	    assertFalse(i.isOpen());
	    /////////////////////////////////////////////
	    //	  contains p23, p45, p56
	    i = s.periodIterator(p15);
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p23);
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p45);
	    i.remove();
	    assertFalse(i.hasNext());
	    i.close();
	    /////////////////////////////////////////////
	    //	  contains p23, p56
	    i = s.periodIterator(p48);
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p56);
	    assertFalse (i.hasNext());
	    i.close();
	    /////////////////////////////////////////////
	    s.clear();
	    s.put(p24);
	    //	  contains p24
	    i = s.periodIterator(p13);
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p24);
	    assertFalse (i.hasNext());

	    i = s.periodIterator(p35);
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p24);
	    assertFalse (i.hasNext());
	    /////////////////////////////////////////////
	    //	  contains p24
	    i = s.periodIterator(p12);
	    assertFalse (i.hasNext());
	    i = s.periodIterator(p45);
	    assertFalse (i.hasNext());
	    /////////////////////////////////////////////
	    s.clear();
	    i = s.periodIterator(p12);
	    assertFalse (i.hasNext());	
	}

	
	/*
	 * Class under test for CloseableIterator periodIterator()
	 */
	public void testPeriodIterator() {
	    //	  will contain p12, p23, p45, p56
	    //	  will contain p12, p23, p45, p56
	    Period p;
	    CloseableIterator i;
	    i = s.periodIterator();
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p12);
	    assertTrue (i.hasNext());
	    p = (Period) i.next();	    
	    assertEquals(p, p23);
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p45);
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p56);
	    assertFalse(i.hasNext());
	    assertFalse(i.isOpen());
	    /////////////////////////////////////////////
	    i = s.periodIterator();
	    assertTrue (i.hasNext());
	    i.close();
        assertFalse(i.hasNext());
	    assertFalse(i.isOpen());
	    /////////////////////////////////////////////
	    i = s.periodIterator();
	    assertTrue(i.isOpen());
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p12);
	    i.remove();
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p23);
	    i.remove();
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p45);
	    i.remove();
	    assertTrue (i.hasNext());
	    p = (Period) i.next();
	    assertEquals(p, p56);
	    i.remove();
	    assertFalse(i.hasNext());
	    assertFalse(i.isOpen());
	    assertTrue("size:"+s.size(), s.isEmpty());
	    
	    i = s.periodIterator();
	    assertFalse(i.hasNext());
	}

}
