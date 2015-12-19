package com.grachro.muddler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FieldGroups extends ArrayList<FieldGroup> {
	private static final long serialVersionUID = 1L;

	public FieldGroups() {
	}

	public FieldGroups(List<FieldGroup> groupList) {
		this.addAll(groupList);
	}

	@Override
	public boolean add(FieldGroup e) {
		e.isOdd = (this.size() % 2 == 1);
		return super.add(e);
	}

	@Override
	public String toString() {
		return FieldGroups.class.getName() + "@" + super.toString();
	}

	public List<String> getAllFields() {
		List<String> result = new ArrayList<String>();

		for (FieldGroup group : this) {
			for (Field f : group.getFields()) {
				result.add(f.fieldName);
			}
		}

		return result;

	}

	public FieldGroups doSomething(Consumer<FieldGroups> somethig) {
		somethig.accept(this);
		return this;
	}
}