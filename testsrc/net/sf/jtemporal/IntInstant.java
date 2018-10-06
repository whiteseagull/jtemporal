/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;


/**
 * Immutable.
 * An ultra simple implementation of Instant to be used in the unit tests. 
 * @author Thomas A Beck
 * @version $Id: IntInstant.java,v 1.6 2007/09/15 18:47:00 tabeck Exp $
 */

public class IntInstant implements Instant
{
  private final int value;
  public static final IntInstant POSITIVE_INFINITY = new IntInstant(Integer.MAX_VALUE); 
  public static final IntInstant NEGATIVE_INFINITY = new IntInstant(Integer.MIN_VALUE); 

  
  public IntInstant (int i) {
    this.value = i;
  }

  /**
   * Compares two IntIntstants numerically.
   *
   * @param   anotherIntInstant   the <code>IntInstant</code> to be compared.
   * @return  the value <code>0</code> if the argument IntInstant is equal to
   *          this IntInstant; a value less than <code>0</code> if this IntInstant
   *          is numerically less than the IntInstant argument; and a
   *          value greater than <code>0</code> if this IntInstant is
   *          numerically greater than the IntInstant argument
   *		(signed comparison).
   * @since   1.2
   */
  public int compareTo(Object anotherIntInstant) {
    if (! (anotherIntInstant instanceof IntInstant)) {
      throw new ClassCastException(anotherIntInstant.toString());
    }
    int thisVal = this.value;
    int anotherVal = ((IntInstant) anotherIntInstant).value;
    return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
  }

  public boolean equals(Object obj) {
    if (obj instanceof IntInstant) {
	return value == ((IntInstant)obj).value;
    }
    return false;
  }

  /**
   * Returns a hash code for this Integer.
   *
   * @return  a hash code value for this object, equal to the
   *          primitive <tt>int</tt> value represented by this
   *          <tt>Integer</tt> object.
   */
  public int hashCode() {
      return value;
  }


  public String toString() {
	  if (this.isPositiveInfinity()) return "+INF";
	  if (this.isNegativeInfinity()) return "-INF";
      return String.valueOf(this.value);
  }

  public boolean isPositiveInfinity() {
  	return this == POSITIVE_INFINITY;
  }


  public boolean isNegativeInfinity() {
  	return this == NEGATIVE_INFINITY;
  }

  public int toIntValue() {
	  return this.value;
  }
}