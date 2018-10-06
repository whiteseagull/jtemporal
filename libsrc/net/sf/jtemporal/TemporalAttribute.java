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
 * Associates periods to zero or one object (value). <br>
 * This is typically used to implement temporal associations with unary cardinality, 
 * for example Employee-Salary. <br>
 * You can get the value valid at a given time by calling 
 * {@linkplain TemporalAttribute#get(Instant)}. <br>
 * To add a mapping, you call {@linkplain TemporalAttribute#put(Period, Object)}. 
 * If another mapping already exists valid during the given period (or part of the period), 
 * the mapping is overwritten for given period.
 * 
 * @author Thomas A Beck
 * @version $Id: TemporalAttribute.java,v 1.9 2007/09/14 20:03:10 tabeck Exp $
 * @param <V> the type of the value
 */
public interface TemporalAttribute<V> extends ReadableTemporalAttribute<V> {

  /**
   * Removes all mappings from this map (optional operation).
   *
   * @throws UnsupportedOperationException clear is not supported by this
   * 		  map.
   */
  void clear();

  /**
   * Returns the number of Period-value mappings in this map.  If the
   * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   * @return the number of Instant-value mappings in this map.
   */
  int size();

  /**
   * Returns <tt>true</tt> if this map contains no Period-value mappings.
   * @return <tt>true</tt> if this map contains no Period-value mappings.
   */
  boolean isEmpty();

  /**
   * Returns the value to which this map maps the specified Instant.  Returns null
   * if the map contains no mapping at this Instant. <br>
   * WARNING: for defragmentation purposes, this method may eventually return a 
   * different instance of value that equals the value stored by put(Period). 
   * @param instant instant whose associated value is to be returned
   * @return the value to which this map maps the specified instant.
   */
  V get(Instant instant);

  /**
   * Returns the period that has a value associated at specified instant.
   * @param instant instant whose associated enclosing period is to be returned
   * @return the period of the entry that overlaps the specified instant.
   * returns <tt>null</tt> if this map does not contain a mapping for the
   * specified instant.
   */
  Period getPeriod(Instant instant);

  /**
   * Returns the entry active at the given instant. <br>
   * This method is like a get(Instant) and getPeriod(Instant) in 
   * a single shot. <br>
   * <b>WARNING: </b> for defragmentation purposes, this method may eventually return a 
   * different instance of value that equals the value stored by put(Period). 
   * @param instant instant whose associated entry is to be returned
   * @return the entry active at the given instant.
   * returns <tt>null</tt> if this map does not contain a mapping for the
   * specified instant.
   */
  TimedObject<V> getEntry(Instant instant);

  /**
   * Returns <tt>true</tt> if this map contains a mapping for the specified
   * Instant.
   * @param i instant whose presence in this map is to be tested.
   * @return <tt>true</tt> if this map contains a mapping for the specified
   * instant.
   */
  boolean containsInstant(Instant i);

  /**
   * Returns <tt>true</tt> if this map maps one or more Periods to the
   * specified value.
   * @param value value whose presence in this map is to be tested.
   * @return <tt>true</tt> if this map maps one or more keys to the
   *         specified value.
   */
  boolean containsValue(Object value);

  /**
   * Associates the specified value with the specified Period in this map
   * (optional operation).
   * If the map previously contained a mapping to a period overlapping
   * this period, the old value is replaced for the given period. <br>
   * WARNING: for defragmentation purposes, get(Instant) and getEntry(Instant)
   * may eventually return a a different instance of value that equals(value). 
   * @param p period with which the specified value is to be associated.
   * @param value value to be associated with the specified key.  Be careful 
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
   * Removes the mapping(s) for this period from this map if present (optional
   * operation).
   *
   * @param p period whose mappings are to be removed from the map.
   * @return <tt>true</tt> if one or more previous values are removed
   *         partially or completely.  <tt>false</tt> if there is any mapping changed.
   * @throws UnsupportedOperationException if the <tt>remove</tt> method is
   *         not supported by this map.
   */
  boolean remove(Period p);

  /**
   * Copies all of the mappings from the specified map to this map
   * (optional operation).  These mappings will replace any mappings that
   * this map had for any of the periods currently in the specified map.
   *
   * @param tm Mappings to be stored in this map.
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
  boolean putAll(TemporalAttribute<? extends V> tm);


  /**
   * Returns a set view of the periods contained in this map.  The set is
   * backed by the map, so changes to the map are reflected in the set, and
   * vice-versa.  If the map is modified while an iteration over the set is
   * in progress, the results of the iteration are undefined.  The set
   * supports element removal, which removes the corresponding mapping from
   * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
   * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
   * It does not support the add or <tt>addAll</tt> operations.
   *
   * @return a set view of the keys contained in this map.
   */
  Set<Period> periodSet();

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
   * Returns the first (lowest) instant currently in this temporal map.
   *
   * @return the first (lowest) instant currently in this temporal map.
   * @throws    NoSuchElementException TemporalAttribute is empty.
   */
  Instant firstInstant();

  /**
   * Returns the last (highest) instant currently in this temporal map.
   *
   * @return the last (highest) instant currently in this temporal map.
   * @throws    NoSuchElementException TemporalAttribute is empty.
   */
  Instant lastInstant();

  /**
   * Returns the first (lowest) period currently in this temporal map.
   *
   * @return the first (lowest) period currently in this temporal map.
   * @throws    NoSuchElementException TemporalAttribute is empty.
   */
  Period  firstPeriod();


  /**
   * Returns the last (highest) period currently in this temporal map.
   *
   * @return the last (highest) period currently in this temporal map.
   * @throws    NoSuchElementException TemporalAttribute is empty.
   */
  Period  lastPeriod();

  /**
   * Returns a period enclosing firstInstant() and lastInstant()
   * @return new Period(firstInstant(), lastInstant())
   * @throws    NoSuchElementException TemporalAttribute is empty.
   * @see TemporalAttribute#firstInstant()
   * @see TemporalAttribute#lastInstant()
   */
  Period  extent();

  TemporalAttribute<V> subMap(Period p);
}
