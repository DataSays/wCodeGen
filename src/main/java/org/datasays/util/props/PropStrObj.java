package org.datasays.util.props;

import java.util.Date;
import java.util.Set;

import org.datasays.util.collection.StrObj;

import jodd.datetime.JDateTime;
import jodd.format.Printf;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;

public interface PropStrObj<D extends PropDef> extends PropSet<D> {
	//属性值
	public StrObj getValues();

	public void setValues(StrObj values);

	//设置属性值
	public default StrObj val(String key, Object val) {
		StrObj values = getValues();
		if (values == null) {
			values = new StrObj();
			setValues(values);
		}
		values.put(key, val);
		return values;
	}

	//获取属性值
	public default Object val(String key) {
		StrObj values = getValues();
		if (values != null && values.get(key) != null) { return values.get(key); }
		D def = getPropDef(key);
		if (def != null) { return def.getDefaultVal(); }
		return null;
	}

	/*
	 * 根据属性定义中的format信息格式化属性值
	 */
	public default String formatVal(String key) {
		D def = getPropDef(key);
		if (def != null) {
			Object val = val(key);
			String format = def.getFormat();
			if (StringUtil.isNotBlank(format)) {
				Class<?> cls = def.getTypeCls();
				if (cls.equals(Date.class)) {
					JDateTime jdt = new JDateTime(TypeConverterManager.convertType(val, Date.class));
					return jdt.toString(format);
				}
				return Printf.str(format, val);
			}
			return val.toString();
		}
		return null;
	}

	/**
	 * TODO 根据属性定义中的validation信息效验所有属性值
	 * 使用jodd.VTor框架效验
	 */
	public default void validateAllVal() {

	}

	//从父级属性集中继承属性定义及属性值, 只继承未设置的属性定义和属性值, 已设置的忽略
	public default void extend(PropStrObj<D> parent) {
		if (parent == null) { return; }
		//继承属性定义
		if (parent.getDefs() != null) {
			//遍历当前属性集的属性key
			Set<String> fields = allDefFields();
			//当前属性集中没有设置过的属性定义则添加
			for (D def : parent.getDefs()) {
				if (!fields.contains(def.getField())) {
					addPropDef(def);
				}
			}
			//重新排序属性定义
			sortDefs();
		}
		//继承属性值
		if (parent.getValues() != null) {
			for (String key : parent.getValues().keySet()) {
				if (val(key) == null) {
					val(key, parent.val(key));
				}
			}
		}
	}

	//TODO 从vo中获取属性值填入values, 使用jodd.bean.BeanUtilBean
	public default void fromBean(Object vo) {

	}

	//TODO 把values中的属性值写入vo
	public default void toBean(Object vo) {

	}

}
