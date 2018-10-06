/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
 Copyright (C) 2002 by the author(s).
 Distributable under LGPL license version 2.1 or later, 
 with NO WARRANTIES given or implied.
 See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.*;

/**
 * Maintains internally a TreeMap over the periods and an HashMap over the values
 * so that query performances are acceptable for queries in both dimensions even when
 * the collection is very large.                          <BR><BR>
 * <b>Note: this implementation is not synchronized.</b> If multiple
 * threads access the map concurrently, and at least one of the threads modifies
 * the map structurally, it <i>must</i> be synchronized externally.  (A
 * structural modification is any operation that adds or deletes one or more
 * mappings; merely changing the value associated with an existing key is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.  If no
 * such object exists, the map should be "wrapped" using the
 * <tt>SynchronizedTemporalMultiMap.getInstance</tt> method.  This is best done at creation
 * time, to prevent accidental unsynchronized access to the map:
 * <pre>
 *     TemporalSet tm = SynchronizedTemporalMap.getInstance(new TreeTemporalSet(...));
 * </pre><p>
 *
 * Note the synchronizing proxies are not available yet. <BR>
 *
 * <BR>(<A HREF="../../../uml/TreeTemporalSet.gif">internal structure</a>)<BR>
 * In a future version, this structure will be optimized in order to reduce the
 * memory usage when the map contains few values.
 * @author Thomas A Beck
 * @version $Id: TreeTemporalSet.java,v 1.14 2009/01/02 17:05:03 tabeck Exp $
 * @param <V> the type of the value
 * @deprecated
 */
public class TreeTemporalSet<V> extends AbstractTemporalSet<V>
{
    // primary data structure: indexed first by value
    // mapkey=value  mapvalue=TemporalUnaryMaps,
    private final Map<V, TemporalAttribute<V>> valueMap = new HashMap<V, TemporalAttribute<V>>();
    
    // secondary data structure: indexed first by endInstant
    // mapkey=endInstant mapvalue=TimeNode
    // this implementation is provisional, will be replaced later by a
    // bi-dimensional indexing strategy (as some temporal databases do)
    private final SortedMap<Instant, TimeNode<V>> timeMap = new TreeMap<Instant, TimeNode<V>>();
    
    // accepted type for values
    private final Class valueType;
    
    /**
     * Default constructor.  Values of any type will be accepted.
     */
    public TreeTemporalSet() {
        this.valueType = null;
    }
    
    /**
     * @param valueType values must be of this type.
     */
    public TreeTemporalSet(Class valueType) {
        this.valueType = valueType;
    }
    
    
    // read methods //
    
    public Instant firstInstant(V value) {
        return getTemporalUnaryMap(value).firstInstant();
    }

    public Instant lastInstant(V value) {
        return getTemporalUnaryMap(value).lastInstant();
    }

    public Period getPeriod(Instant instant, V value) {
        return getTemporalUnaryMap(value).getPeriod(instant);
    }
    public boolean contains(Instant instant, Object value) {
        TemporalAttribute<V> ta = this.getTemporalUnaryMapOrNull(value);
        return ta == null ? false : ta.containsInstant(instant);
    }
    public boolean containsValue(Object value) {
        TemporalAttribute<V> ta = this.getTemporalUnaryMapOrNull(value);
        return ta == null ? false : !ta.isEmpty();
    }

    public Period firstPeriod(V value) {
        return getTemporalUnaryMap(value).firstPeriod();
    }

    public Period lastPeriod(V value) {
        return getTemporalUnaryMap(value).lastPeriod();
    }

    public boolean isEmpty() {
        return this.timeMap.isEmpty();
    }
    
    public int sizeFor(V value) {
        TemporalAttribute<V> ta = getTemporalUnaryMapOrNull(value);
        return ta == null ? 0 : ta.size();
    }
    
    public int sizeAt(Instant instant) {
        throw new RuntimeException("TODO: not implemented yet.");
    }
    
    public int size() {
        throw new RuntimeException("TODO: not implemented yet.");
    }
    
    public int sizeValues() {
        throw new RuntimeException("TODO: not implemented yet.");
    }
    
    /* (non-Javadoc)
     * @see net.sf.jtemporal.TemporalSet#entrySet()
     */
    public Set<TimedObject<V>> entrySet() {
        throw new RuntimeException("TODO: not implemented yet.");
    }
    
    /* (non-Javadoc)
     * @see net.sf.jtemporal.TemporalSet#isEmpty(net.sf.jtemporal.Instant)
     */
    public boolean isEmpty(Instant i) {
        throw new RuntimeException("TODO: not implemented yet.");
    }
    
    // sub-structures //
    
    /* public TemporalSet subMap(Period p) {
     if (p == null) throw new IllegalArgumentException("null");
     //@todo: implement this jtemporal.AbstractTemporalSet abstract method
      throw new UnsupportedOperationException("Method not implemented yet, and it is not clear whether it is feasible :-)");
      }*/
    public Set<Period> periodSet(V value) {
        if (value == null) throw new IllegalArgumentException("null");
        TemporalAttribute<V> ta = this.getTemporalUnaryMapOrNull(value);
        if (ta == null) return Collections.EMPTY_SET;
        return ta.periodSet();
    }
    
    public Set<V> valueSet() {
    	return Collections.unmodifiableSet(this.valueMap.keySet());
    }
    
    public Set<V> valueSet(Instant instant) {
        if (instant == null) throw new IllegalArgumentException("null");
        return new ValueSetView(instant);
    }
    
    private class ValueSetView extends AbstractSet<V> {
        private final Instant instant;
        //private final SortedMap tailMap; // efficient for recent instants
        
        public ValueSetView(Instant instant) {
            this.instant = instant;
            //this.tailMap = TreeTemporalSet.this.timeMap.tailMap(instant);
        }
        
        @Override
        public int size() {
            Iterator<V> i = iterator();
            int size = 0;
            for (;i.hasNext(); size++)
                i.next();
            return size;
        }
        
        @Override
        public boolean isEmpty() {
            Iterator<V> i = iterator();
            return (!i.hasNext()) ;
        }
        
        @Override
        public boolean contains(Object value) {
            Iterator<V> i = iterator();

            while (i.hasNext()) {
                if (i.next().equals(value)) return true;
            }

            return false;
        }
        
        @Override
        public boolean remove(Object value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueSetIterator();
        }
        
        private class ValueSetIterator implements Iterator<V>
        {
            private Iterator<Map.Entry<Instant, TimeNode<V>>> endInstantIterator;
            private Iterator<TimedObject<V>> timeNodeIterator;
            private TimedObject<V> theNext; // buffers one value in advance
            private boolean     hasNext;      // value available in the buffer
            
            private boolean advanceInnerSuccessful() {
                while (timeNodeIterator.hasNext()) {
                    TimedObject<V> to = timeNodeIterator.next();

                    if (to.getPeriod().contains(instant)) {
                        hasNext = true;
                        theNext = to;
                        return true;
                    }
                }
                
                // no next
                timeNodeIterator = null; // iterator consumed, garbage
                return false;
            }
            
            private boolean advanceOuterSuccessful() {
                if (!endInstantIterator.hasNext()) {
                    endInstantIterator = null;  // iterator consumed
                    hasNext = false;
                    theNext = null;
                    return false;
                }

                Map.Entry<Instant, TimeNode<V>> entry = endInstantIterator.next();
                TimeNode<V> tn = entry.getValue();
                timeNodeIterator = tn.iterator();
                return true;
            }
            
            private void advance() {
                while (true) {
                    if (advanceInnerSuccessful())  break;
                    else {
                        if (advanceOuterSuccessful())  continue;
                        else {
                            hasNext = false;
                            break;
                        }
                    }
                } // while
            }
            
            public ValueSetIterator() {
                endInstantIterator = timeMap.tailMap(instant).entrySet().iterator();

                if (advanceOuterSuccessful()) {
                    advance();
                }
            }
            
            public boolean hasNext() {
                return hasNext;
            }

            public V next() {
                if (!hasNext)
                    throw new NoSuchElementException();

                TimedObject<V> to = theNext;
                advance();
                return to.getValue();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        } // ValueSetIterator
    } // ValueSetView
    
    // updates //
    
    public void clear() {
        this.valueMap.clear();
        this.timeMap.clear();
    }
    
    public boolean removeValue(V value) {
        if (value == null)
            throw new IllegalArgumentException("null");
        
        TemporalAttribute<V> ta = getTemporalUnaryMap(value);
        ta.clear();
        this.valueMap.remove(ta);
        return true; // @TODO = SHORT TERM HACK
    }
    
    public boolean remove(Period p, V value) {
        if (value == null || p == null)
            throw new IllegalArgumentException("null");

        TemporalAttribute<V> ta = getTemporalUnaryMap(value);
        boolean result = ta.remove(p);
        if (ta.isEmpty()) this.valueMap.remove(ta);
        return result;
    }
    
    public boolean removePeriod(Period p) {
        boolean ret = false;
        if (p == null) throw new IllegalArgumentException("null");
        for (TemporalAttribute<V> ta : valueMap.values()) {
            ret |= ta.remove(p);
        }

        return ret;
    }
    
    /*
     * javadoc inherited from interface
     */
    public boolean put(Period p, V value) {
        if (value == null) throw new IllegalArgumentException("null");
        
        if (this.valueType != null) {
            if (! this.valueType.isInstance(value) ) {
                throw new ClassCastException();
            }
        }
        
        TemporalAttribute<V> ta = this.getTemporalUnaryMapOrNull(value);
        // check whether a new TemporalAttribute must be created for this value
        if (ta == null) {
            ta = new OldTreeTemporalAttribute(this.valueType, new SortedMapSubscriber<V>(value));
            valueMap.put(value, ta);
        }
        
        return ta.put(p, value);
        
    } // put
    
    
    
    // tools //
    
    /**
     * @return the TemporalAttribute associated to the value,
     * or null is this value is unknown
     */
    private TemporalAttribute<V> getTemporalUnaryMapOrNull(Object value) {
        TemporalAttribute<V> ta = valueMap.get(value);
        return ta;
    }
    
    /**
     * @return the TemporalAttribute associated to the value, or null if not
     * @throws NoSuchElementException is this value is unknown
     */
    private TemporalAttribute<V> getTemporalUnaryMap(V value) {
        TemporalAttribute<V> ta = valueMap.get(value);

        if (ta == null) throw new NoSuchElementException(valueMap.toString());

        return ta;
    }
    
    /**
     * This inner class has been created because it is impossible to implement a non public insterface
     * without declaring the implementing methods as public :-(
     * (http://developer.java.sun.com/developer/bugParade/bugs/4456057.html)
     */
    private final NodeParent<V> nodeDeadListener = new NodeParent<V>() {
        public void nodeDead(TimeNode<V> node) {
            Object r = TreeTemporalSet.this.timeMap.remove(node.getEndInstant());
            if (r == null) {
                throw new IllegalStateException();
            }
        }
    };
    
    /**
     * Listening the inner loop treeMaps, allows to know what is really changed,
     * because for example a "put" invocation can partially overwrite preexisting
     * information. Also a user can make updates using sets or iterators.
     * Whith this "cleaned" information we can now easly replicate the information
     * in the structure having orthogonal indexing.
     * These methods are called by the TemporalUnaryMaps which are inside the valueMap
     * Maintains TreeTemporalSet.this.timeMap in Synch.
     */
    class SortedMapSubscriber<K> implements MapSubscriber<Period, V>
    {
        // the value that the client has added with a Period to the UnaryMap
        // there is one subscriber per UnaryMap
        private final V value;
        
        public SortedMapSubscriber(V value) {
            this.value = value;
            //this.timeMap = TreeTemporalSet.this.timeMap;
        }
        
        public void put(Map<Period, V> publisher, Period period, V val) {
            // pre-conditions
            if (!this.value.equals(val)) {
                throw new IllegalStateException(val.toString());
            }
            
            // in a later version, to improve performances,
            // TimedObjects will not be created anymore
            TimedObject<V> to = new SimpleTimedObject<V>(period, val);
            TimeNode<V> tNode = timeMap.get(period.getEnd());

            if (tNode == null) {
                tNode = new TimeNode<V>(TreeTemporalSet.this.nodeDeadListener, to);
                Object r = TreeTemporalSet.this.timeMap.put(period.getEnd(), tNode);
                if (r != null) {
                    throw new IllegalStateException(r.toString());
                }
            }
            else {
                tNode.add(to);
            }
        }
        
        public void removed(Map<Period, V> publisher, Period period) {
            
            // in a later varsion, TimedObjects will not be created anymore, (@TODO)
            TimedObject<V> to = new SimpleTimedObject<V>(period, this.value);

            TimeNode<V> tn = timeMap.get(period.getEnd());
            
            if (tn == null) {
                throw new NoSuchElementException(period.toString());
            }
            
            tn.remove(to);
        }
        }
}
