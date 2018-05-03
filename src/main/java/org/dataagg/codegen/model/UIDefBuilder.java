package org.dataagg.codegen.model;

import org.dataagg.codegen.base.ADefBuilderBase;
import org.dataagg.codegen.util.ElementUI;
import org.dataagg.util.WJsonUtils;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIDefBuilder extends ADefBuilderBase<UIDef, UIItemDef> {
	private static final Logger LOG = LoggerFactory.getLogger(UIDefBuilder.class);

	public UIDefBuilder(UIDef m) {
		super(m);
	}

	//-----------------------------------------------------------

	/**
	 * 从EntityDef中使用的默认UI显示组件的参数创建
	 *
	 * @param field
	 * @return
	 */
	public UIItemDef createFromEntityDef(String field) {
		for (EntityItemDef eItemDef : main.entityDef.getDefs()) {
			if (field.equals(eItemDef.field)) {
				UIItemDef uiItemDef = eItemDef.uiDef;
				if (uiItemDef == null) {
					LOG.debug("在" + field + "中没有找到对应的的默认配置");
					uiItemDef = ElementUI.elText(field);
					uiItemDef.field = field;
				}
				return WJsonUtils.reGetObj(uiItemDef, UIItemDef.class);
			}
		}
		throw new IllegalArgumentException("没有找到对应的field:" + field);
	}

	/**
	 * 分组显示ui组件
	 * @param title   分组标题
	 * @param fields  显示的字段
	 * @return
	 */
	public UIDefBuilder group(String title, String... fields) {
		String groupId = "group" + main.groupIndex + 1;
		UIItemDef groupDef = ElementUI.fieldset(groupId, title);
		main.addGroupDef(groupDef);

		if (fields != null) {
			for (String field : fields) {
				UIItemDef item = createFromEntityDef(field);
				groupDef.addChild(item);
			}
		}
		return this;
	}

	/**
	 * table显示ui组件
	 *
	 * @param title  表格标题
	 * @param parentField
	 * @param fields 显示的字段
	 * @return
	 */
	public UIDefBuilder table(String title, String parentField, UIItemDef... fields) {
		String groupId = "table" + main.groupIndex + 1;
		UIItemDef groupDef = ElementUI.table(groupId, title);
		main.addGroupDef(groupDef);
		groupDef.addCfg("_parentField", parentField);
		groupDef.addAllChildren(fields);
		return this;
	}

	/**
	 * 配置field对应的显示组件的参数
	 *
	 * @param field
	 * @param cfgKV
	 * @return
	 */
	public UIDefBuilder itemCfg(String field, String... cfgKV) {
		UIItemDef item = main.fetchItem(field);
		cfg(item, cfgKV);
		return this;
	}

	/**
	 * 配置field的title参数
	 *
	 * @param field
	 * @param title
	 * @return
	 */
	public UIDefBuilder itemTitle(String field, String title) {
		UIItemDef item = main.fetchItem(field);
		item.title = title;
		return this;
	}

	/**
	 * 配置field对应的显示组件的显示类型及参数
	 *
	 * @param field
	 * @param cfgKV
	 * @return
	 */
	public UIDefBuilder itemCfg(String field, int type, String... cfgKV) {
		UIItemDef item = main.fetchItem(field);
		if (item != null) {
			cfg(item, type, cfgKV);
		}
		return this;
	}

	/**
	 * 从标准组件参数中复制参数数据
	 *
	 * @param field
	 * @param copyFrom
	 * @return
	 */
	public UIDefBuilder itemCfg(String field, UIItemDef copyFrom) {
		UIItemDef item = main.fetchItem(field);
		if (item != null) {
			if (copyFrom.type != null) {
				item.type = copyFrom.type;
			}
			if (copyFrom.subType != null) {
				item.subType = copyFrom.subType;
			}
			if (copyFrom.title != null) {
				item.title = copyFrom.title;
			}
			if (copyFrom.defaultVal != null) {
				item.defaultVal = copyFrom.defaultVal;
			}
			if (copyFrom.format != null) {
				item.format = copyFrom.format;
			}
			if (copyFrom.description != null) {
				item.description = copyFrom.description;
			}
			if (copyFrom.getItems() != null) {
				item.setItems(copyFrom.getItems());
			}
			if (copyFrom.cfg == null) {
				copyFrom.cfg = new StrObj();
			}
			if (item.type.equals(ElementUI.TYPE_Custom + "")) {
				item.setItems(null);
			}
			if (item.cfg != null) {
				//如果是自定义的组件则清除之前的ui部分配置
				if (item.type.equals(ElementUI.TYPE_Custom + "")) {
					for (String key : item.cfg.keySet()) {
						if (key.startsWith("_") && copyFrom.cfg.get(key) == null) {
							copyFrom.cfg.put(key, item.cfg.get(key));
						}
					}
					item.cfg.clear();
				}
				item.cfg.putAll(copyFrom.cfg);
			} else {
				item.cfg = copyFrom.cfg;
			}
		}
		return this;
	}

	/**
	 * 配置uiItemDef的组件参数
	 *
	 * @param type  组件类型
	 * @param cfgKV UI组件配置的的key&value键值对,详细组件配置，请参考文档http://element-cn.eleme.io/#/zh-CN/component/installation
	 * @return
	 */
	public static UIItemDef cfg(UIItemDef uiItemDef, int type, String... cfgKV) {
		uiItemDef.type = type + "";
		cfg(uiItemDef, cfgKV);
		return uiItemDef;
	}

	public static UIItemDef cfg(UIItemDef uiItemDef, String... cfgKV) {
		if (cfgKV != null && cfgKV.length % 2 == 0) {
			for (int i = 0; i < cfgKV.length - 1; i += 2) {
				uiItemDef.addCfg(cfgKV[i], cfgKV[i + 1]);
			}
		}
		return uiItemDef;
	}

	public UIDefBuilder addItemDef(UIItemDef item) {
		main.addItemDef(item);
		return this;
	}

	/**
	 * 配置字段值的校验规则，可配置多个
	 *
	 * @param defs
	 * @return
	 */
	public UIDefBuilder validates(ValidateDef... defs) {
		if (defs != null) {
			UIItemDef item = lastItem();
			for (ValidateDef ui : defs) {
				ui.field = item.field;
				item.validates.add(ui);
			}
		}
		return this;
	}

	/**
	 * 配置字段值的校验规则，可配置多个
	 *
	 * @param defs
	 * @return
	 */
	public UIDefBuilder validates(String field, ValidateDef... defs) {
		UIItemDef item = main.fetchItem(field);
		if (item != null && defs != null) {
			for (ValidateDef ui : defs) {
				ui.field = field;
				item.validates.add(ui);
			}
		}
		return this;
	}
}
