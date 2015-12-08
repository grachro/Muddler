package com.grachro.muddler;

import java.util.HashMap;

public class TableRecord extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public static class TableEnptyRecord extends TableRecord {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public Object get(Object key) {

		Object o = super.get(key);
		if (o != null) {
			return o;
		}

		if (key instanceof String) {
			String u = ((String) key).toUpperCase();
			return super.get(u);
		}

		return null;
	}

}