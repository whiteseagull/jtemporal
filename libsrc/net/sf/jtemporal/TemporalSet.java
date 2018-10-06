/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This is a map, whose key is a {@linkplain Period} when adding mappings (entries) 
 * and an {@linkplain Instant} when getting mappings. <br>
 * Associates periods to a Set of objects (values). <br>
 * This is typically used to implement temporal associations with non-unary cardinality, 
 * for example Employee-Skills. <br>
 * You can get the Set of values valid at a given time by calling 
 * {@linkplain TemporalSet#valueSet(Instant)}. <br>
 * To add a mapping, you call {@linkplain TemporalSet#put(Period, Object)}.
 *  
 * <p><B>Note:</B> the Object value (passed to many methods) must have equals and hashCode
 * defined consistently, as described in the {@linkplain java.lang.Object java.lang.Object} 
 * documentation.
 * 
 * @author Thomas A Beck
 * @version $Id: TemporalSet.java,v 1.10 2009/01/02 14:55:40 tabeck Exp $
 * @param <V> the type of the value
 */
public interface TemporalSet <V> 
{
  /**
   * Removes all the mappings from this map.
   * @throws UnsupportedOperationException if clear is not supported by this map.
   */
  void clear();

  /**
   * Returns the number of Period-value mappings in this map.  If the
   * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   * @return the number of Period-value mappings in this map.
   */
  int size();

  /**
   * Returns the number of distinct values in this map.  If the
   * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   * @return the number of distinct values in this map.
   */
  int sizeValues();

  /**
   * Returns the number of Period-value mappings in this map for the
   * given value.  If the
   * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   * @param value the number of entries for the given value
   * @return the number of Instant-value mappings in this map.
   */
  int sizeFor(V value);

  /**
   * Returns the number values in this map valid at the given instant.  If the
   * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   * @return the number of entries valid at the given instant.
   * @param instant the instant at which the counted entries are valid
   */
  int sizeAt(Instant instant);

  /**
   * Returns <tt>true</tt> if this map contains no Period-value mappings.
   * @return <tt>true</tt> if this map contains no Period-value mappings.
   */
  boolean isEmpty();

  /**
   * Returns <tt>true</tt> if this map does not contain mappings valid at the given intant.
   * @return <tt>true</tt> if this map does not contain mappings valid at the given intant.
   * @param i when this temporal set is empty
   */
  boolean isEmpty(Instant i);

  /**
   * Returns a read-only Set containing the values defined in this map at the
   * specified instant.
   * Returns an empty set if the map contains no mapping at this Instant.
   * @return a read-only Set containing the values defined in this map at the
   * specified instant.
   * @param instant instant whose associated value is to be returned
   */
  Set<V> valueSet(Instant instant);

  /**
   * Returns a read-only Set containing the distinct values defined somewhen in this map.
   * Returns an empty set if the map contains no mapping at all.
   * @return a read-only Set containing the distinct values defined somewhen in this map.
   */
  Set<V> valueSet();

  /**
   * Returns a set view of the entries (instances of {@linkplain TimedObject}) 
   * contained in this map.  The set is
   * backed by the map, so changes to the map are reflected in the set, and
   * vice-versa.  If the map is modified while an iteration over the set is
   * in progress, the results of the iteration are undefined.  The set
   * supports element removal, which removes the corresponding mapping from
   * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
   * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
   * It does not support the add or <tt>addAll</tt> operations.
   *
   * @return a set view of the keys contained in this map.
   * @see TimedObject
   */
  Set<TimedObject<V>> entrySet();

  /**
   * Returns the period of the mapping valid at the specified instant for the given value.
   * @return the period of the mapping valid at the specified instant for the given value.
   * Returns <tt>null</tt> if this map does not contain a mapping for the given value at the
   * specified instant.
   * @param instant when the given value is valid
   * @param value the value of the entry valid during the returned period
   */
  Period getPeriod(Instant instant, V value);

  /**
   * Returns <tt>true</tt> if there is a mapping for this value at the given instant.
   * Same as containsValue(Instant instant, Object value)
   * @param instant instant whose presence in this map is to be tested.
   * @param value value whose presence in this map is to be tested.
   * @return <tt>true</tt> if there is a mapping for this value at the given instant.
   */
  boolean contains(Instant instant, Object value);

  /**
   * Returns <tt>true</tt> if there is a mapping for the specified value.
   * @param value value whose presence in this map is to be tested.
   * @return <tt>true</tt> if this map maps one or more keys to the
   *         specified value.
   */
  boolean containsValue(Object value);

  /**
   * Associates the specified value with the specified Period in this map.
   * If the map previously contained a mapping to an equal value, during a period
   * overlapping this period, the periods are merged.
   *
   * @param p period with which the specified value is to be associated.
   * @param value value to be associated with the specified period.  Be careful 
   * at the equals implementation of the value object: the result of the equals 
   * method is supposed to be immutable while it is stored in this collection.
   * @return <tt>true</tt> if one or more previous values are overwritten
   *         partially or completely.  <tt>false</tt> if there is no conflicting
   *         mapping.
   *
   * @throws UnsupportedOperationException if the <tt>put</tt> operation is
   *	          not supported by this map.
   * @throws ClassCastException if the class of the specified value
   * 	          prevents it from being stored in this map.
   * @throws IllegalArgumentException if some aspect of this period or value
   *	          prevents it from being stored in this map.
   *              or if the period or the value is <tt>null</tt>
   */
  boolean put(Period p, V value);
  
  /**
   * Removes the mapping(s) for this period and value from this map if present.
   *
   * @param p period whose mappings are to be removed from the map.
   * @param value value whose mapping are to be removed from the map.
   * @return <tt>true</tt> if one or more previous values are removed
   *         partially or completely.  <tt>false</tt> if there is no mapping changed.
   * @throws UnsupportedOperationException if the <tt>remove</tt> method is
   *         not supported by this map.
   * @throws    NoSuchElementException if the value is unknown.
   */
  boolean remove(Period p, V value);

  /**
   * Removes all the mapping(s) for this value from this map if present.
   *
   * @param value value whose mapping are to be removed from the map.
   * @return <code>true</code>if the TemporalSet contained the specified element.
   * @throws UnsupportedOperationException if the <tt>remove</tt> method is
   *         not supported by this map.
   * @throws    NoSuchElementException if the value is unknown.
   */
  boolean removeValue(V value);

  /**
   * Removes all the mapping(s) for this period from this map if present.
   *
   * @param p period whose mappings are to be removed from the map.
   * @return <tt>true</tt> if one or more previous values are removed
   *         partially or completely.  <tt>false</tt> if there is any mapping changed.
   * @throws UnsupportedOperationException if the <tt>remove</tt> method is
   *         not supported by this map.
   */
  boolean removePeriod(Period p);

  /**
   * Copies all of the mappings from the specified map to this map.  
   * These mappings will replace any mappings that
   * this map had for any of the periods currently in the specified map.
   *
   * @param ts Mappings to be stored in this map.
   *
   * @return <tt>true</tt> if one or more previous values are overwritten
   *         partially or completely.  <tt>false</tt> if there is any conflicting
   *         mapping.
   *
   * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
   * 		  not supported by this map.
   *
   * @throws ClassCastException if the class of a value in the
   * 	          specified map prevents it from being stored in this map.
   *
   * @throws IllegalArgumentException some aspect of a value in the
   *	          specified map prevents it from being stored in this map.
   */
  boolean putAll(TemporalSet<? extends V> ts);


  /**
   * Returns a set view of the periods contained in this map for the give value.
   * The set is
   * backed by the map, so changes to the map are reflected in the set, and
   * vice-versa.  If the map is modified while an iteration over the set is
   * in progress, the results of the iteration are undefined.  The set
   * supports element removal, which removes the corresponding mapping from
   * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
   * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
   * It does not support the add or <tt>addAll</tt> operations.
   *
   * @return a set view of the periods contained in this map for the give value.
   * @throws    NoSuchElementException if the value is unknown.
   */
  Set<Period> periodSet(V value);

  /**
   * Returns the first (lowest) instant currently defined for the given value.
   *
   * @return the first (lowest) instant currently defined for the given value.
   * @throws    NoSuchElementException if the value is unknown.
   */
  Instant firstInstant(V value);

  /**
   * Returns the last (highest) instant currently defined for the given value.
   *
   * @return the last (highest) instant currently defined for the given value.
   * @throws    NoSuchElementException if the value is unknown.
   */
  Instant lastInstant(V value);

  /**
   * Returns the first (lowest) period currently defined for the given value.
   *
   * @return the first (lowest) period currently defined for the given value.
   * @throws    NoSuchElementException if the value is unknown.
   */
  Period  firstPeriod(V value);

  /**
   * Returns the last (highest) period currently defined for the given value.
   *
   * @return the last (highest) period currently defined for the given value.
   * @throws    NoSuchElementException if the value is unknown.
   */
  Period  lastPeriod(V value);

  /**
   * Return a period enclosing firstInstant(value) and lastInstantvalue()
   * @return new Period(firstInstant(value), lastInstant(value))
   * @throws    NoSuchElementException if the value is unknown.
   * @see TemporalSet#firstInstant(Object)
   * @see TemporalSet#lastInstant(Object)
   */
  Period extent(V value);

  /**
   * Returns a TemporalAttribute which is either a copy or a view of this map,
   * restricted to the given period.    (optional operation(?)). @TODO
   * @throws UnsupportedOperationException if clear is not supported by this map.
     TemporalSet subMap(Period p); */
}
