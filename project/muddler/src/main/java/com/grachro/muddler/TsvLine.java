package com.grachro.muddler;

import java.util.HashMap;

public class TsvLine extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public static class TsvLineNull extends TsvLine {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public Object get(Object key) {

		if (key instanceof String) {
			String u = ((String) key).toUpperCase();
			return super.get(u);
		}
		return super.get(key);
	}

}