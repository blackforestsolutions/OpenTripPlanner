package org.opentripplanner.common;

import java.util.HashMap;

/**
 * A HashMap that has been extended to track the greatest or smallest value for each key.
 * Note that this does not change the meaning of the 'put' method. It adds two new methods that add the min/max
 * behavior. This class used to be inside SimpleIsochrone.
 */
public class MinMap<K, V extends Comparable<V>> extends HashMap<K, V> {
    private static final long serialVersionUID = -23L;

    /**
     * Put the given key-value pair in the map if the map does not yet contain the key, or if
     * the value is less than the existing value for the same key.
     * @return whether the key-value pair was inserted in the map.
     */
    public boolean putMin(K key, V value) {
        V oldValue = this.get(key);
        if (oldValue == null || value.compareTo(oldValue) < 0) {
            this.put(key, value);
            return true;
        }
        return false;
    }

}
