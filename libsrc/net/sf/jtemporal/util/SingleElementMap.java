/*
   This file is part of the JTemporal framework (http://jtemporal.sf.net).
   Copyright (C) 2002 by the author(s).
   Distributable under LGPL license version 2.1 or later, 
   with no warranties given or implied.
   See terms of license at gnu.org or www.opensource.org/licenses  */

package net.sf.jtemporal.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This is a read-only Map, contining a single entry. <br>
 * This is intended to be used to replace other implementations
 * of {@linkplain Map} when they contain a single entry, to save memory,
 * and improve read performances.  <br>
 * Thread-safe.
 * @author Thomas A Beck
 * @version $Id: SingleElementMap.java,v 1.5 2008/12/14 17:23:12 tabeck Exp $
 */
public class SingleElementMap<K, V> implements Map<K, V>, Cloneable {
    private final K key;
    private final V value;
        
    /**
     * Unique constructor.
     * @param key as defined in {@linkplain Map.Entry}
     * @param value as defined in {@linkplain Map.Entry}
     */
    public SingleElementMap(K key, V value) {
        this.key = key;
        this.value = value;            
    }

    /* (non-Javadoc)
     * @see java.util.Map#size()
     */
    public int size() {
        // faster than that, it is not possible :-9
        return 1;
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object k) {
        return k.equals(this.key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object v) {
        return v.equals(this.value);
    }

    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection<V> values() {
        // values are not necessarily distinct, but 
        // given that there is a single one, we can use a Set
        return new SingleElementSet<V>(this.value);
    }

    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K,? extends V> t) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set<Map.Entry<K,V>> entrySet() {
        return new SingleElementSet<Map.Entry<K,V>>(new MapEntry<K,V>(key, value));
    }

    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set<K> keySet() {
        return new SingleElementSet<K>(key);
    }

    /* (non-Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    public V get(Object k) {
        if (this.key.equals(k)) return this.value;
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    public V remove(Object k) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public V put(K k, V v) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Map#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
    	if (!(o instanceof Map))
    	    return false
    	;
    	Map<K,V> other = (Map<K,V>)o;
    	if (other.size() != 1)
    	    return false
    	;
    	V otherValue = other.get(this.key);
    	if (otherValue == null) return false;
    	return otherValue.equals(this.value);
    }
    
    /* (non-Javadoc)
     * @see java.util.Map#hashCode()
     */
    @Override
    public int hashCode() {
        return this.key.hashCode() ^ this.value.hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        // since I am immutable, this instance can be shared in any case ;-)
        return this;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{" + key + "=" + value + "}";
    }
    
    private static class MapEntry<W, X>  implements Map.Entry<W, X> {
        private final W key;
        private final X value;

        MapEntry(W key, X value) {
            this.key = key;
            this.value = value;            
        }
        
        public W getKey() {
            return this.key;
        }

        public X getValue() {
            return this.value;
        }

        public X setValue(X value) {
            throw new UnsupportedOperationException();
        }
    } // MapEntry

}
