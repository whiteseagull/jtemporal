/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import junit.framework.TestCase;

/**
 * @author Thomas A Beck
 * @version $Id: PeriodTest.java,v 1.8 2007/09/14 20:03:12 tabeck Exp $
 */

public class PeriodTest extends TestCase implements TestConstants
{


  public PeriodTest (String testName) {
    super(testName);
  }

/*protected void setUp() {
  }
  protected void tearDown() {
  }*/

  public void testToString() {
    assertTrue(p34.toString().equals("Period:(300,400)"));
  }

  public void testInstant() {
    assertTrue (i1.compareTo(i1bis) == 0);
    assertTrue (i1.hashCode() == i1bis.hashCode());
    assertTrue (i1.equals(i1bis));

    assertTrue (i2.compareTo(i1bis) != 0);
    assertTrue (i2.hashCode() != i1bis.hashCode());
    assertTrue (!i2.equals(i1bis));
  }

  public void testNewPeriod() {
    try {
      new Period (i2, i1);
    }
    catch (IllegalArgumentException e) {
      return;
    }
    fail ("Missed exception");

    try {
      new Period (i3, i3);
    }
    catch (IllegalArgumentException e) {
      return;
    }
    fail ("Missed exception");

    assertNotNull(p12);

    assertTrue(p12.getStart().equals(i1));
    assertTrue(p12.getEnd().equals(i2));
  }

  public void testEquals() {
    assertEquals(p34, p34);
    assertEquals(p34, p34bis);
    assertTrue(! p12.equals(p13));
    assertTrue(! p14.equals(p34));
  }

  public void testCompareTo() {
    assertTrue(p12.compareTo(p34) < 0);
    assertTrue(p34.compareTo(p34) == 0);
    assertTrue(p34.compareTo(p34bis) == 0);
    assertTrue(p34.compareTo(p12) > 0);
  }

  public void testPrecedes()  {
    assertTrue( p23.precedes(p34));
    assertTrue(!p23.precedes(p12));
    assertTrue(!p14.precedes(p23));
    assertTrue( p12.precedes(p34));
    assertTrue( p13.precedes(p34));
    assertTrue(!p14.precedes(p14));
    assertTrue(!p34.precedes(p34bis));
  }

  public void testSucceeds()  {
    assertTrue( p34.succeeds(p23));
    assertTrue(!p12.succeeds(p23));
    assertTrue(!p23.succeeds(p14));
    assertTrue( p34.succeeds(p12));
    assertTrue( p34.succeeds(p13));
    assertTrue(!p14.succeeds(p14));
    assertTrue(!p34.succeeds(p34bis));
  }

  public void testMeetsBefore()  {
    assertTrue( p23.meetsBefore(p34));
    assertTrue(!p34.meetsBefore(p23));
    assertTrue(!p23.meetsBefore(p12));
    assertTrue(!p14.meetsBefore(p23));
    assertTrue(!p12.meetsBefore(p34));
    assertTrue( p13.meetsBefore(p34));
    assertTrue(!p14.meetsBefore(p14));
  }

  public void testContains()   {
    assertTrue( p14.contains(p23));
    assertTrue( p14.contains(p13));
    assertTrue( p14.contains(p24));
    assertTrue( p14.contains(p14));
    assertTrue(!p23.contains(p14));
    assertTrue(!p23.contains(p24));
    assertTrue(!p23.contains(p13));

    assertTrue(!p24.contains(i1));
    assertTrue( p24.contains(i2));
    assertTrue( p24.contains(i3));
    assertTrue(!p24.contains(i4));
    
    assertTrue( ALWAYS.contains(i1));
	assertTrue( ALWAYS.contains(IntInstant.NEGATIVE_INFINITY));
	assertTrue( ALWAYS.contains(IntInstant.POSITIVE_INFINITY));
	
	assertTrue( ALWAYS.contains(ALWAYS));
  }

  public void testOverlaps()  {
    assertTrue( p13.overlaps(p24));
    assertTrue( p24.overlaps(p13));

    assertTrue(!p13.overlaps(p34));
    assertTrue(!p34.overlaps(p13));

    assertTrue( p14.overlaps(p23));
    assertTrue( p23.overlaps(p14));

    assertTrue(!p12.overlaps(p34));
    assertTrue(!p34.overlaps(p12));
  }

  public void testIntersect()  {
    assertEquals(p13.intersect(p24), p23);
    assertEquals(p24.intersect(p13), p23);

    assertTrue(p14.intersect(p23) == p23);
    assertTrue(p23.intersect(p14) == p23);

    assertNull(p13.intersect(p34));
    assertNull(p34.intersect(p13));
  }

  public void testUnion()  {
    assertEquals(p12.union(p24), p14);
    assertEquals(p23.union(p12), p13);
    try {
      assertEquals(p13.union(p12), p13);
      fail();
    } catch (IllegalArgumentException e) {
      // that's fine
    }
  }

  public void testExcept()  {
	assertNull(p12.except(p12));

    try {
      p14.except(p23);
      fail();
    } catch (IllegalArgumentException e) {
      // that's fine
    }
    
    assertNull(p23.except(p14));

    assertEquals(p13, p14.except(p34));
    assertEquals(p34, p14.except(p13));
    assertEquals(p12, p13.except(p24));
    assertEquals(p34, p24.except(p13));
  }

  public void testPreceding() {
  	assertNull(p34.precedingPeriod(p25));
  	assertNull(p23.precedingPeriod(p24));
  	assertTrue(p12.precedingPeriod(p23) == p12);
  	assertTrue(p12.precedingPeriod(p34) == p12);
  	assertEquals(p15.precedingPeriod(p23), p12);
  	assertEquals(p15.precedingPeriod(p35), p13);
  	assertEquals(p15.precedingPeriod(p46), p14);
  }
  
  public void testSucceding() {
  	assertNull(p34.succedingPeriod(p25));
  	assertNull(p34.succedingPeriod(p24));
  	assertTrue(p23.succedingPeriod(p12) == p23);
  	assertTrue(p34.succedingPeriod(p12) == p34);
  	assertEquals(p15.succedingPeriod(p23), p35);
  	assertEquals(p15.succedingPeriod(p12), p25);
  	assertEquals(p46.succedingPeriod(p15), p56);
  }
}
