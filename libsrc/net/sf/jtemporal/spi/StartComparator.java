/*
 This file is part of the JTemporal framework (http://jtemporal.sf.net).
 Copyright (C) 2002 by the author(s).
 Distributable under LGPL license version 2.1 or later, 
 with no warranties given or implied.
 See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.spi;

import java.util.Comparator;

import net.sf.jtemporal.Instant;
import net.sf.jtemporal.IsTime;
import net.sf.jtemporal.Period;


/**
 * Compares Periods and Instants by using the Period start instant. <br>
 * This is used by all the Storages based on a TreeMap, whose
 * key is supposed to contain instances of {@linkplain net.sf.jtemporal.Period}
 * only. <br>
 * It can look surprising and somewhat hacky that the comparator 
 * simultaneously compares instances of {@linkplain net.sf.jtemporal.Period}
 * and {@linkplain net.sf.jtemporal.Instant}, but this is required by the duality
 * of the map key: Periods when we write, Instants when we read. Given that 
 * all the objects in the key of the map are of the same class, it does not 
 * break Comparator's contract.
 */
public class StartComparator implements Comparator<IsTime>
{
	/**
	 * The shared instance.
	 */
	public static final StartComparator COMPARATOR = new StartComparator();

	/**
	 * Some ORM frameworks require to instantiate new instances of the
	 * comparator, that's why this is public.
	 */
	public StartComparator() {
	    super();
	}
	
	/**
	 * If both objects are Periods, they are compared as periods
	 * otherwise they are compared as instants
	 * (taking the start instant if one is a Period).
	 * It would be more OO to implement a common method in Period and
	 * in Instant, but I want to keep the Instant interface as simple as possible.
	 */
	public int compare(IsTime o1, IsTime o2) {
		Instant i1, i2;
		
		if (o1 instanceof Period) {
			if (o2 instanceof Period)
				return ( (Period) o1).getStart().compareTo(((Period) o2).getStart() )
			;
			i1 = ((Period) o1).getStart();
		} else { // o1 instanceof Instant
			i1 = (Instant) o1;
			if (o2 instanceof Period)
				o2 = ((Period) o2).getStart()
			;
		} // if
		
		i2 = (Instant) o2;
		return i1.compareTo(i2);
		
		/*      if (o1 instanceof Period && o2 instanceof Period)
		 return ((Period) o1).compareTo(o2);
		 
		 Instant i1 = o1 instanceof Instant ? (Instant) o1 : ((Period) o1).getStart();
		 Instant i2 = o2 instanceof Instant ? (Instant) o2 : ((Period) o2).getStart();
		 return i1.compareTo(i2);
		 */
	}
	
}