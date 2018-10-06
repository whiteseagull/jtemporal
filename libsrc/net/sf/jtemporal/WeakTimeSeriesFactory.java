/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Factory for timeseries. <BR>
 * The point is that timeseries values are internally stored in a WeakHashMap, in such 
 * a way that you can globally remove a date from the TimeSeriesMgr to free 
 * the memory of all the TimeSeries simultaenously, for the given date.  <BR>
 * The advantage, is that this costly removal is done by the garbage collector
 * and only if you need memory.  <BR>
 * No strong references are held on the timeseries anymore: as soon you unreference them
 * they are eligible for garbage collection.
 * By moving the responsability to the gc, the code is quite simple, because 
 * we do not need to perform the old dates removal.
 *
 * @author Thomas A Beck
 * @version $Id: WeakTimeSeriesFactory.java,v 1.3 2004/05/02 15:51:00 tabeck Exp $
 * @deprecated
 */
public class WeakTimeSeriesFactory {

	private Map keys = new HashMap();

    Object getKey(Instant i) {
    	Object key = keys.get(i);
    	if (key == null) {
    		key = new Object();
    		this.keys.put(i, key);
    	}
    	return key;
    }

	Object getKeyOrNull(Instant i) {
		Object key = keys.get(i);
		return key;
	}
    
    /**
     * Creates a new TimeSeries, internally linked to this mgr. <BR>
     * This internal link won't prevent the whole timeseries to be garbaged,
     * if unreferenced.
     * @return the new empty TimeSeries instance
     */
    public TimeSeries getHashTimeSeries() {
    	return new WeakHashTimeSeries(this);
    }

	/**
	 * Creates a new TimeSeries, internally linked to this mgr. <BR>
	 * This internal link won't prevent the whole timeseries to be garbaged,
	 * if unreferenced.
	 * @param handler the handler used by the TimeSeries
	 * @return the new empty TimeSeries instance
	 */
	public TimeSeries getHashTimeSeries(MissingInstantHandler handler) {
		return new WeakHashTimeSeries(this, handler);
	}
 
 	/**
 	 * Potentially removes the given instant in all the TimeSeries created
 	 * by this mgr.  <BR>
 	 * Actually, they will be removed by the garbage collector on 
 	 * a "as-needed" basis. <BR>
 	 * Reading a value in a timeseries for this instant, will automatically
 	 * make it strong again.
 	 * @param i the instant whose entries are to be removed from the timeseries.
 	 */
 	public void weaken(Instant i) {
 		this.keys.remove(i);
 	}

	/**
	 * Potentially removes all the known instant in the given Period range,
	 * in all the TimeSeries created by this mgr.  <BR>
 	 * Actually, they will be removed by the garbage collector on 
 	 * a "as-needed" basis.
	 * @param p the period defining the range of deletion
	 */ 	
 	public void weaken(Period p) {
 		for (Iterator it = keys.keySet().iterator(); it.hasNext();) {
			Instant i = (Instant) it.next();
			if (p.contains(i)) it.remove();
 		}
 	}
    
}
