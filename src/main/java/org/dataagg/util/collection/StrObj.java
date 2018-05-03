package org.dataagg.util.collection;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dataagg.util.lang.ITypeHelper;

import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;

public class StrObj extends LinkedHashMap<String, Object> implements ITypeHelper {
	private static final long serialVersionUID = 6451063387258568431L;

	public static Set<String> add4Set(Set<String> set, String key) {
		if (set == null) {
			set = new HashSet<>();
		}
		set.add(key);
		return set;
	}

	public static <T> Map<String, T> add4Set(Map<String, T> map, String key, T val) {
		if (map == null) {
			map = new Hashtable<>();
		}
		map.put(key, val);
		return map;
	}

	public <T> void add4Set(String key, T val) {
		add4Set(this, key, val);
	}

	public StrObj() {
		super();
	}

	public StrObj(Object... allkv) {
		super();
		addAll(allkv);
	}

	public void addAll(Object... allkv) {
		if (allkv != null && allkv.length % 2 == 0) {
			for (int i = 0; i + 1 < allkv.length; i += 2) {
				put(allkv[i].toString(), allkv[i + 1]);
			}
		}
	}

	public void putWithoutNull(String key, Object value) {
		if (value != null) {
			put(key, value);
		}
	}

	public void put(String key, Object value, String defaultValue) {
		if (value == null && defaultValue != null) {
			put(key, defaultValue);
			return;
		}
		put(key, value);
	}

	public String eval(String text) {
		String out = text;
		for (String key : keySet()) {
			out = StringUtil.replace(out, "${" + key + "}", get(key).toString());
		}
		return out;
	}
}
