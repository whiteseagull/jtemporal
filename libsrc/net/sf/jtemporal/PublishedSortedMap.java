/* This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with NO WARRANTIES given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal;

import java.util.SortedMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.Iterator;


/**
 * Decorates a SortedMap allowing a single third object to subscribe to updates.
 * That's more efficient in terms of performance than using events and listeners.
 * If you need events and multiple listener, you need to create an intermediate
 * map subscriber class, which creates and dispatches the events.  <BR>
 * This class is optimized for the jtemporal use, that's
 * why the class is not public (also some methods not used by JTemporal are not
 * implemented). However with minimal changes, this class can become generic.
 * @author Thomas A Beck
 * @version $Id: PublishedSortedMap.java,v 1.2 2009/01/02 17:05:03 tabeck Exp $
 */

class PublishedSortedMap<K, V> implements SortedMap<K, V> {

    private final SortedMap<K, V> map;
    private final MapSubscriber<K, V> subscriber;

  // compulsory constructor
    public PublishedSortedMap(SortedMap<K, V> decoratedMap,
                              MapSubscriber<K, V> sub) {
        map = decoratedMap;
        subscriber = sub;
  }

  //accessors
    public SortedMap<K, V> getDecoratedMap() {
       return map;
  }

    public MapSubscriber<K, V> getSubscriber() {
        return subscriber;
  }

  // notifications
    private void firePut(K key, V value) {
        subscriber.put(this, key, value);
  }

    private void fireRemoved(K key) {
        subscriber.removed(this, key);
  }

  // read-only methods

    public Comparator<? super K> comparator() {
        return map.comparator();
  }

    public K firstKey() {
        return map.firstKey();
  }

    public K lastKey() {
        return map.lastKey();
  }

  public int size() {
    return this.map.size();
  }
  public boolean isEmpty() {
    return this.map.isEmpty();
  }
  public boolean containsKey(Object key) {
    return this.map.containsKey(key);
  }
  public boolean containsValue(Object value) {
    return this.map.containsValue(value);
  }

    public V get(Object key) {
        return map.get(key);
  }

    @Override
  public boolean equals(Object o) {
        return map.equals(o);
  }

    @Override
  public int hashCode() {
    return this.map.hashCode();
  }

  // submaps
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        SortedMap<K, V> subMap = map.subMap(fromKey, toKey);
        return new PublishedSortedMap<K, V>(subMap, subscriber);
  }

    public SortedMap<K, V> headMap(K toKey) {
        SortedMap<K, V> subMap = map.headMap(toKey);
        return new PublishedSortedMap<K, V>(subMap, subscriber);
  }

    public SortedMap<K, V> tailMap(K fromKey) {
        SortedMap<K, V> subMap = map.tailMap(fromKey);
        return new PublishedSortedMap<K, V>(subMap, subscriber);
    }

  // changes
    public V put(K key, V value) {
        V replaced = map.put(key, value);
        firePut(key, value);
    return replaced;
  }

    public V remove(Object key) {
        V removed = map.remove(key);
        fireRemoved((K)key);
    return removed;
  }

  /**
   * Internaly, instead caling clear(); this method iterates and removes all the
   * elements one by one.  That allows a subscriber to receive the detail of the
   * deletions.
   */
  public void clear() {
    Iterator<K> i = this.keySet().iterator();
    while (i.hasNext()) {
      i.next();
      i.remove();
    }
    //consistency check
    if (!this.map.isEmpty()) {
      // should never happen
      throw new IllegalStateException("The published SortedMap is inconsistent");
    }
  }

    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
      put(e.getKey(), e.getValue());
    }
  }

    public Set<K> keySet() {
        Set<K> decSet = map.keySet();
        return new KeySet(decSet);
  }

    public Collection<V> values() {
    // not used by JTemporal
    // must be implemented if used outside JTemporal
    throw new UnsupportedOperationException();
    //return this.map.values();
  }

    public Set<Map.Entry<K, V>> entrySet() {
    // not used by JTemporal
    // must be implemented if used outside JTemporal
    throw new UnsupportedOperationException();
    //return this.map.entrySet();
  }

    private class KeySet extends AbstractSet<K>
  {
        final Set<K> decSet;

        public KeySet(Set<K> decoratedSet) {
            decSet = decoratedSet;
    }

        @Override
    public int size() {
      return this.decSet.size();
    }

        @Override
    public boolean isEmpty() {
      return this.decSet.isEmpty();
    }

        @Override
    public boolean contains(Object o) {
      return this.decSet.contains(o);
    }

        @Override
    public boolean remove(Object o) {
      boolean b = this.decSet.remove(o);
      PublishedSortedMap.this.fireRemoved((K)o);
      return b;
    }

        @Override
        public Iterator<K> iterator() {
            Iterator<K> decIterator = decSet.iterator();
      return new SetIterator(decIterator);
    } // iterator()

        private class SetIterator implements Iterator<K>
    {
            private final Iterator<K> decIterator;
            private K lastRead = null;

            SetIterator(Iterator<K> decoratedIterator) {
                decIterator = decoratedIterator;
      }

      public boolean hasNext() {
	return decIterator.hasNext();
      }

            public K next() {
	return lastRead = decIterator.next();
      }

      public void remove() {
	decIterator.remove();
	PublishedSortedMap.this.fireRemoved(lastRead);
      }

    } // SetIterator


  } // class KeySet */

} // class PublishedSortedMap
