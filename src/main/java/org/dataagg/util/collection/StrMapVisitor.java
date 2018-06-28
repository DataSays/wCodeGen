package org.dataagg.util.collection;

import java.util.Map;

import org.dataagg.util.lang.ITypeHelper;

public class StrMapVisitor implements ITypeHelper {

	private Map<String, ?> map;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StrMapVisitor(Map map) {
		this.map = map;
	}

	@Override
	public Object get(Object key) {
		if (map != null) { return map.get(key); }
		return null;
	}

	public boolean notNull(String key) {
		return get(key) != null;
	}
}
