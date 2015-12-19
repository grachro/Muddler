package com.grachro.muddler;

public class Field {
	public String fieldName;
	public String caption;
	public String fieldType;

	public Field(String fieldName, String caption) {
		this.fieldName = fieldName;
		this.caption = caption;
	}

	public Field(String fieldName, String caption, String fieldType) {
		this.fieldName = fieldName;
		this.caption = caption;
		this.fieldType = fieldType;
	}
}
