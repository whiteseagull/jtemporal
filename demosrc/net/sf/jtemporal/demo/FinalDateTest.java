/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2004 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.demo;

import junit.framework.TestCase;

/**
 * @author Thomas A Beck
 * @version $Id: FinalDateTest.java,v 1.3 2005/05/14 16:37:21 tabeck Exp $
 */
public class FinalDateTest extends TestCase {

	/**
	 * Constructor for FinalDateTest.
	 * @param name
	 */
	public FinalDateTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FinalDateTest.class);
	}

	/*
	 * Test for boolean equals(Object)
	 */
	public void testEqualsObject() {
		assertTrue(FinalDate.POSITIVE_INFINITY.equals(FinalDate.POSITIVE_INFINITY));
	}
	
	public void testCompareTo() {
		assertTrue(FinalDate.POSITIVE_INFINITY.compareTo(FinalDate.NEGATIVE_INFINITY) > 0);
		
		FinalDate date = FinalDate.valueOf("2004-01-01");
		FinalDate date2 = FinalDate.valueOf("2004-01-01");
		
		assertTrue(FinalDate.POSITIVE_INFINITY.compareTo(date) > 0);
		assertTrue(date.compareTo(FinalDate.POSITIVE_INFINITY) < 0);
		assertTrue(FinalDate.NEGATIVE_INFINITY.compareTo(date) < 0);
		assertTrue(date.compareTo(FinalDate.NEGATIVE_INFINITY) > 0);
		assertEquals(date, date2);
		assertTrue(date.compareTo(date2) == 0);
		assertTrue(date.hashcode() == date2.hashcode());
		assertTrue(date == date2);
	}

	/*
	 * Test for String toString()
	 */
	public void testToString() {
		FinalDate date = FinalDate.valueOf("2004-01-01");
		assertTrue(date.toString().equals("2004-01-01"));
	}

}
