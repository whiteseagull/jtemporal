/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * Reacts on missing elements.
 * @author Thomas A Beck
 * @version $Id: MissingInstantHandler.java,v 1.7 2008/12/14 21:23:17 tabeck Exp $
 * @param <V> the type of the values held in the timeseries
 * @deprecated
 */
@Deprecated
public interface MissingInstantHandler<V> {
	/**
	 * This method is called when get(Instant) is called on the timeseries
	 * and no value is found. <BR>
	 * It's up to the implementor to decide whether only the missing entry 
	 * must be added to the timeseries, or whether other entry are added by prefetching. 
	 * This method, should typically call {@linkplain CachedTimeSeries#putInCache(Instant, Object)},
	 * by storing one or multiple entries. <br>
	 * <b>Note:</b> If there exist no entry for the given instant, then you should
	 * store a <code>null</code>.  
	 * This will cache the information that there is no entry for this instant.
	 * @param ts the TimeSeries instance originating the call
	 * @param i the keys whose no value is associated
	 */
  void handleMissingElement(TimeSeries<V> ts, Instant i);
}
