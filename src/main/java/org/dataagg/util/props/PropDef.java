package org.dataagg.util.props;

import java.io.Serializable;

import org.dataagg.util.WJsonExclued;
import org.dataagg.util.collection.StrObj;

/**
 * 属性定义
 * @author watano
 *
 */
public class PropDef implements Serializable {
	private static final long serialVersionUID = -8319325218857854726L;
	private Integer sort = 10;//排序
	private String type = "1"; //属性类型, 对应PropDef.TYPE_*常量
	private String field;//属性字段
	private String title;//显示标题
	private String valCls;//属性值的类型
	private Object defaultVal;//默认值
	private Boolean isArray = false;//是否是多值
	private Boolean enableFlag = true;//启用状态

	/**
	 * 属性的显示格式
	 * Date类型参照jodd.datetime.JDateTime.toString(String)
	 * 其他类型参照jodd.format.Printf.str(String, Object)
	 * @see org.dataagg.util.props.PropSet.formatVal(String)
	 */
	private String format;

	/**
	 * 属性值效验信息
	 * MaxConstraint, MaxConstraint, RangeConstraint - defines min and max numeric value.
	 * LengthConstraint, HasSubstringConstraint, LengthConstraint, WildcardMatchConstraint… - for checking string values.
	 * EqualToFieldConstraint - checks if two fields are equal.
	 * ...
	 * @see org.dataagg.util.props.PropSet.validateAllVal()
	 */
	private String validation;
	private String description;//属性描述

	//属性的额外配置信息
	private StrObj cfg;

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
		to.validation = validation;
		to.cfg = new StrObj();
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValCls() {
		return valCls;
	}

	public void setValCls(String valCls) {
		this.valCls = valCls;
	}

	public Object getDefaultVal() {
		return defaultVal;
	}

	public void setDefaultVal(Object defaultVal) {
		this.defaultVal = defaultVal;
	}

	public Boolean isArray() {
		return isArray;
	}

	public void setArray(Boolean isArray) {
		this.isArray = isArray;
	}

	public Boolean isEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(Boolean enableFlag) {
		this.enableFlag = enableFlag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public StrObj getCfg() {
		return cfg;
	}

	public void setCfg(StrObj cfg) {
		this.cfg = cfg;
	}
}
