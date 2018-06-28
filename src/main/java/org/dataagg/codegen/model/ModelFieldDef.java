package org.dataagg.codegen.model;

import java.util.List;

import org.dataagg.util.lang.IStringHelper;

import jodd.util.StringUtil;

public class ModelFieldDef implements IStringHelper {
	public String name;
	public boolean required = false;
	public String cls;
	public String comment;
	public String defaultVal;
	public String scope;

	public ModelFieldDef(String propName) {
		name = StringUtil.toCamelCase(propName, false, '-');
	}

	//propName, 0: required, 1:cls, 2:comment, 3:defaultVal, 4:scope+
	public static ModelFieldDef modleFiled(String propName, String[] fieldDefs) {
		ModelFieldDef def = new ModelFieldDef(propName);
		def.required = !"false".equalsIgnoreCase(fieldDefs[0]);
		def.cls = fieldDefs[1];
		if ("string".equalsIgnoreCase(def.cls)) {
			def.cls = "String";
		} else if ("object".equalsIgnoreCase(def.cls)) {
			def.cls = "Object";
		}
		def.comment = fieldDefs.length > 3 ? fieldDefs[2] : "";
		def.defaultVal = fieldDefs.length > 4 ? fieldDefs[3] : null;
		def.scope = fieldDefs.length > 5 ? fieldDefs[4] : "";
		return def;
	}

	//0: paramName, 1: required, 2:cls, 3:comment, 4:defaultVal, 5:scope
	public static ModelFieldDef param(List<?> fieldDefs) {
		ModelFieldDef def = new ModelFieldDef(fieldDefs.get(0).toString());
		def.required = !"false".equalsIgnoreCase(fieldDefs.get(1).toString());
		def.cls = (String) fieldDefs.get(2);
		if ("string".equalsIgnoreCase(def.cls)) {
			def.cls = "String";
		} else if ("object".equalsIgnoreCase(def.cls)) {
			def.cls = "Object";
		} else if ("integer".equalsIgnoreCase(def.cls)) {
			def.cls = "Integer";
		}
		def.comment = fieldDefs.size() > 3 ? (String) fieldDefs.get(3) : "";
		def.defaultVal = fieldDefs.size() > 4 ? (String) fieldDefs.get(4) : null;
		def.scope = fieldDefs.size() > 5 ? (String) fieldDefs.get(5) : "";
		return def;
	}
}
