package com.qbanalytix.cognostest.common;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class FieldExclusionStrategy implements ExclusionStrategy {

	private String[] fields;

	public FieldExclusionStrategy(String... fields) {
		this.fields = fields;
	}

	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes fieldAttribute) {
		for (String field : fields) {
			if (fieldAttribute.getName().equals(field)) {
				return true;
			}
		}
		return false;
	}
}
