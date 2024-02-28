package kafka.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A minimal immutable wrapper around a {@link java.util.HashMap}. All operations that normally mutate maps will instead
 * produce an updated immutable map with the desired changes.
 * @param <K> The type of the keys.
 * @param <V> The type of the values.
 */
public class ImmutableMap<K, V> {
    private final HashMap<K, V> mp;

    /**
     * Creates an empty {@link ImmutableMap}.
     * @return The empty map.
     * @param <K> The type of the keys.
     * @param <V> The type of the values.
     */
    public static <K, V> ImmutableMap<K, V> empty() {
        return new ImmutableMap<>();
    }
    private ImmutableMap() {
        this.mp = new HashMap<>();
    }

    private ImmutableMap(HashMap<K, V> mp) {
        this.mp = mp;
    }

    /**
     * Gets a value corresponding to a key from the map if it exists, otherwise return the default value.
     * @param key The key corresponding to the desired value.
     * @param defaultValue The default value if the value corresponding to the key does not exist in the map.
     * @return The value corresponding to the key, or the default value if it doesn't exist.
     */
    public V getOrDefault(Object key, V defaultValue) {
        return mp.getOrDefault(key, defaultValue);
    }

    @Override
    public int hashCode() {
        return mp.hashCode();
    }

    /**
     * Returns a new {@link ImmutableMap} where the input key-value pair has been added to the map. If there is an
     * existing value corresponding to the key in this map, then it is replaced in the new map.
     * @param key The key of the new key-value pair.
     * @param value The value of the new key-value pair.
     * @return The resulting map from assigning the new key-value pair.
     */
    public ImmutableMap<K, V> put(K key, V value) {
        HashMap<K, V> newMap = toMap();
        newMap.put(key, value);
        return new ImmutableMap<>(newMap);
    }

    /**
     * Produces a {@link java.util.HashMap} of all the key-value pairs in this map.
     * @return A {@link java.util.HashMap} of all the key-value pairs in this map.
     */
    public HashMap<K, V> toMap() {
        return (HashMap<K, V>) this.mp.clone();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ImmutableMap)
                && ((ImmutableMap<?, ?>) o).mp.equals(this.mp);
    }

    @Override
    public String toString() {
        return "=====\n" + this.mp.values().stream().map(Object::toString).collect(Collectors.joining("\n")) + "\n=====";
    }

}
