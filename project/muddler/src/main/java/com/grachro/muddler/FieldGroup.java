package com.grachro.muddler;

import java.util.ArrayList;
import java.util.List;

public class FieldGroup {
	private String name;
	private List<Field> fields = new ArrayList<Field>();
	boolean isOdd; // 奇数

	public FieldGroup(String name, List<Field> fields) {
		this.name = name;
		this.fields = fields;
	}

	private static List<Field> toList(Field field) {
		List<Field> fields = new ArrayList<Field>();
		fields.add(field);
		return fields;
	}

	public FieldGroup(String name, Field field) {
		this(name, toList(field));
	}

	public String getName() {
		return name;
	}

	public List<Field> getFields() {
		return fields;
	}

}