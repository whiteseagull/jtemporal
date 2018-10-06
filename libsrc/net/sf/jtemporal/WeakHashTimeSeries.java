/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.Map;
import java.util.WeakHashMap;


/**
 * Acts as a cache for an external TimeSeries (for ex. a database).
 * Each time this collection is queried, if the value for the given instant is unknown,
 * the MissingInstantHandler is invoked.
 * The point is that the values are internally stored in a WeakHashMap, in such 
 * a way that you can globally remove an instant from the TimeSeriesMgr to free 
 * the memory of all the WeakHashTimeSeries simultaenously, for the given date.  <BR>
 * The advantage, is that this costly removal is done by the garbage collector
 * and only if you need memory.  <BR>
 * By moving the responsability to the gc, the code becomes simpler, because 
 * we do not need to remove the old instants.
 * This is a manual caching mechanism: you specify which instants you do not need anymore.
 *
 * @author Thomas A Beck
 * @version $Id: WeakHashTimeSeries.java,v 1.5 2004/05/23 10:46:49 tabeck Exp $
 * @see net.sf.jtemporal.MissingInstantHandler
 * @deprecated
 */

class WeakHashTimeSeries implements TimeSeries {
	private final WeakTimeSeriesFactory factory;
	
	// key:object provided by the MGR, value:objec provide by the client
	private final Map map = new WeakHashMap();
	
	private final MissingInstantHandler missingInstantHandler;
	
	WeakHashTimeSeries(WeakTimeSeriesFactory timeSeriesMgr) {
		this.factory = timeSeriesMgr;
		this.missingInstantHandler = DefaultMissingInstantHandler.INSTANCE; 
	}

	WeakHashTimeSeries(WeakTimeSeriesFactory timeSeriesMgr, MissingInstantHandler handler) {
		this.factory = timeSeriesMgr;
		this.missingInstantHandler = handler; 
	}
		
	/*
	 *  (non-Javadoc)
	 * @see jtemporal.TimeSeries#put(jtemporal.Instant, java.lang.Object)
	 */
    public Object put(Instant i, Object value) {
    	Object key = factory.getKey(i);
    	Object previous = this.map.put(key, value);
    	return previous;
    }

	/*
	 *  (non-Javadoc)
	 * @see jtemporal.TimeSeries#get(jtemporal.Instant)
	 */
    public Object get(Instant i) {
		Object value = getFromMap(i);
		if (value == NoElementObject.value) {
			return null;
		}
		if (value == null) { 
			this.missingInstantHandler.handleMissingElement(this, i);
			value = getFromMap(i);
			if (value == null) { //still?
				map.put(i, NoElementObject.value); // remember for the next time
			}
		}
		return value;
    }

	private Object getFromMap(Instant i) {
		Object key = factory.getKeyOrNull(i);
		if (key == null) return null;
		return map.get(key);
	}



	/* (non-Javadoc)
	 * @see jtemporal.TimeSeries#remove(jtemporal.Instant)
	 */
	public Object remove(Instant i) {
		Object key = factory.getKeyOrNull(i);
		if (key == null) return null;
		return map.remove(key);
	}

	/**
	 * Throws UnsupportedOperationException in all cases.
	 * @throws UnsupportedOperationException in all cases.
	 * @see jtemporal.TimeSeries#remove(jtemporal.Period)
	 */
	public void remove(Period p) {
		throw new UnsupportedOperationException();
	}
	
	private static class NoElementObject {
		public static NoElementObject value = new NoElementObject();
		private NoElementObject() {}
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.TimeSeries#containsInstant(net.sf.jtemporal.Instant)
	 */
	public boolean containsInstant(Instant i) {
		Object value = this.get(i);
		return value != null;
	}
}
