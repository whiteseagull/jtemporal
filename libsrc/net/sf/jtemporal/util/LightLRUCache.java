/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.*;

/**
 * A lightweight fast LRU cache, based on a HashMap and a linked list. <br>  
 * Reading a value, updates the LRU order.
 * This class is NOT thread-safe. <br>
 * Note the the performance is directly dependent on the performance of the
 * hashcode() and equals() implementation of your key objects. <br>
 * The code is optimized in such a way, that if you query twice in a row the same
 * key, the second call returns instantaneously, without a hashcode lookup.
 * @author Thomas A Beck
 * @version $Id: LightLRUCache.java,v 1.8 2008/12/14 13:30:58 tabeck Exp $
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public class LightLRUCache <K,V>{

	private final int maxSize;
	
	// key:Object value:MapEntry 
    private final Map<K,MapEntry<K,V>> map;
	
	// first entry of the linked list
    private MapEntry<K,V> firstMapEntry = null;
	
	// last entry of the linked list
    private MapEntry<K,V> lastMapEntry = null;
		
	/**
	 * Creates a new LRU cache, of the given size.
	 * @param size the maximum number of entries kept in this cache 
	 */
	public LightLRUCache(int size) {
        maxSize = size;
        map = new HashMap<K,MapEntry<K,V>>(size + 1);
	}
	
	/**
	 * Creates a new LRU cache, of the given size, whose internal HashMap
	 * has the given load factor
	 * @param size the maximum number of entries kept in this cache
	 * @param loadFactor as defined in {@linkplain HashMap#}. 
	 * Default is 0.75, higher saves space, lower is faster. 
	 */
	public LightLRUCache(int size, float loadFactor) {
        maxSize = size;
        map = new HashMap<K,MapEntry<K,V>>(size + 1, loadFactor);
	}
		
    private void addToEndOfList(MapEntry<K,V> entry) {
        if (lastMapEntry == null) {
            firstMapEntry = entry;
            lastMapEntry = entry;
		}
		else {
			//assert this.lastMapEntry.next == null;
			entry.previous = this.lastMapEntry;
			entry.next = null;
			this.lastMapEntry.next = entry;
			this.lastMapEntry = entry;
		}
		this.lastMapEntry = entry;
	}
	
    private void removeFromList(MapEntry<K,V> entry) {
		if (entry.previous == null) {
			this.firstMapEntry = entry.next;
		}
		else {
			entry.previous.next = entry.next;
		}

		if (entry.next == null) {
			this.lastMapEntry = entry.previous;
		}
		else {
			entry.next.previous = entry.previous;
		}

		entry.previous = null;
		entry.next = null;
	}
	
	private void moveToEndOfList(MapEntry<K,V> entry) {
		this.removeFromList(entry);
		this.addToEndOfList(entry);
	}
	
	/**
	 * Adds an new entry to this cache.  If the cache has already reached it's 
	 * maximum size, the Least Recently Used entry is deleted. <br>
	 * @param key
	 * @param value
	 * @return the previous values associated to this key, or null
	 */
	public V put(K key, V value) {
		if (key == null) {
            throw new IllegalArgumentException("null key");
		}

		if (value == null) {
            throw new IllegalArgumentException("null value");
		}
		
		// create the new entry
        MapEntry<K,V> mapEntry = new MapEntry<K,V>();
		mapEntry.key = key;
		mapEntry.value = value;
		this.addToEndOfList(mapEntry);
		
		// put the new entry and remove the lru if appropriate
		MapEntry<K,V> prev = this.map.put(key, mapEntry);

		if (this.map.size() > this.maxSize) {
			this.remove(this.firstMapEntry.key);
			if (this.map.size() != this.maxSize) {
				throw new IllegalStateException(
					"Size of inner map " + map.size()+ " should be " + maxSize
                );
			}
		}

		if (prev == null) {	
			return null;
		}

		this.removeFromList(prev);
		return prev.value;	
	}
	
	
	/**
	 * Retrieves an entry.  Additionally, the retrieved entry becomes the mru.
	 * @param key
	 * @return the value currently associated to this key, or null if not found.
	 */
	public V get(Object key) {
		if (key == null) {
            throw new IllegalArgumentException("null key");
		}

		// try an instantaneous answer (happens often => performance)
        if (lastMapEntry != null) {
        	// try first '==' as it can be faster than 'equals'
        	if (key == lastMapEntry.key || key.equals(lastMapEntry.key)) {
            return lastMapEntry.value;
        }
		}
		
		// instantaneous answer did not succeed, try the normal way
        MapEntry<K,V> mapEntry = map.get(key);

		if (mapEntry == null) return null;
		this.moveToEndOfList(mapEntry);
		return mapEntry.value;	
	}
	
	/**
	 * Removes an entry.
	 * @param key
	 * @return the value currently associated to this key, or null if not found.
	 */
	public V remove(Object key) {
		if (key == null) {
            throw new IllegalArgumentException("null key");
		}

        MapEntry<K,V> mapEntry = map.remove(key);
		if (mapEntry == null) return null;
		this.removeFromList(mapEntry);
		return mapEntry.value;
	}
	
	/**
	 * Empties the cache.
	 */
	public void clear() {
		this.firstMapEntry = null;
		this.lastMapEntry = null; // the gc will to the rest
		map.clear();
	}
	
	/**
	 * The number of entries currently in the cache.
	 * @return The number of entries currently in the cache.
	 */
	public int currentSize() {
		return this.map.size();
	}
	
	/**
	 * The maximum number of entries.
	 * @return The maximum number of entries.
	 */
	public int maxSize() {
		return this.maxSize;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.map.toString();
    }
	
    private static class MapEntry<KK,VV> {
		//long seq;
        MapEntry<KK,VV> previous = null;
        MapEntry<KK,VV> next = null;
        KK key;
        VV value;
	}
	
	/*
	static void testLinkedList() {
		LightLRUCache cache = new LightLRUCache(10);
		assertTrue (cache.firstMapEntry == null);
		assertTrue (cache.lastMapEntry == null);
		////////////////////////////////////////////
		MapEntry e1 = new MapEntry();
		cache.addToEndOfList(e1);
		assertTrue (cache.firstMapEntry == e1);
		assertTrue (cache.lastMapEntry == e1);
		assertTrue (e1.previous == null);
		assertTrue (e1.next == null);
		////////////////////////////////////////////
		MapEntry e2 = new MapEntry();
		cache.addToEndOfList(e2);
		assertTrue (cache.firstMapEntry == e1);
		assertTrue (cache.lastMapEntry == e2);
		assertTrue (e1.previous == null);
		assertTrue (e1.next == e2);
		assertTrue (e2.previous == e1);
		assertTrue (e2.next == null);
		////////////////////////////////////////////
		MapEntry e3 = new MapEntry();
		cache.addToEndOfList(e3);
		assertTrue (cache.firstMapEntry == e1);
		assertTrue (cache.lastMapEntry == e3);
		assertTrue (e1.previous == null);
		assertTrue (e1.next == e2);
		assertTrue (e2.previous == e1);
		assertTrue (e2.next == e3);
		assertTrue (e3.previous == e2);
		assertTrue (e3.next == null);
		////////////////////////////////////////////
		cache.removeFromList(e2);
		assertTrue (cache.firstMapEntry == e1);
		assertTrue (cache.lastMapEntry == e3);
		assertTrue (e1.previous == null);
		assertTrue (e1.next == e3);
		assertTrue (e3.previous == e1);
		assertTrue (e3.next == null);
		assertTrue (e2.previous == null);
		assertTrue (e2.next == null);
		////////////////////////////////////////////
		cache.removeFromList(e1);
		assertTrue (cache.firstMapEntry == e3);
		assertTrue (cache.lastMapEntry == e3);
		assertTrue (e1.previous == null);
		assertTrue (e1.next == null);
		assertTrue (e3.previous == null);
		assertTrue (e3.next == null);
		////////////////////////////////////////////
		cache.removeFromList(e1);
		assertTrue (cache.firstMapEntry == null);
		assertTrue (cache.lastMapEntry == null);
		assertTrue (e1.previous == null);
		assertTrue (e1.next == null);
	}
	private static void assertTrue(boolean b) {
		if (b == false) {
			throw new RuntimeException("assertTrue failed");
		}
	}*/
}

