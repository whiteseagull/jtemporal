/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

/**
 * Features common to the TemporalAttributes.
 * @author Thomas A Beck
 * @version $Id: AbstractTemporalAttribute.java,v 1.9 2009/01/02 14:55:40 tabeck Exp $
 * @param <V> the type of the value
 */

abstract public class AbstractTemporalAttribute<V> implements TemporalAttribute<V> {

	public boolean putAll(TemporalAttribute<? extends V> tm) {
    boolean ret = false;
    for (Period p : tm.periodSet()) {
        ret |= this.put(p, tm.get(p.getStart())) ;
    }
    return ret;
  }

  public Period extent() {
    Instant first = firstInstant(); // throws NoSuchElementException
    Instant last  = lastInstant();  // throws NoSuchElementException
    return new Period(first, last);
  }

  public TimedObject<V> getEntry(Instant instant) {
	// default implementation, to be overridden for better performance
	Period p = this.getPeriod(instant);
	if (p == null) {
		return null;
	}
	V value = this.get(instant);
	return new SimpleTimedObject<V>(p, value);
  }


  // Comparison and hashing

  /**
   * Compares the specified object with this map for content equality.
   * Returns
   * <tt>true</tt> if the given object contains the same mappings.
   *
   * <BR>
   * This implementation first checks if the specified object is this map;
   * if so it returns <tt>true</tt>.  Then, it checks if the specified
   * object is a map whose size is identical to the size of this set; if
   * not, it it returns <tt>false</tt>.  If so, it iterates over this map's
   * Periods and Values, and checks them for equality.
   * <BR>
   * The method equals is not overridden in order to keep the default hashcode()
   * method.  Overriding hashcode() would lead to poor performances when adding
   * this map to a hashmap (which is done by the TemporalSet).
   *
   * @param o object to be compared for equality with this map.
   * @return <tt>true</tt> if the specified object is equal to this map.
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
      if (o == this)
          return true;

      if (!(o instanceof AbstractTemporalAttribute))
          return false;
      TemporalAttribute<V> t = (TemporalAttribute<V>) o;
      int size = this.size();
      if (t.size() != size)
          return false;
      if (size == 0) return true;

      try {
          for (Period p : periodSet()) {
              V value = this.get(p.getStart());
              if (value == null) {
                throw new IllegalStateException(); // should normally never happen
            } else {
                  if (!p.equals(t.getPeriod(p.getStart())))
                      return false;

                  if (!value.equals(t.get(p.getStart())))
                      return false;
            }
          }
      } catch(ClassCastException unused)   {
          return false;
      } catch(NullPointerException unused) {
          return false;
      }
      return true;
  }

  /**
   * Returns the hash code value for this map.  The hash code of a map is
   * defined to be the sum of the hash codes of each entry in the map's
   * <tt>entrySet()</tt> view.  This ensures that <tt>t1.equals(t2)</tt>
   * implies that <tt>t1.hashCode()==t2.hashCode()</tt> for any two maps
   * <tt>t1</tt> and <tt>t2</tt>, as required by the general contract of
   * Object.hashCode.<p>
   *
   * This implementation iterates over <tt>entrySet()</tt>, calling
   * <tt>hashCode</tt> on each element (entry) in the Collection, and adding
   * up the results.
   *
   * @return the hash code value for this map.
   * @see Map.Entry#hashCode()
   * @see Object#hashCode()
   * @see Object#equals(Object)
   * @see Set#equals(Object)
   */
/*  public int hashCode() {
    throw new UnsupportedOperationException();
      int h = 0;
      Iterator i = entrySet().iterator();
      while (i.hasNext())
          h += i.next().hashCode();
      return h;
    } */


}
