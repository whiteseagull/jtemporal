/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
 Copyright (C) 2002 by the author(s).
 Distributable under LGPL license version 2.1 or later, 
 with NO WARRANTIES given or implied.
 See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.*;

/**
 * Uses internally a TreeMap to store the mappings.
 * The Periods as used as keys.
 * <BR>
 * Links at any time an object (the map holder) to zero or one object (value),
 * example: Employee-Salary.
 * You get the value valid at a given time with the method Object get(Instant i).
 * To add a mapping, you do not specify an Instant, but a Period specifying the validity
 * of the object.  If another object exists valid during this period, the validity will be
 * overwritten for new object period.
 * <BR><b>
 * Note that this implementation is not synchronized.</b> If multiple
 * threads access a map concurrently, and at least one of the threads modifies
 * the map structurally, it <i>must</i> be synchronized externally.  (A
 * structural modification is any operation that adds or deletes one or more
 * mappings; merely changing the value associated with an existing key is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.  If no
 * such object exists, the map should be "wrapped" using the
 * <tt>Collections.synchronizedMap</tt> method.  This is best done at creation
 * time, to prevent accidental unsynchronized access to the map:
 * <pre>
 *     TemporalAttribute tm = SynchronizedTemporalMap.getInstance(new TreeTemporalSet(...));
 * </pre><p>
 * @author Thomas A Beck
 * @version $Id: OldTreeTemporalAttribute.java,v 1.7 2009/01/02 17:05:03 tabeck Exp $
 * @deprecated
 */

class OldTreeTemporalAttribute extends AbstractTemporalAttribute
{
    private final Class valueType;
    private final SortedMap map;
    
    /**
     * The number of changes to the keys (Periods).
     * Needed by views to know whether the model has changed.
     */
    private transient int versionNumber = 0;
    
    //private transient Object  lastValue   = null;
    //private transient Period  lastPeriod  = null;
    //private transient Instant lastInstant = null;
    
    /**
     * Creates an empty TreeTemporalSet accepting values of any type.
     */
    public OldTreeTemporalAttribute() {
        this.valueType = null;
        this.map = new TreeMap(new TreeComparator());
    }
    
    /**
     * The put(Period p, Object o) method will accept only values of the valueType type.
     * Allows the map to be type-safe (runtime checking).
     * This constructor could be deprecated with JDK 1.5
     * @param valueType the class whose instances the map is allowed to accept as values.
     */
    public OldTreeTemporalAttribute(Class valueType) {
        this.valueType = valueType;
        this.map = new TreeMap(new TreeComparator());
    }
    
    /**
     * @param mapSubscr the object listening for updates.
     */
    OldTreeTemporalAttribute(MapSubscriber mapSubscr) {
        this.valueType = null;
        this.map = new PublishedSortedMap(
                new TreeMap(new TreeComparator())
                , mapSubscr
        );
    }
    
    /**
     * @param mapSubscr the object listening for updates.
     * @param valueType the class whose instances the map is allowed to accept as values.
     */
    OldTreeTemporalAttribute(Class valueType, MapSubscriber mapSubscr) {
        this.valueType = valueType;
        this.map = new PublishedSortedMap(
                new TreeMap(new TreeComparator())
                , mapSubscr
        );
    }
    
    /**
     * Package-private method used internally for unit tests only
     * Do not use: breaks encapsulation.
     * @return the private internal TreeMap.
     */
    SortedMap getInternalMap() {
        return this.map;
    }
    
    public void clear() {
        this.versionNumber++;
        this.map.clear();
    }
    
    public int size() {
        return this.map.size();
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public Object get(Instant instant) {
        Period period = getPeriod(instant);  // find the enclosing period
        if (period == null)
            return null;   // no value found for this period
        else
            return map.get(period);
    }
    
    
    
    
    /*
     * // GET_PERIOD
     */
    public Period getPeriod(Instant instant) {
        
        SortedMap headMap = this.map.headMap(instant);  //headMap < key
        if (!headMap.isEmpty()) {
            Period prev = (Period) headMap.lastKey();
            if (prev.contains(instant)) return prev;
        }
        
        SortedMap tailMap = this.map.tailMap(instant);  //tailMap >= key
        if (!tailMap.isEmpty()) {
            Period next = (Period) tailMap.firstKey();
            if (next.contains(instant)) return next;
        }
        
        return null;  // no period found containing this instant
    }
    
    public boolean containsInstant(Instant instant) {
        return getPeriod(instant) != null ;
    }
    
    
    public boolean containsValue(Object value) {
        if (this.valueType != null) {
            if (! this.valueType.isInstance(value) ) {
                throw new ClassCastException();
            }
        }
        return this.map.containsValue(value);
    }
    
    
    
    public boolean put(Period p, Object value) {
        if (value == null) throw new IllegalArgumentException("null");
        
        if (this.valueType != null) {
            if (! this.valueType.isInstance(value) ) {
                throw new ClassCastException();
            }
        }
        
        boolean ret = this.remove(p);
        //this.versionNumber++; done by remove(p)
        Object o = map.put(p, value);
        if (o != null)  throw new IllegalStateException();  // should never happen
        
        return ret;
    }
    
    
    
    public boolean remove(Period p) {
        if (p == null) throw new IllegalArgumentException("null");
        
        this.versionNumber++;
        boolean removed = false;
        Instant start = p.getStart();
        Instant end = p.getEnd();
        
        //first overlapping period
        Period firstPeriod = this.getPeriod(start);
        if (firstPeriod != null) {
            removed = true;
            
            Object value = map.remove(firstPeriod);
            
            if (firstPeriod.getStart().compareTo(start) < 0) {  // if startPeriod.start < start
                map.put(new Period(firstPeriod.getStart(), start), value);
            }
            
            if (firstPeriod.getEnd().compareTo(end) > 0) { // if startPeriod.end > end
                map.put(new Period(end, firstPeriod.getEnd()), value);      	
            }
        }
        
        //other overlapping periods
        Iterator i = this.map.subMap(start, end).keySet().iterator();
        while (i.hasNext()) {
            removed = true;
            Period act = (Period) i.next();
            if (act.getEnd().compareTo(end) <= 0) {
                i.remove();
            } else {
                if (i.hasNext()) {
                    // this can never happen, unless there is a bug in my code
                    throw new IllegalStateException();
                }
                Object value = map.remove(act);
                map.put(new Period(end, act.getEnd()), value);
                break;
            }
        }
        
        return removed;
        
    }
    
    /**
     * Returns a Set view of the keys (Periods) contained in this map.  The set's
     * iterator will return the keys in ascending order.  The map is backed by
     * this <tt>TreeTemporalAttribute</tt> instance, so changes to this map are reflected in
     * the Set, and vice-versa.  The Set supports element removal, which
     * removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt>, and <tt>clear</tt> operations.  It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this TreeMap.
     */
    public Set periodSet() {
        return this.map.keySet();
    }
    
    public Instant firstInstant() {
        return ((Period) this.map.firstKey()).getStart();
    }
    
    public Instant lastInstant() {
        return ((Period) this.map.lastKey()).getEnd();
    }
    
    public Period firstPeriod() {
        return (Period) this.map.firstKey();
    }
    
    public Period lastPeriod() {
        return (Period) this.map.lastKey();
    }
    
    
    public TemporalAttribute subMap(Period range) {
        if (range == null) throw new IllegalArgumentException("null");
        return new SubMap(range);
    }
    
    public String toString() {
        return map.toString();
    }
    
    /*****************************************************************************
     * The object delivered by TreeTemporalSet.subMap(Period)
     */
    private class SubMap extends AbstractTemporalAttribute
    {
        private final Period range;
        private SortedMap subMap;
        private transient int subMapVersionNumber;
        
        SubMap(Period p) {
            this.range = p;
        }
        
        private SortedMap getSubMap() {
            // if the subMap is still up to date, return it
            if (subMapVersionNumber == OldTreeTemporalAttribute.this.versionNumber) return subMap;
            
            // then refresh the submap
            Period firstPeriod = OldTreeTemporalAttribute.this.getPeriod(range.getStart());
            Instant firstLimit = firstPeriod == null ? range.getStart() : firstPeriod.getStart();
            subMapVersionNumber = OldTreeTemporalAttribute.this.versionNumber;
            return subMap = map.subMap(firstLimit, range.getEnd());
        }
        
        public void clear() {
            OldTreeTemporalAttribute.this.remove(range);
        }
        
        public int size() {
            /*Period firstPeriod = TreeTemporalAttribute.this.getPeriod(range.getStart());
             if (firstPeriod != null
             &&  firstPeriod.getStart().compareTo(range.getStart()) < 0) {
             return getSubMap().size() + 1;
             }*/
            return getSubMap().size();
        }
        
        public boolean isEmpty() {
            return getSubMap().isEmpty();
        }
        
        public Object get(Instant instant) {
            if (!range.contains(instant)) {
                throw new IllegalArgumentException("instant out of range");
            }
            return OldTreeTemporalAttribute.this.get(instant);
        }
        
        public Period getPeriod(Instant instant) {
            if (!range.contains(instant)) {
                throw new IllegalArgumentException("instant out of range");
            }
            return OldTreeTemporalAttribute.this.getPeriod(instant).intersect(range);
        }
        
        
        public boolean containsInstant(Instant instant) {
            if (!range.contains(instant)) {
                throw new IllegalArgumentException("instant out of range");
            }
            return OldTreeTemporalAttribute.this.containsInstant(instant);
        }
        
        
        public boolean containsValue(Object value) {
            return getSubMap().containsValue(value);
        }
        
        
        public boolean put(Period p, Object value) {
            if (!range.contains(p)) {
                throw new IllegalArgumentException("period out of range");
            }
            return OldTreeTemporalAttribute.this.put(p, value);
        }
        
        public boolean remove(Period p) {
            if (!range.contains(p)) {
                throw new IllegalArgumentException("period out of range");
            }
            return OldTreeTemporalAttribute.this.remove(p);
        }
        
        
        public boolean putAll(TemporalAttribute tm) {
            if (!range.contains(tm.extent())) {
                throw new IllegalArgumentException("period out of range");
            }
            return OldTreeTemporalAttribute.this.putAll(tm);
        }
        
        public Set periodSet() {
            return new PeriodSetView();
        }
        
        public Instant firstInstant() {
            Period firstPeriod = OldTreeTemporalAttribute.this.getPeriod(range.getStart());
            if (firstPeriod != null) return range.getStart();
            return ((Period) getSubMap().firstKey()) .getStart();
        }
        
        public Instant lastInstant() {
            Period lp = (Period) getSubMap().lastKey();
            return lp.getEnd().compareTo(range.getEnd()) > 0 ? range.getEnd() : lp.getEnd();
        }
        
        public Period firstPeriod() {
            return ((Period) getSubMap().firstKey()).intersect(range);
        }
        
        public Period lastPeriod() {
            return ((Period) getSubMap().lastKey()).intersect(range);
        }
        
        public TemporalAttribute subMap(Period subRange) {
            if (!this.range.contains(subRange)) {
                throw new IllegalArgumentException("period out of range");
            }
            return new SubMap(subRange);
        }
        
        public String toString() {
            return getSubMap().toString();
            //throw new java.lang.UnsupportedOperationException("Method not yet implemented.");
        }
        
        
        private class PeriodSetView extends AbstractSet {
            private Set subSet;
            private transient int subSetVersionNumber;
            
            public PeriodSetView() {
            }
            
            private Set getSubSet() {
                // if the subMap is still up to date, return it
                if (subSetVersionNumber == OldTreeTemporalAttribute.this.versionNumber) return subSet;
                
                // then refresh the subset
                subSetVersionNumber = OldTreeTemporalAttribute.this.versionNumber;
                return subSet = getSubMap().keySet();
            }
            
            public int size() {
                return getSubSet().size();
            }
            
            public boolean isEmpty() {
                return getSubSet().isEmpty();
            }
            
            public boolean contains(Object o) {
                return getSubSet().contains(o);
            }
            
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }
            
            
            public Iterator iterator() {
                return new SetIterator();
            } // iterator()
            
            private class SetIterator implements Iterator
            {
                private transient final int iteratorVersionNumber;
                private transient final Iterator subIterator;
                
                SetIterator() {
                    iteratorVersionNumber = OldTreeTemporalAttribute.this.versionNumber;
                    subIterator = getSubSet().iterator();
                }
                
                public boolean hasNext() {
                    if (iteratorVersionNumber != OldTreeTemporalAttribute.this.versionNumber)
                        throw new ConcurrentModificationException();
                    return subIterator.hasNext();
                }
                
                public Object next() {
                    if (iteratorVersionNumber != OldTreeTemporalAttribute.this.versionNumber)
                        throw new ConcurrentModificationException();
                    Period p = (Period) subIterator.next();
                    return p.intersect(range);
                }
                
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
            } // SetIterator
            
            
        } // periodSetView  */
        
        public Set entrySet() {
            // @TODO
            throw new UnsupportedOperationException("Not implemented yet");
        } 
        
    } // SubMap
    
    
    
    
    /**
     * Compares Periods and Instants by using the Period start instant.
     * Storing instants in the TreeMap would have been more logical, but that would
     * have needed an additional object containing period+value.
     * The performace difference is not clear, but it's clear the code is more simple in this way.
     */
    static private class TreeComparator implements Comparator
    {
        /**
         * If both objects are Periods, they are compared as periods
         * otherwise they are compared as instants
         * (taking the start instant if one is a Period).
         * It would be more OO to implement a common method in Period and
         * in Instant, but I want to keep the Instant interface as simple as possible.
         */
        public int compare(Object o1, Object o2) {
            Instant i1, i2;
            
            if (o1 instanceof Period) {
                if (o2 instanceof Period)
                    return ((Period) o1).compareTo((Period) o2);
                else
                    i1 = ((Period) o1).getStart();
            } else { // o1 instanceof Instant
                i1 = (Instant) o1;
                if (o2 instanceof Period)
                    o2 = ((Period) o2).getStart();
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
    
    
    
    
    /* (non-Javadoc)
     * @see net.sf.jtemporal.TemporalAttribute#entrySet()
     */
    public Set entrySet() {
        // @TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    
}