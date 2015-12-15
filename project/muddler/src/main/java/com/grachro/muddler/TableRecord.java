package com.grachro.muddler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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

	public String insertSqlForSqliet3(String tableName, List<String> fieldNames) {
		StringBuilder sb = new StringBuilder();

		sb.append("insert into ").append(tableName).append(" (");
		sb.append(StringUtils.join(fieldNames, ","));
		sb.append(" ) values (");

		boolean first = true;
		for (String fieldName : fieldNames) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}

			Object value = this.get(fieldName);
			if (value instanceof Integer) {
				sb.append(this.get(fieldName));
			} else if (value instanceof Double) {
				sb.append(this.get(fieldName));
			} else if (value instanceof Float) {
				sb.append(this.get(fieldName));
			} else if (value instanceof BigDecimal) {
				sb.append(this.get(fieldName));
			} else if (value instanceof String) {
				String s = (String) value;
				s = s.replaceAll("'", "''");
				sb.append("'").append(s).append("'");
			} else {
				sb.append("'").append(this.get(fieldName)).append("'");
			}

		}
		sb.append(")");

		return sb.toString();
	}

}