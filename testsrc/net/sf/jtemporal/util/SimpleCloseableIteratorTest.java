/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.jtemporal.TestConstants;

import junit.framework.TestCase;

/**
 * @author Thomas A Beck
 * @version $Id: SimpleCloseableIteratorTest.java,v 1.1 2007/05/27 10:05:18 tabeck Exp $
 */
public class SimpleCloseableIteratorTest extends TestCase implements TestConstants 
{
    
    List list;
    SimpleCloseableIterator i;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        list = new ArrayList(3);
        list.add(s1);
        list.add(s2);
        list.add(s3);
        i = new SimpleCloseableIterator(list.iterator());
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        list = null;
    }

    public void testClear() {
        assertTrue(i.isOpen());
        while (i.hasNext()) {
            i.next();
            i.remove();
        }
        assertFalse(i.isOpen());
        i.close();
        assertFalse(i.isOpen());
    }
    
    public void testGetLastFetched() {
        try {
            i.getLastFetched();
            fail();
        }
        catch (IllegalStateException e) {
            // fine
        }
        Object o = i.next();
        assertTrue(o == s1);
        assertTrue(o == i.getLastFetched());
        i.remove();
        try {
            i.getLastFetched();
            fail();
        }
        catch (IllegalStateException e) {
            // fine
        }
        try {
            i.remove();
            fail();
        }
        catch (IllegalStateException e) {
            // fine
        }
    }



}
