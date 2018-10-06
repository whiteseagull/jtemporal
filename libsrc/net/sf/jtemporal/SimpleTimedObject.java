/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;


/**
 * Associates an Object to a Period.
 * The semantics of the period is free.
 * Is immutable when the value is immutable as well.
 * @author Thomas A Beck
 * @version $Id: SimpleTimedObject.java,v 1.7 2009/01/02 14:55:40 tabeck Exp $
 * @param <V> the type of the value
 */
public class SimpleTimedObject<V> extends AbstractTimedObject<V> {
  private final Period period;
  private final V value;

  /**
   * Unique constructor initializing the final field.
   */
  public SimpleTimedObject(Period period, V value){
    this.value  = value;
    this.period = period;
  }

  /**
   * @return the period associated to the value
   */
    @Override
  public Period getPeriod(){
    return period;
  }

  /**
   * @return the value
   */
    @Override
  public V getValue(){
    return value;
  }
  
  /**
   * Returns a new SimpleTimedObject representing the common part in the two periods.  <BR>
   * Note: returns null if !getPeriod().overlaps(p)
   * @param to the TimedObject to be intersected, whose value is returned
   * @param p the Period to be intersected
   * @param <W> The value held by the timedobjects
   * @return a new SimpleTimedObject whose validity is the intersection of both periods,
   * or <code>null</code> if the intersection is null.
   */
  public static <W> TimedObject<W> intersect(TimedObject<W> to, Period p) {
	// this method is static, since we want to intersect any kind of TimedObject
	// implementation, even those who do not support intersection
    Period toPeriod = to.getPeriod();
  	Period newPeriod = toPeriod.intersect(p);
  	if (newPeriod == null) {
  		return null;
  	}
  	if (toPeriod == newPeriod) {
  		return to;
  	}
  	return new SimpleTimedObject<W>(newPeriod, to.getValue());
  }

}
