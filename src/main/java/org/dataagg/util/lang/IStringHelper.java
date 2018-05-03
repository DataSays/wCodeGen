package org.dataagg.util.lang;

import jodd.util.StringUtil;

public interface IStringHelper {
	public default String ufield(String field) {
		return capFirst(polishField(field));
	}

	public default String lfield(String field) {
		return uncapFirst(polishField(field));
	}

	public default String polishField(String field) {
		String fieldNew = field;
		if (fieldNew.equals(fieldNew.toUpperCase())) {
			//全大写
			fieldNew = fieldNew.toLowerCase();
		}
		if (fieldNew.indexOf('_') >= 0) {
			fieldNew = StringUtil.toCamelCase(fieldNew, false, '_');
		}
		return fieldNew;
	}

	public default String capFirst(String field) {
		return field.substring(0, 1).toUpperCase() + field.substring(1);
	}

	public default String uncapFirst(String field) {
		return field.substring(0, 1).toLowerCase() + field.substring(1);
	}

	public default String joinPrefix(String prefix, String... texts) {
		String outText = "";
		if (texts != null) {
			for (String inter : texts) {
				if (StringUtil.isNotBlank(inter)) {
					outText += prefix + inter.trim();
				}
			}
			outText = StringUtil.cutPrefix(outText, prefix);
		}
		return outText;
	}

	public default String joinSuffix(String suffix, String... texts) {
		String outText = "";
		if (texts != null) {
			for (String inter : texts) {
				if (StringUtil.isNotBlank(inter)) {
					outText += inter.trim() + suffix;
				}
			}
			outText = StringUtil.cutSuffix(outText, suffix);
		}
		return outText;
	}

	public default String cutRight(String text, String indexStr) {
		int index = text.lastIndexOf(indexStr);
		if (index > 0) { return text.substring(0, index); }
		return text;
	}

	public default String cutLeft(String text, String indexStr) {
		int index = text.indexOf(indexStr);
		if (index > 0) { return text.substring(index); }
		return text;
	}

	public default String cut(String text, String rmStr) {
		return StringUtil.replace(text, rmStr, "");
	}
}
