/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2006 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.sf.jtemporal.SimpleTimedObject;
import net.sf.jtemporal.TestConstants;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.util.CloseableIterator;
import junit.framework.TestCase;

/**
 * @author Thomas A Beck
 * @version $Id: AbstractTemporalSetStorageTest.java,v 1.5 2008/06/22 19:20:03 tabeck Exp $
 */
public abstract class AbstractTemporalSetStorageTest extends TestCase 
implements TestConstants
{

	protected abstract TemporalSetStorage newStorage();
	
	private TemporalSetStorage storage;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.storage = this.newStorage(); 
        this.populate();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    private void populate() {
        this.storage.put(p13, s1);
        this.storage.put(p13, s2);
        this.storage.put(p34, s1);
        this.storage.put(p24, s3);        
    }

    public void testLastPeriod() {
        assertEquals(this.storage.lastPeriod(s1), p34);
        assertEquals(this.storage.lastPeriod(s2), p13);
        assertEquals(this.storage.lastPeriod(s3), p24);
        try {
            assertEquals(this.storage.lastPeriod(s4), null);
        } catch (NoSuchElementException e) {
            return;
        }
        fail();
    }

    public void testFirstPeriod() {
        assertEquals(this.storage.firstPeriod(s1), p13);
        assertEquals(this.storage.firstPeriod(s2), p13);
        assertEquals(this.storage.firstPeriod(s3), p24);
        try {
            assertEquals(this.storage.firstPeriod(s4), null);
        } catch (NoSuchElementException e) {
            return;
        }
        fail();
    }

    /*
     * Class under test for boolean isEmpty()
     */
    public void testIsEmpty() {
        assertFalse(this.storage.isEmpty());
        assertTrue(this.storage.size() == 4);
        this.storage.clear();
        assertTrue(this.storage.isEmpty());
        assertTrue(this.storage.size() == 0);
        this.storage.put(p13, s1);
        assertFalse(this.storage.isEmpty());
        assertTrue(this.storage.size() == 1);
    }

    /*
     * Class under test for boolean isEmpty(Instant)
     */
    public void testIsEmptyInstant() {
        assertFalse(this.storage.isEmpty(i1));
        assertFalse(this.storage.isEmpty(i2));
        assertFalse(this.storage.isEmpty(i3));
        assertTrue(this.storage.isEmpty(i4));
        this.storage.clear();
        assertTrue(this.storage.isEmpty(i1));
    }

    /*
     * Class under test for boolean containsValue(Object)
     */
    public void testContainsValueObject() {
        assertTrue(this.storage.containsValue(s1));
        assertTrue(this.storage.containsValue(s2));
        assertTrue(this.storage.containsValue(s3));
        assertFalse(this.storage.containsValue(s4));
    }

    /*
     * Class under test for boolean containsValue(Instant, Object)
     */
    public void testContainsValueInstantObject() {
        assertTrue(this.storage.containsValue(i1, s1));
        assertTrue(this.storage.containsValue(i2, s1));
        assertTrue(this.storage.containsValue(i3, s1));
        assertFalse(this.storage.containsValue(i4, s1));
        assertFalse(this.storage.containsValue(i5, s1));
        
        assertTrue(this.storage.containsValue(i1, s1));
        assertTrue(this.storage.containsValue(i2, s1));
        assertTrue(this.storage.containsValue(i3, s1));
        assertFalse(this.storage.containsValue(i4, s2));
    }

    public void testGetPeriod() {
    	assertEquals(p13, this.storage.getPeriod(i1, s1));
    	assertEquals(p13, this.storage.getPeriod(i2, s1));
    	assertEquals(p34, this.storage.getPeriod(i3, s1));
    	assertNull(this.storage.getPeriod(i4, s1));
    	assertNull(this.storage.getPeriod(i5, s1));

    	assertEquals(p13, this.storage.getPeriod(i1, s2));
    	assertEquals(p13, this.storage.getPeriod(i2, s2));

    	assertNull(this.storage.getPeriod(i3, s2));
    	assertNull(this.storage.getPeriod(i4, s2));
    	        
    }

    public void testGetPeriodEndingAt() {
    	assertNull(this.storage.getPeriodEndingAt(i1, s1));
    	assertNull(this.storage.getPeriodEndingAt(i2, s1));
    	assertEquals(p13, this.storage.getPeriodEndingAt(i3, s1));
    	assertEquals(p34, this.storage.getPeriodEndingAt(i4, s1));
    	assertNull(this.storage.getPeriodEndingAt(i5, s1));
    }

    public void testGetPeriodStartingAt() {
    	assertNull(this.storage.getPeriodStartingAt(i2, s1));
    	assertNull(this.storage.getPeriodStartingAt(i4, s1));
    	assertEquals(p13, this.storage.getPeriodStartingAt(i1, s1));
    	assertEquals(p34, this.storage.getPeriodStartingAt(i3, s1));
    	assertNull(this.storage.getPeriodStartingAt(i5, s1));
    	
    }

    public void testBeginAtomicOperation() {
    	this.storage.beginAtomicOperation();
    }

    public void testEndAtomicOperation() {
    	this.storage.endAtomicOperation();
    }

    public void testClear() {
    	assertTrue(this.storage.size() == 4);
    	this.storage.clear();
    	assertTrue(this.storage.size() == 0);
    }

    public void testSize() {
    	assertTrue(this.storage.size() == 4);
    }

    public void testSizeFor() {
    	assertEquals(new Integer(this.storage.sizeFor(s1)), new Integer( 2));
    	assertTrue(this.storage.sizeFor("xx") == 0);
    	assertTrue(this.storage.sizeFor(s2) == 1);
    }

    public void testSizeValues() {
    	assertTrue(this.storage.sizeValues() == 3);
    }

    public void testSizeAt() {
    	assertTrue(this.storage.sizeAt(i1) == 2);
    	assertTrue(this.storage.sizeAt(i2) == 3);
    	assertTrue(this.storage.sizeAt(i3) == 2);
    	assertTrue(this.storage.sizeAt(i4) == 0);
    	

    }

    public void testPut() {
    	try {
    		this.storage.put(p13, s1);
    		fail();
    	}
    	catch (IllegalArgumentException e) {
    		// this is fine
    	}
        assertTrue(this.storage.size() == 4);
    }

    public void testRemove() {
    	try {
    		this.storage.remove(p24, s1);
    		fail();
    	}
    	catch (NoSuchElementException e) {
    		// this is fine
    	}
    	
    	this.storage.remove(p13,s2);
    	assertTrue (this.storage.size() == 3);
    }

    public void testRemoveValue() {
    	boolean ok;
    	ok = this.storage.removeValue(s4);
		assertFalse(ok);
    	assertTrue (this.storage.size() == 4);
    	ok = this.storage.removeValue(s1);
    	assertTrue (this.storage.size() == 2);

    }

    /*
     * Class under test for CloseableIterator entryIterator()
     */
    public void testEntryIterator() {
    	/*
    	 *         this.storage.put(p13, s1);
    	 			this.storage.put(p13, s2);
    	 			this.storage.put(p34, s1);
    	 			this.storage.put(p24, s3);        
    	 */
    	ArrayList<TimedObject> list = new ArrayList<TimedObject>();
    	list.add(new SimpleTimedObject(p13, s1));
    	list.add(new SimpleTimedObject(p13, s2));
		list.add(new SimpleTimedObject(p34, s1));
		list.add(new SimpleTimedObject(p24, s3));
		
		CloseableIterator i = this.storage.entryIterator();
		int cnt = 0;
		while (i.hasNext()) {
			TimedObject to = (TimedObject) i.next();
			if (list.contains(to)) cnt++;
			i.remove();
		}
		
		assertTrue(cnt == 4);
		assertTrue(this.storage.isEmpty());

    }

    /*
     * Class under test for CloseableIterator entryIterator(Instant)
     */
    public void testEntryIteratorInstant() {
    }

    /*
     * Class under test for CloseableIterator entryIterator(Period)
     */
    public void testEntryIteratorPeriod() {
    }

    /*
     * Class under test for CloseableIterator entryIterator(Period, Object)
     */
    public void testEntryIteratorPeriodObject() {
    }

    public void testPeriodIterator() {
    }

    /*
     * Class under test for CloseableIterator valueIterator()
     */
    public void testValueIterator() {
        for (Iterator i = storage.valueIterator(); i.hasNext(); ) {
            Object v = i.next();
        }
        storage.clear();
        for (Iterator i = storage.valueIterator(); i.hasNext(); ) {
            Object v = i.next();
        }
        
    }

    /*
     * Class under test for CloseableIterator valueIterator(Instant)
     */
    public void testValueIteratorInstant() {
    }

}
