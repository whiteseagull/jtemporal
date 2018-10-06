/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.*;

import junit.framework.TestCase;

/**
 * @author Thomas A Beck
 * @version $Id: CachedTimeSeriesTest.java,v 1.6 2008/12/14 19:30:27 tabeck Exp $
 */
public class CachedTimeSeriesTest extends TestCase implements TestConstants {
	
	// the physical storage where the cache takes the data from
	private Map<Instant,String> storage;
	
	// physical ops counters
	private int physReads;
	private int physInserts; 
	private int physDeletes; 

	/**
	 * Constructor for CachedTimeSeriesTest.
	 * @param name
	 */
	public CachedTimeSeriesTest(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		storage = new TreeMap<Instant,String>();
		storage.put(i1, s1);
		storage.put(i2, s2);
		storage.put(i3, s3);
		storage.put(i4, s4);
		storage.put(i5, s5);
		storage.put(i6, s6);
		storage.put(i7, s7);
		physReads = 0;
		physInserts = 0;
		physDeletes = 0;
	}



	public void testCycle() {
		Handler h = new Handler();
		assertTrue(this.storage.size() == 7);
		TimeSeries<String> ts = new CachedTimeSeries<String>(3, h, h);
		String val;
		
		// check the storage
		val = storage.get(i1);
		assertTrue(val == s1);
		assertTrue(this.physReads == 0);

		// test cache reads
		val = ts.get(i1);
		assertTrue(val == s1);
		assertTrue(this.physReads == 1);

		val = ts.get(i7);
		assertTrue(val == s7);
		assertTrue(this.physReads == 1);

		val = ts.get(i1);
		assertTrue(val == s1);
		assertTrue(this.physReads == 1);
		
		val = ts.get(i2);
		assertTrue(val == s2);
		assertTrue(this.physReads == 2);

		val = ts.get(i7);
		assertTrue(val == s7);
		assertTrue(this.physReads == 2);

		// cache currently contains i1,i2,i7

		val = ts.get(i3);
		assertTrue(val == s3);
		assertTrue(this.physReads == 3);

		// cache currently contains i2,i7,i3;  i1 has left

		val = ts.get(i1);
		assertTrue(val == s1);
		assertTrue(this.physReads == 4);

		// cache currently contains i7,i3,i1;  i2 has left

		val = ts.get(i3);
		assertTrue(val == s3);
		assertTrue(this.physReads == 4);

		// cache currently contains i7,i1,i3;
		
		// test chache remove (key in cache)
		val = ts.remove(i3);
		assertTrue(val == s3);
		assertTrue(this.physReads == 4);
		assertTrue(this.physDeletes == 1);
		assertTrue(!this.storage.containsKey(i3));
		
		// cache currently contains i7,i1;
		
		// test that i1 is still in cache
		val = ts.get(i1);
		assertTrue(val == s1);
		assertTrue(this.physReads == 4);
		
		// cache currently contains i7,i1;

		//test read missing value
		val = ts.get(infinite);
		assertTrue(val == null);
		assertTrue(this.physReads == 5);

		// cache currently contains i7,i1,inf;

		// test that i1 is still in cache
		val = ts.get(i1);
		assertTrue(val == s1);
		assertTrue(this.physReads == 5);

		// cache currently contains i7,inf,i1;

		//test again read missing value
		val = ts.get(infinite);
		assertTrue(val == null);
		assertTrue(this.physReads == 5);

		// cache currently contains i7,i1,inf;

		// test chache remove (key not in cache)
		val = ts.remove(i6);
		assertTrue(val == s6);
		assertTrue(this.physReads == 6);
		assertTrue(this.physDeletes == 2);
		assertTrue(!this.storage.containsKey(i6));

		// cache currently contains inf,i7,i6;

		// test that inf is still in cache
		val = ts.get(infinite);
		assertTrue(val == null);
		assertTrue(this.physReads == 6);
		
		// test that i6 is still in cache
		val = ts.get(i6);
		assertTrue(val == null);
		assertTrue(this.physReads == 6);

		// test that i7 is still in cache
		val = ts.get(i7);
		assertTrue(val == s7);
		assertTrue(this.physReads == 6);
		
		// update i7
		val = ts.put(i7, s6);
		assertTrue(val == s7);
		assertTrue(this.physReads == 6);
		assertTrue(this.storage.get(i7) == s6);
		
		// test that i7 is still in cache
		val = ts.get(i7);
		assertTrue(this.physReads == 6);
		assertTrue(val == s6);
		
		// test contains method
		assertTrue(ts.containsInstant(i7));
		assertTrue(this.physReads == 6);
		assertTrue(!ts.containsInstant(infinite));
		assertTrue(this.physReads == 6);
		assertTrue(!ts.containsInstant(i8));
		assertTrue(this.physReads == 7);
		assertTrue(ts.containsInstant(i1));
		assertTrue(this.physReads == 8);
	}

	private class Handler 
	implements CachedTimeSeries.ChangeHandler<String>, CachedTimeSeries.MissingElementHandler<String> {
		boolean readAhead;
		Map<Instant,String> storage = CachedTimeSeriesTest.this.storage; 
		
		public Handler() {
		}

		public void put(CachedTimeSeries<String> source, Instant i, String value) {
			this.storage.put(i, value);
			CachedTimeSeriesTest.this.physInserts++;
		}

		public void remove(CachedTimeSeries<String> source, Instant i) {
			this.storage.remove(i);
			CachedTimeSeriesTest.this.physDeletes++;
		}

		public void remove(CachedTimeSeries<String> source, Period p) {
			throw new UnsupportedOperationException();
		}

		public void populateElement(CachedTimeSeries<String> ts, Instant i) {
			ts.putInCache(i, storage.get(i));
			ts.putInCache(i7, storage.get(i7));
			CachedTimeSeriesTest.this.physReads++;
		}
	}
}
