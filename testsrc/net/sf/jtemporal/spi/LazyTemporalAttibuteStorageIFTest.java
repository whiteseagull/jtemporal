package net.sf.jtemporal.spi;

import java.util.Iterator;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.IntInstant;
import net.sf.jtemporal.Period;
import net.sf.jtemporal.TimedObject;

public class LazyTemporalAttibuteStorageIFTest extends
		AbstractTemporalAttributeStorageTest {

	public LazyTemporalAttibuteStorageIFTest(String name) {
		super(name);
	}

	protected TemporalAttributeStorage newStorage() {
		// the physical storage where the cache takes de data from
		TemporalAttributeStorage db = new TreeTemporalAttributeStorage();
		//System.err.println("Created " +db.hashCode());
		TemporalAttributeStorage ret 
		   = new LazyTemporalAttributeStorage<String>(
				new Reader(db), new Writer(db)
		);
		//System.err.println("Created @" +ret.hashCode());
		return ret;
	}

	
	private class Reader 
	implements LazyTemporalAttributeStorage.MissingRangeHandler<String>
	{
		private final TemporalAttributeStorage db;
		Reader (TemporalAttributeStorage db) {
			this.db = db;
		}
		public Period getAlwaysPeriod() {
			return ALWAYS;
		}
		public void populateRange(LazyTemporalAttributeStorage target, Period range) {
			for (Iterator i=db.entryIterator(range); i.hasNext();) {
				TimedObject to = (TimedObject) i.next();
				//System.err.println("Putting in target @"+target.hashCode()+" " +to+" in reponse to "+range);
				try {
					target.putInCache(to.getPeriod(), to.getValue());
				}
				catch (IllegalStateException e) {
					//System.err.println("Got the exception when putting "+to);
					throw e;
				}
			}
			target.populated(range);
		}
		public void populateEntryEndingAt(LazyTemporalAttributeStorage target, Instant instant) {
			TimedObject<String> entry = db.getEntryEndingAt(instant);
			if (entry != null) {
				target.putInCache(entry.getPeriod(), entry.getValue());
			}
		}
	}
	
	private static int instances = 0;

	private class Writer
	implements LazyTemporalAttributeStorage.ChangeHandler {
		private final TemporalAttributeStorage db;
		private final int instance;
		
		Writer (TemporalAttributeStorage db) {
			this.db = db;
			this.instance = instances++;
		}
		public void clear(LazyTemporalAttributeStorage source) {
			//System.err.println("Clearing "+instance);
			db.clear();
		}
		public void put(LazyTemporalAttributeStorage source, Period p, Object value) {
			//System.err.println("Putting "+p+" "+value+" "+instance);
			db.put(p, value);
		}
		public void remove(LazyTemporalAttributeStorage source, Period p) {
			db.removeEntry(p);			
		}
	}
}
