package dbot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niklas on 30.09.16.
 */
public class DataMap<K, V> {
	private final List<Pair> pairs = new ArrayList<>();

	public DataMap() {}

	public void put(K key, V value) {
		pairs.add(new Pair(key, value));
	}

	public int size() {
		return pairs.size();
	}

	public K getKey(int i) {
		return pairs.get(i).getKey();
	}

	public K getKey(K key) {
		for (Pair pair : pairs) {
			if (pair.key.equals(key)) return pair.key;
		}
		return null;
	}

	public int getKeyIndex(K key) {
		for (int i = 0; i < pairs.size(); i++) {
			if (pairs.get(i).key.equals(key)) return i;
		}
		return -1;
	}

	public void removeKey(K key) {
		pairs.remove(getKeyIndex(key));//TODO: -1 abfangen?
	}

	public V getValue(int i) {
		return pairs.get(i).getValue();
	}

	public V getValue(V value) {
		for (Pair pair : pairs) {
			if (pair.value.equals(value)) return pair.value;
		}
		return null;
	}

	public V getValueOfKey(K key) {
		for (Pair pair : pairs) {
			if (pair.key.equals(key)) return pair.value;
		}
		return null;//TODO: lieber exception?
	}

	public ArrayList<K> getKeysOfValue(V value) {
		ArrayList<K> keyList = new ArrayList<>();
		for (Pair pair : pairs) {
			if (pair.value.equals(value)) keyList.add(pair.key);
		}
		return keyList;
	}

	public ArrayList<K> getKeysOfKey(K key) {
		ArrayList<K> keyList = new ArrayList<>();
		for (Pair pair : pairs) {
			if (pair.key.equals(key)) keyList.add(pair.key);
		}
		return keyList;
	}

	public boolean containsKey(K key) {
		return key == null || getKey(key) != null;
	}

	public boolean containsValue(V value) {
		return value == null || getValue(value) != null;
	}

	public void clear() {
		pairs.clear();
	}

	/*public boolean contains(Object o) {
		return pairs.contains(o);
	}*/

	private class Pair {
		private final K key;
		private V value;

		private Pair(K key, V value) {
			this.key = key;
			this.value = value;
		}

		private K getKey() {
			return key;
		}

		private void setValue(V value) {
			this.value = value;
		}

		private V getValue() {
			return value;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null) return false;
			if (this == that) return true;
			if (!that.getClass().equals(getClass())) return false;
			return (this.key.equals(((Pair)that).key));//TODO: suppresswarnings?; value miteinbeziehen?
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}
	}
}
