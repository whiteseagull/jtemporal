/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import junit.framework.TestCase;
import java.util.*;

import net.sf.jtemporal.spi.CompactTemporalSetStorage;

/**
 * @author Thomas A Beck
 * @version $Id: TemporalSetTest.java,v 1.2 2005/07/17 08:39:52 tabeck Exp $
 */
public class TemporalSetTest extends TestCase implements TestConstants
{

  TemporalSet map;
  TemporalSet map2;

  public TemporalSetTest (String testName) {
    super(testName);
  }

  public void setUp() {
    map  = new TemporalSetImpl(new CompactTemporalSetStorage());
    map2  = new TemporalSetImpl(new CompactTemporalSetStorage());
    assertNotNull (map);
    assertNotNull (map2);
    assertTrue (map != map2);
  }

  public void tearDown() {
    //System.out.println("TEARDOWN" /*+ map.size()*/);
    checkConsistency(map);
    checkConsistency(map2);
  }


  public void testShortCycle()  {
    map.put(p14, s1);
    assertEquals( map.extent(s1), p14);
    
    map.put(p34, s2);
    assertEquals( map.extent(s2), p34);

    map.put(p35, s1);

    assertTrue(map.sizeFor(s1) == 1);
    assertTrue(map.sizeFor(s2) == 1);
    assertTrue(map.sizeFor(s3) == 0);

    assertEquals( map.extent(s1), p15);

    map.removeValue(s1);
    assertTrue(map.sizeFor(s1) == 0);
    assertTrue(map.sizeFor(s2) == 1);
    assertTrue(!map.isEmpty());
    assertTrue(!map.containsValue(s1));
    assertTrue( map.containsValue(s2));
  }

  public void testAddAndRemoveOne() {
      map.put(p14, s1);
      map.remove(p12, s1);
      map.remove(p25, s1);
      assertTrue(map.isEmpty());
      
      map.put(p14, s1);
      Iterator i = map.valueSet().iterator();
      assertTrue (i.hasNext());
      assertEquals (i.next(), s1);
      i.remove();
      assertTrue(map.isEmpty());      
  }
  
  public void testPeriodSet() {
    map.put(p12, s1);
    map.put(p45, s1);
    map.put(p78, s1);
    map.put(p23, s3); // just noise
    map.put(p24, s4); // just noise
    map.put(p12, s5); // just noise
    Set set2 = map.periodSet(s2);
    assertTrue(set2.size() == 0);

    Set set1 = map.periodSet(s1);
    assertTrue(set1.size() == 3);
    set1.clear();
    Set values = map.valueSet(i1);
    assertTrue(values.size() == 1);

  }

  public void testInstantSnapshot() {
    map.put(p14, s1);
    map.put(p34, s2);
    map.put(p35, s1);
    map.put(p23, s3);
    map.put(p24, s4);
    map.put(p12, s5);

    Set snapShot = map.valueSet(i2);
    Iterator i = snapShot.iterator();
    assertTrue(i.hasNext());
    String total = "";
    total += (String) i.next();

    assertTrue(i.hasNext());
    total += (String) i.next();

    assertTrue(i.hasNext());
    total += (String) i.next();
    assertEquals (total.length(), "onethreefour".length());

    assertFalse(i.hasNext());

    assertTrue (snapShot.size() == 3);
    assertTrue (!snapShot.isEmpty());

    assertTrue (snapShot.contains("one"));
    assertTrue (snapShot.contains("three"));
    assertTrue (snapShot.contains("four"));
    assertTrue (!snapShot.contains("two"));
  }

  public void testRemovePeriod() {
      map.put(p18, s1);
      map.put(p35, s1);
      map.put(p34, s2);
      map.put(p23, s3);
      map.put(p24, s4);
      map.put(p12, s5);
      //map.removePeriod(p24); TODO TODO **************************
      
  }
  
  /**
   * Checks invariants on a TemporalSet
   * @throws IllegalStateException
   */
  public void checkConsistency(TemporalSet tum) {
  }


} // TreeTemporalSetTest