package org.dataagg.codegen.model;

import java.util.List;

import org.dataagg.codegen.base.ADefBuilderBase;
import org.dataagg.codegen.base.AUIDefSet;

public class UIDefBuilder extends ADefBuilderBase<AUIDefSet, UIItemDef> {
	public UIDefBuilder(AUIDefSet m) {
		super(m);
	}

	public static UIDefBuilder newUIDef(AUIDefSet uiDef) {
		UIDefBuilder builder = new UIDefBuilder(uiDef);
		return builder;
	}

	public static UIDefBuilder newUIFormDef(EntityDef entityDef) {
		UIFormDef uiFormDef = buildUIFormDef(entityDef);
		UIDefBuilder builder = new UIDefBuilder(uiFormDef);
		return builder;
	}

	public static UIDefBuilder newUIDataTableDef(EntityDef entityDef) {
		UIDataTableDef uiDataTableDef = buildUIDataTableDef(entityDef);
		UIDefBuilder builder = new UIDefBuilder(uiDataTableDef);
		return builder;
	}

	public static UIDefBuilder newUIDataGridDef(EntityDef entityDef) {
		UIDataGridDef uiDataGridDef = buildUIDataGridDef(entityDef);
		UIDefBuilder builder = new UIDefBuilder(uiDataGridDef);
		return builder;
	}

	private static UIItemDef create(EntityItemDef entityItemDef) {
		UIItemDef itemDef = new UIItemDef(entityItemDef.getField(), entityItemDef.getTitle());
		entityItemDef.copyTo(itemDef);
		String field = entityItemDef.getField();
		if ("delFlag".equals(field) || field.endsWith(".delFlag")) {
			itemDef.setType(UIFormDef.TYPE_Switch + "");
			itemDef.setTitle("删除标记");
		}
		if ("enabled".equals(field) || field.endsWith(".enabled")) {
			itemDef.setType(UIFormDef.TYPE_Switch + "");
			itemDef.setTitle("是否启用");
		}
		if (field.endsWith("defs[].type")) {
			itemDef.setTitle("组件类型");
		}
		if (field.endsWith("defs[].valCls")) {
			itemDef.setTitle("字段类型");
			itemDef.setSort(8);
		}
		if (field.endsWith("defs[].field")) {
			itemDef.setTitle("字段");
			itemDef.setSort(9);
		}
		if (field.endsWith("defs[].isArray") || field.endsWith("defs[].enabled") || field.endsWith("defs[].format") || field.endsWith("defs[].validation") || field.endsWith("defs[].description")) {
			itemDef.setType(UIFormDef.TYPE_Hidden + "");
		}
		return itemDef;
	}

	/**
	 * 转化所有实体定义的所有字段到UI表单定义
	 * @param pkg
	 * @param entityCls
	 * @return
	 */
	public static UIFormDef buildUIFormDef(EntityDef entityDef) {
		UIFormDef formDef = new UIFormDef(entityDef);
		List<EntityItemDef> allItems = entityDef.getDefs();
		formDef.init(entityDef.getName(), entityDef.getComments() + "表单", entityDef.getEntityCls(), entityDef.isTree());
		for (EntityItemDef entityItemDef : allItems) {
			UIItemDef itemDef = create(entityItemDef);
			String field = entityItemDef.getField();
			//设置默认显示类型
			if ("delFlag".equals(field) || field.endsWith(".delFlag")) {
				itemDef.setType(UIFormDef.TYPE_Switch + "");
				itemDef.setTitle("删除标记");
			} else if ("enabled".equals(field) || field.endsWith(".enabled")) {
				itemDef.setType(UIFormDef.TYPE_Switch + "");
				itemDef.addCfg("yes", "启用");
				itemDef.addCfg("no", "禁用");
			} else if ("sort".equals(field) || field.endsWith(".sort")) {
				itemDef.setType(UIFormDef.TYPE_InputNumber + "");
			} else if ("comment".equals(field) || field.endsWith(".comment")) {
				itemDef.setType(UIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if ("description".equals(field) || field.endsWith(".description")) {
				itemDef.setType(UIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if ("cfg".equals(field) || field.endsWith(".cfg")) {
				itemDef.setType(UIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if (("type".equals(field) || field.endsWith(".type")) && itemDef.getCfg("dictName") != null) {
				itemDef.setType(UIFormDef.TYPE_Select + "");
			} else if (field.endsWith("Id")) {
				itemDef.setType(UIFormDef.TYPE_Hidden + "");
			} else if ("id".equals(field) || field.endsWith(".id")) {
				itemDef.setType(UIFormDef.TYPE_Hidden + "");
			} else if ("gender".equals(field) || field.endsWith(".gender")) {
				itemDef.setType(UIFormDef.TYPE_Radio + "");
				itemDef.setTitle("性别");
				itemDef.setDictName("gender");
			} else if ("String".equals(itemDef.getValCls()) && entityItemDef.getWidth() != null && entityItemDef.getWidth() > 200) {
				itemDef.setType(UIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if ("String".equals(itemDef.getValCls()) || (UIFormDef.TYPE_Text + "").equals(itemDef.getType())) {
				itemDef.setType(UIFormDef.TYPE_Text + "");
			} else if ("boolean".equals(itemDef.getValCls()) || (UIFormDef.TYPE_Switch + "").equals(itemDef.getType())) {
				itemDef.setType(UIFormDef.TYPE_Switch + "");
			} else if ((UIFormDef.TYPE_Date + "").equals(itemDef.getType())) {
				itemDef.setType(UIFormDef.TYPE_Date + "");
			} else if ((UIFormDef.TYPE_Time + "").equals(itemDef.getType())) {
				itemDef.setType(UIFormDef.TYPE_Time + "");
			} else if ((UIFormDef.TYPE_DateTime + "").equals(itemDef.getType())) {
				itemDef.setType(UIFormDef.TYPE_DateTime + "");
			} else if ((UIFormDef.TYPE_InputNumber + "").equals(itemDef.getType())) {
				itemDef.setType(UIFormDef.TYPE_InputNumber + "");
			} else {
				itemDef.setType(UIFormDef.TYPE_Hidden + "");
			}
			formDef.addPropDef(itemDef);
		}
		formDef.upDefSorts();
		return formDef;
	}

	/**
	 * 转化所有实体定义的所有字段到UI列表定义
	 * @param pkg
	 * @param entityCls
	 * @return
	 */
	public static UIDataTableDef buildUIDataTableDef(EntityDef entityDef) {
		UIDataTableDef dataTableDef = new UIDataTableDef(entityDef);
		List<EntityItemDef> allItems = entityDef.getDefs();
		dataTableDef.init(entityDef.getName(), entityDef.getComments() + "表格", entityDef.getEntityCls(), entityDef.isTree());
		for (EntityItemDef entityItemDef : allItems) {
			UIItemDef itemDef = create(entityItemDef);
			//			String field = entityItemDef.getField();
			//设置默认显示类型
			if ("String".equals(entityItemDef.getValCls())) {
				itemDef.setType(UIDataTableDef.TYPE_Text + "");
			} else if ("java.util.Date".equals(entityItemDef.getValCls())) {
				itemDef.setType(UIDataTableDef.TYPE_DateTime + "");
			} else {
				itemDef.setType(UIDataTableDef.TYPE_Hidden + "");
			}
			dataTableDef.addPropDef(itemDef);
		}
		dataTableDef.upDefSorts();
		return dataTableDef;
	}

	/**
	 * 转化所有实体定义的所有字段到UI列表定义
	 * @param pkg
	 * @param entityCls
	 * @return
	 */
	public static UIDataGridDef buildUIDataGridDef(EntityDef entityDef) {
		UIDataGridDef dataGridDef = new UIDataGridDef(entityDef);
		UIDataTableDef dataTableDef = new UIDataTableDef(entityDef);
		List<EntityItemDef> allItems = entityDef.getDefs();
		dataGridDef.init(entityDef.getName(), entityDef.getComments() + "表格", entityDef.getEntityCls(), entityDef.isTree());
		for (EntityItemDef entityItemDef : allItems) {
			UIItemDef itemDef = create(entityItemDef);
			//dataTableDef和dataGridDef不能使用同一个itemDef实例
			UIItemDef itemDef2 = create(entityItemDef);
			String field = entityItemDef.getField();
			//设置默认显示类型
			if ("String".equals(entityItemDef.getValCls())) {
				itemDef.setType(UIDataTableDef.TYPE_Text + "");
			} else if ("java.util.Date".equals(entityItemDef.getValCls())) {
				itemDef.setType(UIDataTableDef.TYPE_DateTime + "");
			} else {
				itemDef.setType(UIDataTableDef.TYPE_Hidden + "");
			}

			if (field.startsWith("dataTable.")) {
				if ("dataTable.id".equals(field) || "dataTable.name".equals(field) || "dataTable.outDir".equals(field) || "dataTable.action".equals(field) || "dataTable.comment".equals(field)) {} else {
					dataTableDef.addPropDef(itemDef);
				}
			} else {
				if (field.indexOf(".") > 0) {
					itemDef.setType(UIDataTableDef.TYPE_Hidden + "");
				}
				dataTableDef.addPropDef(itemDef2);
				dataGridDef.addPropDef(itemDef);
			}
		}
		dataTableDef.upDefSorts();
		dataGridDef.setDataTable(dataTableDef);
		dataGridDef.upDefSorts();
		return dataGridDef;
	}

	private UIItemDef fetchItem(String field) {
		for (UIItemDef item : main.getDefs()) {
			if (field.equals(item.getField())) { return item; }
		}
		return null;
	}

	private UIItemDef fetchTableItem(String field) {
		if (main instanceof UIDataGridDef) {
			UIDataGridDef dataGridDef = (UIDataGridDef) main;
			for (UIItemDef item : dataGridDef.getDataTable().getDefs()) {
				if (field.equals(item.getField())) { return item; }
			}
		}
		return null;
	}

	//-----------------------UIFormDef-----------------------------------
	public UIDefBuilder fHidden(String... fields) {
		for (String field : fields) {
			UIItemDef item = fetchItem(field);
			item.setType(UIFormDef.TYPE_Hidden + "");
		}
		return this;
	}

	//普通文本
	public UIDefBuilder fText(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Text + "");
		return this;
	}

	//多行文本
	public UIDefBuilder fTextarea(String field, int rows, int colSpan) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_TextArea + "");
		item.addCfg("rows", rows);
		item.addCfg("colSpan", colSpan);
		return this;
	}

	//富文本
	public UIDefBuilder fRiceText(String field, Object editorOption) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_RiceText + "");
		item.addCfg("editorOption", editorOption);
		return this;
	}

	//密码类型
	public UIDefBuilder fPassword(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Password + "");
		return this;
	}

	//计数器
	public UIDefBuilder fInputNumber(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_InputNumber + "");
		return this;
	}

	//滑块
	public UIDefBuilder fSlider(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Slider + "");
		return this;
	}

	//开关
	public UIDefBuilder fSwitch(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Switch + "");
		return this;
	}

	//评分
	public UIDefBuilder fRate(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Rate + "");
		return this;
	}

	//颜色
	public UIDefBuilder fColor(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Color + "");
		return this;
	}

	//多选框
	public UIDefBuilder fCheckBox(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_CheckBox + "");
		return this;
	}

	//单选
	public UIDefBuilder fRadio(String field, String dictName) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Radio + "");
		item.setDictName(dictName);
		return this;
	}

	//选择器
	public UIDefBuilder fSelect(String field, String dictName) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Select + "");
		item.setDictName(dictName);
		return this;
	}

	//级联选择器
	public UIDefBuilder fCascader(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Cascader + "");
		return this;
	}

	//日期
	public UIDefBuilder fDate(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Date + "");
		return this;
	}

	//时间
	public UIDefBuilder fTime(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Time + "");
		return this;
	}

	//日期时间
	public UIDefBuilder fDateTime(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_DateTime + "");
		return this;
	}

	//文件上传
	public UIDefBuilder fUpload(String field) {
		UIItemDef item = fetchItem(field);
		item.setType(UIFormDef.TYPE_Upload + "");
		return this;
	}

	//-----------------------UIDataTableDef-----------------------------------
	public UIDefBuilder tHidden(String... fields) {
		for (String field : fields) {
			UIItemDef item = fetchTableItem(field);
			item.setType(UIDataTableDef.TYPE_Hidden + "");
		}
		return this;
	}

	//字典数据文本
	public UIDefBuilder tDictText(String field, String dictName) {
		UIItemDef item = fetchTableItem(field);
		item.setType(UIDataTableDef.TYPE_DictText + "");
		item.setDictName(dictName);
		return this;
	}

	//普通文本
	public UIDefBuilder tText(String field) {
		UIItemDef item = fetchTableItem(field);
		item.setType(UIDataTableDef.TYPE_Text + "");
		return this;
	}

	//表达式定义的文本
	public UIDefBuilder tELText(String field) {
		UIItemDef item = fetchTableItem(field);
		item.setType(UIDataTableDef.TYPE_ELText + "");
		return this;
	}

	//日期
	public UIDefBuilder tDate(String field) {
		UIItemDef item = fetchTableItem(field);
		item.setType(UIDataTableDef.TYPE_Date + "");
		return this;
	}

	//时间
	public UIDefBuilder tTime(String field) {
		UIItemDef item = fetchTableItem(field);
		item.setType(UIDataTableDef.TYPE_Time + "");
		return this;
	}

	//日期时间
	public UIDefBuilder tDateTime(String field) {
		UIItemDef item = fetchTableItem(field);
		item.setType(UIDataTableDef.TYPE_DateTime + "");
		return this;
	}

	//上传文件
	public UIDefBuilder tUpload(String field) {
		UIItemDef item = fetchTableItem(field);
		item.setType(UIDataTableDef.TYPE_Upload + "");
		return this;
	}

	//------------------------表格显示配置-----------------------------------
}
