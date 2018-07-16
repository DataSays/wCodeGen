package org.dataagg.util.collection;

import java.util.ArrayList;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StrObj(Map map) {
		super();
		putAll(map);
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

	public boolean has(String key) {
		return get(key) != null;
	}

	public <T> T getAs(String key, Class<T> cls) {
		return has(key) ? converter2().convertType(get(key), cls) : null;
	}

	public String eval(String text) {
		String out = text;
		for (String key : keySet()) {
			out = StringUtil.replace(out, "${" + key + "}", get(key).toString());
		}
		return out;
	}

	public void mergeAll(StrObj data2) {
		for (String key : data2.keySet()) {
			merge(key, data2.get(key));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void merge(String key, Object value) {
		if (value == null) { return; }
		Object currValue = get(key);
		if (currValue == null) {
			put(key, value);
		} else if (currValue instanceof Map && value instanceof Map) {
			StrObj obj = mapVal(key);
			Map vObj = (Map) value;
			for (Object k : vObj.keySet()) {
				obj.merge(k.toString(), vObj.get(k));
			}
			put(key, obj);
		} else if (currValue instanceof List) {
			List<Object> lst = listVal(key, Object.class);
			if (value instanceof List) {
				for (Object o : (List<Object>) value) {
					if (!lst.contains(o)) {
						lst.add(o);
					}
				}
			} else if (value instanceof Object[]) {
				for (Object o : (Object[]) value) {
					if (!lst.contains(o)) {
						lst.add(o);
					}
				}
			} else {
				lst.add(value);
			}
			put(key, lst);
		} else {
			put(key, value);
		}
	}
}
