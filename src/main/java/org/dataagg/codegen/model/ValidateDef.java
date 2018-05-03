package org.dataagg.codegen.model;

import org.dataagg.util.WJsonUtils;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.lang.IStringHelper;
import org.dataagg.util.props.PropDef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 数据字段效验规则
 * https://github.com/yiminghe/async-validator
 */
public class ValidateDef implements Serializable, IStringHelper {
	private static final long serialVersionUID = -2293853267386838089L;
	public String field;
	public StrObj cfg = new StrObj();
	public String message;
	public String trigger = "blur";

	private ValidateDef(String message) {
		this.message = message;
	}

	public static ValidateDef Required(String message) {
		ValidateDef def = new ValidateDef(message);
		def.cfg.put("required", true);
		return def;
	}

	public static ValidateDef Range(String message, int min, int max) {
		ValidateDef def = new ValidateDef(message);
		def.cfg.put("min", min);
		def.cfg.put("max", max);
		return def;
	}

	public static ValidateDef Enum(String message, String... enums) {
		ValidateDef def = new ValidateDef(message);
		def.type("enum");
		def.cfg.put("type", "enum");
		def.cfg.put("enum", enums);
		return def;
	}

	public ValidateDef triggerChange() {
		trigger = "change";
		return this;
	}

	public ValidateDef cfg(String... cfgKV) {
		if (cfgKV != null && cfgKV.length % 2 == 0) {
			for (int i = 0; i < cfgKV.length - 1; i += 2) {
				cfg.put(cfgKV[i], cfgKV[i + 1]);
			}
		}
		return this;
	}

	/**
	 * @param type string: Must be of type string. This is the default type.
	 *             number: Must be of type number.
	 *             boolean: Must be of type boolean.
	 *             method: Must be of type function.
	 *             regexp: Must be an instance of RegExp or a string that does not generate an exception when creating a new RegExp.
	 *             integer: Must be of type number and an integer.
	 *             float: Must be of type number and a floating point number.
	 *             array: Must be an array as determined by Array.isArray.
	 *             object: Must be of type object and not Array.isArray.
	 *             enum: Value must exist in the enum.
	 *             date: Value must be valid as determined by Date
	 *             url: Must be of type url.
	 *             hex: Must be of type hex.
	 *             email: Must be of type email.
	 * @return
	 */
	public ValidateDef type(String type) {
		cfg.put("type", type);
		return this;
	}

	public static String genRules(List<ValidateDef> allValidateDefs) {
		Map<String, List<StrObj>> rules = new LinkedHashMap<>();
		if (allValidateDefs != null && allValidateDefs.size() > 0) {
			for (ValidateDef def : allValidateDefs) {
				List<StrObj> items = rules.get(def.field);
				if (items == null) {
					items = new LinkedList<>();
				}
				StrObj item = new StrObj();
				item.putAll(def.cfg);
				item.put("message", def.message);
				item.put("trigger", def.trigger);
				items.add(item);
				rules.put(def.field, items);
			}
		}
		return WJsonUtils.toJson(rules, false);
	}
}
