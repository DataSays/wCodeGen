package io.github.datasays.util;

/**
 * Created by watano on 2017/2/7.
 */
public class YmlGenHelper extends CodeGenHelper {
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

	public YmlGenHelper inlineList(String key, Object... values) {
		append(key(key) + ": [");
		int i = 0;
		for (Object v : values) {
			append(val(v));
			if (i < values.length - 1) {
				append(", ");
			}
			i++;
		}
		appendln("]");
		return this;
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
}
