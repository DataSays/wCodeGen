package org.dataagg.util.props;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dataagg.codegen.util.CodeGenUtils;

/**
 * 属性集
 * @author watano
 *
 */
public interface PropSet<D extends PropDef> extends Serializable {
	public D newDef(String key, String title);

	public List<D> getDefs();

	public void setDefs(List<D> setDefs);

	//添加属性定义
	public default D addPropDef(D def) {
		if (getDefs() == null) {
			setDefs(new ArrayList<>());
		}
		getDefs().add(def);
		return def;
	}

	/**
	 *
	 * @param key 属性定义的key
	 * @param title 属性定义的显示名称
	 * @param valCls 属性的存储类型
	 * @return
	 */
	public default D addPropDef(String key, String title, String valCls) {
		D def = newDef(key, title);
		def.setValCls(CodeGenUtils.simpleClsText(null, valCls));
		addPropDef(def);
		return def;
	}

	public default D addListDef(String key, String title, String valCls) {
		D def = addPropDef((key == null) ? "items" : key, title, valCls);
		def.setArray(true);
		return def;
	}

	//根据属性定义的key获取属性定义
	public default D getPropDef(String field) {
		List<D> defs = getDefs();
		for (D def : defs) {
			if (field.equalsIgnoreCase(def.getField())) { return def; }
		}
		return null;
	}

	//获取所有属性定义的key
	public default Set<String> allDefFields() {
		//遍历当前属性集的属性key
		Set<String> fields = new HashSet<>();
		List<D> defs = getDefs();
		if (defs != null) {
			for (PropDef def : defs) {
				fields.add(def.getField());
			}
		}
		return fields;
	}

	//按照属性定义的sort排序defs
	public default void sortDefs() {
		List<D> defs = getDefs();
		if (defs != null) {
			defs.sort((o1, o2) -> Integer.parseInt(o1.getSort().toString()) - Integer.parseInt(o2.getSort().toString()));
		}
	}

	//根据defs的顺序设置sort值
	public default void upDefSorts() {
		List<D> defs = getDefs();
		if (defs != null) {
			Integer index = 1;
			for (PropDef def : defs) {
				def.setSort(index++);
			}
		}
	}
}
