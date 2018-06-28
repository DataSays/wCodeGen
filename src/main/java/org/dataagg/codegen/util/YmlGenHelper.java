package org.dataagg.codegen.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dataagg.util.collection.StrObj;

/**
 * Created by watano on 2017/2/7.
 */
public class YmlGenHelper extends CodeGenHelper {
	@Override
	public String indentStr() {
		return "  ";
	}

	private String val(Object value) {
		return value.toString();
	}

	private String key(String key) {
		return indent() + key;
	}

	public YmlGenHelper set(String key, Object value) {
		appendln(key(key) + ": " + val(value));
		return this;
	}

	public YmlGenHelper setNotNull(String key, Object value) {
		if (value != null) {
			appendln(key(key) + ": " + val(value));
		}
		return this;
	}

	public YmlGenHelper inlineList(String key, Object... values) {
		appendln(key(key) + ": " + getInlineLst(values));
		return this;
	}

	public String getInlineLst(Object... values) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int i = 0;
		for (Object v : values) {
			sb.append(val(v));
			if (i < values.length - 1) {
				sb.append(", ");
			}
			i++;
		}
		sb.append("]");
		return sb.toString();
	}

	public YmlGenHelper inlineMap(String key, Object... kvs) {
		append(key(key) + ": {");
		for (int i = 0; i < kvs.length - 1; i += 2) {
			append(key(kvs[i].toString()) + ": " + val(kvs[i + 1]));
			if (i < kvs.length - 3) {
				append(",");
			}
		}
		appendln("}");
		return this;
	}

	public YmlGenHelper beginLst(String key) {
		appendln(key(key) + ":");
		beginIndent();
		return this;
	}

	public YmlGenHelper addLst(String value) {
		appendln(indent() + "- " + val(value));
		return this;
	}

	public YmlGenHelper endLst() {
		endIndent();
		return this;
	}

	public YmlGenHelper beginMap(String key) {
		appendln(key(key) + ":");
		beginIndent();
		return this;
	}

	public YmlGenHelper endMap() {
		endIndent();
		return this;
	}

	public YmlGenHelper comment(String comment) {
		if (comment != null) {
			appendln(comment);
		} else {
			appendln("");
		}
		return this;
	}

	public YmlGenHelper addSetData(String key, Set<?> data) {
		beginLst(key);
		data.forEach(o -> {
			addLst(o.toString());
		});
		endLst();
		return this;
	}

	public <T> YmlGenHelper addSetData(String key, List<T> data) {
		Set<T> data2 = new HashSet<>();
		if (data != null) {
			data2.addAll(data);
		}
		return addSetData(key, data2);
	}

	public YmlGenHelper addSetData2(String key, Object... data) {
		Set<Object> data2 = new HashSet<>();
		if (data != null) {
			for (Object o : data) {
				data2.add(o);
			}
		}
		return addSetData(key, data2);
	}
}
