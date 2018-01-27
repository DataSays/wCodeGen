package org.dataagg.codegen.util;

import java.util.HashSet;
import java.util.Set;

import jodd.util.StringUtil;

public class JCoder extends CodeGenHelper {
	private int importOffset = 1;
	private Set<String> imports;

	public JCoder(String javaFile, boolean mergeCode) {
		super(javaFile);
		codeMerger = new JCodeMerger(javaFile);
		codeMerger.setMergeCode(mergeCode);
		imports = new HashSet<>();
	}

	public static String fieldDef(String fieldType, String fieldName, Object defaultVal) {
		if (fieldType.indexOf(".") > 0) {
			fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
		}
		if (defaultVal == null) {
			return String.format("private %s %s;", fieldType, uncapFirst(fieldName));
		} else {
			String valCode = "";
			if (defaultVal instanceof Number) {
				if ("Long".equals(fieldType) || "long".equals(fieldType)) {
					valCode = defaultVal.toString() + "L";
				} else {
					valCode = defaultVal.toString();
				}
			} else if (defaultVal instanceof Character) {
				valCode = "'" + defaultVal.toString() + "'";
			} else if (defaultVal instanceof Boolean) {
				valCode = ((Boolean) defaultVal).booleanValue() ? "true" : "false";
			} else {
				valCode = "\"" + defaultVal.toString() + "\"";
			}
			if (fieldType.startsWith("List<")) {
				valCode = "Lists.newArrayList(" + valCode + ")";
			}
			return String.format("private %s %s=%s;", fieldType, uncapFirst(fieldName), valCode);
		}
	}

	public static String longComment(String comment, String author) {
		StringBuffer sb = new StringBuffer();
		appendln(sb, "/**");
		appendln(sb, " *");
		appendln(sb, " * %s", comment);
		appendln(sb, " *");
		appendln(sb, " * %s", author != null ? author : "DataAgg");
		appendln(sb, " *");
		appendln(sb, " */");
		return sb.toString();
	}

	public static String lineBreakComment(String comment) {
		StringBuffer sb = new StringBuffer();
		appendln(sb, "//--------------------------%s--------------------------------", comment);
		return sb.toString();
	}

	public static String serialVersionUID(Long serialVersionUID) {
		if (serialVersionUID != null) {
			return String.format("private static final long serialVersionUID = %dL;", serialVersionUID);
		} else {
			return "";
		}
	}

	public static String publicClsDef(String cls, String extendCls, String... interfaces) {
		return clsDef("public", cls, extendCls, interfaces);
	}

	public static String clsDef(String clsDef, String cls, String extendCls, String... interfaces) {
		StringBuffer sb = new StringBuffer();
		sb.append(clsDef + " class " + cls.trim());
		if (StringUtil.isNotBlank(extendCls)) {
			if (extendCls.trim().startsWith("extends ")) {
				sb.append(" " + extendCls);
			} else if (extendCls.trim().startsWith("implements ")) {
				sb.append(" " + extendCls);
			} else {
				sb.append(" extends " + extendCls);
			}
		}
		String code = joinSuffix(", ", interfaces);
		if (code.length() > 0) {
			if (extendCls.indexOf(" implements ") > 0) {
				sb.append(", " + code);
			} else {
				sb.append(" implements " + code);
			}
		}
		sb.append(" {");
		return sb.toString();
	}

	/**
	 * 标记插入import语句的位置
	 */
	public void markImportCodeOffset() {
		importOffset = offset();
	}

	/**
	 *	新增一个待import的cls;
	 * @param cls
	 */
	public void addImportCls(String cls) {
		imports.add(cls);
	}

	/**
	 * 插入所有的import类
	 * @param imports
	 */
	public void insertImportCodes() {
		//插入ImportCodes
		String importCode = ((JCodeMerger) codeMerger).getImportCodes();
		insert(importOffset, importCode);

		//插入新增引用的实体类
		for (String importcls : imports) {
			insert(importOffset, String.format("import %s;%n", importcls));
		}
	}

	public void appendFieldGSetter(String fieldType, String fieldName) {
		String ufield = capFirst(fieldName);
		String lfield = uncapFirst(fieldName);
		String methodGet = "get" + ufield;
		String methodSet = "set" + ufield;
		if (ufield.startsWith("Is")) {
			ufield = ufield.substring(2);
			ufield = capFirst(ufield);
			methodGet = "is" + ufield;
			methodSet = "set" + ufield;
		}
		if (fieldType.indexOf(".") > 0) {
			fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
		}

		appendln2("public %s %s() {", fieldType, methodGet);
		appendln2("	return %s;", lfield);
		appendln2("}");
		appendln("");

		appendln2("public void %s(%s %s) {", methodSet, fieldType, lfield);
		beginIndent();
		appendln2("this.%s = %s;", lfield, lfield);
		endIndent();
		appendln2("}");
		appendln("");
	}
}
