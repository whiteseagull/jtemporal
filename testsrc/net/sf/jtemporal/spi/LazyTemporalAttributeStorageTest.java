package net.sf.jtemporal.spi;

import java.util.Iterator;
import junit.framework.TestCase;
import net.sf.jtemporal.Instant;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.TestConstants;
import net.sf.jtemporal.TimedObject;
import net.sf.jtemporal.spi.LazyTemporalAttributeStorage.MissingRangeHandler;

public class LazyTemporalAttributeStorageTest 
extends TestCase implements TestConstants {

	// the physical storage where the cache takes de data from
	private final TemporalAttributeStorage storage =
		new TreeTemporalAttributeStorage();

	// physical ops counters	
	private int physReads;
	private int physInserts; 
	private int physDeletes; 

	private LazyTemporalAttributeStorage ta;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		this.ta = new LazyTemporalAttributeStorage(
			new Loader()
		); 
		storage.clear();
		storage.put(p12, s1);
		storage.put(p35, s2);
		storage.put(p56, s3);
		storage.put(p78, s4);
		storage.put(p89, s5);

		physReads = 0;
		physInserts = 0;
		physDeletes = 0;
	}
	
	protected TemporalAttributeStorage newStorage() {
		return new LazyTemporalAttributeStorage(
			new Loader()
		);
	}
	
	
	/**
	 * Constructor for TreeTemporalAttributeStorageTest.
	 * @param name
	 */
	public LazyTemporalAttributeStorageTest(String name) {
		super(name);
	}

	/*
	 * Test method for 'net.sf.jtemporal.spi.LazyTemporalAttributeStorage.clearCache()'
	 */
	public void testClearCache() {

	}

	/*
	 * Test method for 'net.sf.jtemporal.spi.LazyTemporalAttributeStorage.putInCache(Period, Object)'
	 */
	public void testPutInCache() {

	}

	/*
	 * Test method for 'net.sf.jtemporal.spi.LazyTemporalAttributeStorage.populated(Period)'
	 */
	public void testPopulated() {

	}


	private class Loader implements  MissingRangeHandler {
		public Period getAlwaysPeriod() {
			return ALWAYS;
		}
		public void populateRange(LazyTemporalAttributeStorage target, Period range) {
			for (Iterator i=storage.entryIterator(range); i.hasNext();) {
				TimedObject to = (TimedObject) i.next();
				ta.putInCache(to.getPeriod(), to.getValue());
			}
		}
		public void populateEntryEndingAt(LazyTemporalAttributeStorage target, Instant instant) {
			throw new UnsupportedOperationException("not implemented yet");
		}
	}
}
