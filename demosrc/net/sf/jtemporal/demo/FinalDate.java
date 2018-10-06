/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2004 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */
   
package net.sf.jtemporal.demo;

import java.sql.Date;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.util.LightLRUCache;

/**
 * An example of simple Instant wrapper around java.sql.Date (which is a subtype of java.util.Date).<br>
 * It can possibly be handy (but hacky) to make this class extend java.sql.Date
 * and throw a UnsupportedOperationException on setters.
 * @author Thomas A Beck
 * @version $Id: FinalDate.java,v 1.8 2008/12/23 15:00:22 tabeck Exp $
 */
public class FinalDate implements Instant {

	public static FinalDate POSITIVE_INFINITY = new FinalDate(new Date(Long.MAX_VALUE));
	public static FinalDate NEGATIVE_INFINITY = new FinalDate(new Date(Long.MIN_VALUE));

	private final Date date;	
	private final String toString; // for easy debugging
	
	// given that FinalDates are immutable,
	// we can cache the last 400 created FinalDates, to save memory
	private static final LightLRUCache<Long, FinalDate> CACHE = new LightLRUCache<Long, FinalDate>(400);
	
	/**
	 * Uses the default timezone to build the date.
	 * @param s
	 * @return 
	 */
	public static FinalDate valueOf(String s) {
		return valueOf(Date.valueOf(s));
	}

	/**
	 * Assumes that the default timezone has been used to build this date.
	 * @param date
	 * @return
	 */	
	public static FinalDate valueOf(Date date) {
		FinalDate ret;
		synchronized (CACHE) {
			ret = (FinalDate) CACHE.get(date.getTime());
			if (ret == null) {
				ret = new FinalDate(date);
				CACHE.put(date.getTime(), ret);			
			}			
		}
		return ret;
	}
	
	/**
	 * Assumes that the default timezone has been used to build this date.
	 * @param date the source date.  Note that the date is internally cloned to ensure immutability.
	 */
	private FinalDate(Date date) {
		// defensive copy, because java.util.date is mutable
		this.date = (Date) date.clone(); 
		this.toString = this.date.toString();
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object i) {
		FinalDate other = (FinalDate)i;
		// subtraction leads to wrong result because of the overflow
		if (this.date.getTime() > other.date.getTime()) return 1;
		if (this.date.getTime() < other.date.getTime()) return -1;
		return 0;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof FinalDate)) {	
			return false;
		}
		FinalDate other = (FinalDate) o;
		// equals of java.sql.Date is buggy
		return this.date.getTime() == other.date.getTime();
	}
	
	public int hashcode() {
		return (int) this.date.getTime();
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.Instant#positiveInfinity()
	 */
	public boolean isPositiveInfinity() {
		return this == FinalDate.POSITIVE_INFINITY;
	}

	/* (non-Javadoc)
	 * @see net.sf.jtemporal.Instant#negativeInfinity()
	 */
	public boolean isNegativeInfinity() {
		return this == FinalDate.NEGATIVE_INFINITY;
	}
	
	public String toString() {
		if (this.isPositiveInfinity()) {
			return "+INF";
		}
		if (this.isNegativeInfinity()) {
			return "-INF";
		}
		return this.toString;
	}
	
	/**
	 * Converts to a java.sql.Date (or java.util.Date)
	 * @return
	 */
	public Date toDate() {
	    // defensive copy, because java.util.date is mutable
	    return (Date) this.date.clone();
	}


}
