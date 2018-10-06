/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
 Copyright (C) 2002 by the author(s).
 Distributable under LGPL license version 2.1 or later, 
 with NO WARRANTIES given or implied.
 See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import junit.framework.*;

/**
 * Complete test suite
 * @author Thomas A Beck
 * @version $Id: AllTests.java,v 1.10 2005/10/29 16:33:57 tabeck Exp $
 */
public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
		//junit.swingui.TestRunner.run(AllTests.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite() {
			public String toString() {
				return "JTemporal Root Test Suite";
			}
		};

		//$JUnit-BEGIN$
		suite.addTestSuite(TemporalAttributePeriodSetTest.class);
		suite.addTestSuite(TreeTemporalSetTest.class);
		suite.addTestSuite(TreeTemporalAttributeTest.class);
		suite.addTestSuite(PeriodTest.class);
		suite.addTestSuite(TemporalAttributeImplTest.class);
		suite.addTestSuite(CachedTimeSeriesTest.class);
		suite.addTestSuite(TemporalAttributeEntrySetTest.class);
		suite.addTestSuite(TimeNodeTest.class);
		suite.addTestSuite(TemporalSetTest.class);
		//$JUnit-END$

		// util package
		suite.addTest(net.sf.jtemporal.util.AllTests.suite());
		// spi package
		suite.addTest(net.sf.jtemporal.spi.AllTests.suite());

		return suite;
	}

}