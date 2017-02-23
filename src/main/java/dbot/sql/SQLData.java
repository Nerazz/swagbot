package dbot.sql;

/**
 * Created by Niklas on 23.10.2016.
 */
public class SQLData {
	private final String[] strings;
	private final Object[] data;
	private final int size;

	SQLData(String[] strings, Object[] data) {
		this.strings = strings;
		this.data = data;
		size = strings.length;
	}

	public Object get(String s) {
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].equals(s)) return data[i];
		}
		System.out.println("column not found: " + s);//TODO: logger
		return null;
	}

	public String getString(String s) {
		try {
			return get(s).toString();
		} catch(ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getInt(String s) {
		try {
			return (Integer)get(s);
		} catch(ClassCastException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public double getDouble(String s) {
		try {
			return (Double)get(s);
		} catch(ClassCastException e) {
			e.printStackTrace();
		}
		return -1.0;
	}

	public int size() {
		return size;
	}

	public String getString(int i) {
		return strings[i];
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < data.length; i++) {
			s += strings[i] + "=" + data[i] + ", ";
		}
		return s;
	}
}