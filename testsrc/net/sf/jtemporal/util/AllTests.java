/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
 Copyright (C) 2002 by the author(s).
 Distributable under LGPL license version 2.1 or later, 
 with NO WARRANTIES given or implied.
 See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import junit.framework.*;

/**
 * Test suite for the util package
 * @author Thomas A Beck
 * @version $Id: AllTests.java,v 1.7 2008/12/14 14:31:13 tabeck Exp $
 */
public class AllTests //extends TestCase
{
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for net.sf.jtemporal.util");
		//$JUnit-BEGIN$
		suite.addTestSuite(SerializableSingletonTest.class);
		suite.addTestSuite(LRUCacheTest.class);
		suite.addTestSuite(HashSetTest.class);
		suite.addTestSuite(SimpleCloseableIteratorTest.class);
		//$JUnit-END$
		return suite;
	}

}