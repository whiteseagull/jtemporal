/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import junit.framework.TestCase;
import java.util.*;

/**
 * @author Thomas A Beck
 * @version $Id: TimeNodeTest.java,v 1.3 2006/02/16 14:23:04 tabeck Exp $
 */

public class TimeNodeTest extends TestCase implements TestConstants
{
  static final TimedObject p14s1 = new SimpleTimedObject(p14, s1);
  static final TimedObject p24s2 = new SimpleTimedObject(p14, s2);
  static final TimedObject p12s6 = new SimpleTimedObject(p12, s6);

  public TimeNodeTest (String testName) {
    super(testName);
  }

  TimeNode tn;
  boolean tnDead;

  protected void setUp() {
    TimeNodeTest.this.tnDead = false;

    NodeParent listener = new NodeParent() {
      public void  nodeDead(TimeNode n) {
	TimeNodeTest.this.tnDead = true;
      }
    };

    tn = new TimeNode(listener, p14s1);
  }

  /*protected void tearDown() {
  }*/

  public void testToString() {
    tn.add(p24s2);
    assertTrue(
      tn.toString().equals("TimeNode:endInstant=400[one,two]")  ||
      tn.toString().equals("TimeNode:endInstant=400[two,one]")
    );
  }

  public void testFullCycle() {
    assertTrue(!tnDead);
    assertTrue(tn.size() == 1);
    assertTrue(tn.getEndInstant().equals(i4));
    Iterator i = tn.iterator();
    assertTrue(i.hasNext());
    assertTrue(i.next() == p14s1);

    tn.add(p24s2);
    assertTrue(!tnDead);
    assertTrue(tn.size() == 2);
    assertTrue(tn.getEndInstant().equals(i4));
    i = tn.iterator();
    assertTrue(i.hasNext());
    TimedObject to = (TimedObject) i.next();
    assertTrue(i.hasNext());
    TimedObject to2 = (TimedObject) i.next();
    assertTrue(
      (to2 == p24s2  &&  to == p14s1 ) ||
      (to2 == p14s1  &&  to == p24s2 )    // in JDK1.3 HashMaps have a different order
    );

    try {
      tn.add(p24s2);
      fail();
    } catch (IllegalStateException e) {
      // that's fine
    }
    try {
      tn.add(p12s6);
      fail();
    } catch (IllegalArgumentException e) {
      // that's fine
    }

    assertTrue(tn.size() == 2);
    i.remove();
    assertTrue(!tnDead);
    assertTrue(tn.size() == 1);
    i = tn.iterator();
    tn.remove((TimedObject) i.next());
    assertTrue( tnDead);

    try {
      tn.size();
      fail();
    } catch (IllegalStateException e) {
      // that's fine
    }
  }
}
