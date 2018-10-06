/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.*;

import junit.framework.TestCase;
import net.sf.jtemporal.spi.*;

/**
 * @author Thomas A Beck
 * @version $Id: TemporalAttributeImplTest.java,v 1.10 2007/05/27 08:43:19 tabeck Exp $
 */

public class TemporalAttributeImplTest extends TestCase implements TestConstants
{

  TemporalAttribute map;
  TemporalAttribute map2;
  PutCounterStorage putCounterStorage;

  /* Author Didier Dubois */
  private class PutCounterStorage extends TemporalAttributeStorageProxy
  {
		int putCount = 0;
		int removeCount = 0;
		boolean log = false;

		public void removeEntry(Period p) {
			removeCount++;
			if (log)
				System.out.println("DEL Ent: " + p + " -" + getValue(p.getStart()))
		    ;
			super.removeEntry(p);
		}

		public int removeRange(Period p) {
			removeCount++;
			if (log)
				System.out.println("DEL Ran: " + p + " -" + getValue(p.getStart()))
			;
			return super.removeRange(p);
		}

		public PutCounterStorage(TemporalAttributeStorage storage) {
			super(storage);
		}

		public void put(Period p, Object value) {
			putCount++;
			if (log)
				System.out.println("ADD Ran: " + p + " -" + value)
			;
			super.put(p, value);
		}
  }
  
  //TreeMapCopy mapCopy;

  public TemporalAttributeImplTest (String testName) {
    super(testName);
  }

  public void setUp() {
    //mapCopy = new TreeMapCopy();
//  map  = new TreeTemporalAttribute(String.class);
//    map  = new TreeTemporalAttribute(String.class, mapCopy);
    
    putCounterStorage = new PutCounterStorage(
      	new TreeTemporalAttributeStorage()
    );
      
    map = new TemporalAttributeImpl(
    	putCounterStorage
    );


	map2 = new TemporalAttributeImpl(
		new TreeTemporalAttributeStorage()
	);

    assertNotNull (map);
    assertNotNull (map2);
    assertTrue (map != map2);
  }

  public void tearDown() {
    //System.out.println("TEARDOWN" + map.getInternalMap().size());
    checkConsistency(map);
  }

  public void testEqualContent() {
 
    assertTrue(map.equals(map));
    map.put(p12, s1);
    map.put(p34, s2);
    assertTrue(map.equals(map));
    assertTrue(!map.equals(map2));
    map2.put(p12, s1);
    assertTrue(!map.equals(map2));
    map2.put(p34, s2);
    assertTrue(map.equals(map2));
 
  }

  public void testSubMap() {
    map.put(p13, s1);
    checkConsistency(map);
    map.put(p46, s2);
    checkConsistency(map);
    TemporalAttribute sub = map.subMap(p25);
    checkConsistency(sub);
    checkConsistency(map);
    assertTrue (sub.size() == 2);
    assertTrue( sub.firstPeriod().equals(p23));
    assertTrue( sub.lastPeriod().equals(p45));
    sub.clear();
    checkConsistency(sub);
    checkConsistency(map);
    sub.put(p34, s3);
    checkConsistency(sub);
    checkConsistency(map);
    sub.put(p34, s4);
    checkConsistency(sub);
    checkConsistency(map);
    assertTrue(map.get(i1) == s1);
    assertTrue(map.get(i2) == null);
    assertTrue(map.get(i3) == s4);
    assertTrue(map.get(i4) == null);
    assertTrue(map.get(i5) == s2);

    assertTrue (!sub.isEmpty());

    assertTrue (sub.size() == 1);
  }

  
  /*
   * @deprecated
  public void testTypeSafe()  {
    map2.put(p12, new Integer(27));

    map.put(p12, s1);
    try {
      map.put(p12, new Integer(27));
    }
    catch (ClassCastException e) {
      return;
    }
    fail();
  }
   */


  public void testVarious() {
    assertTrue(map.size() == 0);
    assertTrue(map.isEmpty());
    map.put(p24, s1);
    assertTrue(map.size() == 1);
    assertTrue(!map.isEmpty());
    assertTrue(!map.containsInstant(i1));
    assertTrue( map.containsInstant(i2));
    assertTrue( map.containsInstant(i3));
    assertTrue(!map.containsInstant(i4));
    assertTrue(map.get(i2) == s1);
    assertTrue(map.get(i3) == s1);
    assertNull(map.get(i4));
    assertTrue(map.getPeriod(i2) == p24);
    assertTrue(map.getPeriod(i3) == p24);
    assertNull(map.getPeriod(i4));

    assertTrue( map.containsValue(s1));
    assertTrue(!map.containsValue(s2));

    assertTrue( map.firstInstant() == i2);
    assertTrue( map.lastInstant()  == i4);
    assertTrue( map.firstPeriod()  == p24);
    assertTrue( map.lastPeriod()   == p24);
  }
  
  public void testLargeOverwrite() {
  	map.put(p13, s1);
  	map.put(p34, s2);
  	map.put(p46, s3);
  	
  	map.put(p25, s4); // large overwrite here
  	
  	assertTrue(map.get(i1) == s1);
	assertTrue(map.get(i2) == s4);
	assertTrue(map.get(i3) == s4);
	assertTrue(map.get(i4) == s4);
	assertTrue(map.get(i5) == s3);
	assertTrue(map.get(i6) == null);
  }
  
  public void testSmallMiddleOverwrite() {
  	map.put(p14, s1);
  	map.put(p23, s2); // small overwrite here
  	
	assertTrue(map.get(i1) == s1);
	assertTrue(map.get(i2) == s2);
	assertTrue(map.get(i3) == s1);
	assertTrue(map.get(i4) == null);
  }
  
  public void testSmallBeginningOverwrite() {
  	map.put(p24, s1);
  	map.put(p13, s2);
  	
	assertTrue(map.get(i1) == s2);
	assertTrue(map.get(i2) == s2);
	assertTrue(map.get(i3) == s1);
	assertTrue(map.get(i4) == null);
  }

  public void testPeriodSet() {
    map.put(p24, s1);
    Set set = map.periodSet();

    //System.err.println(map);
    //System.err.println("SET:" + set);

    try {
      set.add(s4);
      fail();
    } catch (UnsupportedOperationException e) {
      // ok
    }

    assertTrue( set.contains(p24));
    assertTrue(!set.contains(p25));

    map.put(p46, s2);

    assertTrue( set.contains(p46));

    Iterator i = set.iterator();
    assertTrue (i.hasNext());
    assertEquals (i.next(), p24);
    assertEquals (i.next(), p46);
    i.remove();
    assertTrue(!set.contains(p46));
    assertTrue( set.contains(p24));
    assertTrue(map.size() == 1);

    try {
      set.clear();
    } catch (UnsupportedOperationException e) {
      fail();
    }
    
    assertTrue(map.size() == 0);
  }
  
  public void testRemove() {
    boolean ret;
    map.put(p13, s1);
    map.put(p34, s2);
    map.put(p46, s3);
    ret = map.remove(p25);
    assertTrue(ret);
    ret = map.remove(p34);
    assertTrue(ret == false);
    ret = map.remove(p25);
    assertTrue(ret == false);
    assertTrue(map.size() == 2);
    assertTrue(!map.isEmpty());
    assertTrue( map.containsInstant(i1));
    assertTrue(!map.containsInstant(i2));
    assertTrue(!map.containsInstant(i3));
    assertTrue(!map.containsInstant(i4));
    assertTrue( map.containsInstant(i5));
    assertTrue(!map.containsInstant(i6));
    assertEquals(map.getPeriod(i1), p12);
    assertEquals(map.getPeriod(i5), p56);
    assertNull(map.getPeriod(i4));
    assertTrue(map.get(i1) == s1);
    assertTrue(map.get(i5) == s3);
  }


  public void testUpdate() {
    boolean ret;
    ret = map.put(p13, s1);
    assertTrue(ret == false);
    ret = map.put(p34, s2);
    assertTrue(ret == false);
    ret = map.put(p46, s3);
    assertTrue(ret == false);
    ret = map.put(p25, s4);
    assertTrue(ret);
    ret = map.put(p34, s5);
    assertTrue(ret);
    ret = map.put(p25, s6);
    assertTrue(ret);
    //System.out.println(map);
    assertTrue(map.size() == 3);
    assertTrue(!map.isEmpty());
    assertTrue( map.containsInstant(i1));
    assertTrue( map.containsInstant(i2));
    assertTrue( map.containsInstant(i3));
    assertTrue( map.containsInstant(i4));
    assertTrue( map.containsInstant(i5));
    assertTrue(!map.containsInstant(i6));
    assertEquals(map.getPeriod(i1), p12);
    assertEquals(map.getPeriod(i5), p56);
    assertEquals(map.getPeriod(i4), p25);
    assertTrue(map.get(i1) == s1);
    assertTrue(map.get(i2) == s6);
    assertTrue(map.get(i3) == s6);
    assertTrue(map.get(i4) == s6);
    assertTrue(map.get(i5) == s3);
    assertTrue(map.get(i6) == null);
  }
  
  public void testMerge() {
	  map.put(p18, s1);
	  map.put(p34, s2);
	  map.put(p34, s1bis);
	  TimedObject to = map.getEntry(i1);
	  assertEquals(to.getPeriod(), p18);
	  assertTrue(to.getValue() == s1);      
  }

  public void testMerge2() {
	  map.put(p14, s1);
	  map.put(p46, s2);
	  map.put(p35, s1bis);
	  TimedObject to = map.getEntry(i1);
	  assertEquals(to.getPeriod(), p15);
	  assertTrue(to.getValue() == s1);      
  }


  public void testFirstLast() {
    try {
      Instant i = map.firstInstant();
      fail();
    } catch (NoSuchElementException e) {
      // fine
    }

    try {
      Instant i = map.lastInstant();
      fail();
    } catch (NoSuchElementException e) {
      // fine
    }

    try {
      Period p = map.firstPeriod();
      fail();
    } catch (NoSuchElementException e) {
      // fine
    }

    try {
      Period p = map.lastPeriod();
      fail();
    } catch (NoSuchElementException e) {
      // fine
    }

    try {
      Period p = map.extent();
      fail();
    } catch (NoSuchElementException e) {
      // fine
    }

    map.put(p24, s3);
    map.put(p35, s1);
    assertTrue(map.firstInstant() == i2);
    assertTrue(map.lastInstant() == i5);
    assertTrue(map.firstPeriod().equals(p23));
    assertTrue(map.lastPeriod()  == p35);
    assertTrue(map.extent().equals(p25));
  } // testFirstLast()

  public void testLoopForward() {
    map.put(p12, s1);
    map.put(p23, s2);
    map.put(p34, s3);
    map.put(p46, s4);

    TemporalAttribute um = map;
    Period p;

    assertEquals(um.firstInstant(), i1);
    p = um.getPeriod(i1);
    assertEquals(p, p12);
    um = map.subMap(new Period(p.getEnd(), infinite));
    assertTrue (um.size() == 3);

    assertEquals(um.firstInstant(), i2);
    p = um.getPeriod(i2);
    assertEquals(p, p23);
    um = map.subMap(new Period(p.getEnd(), infinite));
    assertTrue (um.size() == 2);

    assertEquals(um.firstInstant(), i3);
    p = um.getPeriod(i3);
    assertEquals(p, p34);
    um = map.subMap(new Period(p.getEnd(), infinite));
    assertTrue (um.size() == 1);

    assertEquals(um.firstInstant(), i4);
    p = um.getPeriod(i4);
    assertEquals(p, p46);
    um = map.subMap(new Period(p.getEnd(), infinite));
    assertTrue (um.size() == 0);
  } // testLoopForward()


  public void testLoopBackwards() {
    map.put(p12, s1);
    map.put(p23, s2);
    map.put(p34, s3);
    map.put(p46, s4);

    TemporalAttribute um = map;
    Period p;

    p = um.lastPeriod();
    assertEquals(p, p46);
    um = map.subMap(new Period(i0, p.getStart()));
    assertTrue (um.size() == 3);

    p = um.lastPeriod();
    assertEquals(p, p34);
    um = map.subMap(new Period(i0, p.getStart()));
    assertTrue (um.size() == 2);

    p = um.lastPeriod();
    assertEquals(p, p23);
    um = map.subMap(new Period(i0, p.getStart()));
    assertTrue (um.size() == 1);

    p = um.lastPeriod();
    assertEquals(p, p12);
    um = map.subMap(new Period(i0, p.getStart()));
    assertTrue (um.size() == 0);
  } // testLoopBackwards()

  public void testInfinities() {
	map.put(ALWAYS, s2);
	assertTrue(map.get(i1) == s2);
	assertTrue(map.get(IntInstant.NEGATIVE_INFINITY) == s2);
	assertTrue(map.get(IntInstant.POSITIVE_INFINITY) == s2);
  }

  public void testRewriteSame() {
  	map.put(ALWAYS, s2);
	assertTrue(map.get(i1) == s2);
	int initialCount = this.putCounterStorage.putCount;
	map.put(p12, s2);
	map.put(ALWAYS, s2);
	int newCount = this.putCounterStorage.putCount;
	assertTrue(
	    "count changed from "+initialCount+" to "+this.putCounterStorage.putCount,
	    initialCount == newCount
	);
  }


	/**
	 * Author : Didier Dubois
	 * <pre>
	 *
	 * ORGIN
	 * 
	 * s3                       |--------------------- 
	 *                          | 
	 * s2 ----------------------|
	 *                          i3
	 * 
	 * INSERT:
	 * 
	 * s2               |-------|
	 *                  i1      i3
	 *                  
	 * Should be the same.                  
	 * s3                       |--------------------- 
	 *                          | 
	 * s2 ----------------------|
	 *                  i1      i3
	 * </pre>
	 * 
	 * 
	 */
  public void testRewriteSame2() {
		map.put(ALWAYS, s2);
		assertTrue(map.get(i1) == s2);

		map.put(new Period(i3, infinite), s3);
		assertTrue(map.get(i1) == s2);
		assertTrue(map.get(i4) == s3);

		int initialCount = this.putCounterStorage.putCount;
		//this.putCounterStorage.log = true;
		map.put(p13, s2);
		int newCount = this.putCounterStorage.putCount;
		assertTrue("count changed from " + initialCount + " to "
			+ this.putCounterStorage.putCount, initialCount == newCount
		);
	}

  
  
  /**
   * Checks invariants on a TemporalAttribute
   * @throws IllegalStateException
   */
	public static void checkConsistency(TemporalAttribute ta) {
		int size = ta.size();
		Iterator it = ta.periodSet().iterator();
		Period p, p2 = null;
		for (int i = 1; i <= size; i++) {
			if (!it.hasNext())
				throw new IllegalStateException("Outer iterator has no next");
			p = (Period) it.next();
			Iterator it2 = ta.periodSet().iterator();
			while (it2.hasNext()) {
				p2 = (Period) it2.next();
				if (p.equals(p2))
					continue;
				if (p.overlaps(p2))
					throw new IllegalStateException(
						"Found overlapping periods. P:" + p + " p2:" + p2);
			}
	
			if (i == 1) { //check first
				if (!ta.firstPeriod().equals(p))
					throw new IllegalStateException("First period does not match.  first:"
						+ ta.firstPeriod() + " iterator period:" + p);
				if (!ta.lastPeriod().equals(p2))
					throw new IllegalStateException("Last period does not match");
				if (!ta.firstInstant().equals(p.getStart()))
					throw new IllegalStateException("First instant does not match. Instant:"
						+ ta.firstInstant() + ", iterator period :"+p);
				if (!ta.lastInstant().equals(p2.getEnd()))
					throw new IllegalStateException("Last instant does not match. Instant:"
						+ ta.lastInstant() + ", iterator period :"+p2);
			}
		} //for
		if (it.hasNext())
			throw new IllegalStateException("size does not match outer iterator");
	} // checkConsistency

}