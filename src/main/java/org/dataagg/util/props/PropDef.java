package org.dataagg.util.props;

import java.io.Serializable;

import org.dataagg.util.WJsonExclued;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.lang.IStringHelper;

/**
 * 属性定义
 * @author watano
 *
 */
public class PropDef implements Serializable, IStringHelper {
	private static final long serialVersionUID = -8319325218857854726L;
	public Integer sort = 10;//排序
	public String type = "1"; //属性类型
	public String subType = "1"; //属性子类型
	public String field;//属性字段
	public String title;//显示标题
	public String valCls;//属性值的类型
	public Object defaultVal;//默认值
	public Boolean isArray = false;//是否是多值
	public Boolean enableFlag = true;//启用状态

	/**
	 * 属性的显示格式
	 * Date类型参照jodd.datetime.JDateTime.toString(String)
	 * 其他类型参照jodd.format.Printf.str(String, Object)
	 */
	public String format;
	public String description;//属性描述

	//属性的额外配置信息
	public StrObj cfg;

	public PropDef() {

	}

	public PropDef(String field, String title) {
		this(10, field, "10", title);
	}

	public PropDef(Integer sort, String field, String type, String title) {
		super();
		this.sort = sort;
		this.field = field;
		this.type = (type == null) ? "1" : type;
		this.title = title;
		valCls = defaultValCls();
	}

	//属性值的数据类型
	@WJsonExclued
	public Class<?> getTypeCls() {
		return String.class;
	}

	public String defaultValCls() {
		String cls = getTypeCls().getName();
		if (cls.startsWith("java.lang.")) {
			cls = cls.substring(10);
		} else if (cls.startsWith("java.util.")) {
			cls = cls.substring(10);
		} else if (cls.startsWith("java.io.")) {
			cls = cls.substring(8);
		}
		return cls;
	}

	public StrObj addCfg(String name, Object value) {
		if (value == null) { return cfg; }
		if (cfg == null) {
			cfg = new StrObj(name, value);
		} else {
			cfg.put(name, value);
		}
		return cfg;
	}

	public Object getCfg(String name) {
		return cfg == null ? null : cfg.get(name);
	}

	public void copyTo(PropDef to) {
		to.field = field;
		to.sort = sort;
		to.valCls = valCls;
		to.title = title;
		to.description = description;
		to.type = type;
		to.defaultVal = defaultVal;
		to.description = description;
		to.isArray = isArray;
		to.enableFlag = enableFlag;
		to.format = format;
		to.cfg = new StrObj();
	}
}
