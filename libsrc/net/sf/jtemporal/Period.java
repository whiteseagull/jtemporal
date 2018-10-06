/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */


package net.sf.jtemporal;

import java.io.Serializable;

/**
 * Represents a "half-open" immutable time range (which includes the start
 * instant, but not the end instant).    <BR>
 * It does not implement Cloneable, because there is no reason to clone an
 * immutable object.
 * <p><b>Note:</b> if the period is [negativeInfinity, positiveInfinity[ , 
 * then <b>all</b> the instants are contained, including positive infinity.
 * @stereotype immutable
 * @invariant start < end
 * @version $Id: Period.java,v 1.13 2008/12/23 17:44:37 tabeck Exp $
 * @author Thomas A Beck
 */
public class Period implements Comparable<Period>, Serializable, IsTime
{
    private static final long serialVersionUID = 3833750966729257784L;
    
    private final Instant start;
    private final Instant end;

    /**
     * Constructs an immutable Instant instance.
     * @throws IllegalArgumentException when start >= end
     */
    public Period(Instant start, Instant end) {
    	if (start.compareTo(end) >= 0)
    		throw new IllegalArgumentException(
    			"start : " + start + " must be < than end : " + end
    		)
    	;
    	this.start = start;
    	this.end = end;
    }
    
    /**
     * DO NOT USE : For internal use of jtemporal only. 
     */
    /*protected Period() {this.start=null; this.end=null;}*/

    /**
     * Orders first by start and if start instants are equals, by end.    <BR>
     * No matter the periods are overlapping or not.
     * @return 0 only when equals is true
     */
    public int	compareTo(Period p) {
      int startComp = this.getStart().compareTo(p.getStart());
      return startComp == 0 ? this.getEnd().compareTo(p.getEnd()) : startComp;
    }


    /**
     * Compares this object against the specified object.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and is a <code>Period</code> object that
     * contains the same <code>start</code> and <code>end</code> values as this object.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
       if (o == null) return false;
       if (o == this) return true;
       if (! (o instanceof Period)) return false;

       Period p = (Period) o;
       return (this.getStart().equals(p.getStart()) && this.getEnd().equals(p.getEnd()));
    }


    /**
     * Computes a hash code for this Period.     <BR>
     * The result is the binary exclusive OR of the hash codes of start and end.
     * @return  a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return this.getStart().hashCode() ^ this.getEnd().hashCode();
    }


    /**
     * The instant delimiting the beginning of this period.    <BR>
     * The start instant is part of the period.
     * @return the start instant
     */
    public Instant getStart() {
      return start;
    }

    /**
     * The instant delimiting the end of this period.    <BR>
     * The end instant is not part of the period.
     * @return the end instant.
     */
    public Instant getEnd() {
      return end;
    }

    /**
     * Return true is this period precedes p.    <BR>
     * Note: returns false if the periods are overlapping.
     * @param p the period checked to be succeeding.
     * @return this.getEnd() <= p.getStart()
     */
    public boolean precedes(Period p) {
      return this.getEnd().compareTo(p.getStart()) <= 0;
    }

    /**
     * Return true is this period precedes i.    <BR>
     * Note: returns false if the period contains i.
     * @param i the instant checked to be succeeding.
     * @return this.getEnd() <= i
     */
    public boolean precedes(Instant i) {
      return this.getEnd().compareTo(i) <= 0;
    }


    /**
     * Returns true if this period succeeds p.    <BR>
     * Note: returns false if the periods are overlapping.
     * @param p the period checked to be preceding.
     * @return this.getStart() >= p.getEnd()
     */
    public boolean succeeds(Period p) {
      return this.getStart().compareTo(p.getEnd()) >= 0;
    }

    /**
     * Returns true if this period succeeds i.    <BR>
     * Note: returns false if the period contains i.
     * @param p the period checked to be preceding.
     * @return this.getStart() > i
     */
    public boolean succeeds(Instant i) {
      return this.getStart().compareTo(i) > 0;
    }

    /**
     * Returns true if this period meets p.
     * @param p the period checked to be immediately succeeding or preceding.
     * @return this.getEnd() = p.getStart() OR this.getStart() = p.getEnd()
     */
    public boolean meets(Period p) {
      return this.getEnd().equals(p.getStart()) || this.getStart().equals(p.getEnd());
    }

    /**
     * Returns true if this period meets and precedes p.
     * @param p the period checked to be immediately succeeding.
     * @return this.getEnd() = p.getStart()
     */
    public boolean meetsBefore(Period p) {
      return this.getEnd().equals(p.getStart());
    }

    /**
     * Returns true if this period meets and succeeds p.
     * @param p the period checked to be immediately succeeding.
     * @return this.getStart() = p.getEnd()
     */
    public boolean meetsAfter(Period p) {
      return this.getStart().equals(p.getEnd());
    }

    /**
     * Returns true if this period contains of is equal to p.
     * @param p the period checked to be contained.
     * @return this.getStart().compareTo(p.getStart()) <= 0 && this.getEnd().compareTo(p.getEnd()) >= 0
     */
    public boolean contains(Period p) {
      return this.getStart().compareTo(p.getStart()) <= 0 && this.getEnd().compareTo(p.getEnd()) >= 0;
    }

    /**
     * Returns true if this period contains the instant i. <br>
     * <b>Note:</b> if this.getStart() is negative infinity and 
     * this.getEnd() is positiveInfinity, then all the instants are contained,
     * including positive infinity.
     * @param i the instant checked to be contained.
     * @return this.getStart().compareTo(i) <= 0 && this.getEnd().compareTo(i) > 0
     */
    public boolean contains(Instant i) {
    	if (this.getStart().compareTo(i) > 0)	return false;
    	if (this.getEnd().isPositiveInfinity())	return true;
    	if (this.getEnd().compareTo(i) <= 0 )	return false;
    	return true;
    }

    /**
     * Returns true if there exists an intersection between the two periods.
     * @param p the period to be checked for intersection against this period
     * @return <code>true</code> when this.getStart() < p.getEnd() and this.getEnd() > p.getStart()
     */
    public boolean overlaps(Period p) {
      return this.getStart().compareTo(p.getEnd()) < 0 && this.getEnd().compareTo(p.getStart()) > 0;
    }

    /**
     * Returns the result of the merge of two periods.    <BR>
     * One period must meet the other.
     * @return a new period representing the merge
     * @param p the period to be merged to this period
     * @throws IllegalArgumentException when the periods does not match
     */
    public Period union(Period p) {
      if (this.meetsBefore(p)) {
          return new Period(this.getStart(), p.getEnd());
      }
      if (this.meetsAfter(p)) {
          return new Period(p.getStart(), this.getEnd());
      }

        throw new IllegalArgumentException(p + " and " + this
                                           + " do not match. Union impossible");
    }

    /**
     * Returns the part of this period which is not part of p.
     * but if p.contains(this) or p.equals(this) it returns null
     * @return the part of this period which is not part of p.
     * @param p the period to be subtracted from this period
     * @throws IllegalArgumentException when operation should 
     * return two distinct periods
     */
    public Period except(Period p) {
	  if (p.contains(this)) {
	  	return null;
	  }

      if (this.getStart().compareTo(p.getStart()) < 0  &&  this.getEnd().compareTo(p.getEnd()) > 0) {
            throw new IllegalArgumentException("Cannot return two distinct periods from "
                                               + p + " and " + this);      }

      if (this.getStart().compareTo(p.getStart()) < 0) return new Period(this.getStart(), p.getStart());
      if (this.getEnd()  .compareTo(p.getEnd())   > 0) return new Period(p.getEnd(), this.getEnd());
      throw new Error(); // if that happens, it means that my logic is wrong
    }

	/**
	 * Returns the part of this period that precedes p,
	 * or null if p.getStart() < this.getStart()
	 * @return the part of this period that precedes p, or null.
	 * @param p the period that meetsAfter the result
	 */
	public Period precedingPeriod(Period p) {
		if (this.getStart().compareTo(p.getStart()) >=0) {
			return null;
		}
		if (this.getEnd().compareTo(p.getStart()) <=0 ) {
			// we assume Period is immutable
			return this;
		}
		return new Period(this.getStart(), p.getStart());
	}

	/**
	 * Returns the part of this period that succeeds p,
	 * or null if p.getEnd() > this.getEnd()
	 * @return the part of this period that succeeds p, or null.
	 * @param p the period that meetsBefore the result
	 */
	public Period succedingPeriod(Period p) {
		if (this.getEnd().compareTo(p.getEnd()) <= 0) {
			return null;
		}
		if (this.getStart().compareTo(p.getEnd()) >= 0) {
			return this;
		}
		return new Period(p.getEnd(), this.getEnd());
	}

    /**
     * Returns a period representing the common part in the two periods.    <BR>
     * Note: returns null if this.overlaps(p) == false
     * @param p the period to be intersected with this period
     * @return the intersection between this period and p
     */
    public Period intersect(Period p) {
      if (!this.overlaps(p))  return null;
      if (this.contains(p))   return p;     // optimization: avoid to create a new instance in this case
      if (p.contains(this))   return this;  // optimization: avoid to create a new instance in this case
      return new Period(
        this.getStart().compareTo(p.getStart()) < 0 ? p.getStart()  : this.getStart(),
        this.getEnd().  compareTo(p.getEnd())   < 0 ? this.getEnd() : p.getEnd()
      );
    }

    /**
     * Textual representation of this object.
     * @return "Period:(" + start + "," + end + ")"
     */
    @Override
    public String toString() {
      return "Period:(" + this.getStart() + "," + this.getEnd() + ")";
    }

}
