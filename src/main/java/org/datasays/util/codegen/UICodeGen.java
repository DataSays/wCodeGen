package org.datasays.util.codegen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.datasays.util.WJsonUtils;
import org.datasays.util.codegen.model.EEntityDef;
import org.datasays.util.codegen.model.EEntityItemDef;
import org.datasays.util.codegen.model.EUIDataGridDef;
import org.datasays.util.codegen.model.EUIDataTableDef;
import org.datasays.util.codegen.model.EUIFormDef;
import org.datasays.util.codegen.model.EUIItemDef;
import org.datasays.util.codegen.service.EntityDefService;
import org.datasays.util.collection.StrObj;

public class UICodeGen {
	private static final Logger LOG = LoggerFactory.getLogger(UICodeGen.class);

	public String baseDir = "..\\..\\pscWeb\\src\\";

	private EntityDefService entityDefService;

	public EUIItemDef create(EEntityItemDef entityItemDef) {
		EUIItemDef itemDef = new EUIItemDef(entityItemDef.getField(), entityItemDef.getTitle());
		entityItemDef.copyTo(itemDef);
		//TODO add check code style
		String field = entityItemDef.getField();
		LOG.warn("---------------" + field);
		if ("delFlag".equals(field) || field.endsWith(".delFlag")) {
			itemDef.setType(EUIFormDef.TYPE_Switch + "");
			itemDef.setTitle("删除标记");
		}
		if ("enabled".equals(field) || field.endsWith(".enabled")) {
			itemDef.setType(EUIFormDef.TYPE_Switch + "");
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
			itemDef.setType(EUIFormDef.TYPE_Hidden + "");
		}
		return itemDef;
	}

	/**
	 * 转化所有实体定义的所有字段到UI表单定义
	 * @param pkg
	 * @param entityCls
	 * @return
	 */
	public EUIFormDef buildUIFormDef(String pkg, String entityCls) {
		EUIFormDef formDef = new EUIFormDef();
		EEntityDef entityDef = entityDefService.fetch(pkg, entityCls);
		formDef.init(entityDef.getName(), entityDef.getComments() + "表单", entityCls, entityDef.isTree());
		List<EEntityItemDef> allItems = entityDefService.fetchAllItems(pkg, entityCls);
		for (EEntityItemDef entityItemDef : allItems) {
			EUIItemDef itemDef = create(entityItemDef);
			String field = entityItemDef.getField();
			//设置默认显示类型
			if ("delFlag".equals(field) || field.endsWith(".delFlag")) {
				itemDef.setType(EUIFormDef.TYPE_Switch + "");
				itemDef.setTitle("删除标记");
			} else if ("enabled".equals(field) || field.endsWith(".enabled")) {
				itemDef.setType(EUIFormDef.TYPE_Switch + "");
				itemDef.addCfg("yes", "启用");
				itemDef.addCfg("no", "禁用");
			} else if ("sort".equals(field) || field.endsWith(".sort")) {
				itemDef.setType(EUIFormDef.TYPE_InputNumber + "");
			} else if ("comment".equals(field) || field.endsWith(".comment")) {
				itemDef.setType(EUIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if ("description".equals(field) || field.endsWith(".description")) {
				itemDef.setType(EUIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if ("cfg".equals(field) || field.endsWith(".cfg")) {
				itemDef.setType(EUIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if (("type".equals(field) || field.endsWith(".type")) && itemDef.getCfg("dictName") != null) {
				itemDef.setType(EUIFormDef.TYPE_Select + "");
			} else if (field.endsWith("Id")) {
				itemDef.setType(EUIFormDef.TYPE_Hidden + "");
			} else if ("id".equals(field) || field.endsWith(".id")) {
				itemDef.setType(EUIFormDef.TYPE_Hidden + "");
			} else if ("gender".equals(field) || field.endsWith(".gender")) {
				itemDef.setType(EUIFormDef.TYPE_Radio + "");
				itemDef.setTitle("性别");
				itemDef.setDictName("gender");
			} else if ("String".equals(itemDef.getValCls()) && entityItemDef.getWidth() != null && entityItemDef.getWidth() > 100) {
				itemDef.setType(EUIFormDef.TYPE_TextArea + "");
				itemDef.addCfg("rows", 5);
				itemDef.addCfg("colSpan", 1);
			} else if ("String".equals(itemDef.getValCls()) || (EUIFormDef.TYPE_Text + "").equals(itemDef.getType())) {
				itemDef.setType(EUIFormDef.TYPE_Text + "");
			} else if ("boolean".equals(itemDef.getValCls()) || (EUIFormDef.TYPE_Switch + "").equals(itemDef.getType())) {
				itemDef.setType(EUIFormDef.TYPE_Switch + "");
			} else if ((EUIFormDef.TYPE_Date + "").equals(itemDef.getType())) {
				itemDef.setType(EUIFormDef.TYPE_Date + "");
			} else if ((EUIFormDef.TYPE_Time + "").equals(itemDef.getType())) {
				itemDef.setType(EUIFormDef.TYPE_Time + "");
			} else if ((EUIFormDef.TYPE_DateTime + "").equals(itemDef.getType())) {
				itemDef.setType(EUIFormDef.TYPE_DateTime + "");
			} else if ((EUIFormDef.TYPE_InputNumber + "").equals(itemDef.getType())) {
				itemDef.setType(EUIFormDef.TYPE_InputNumber + "");
			} else {
				itemDef.setType(EUIFormDef.TYPE_Hidden + "");
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
	public EUIDataTableDef buildUIDataTableDef(String pkg, String entityCls) {
		EUIDataTableDef dataTableDef = new EUIDataTableDef();
		EEntityDef entityDef = entityDefService.fetch(pkg, entityCls);
		dataTableDef.init(entityDef.getName(), entityDef.getComments() + "表格", entityCls, entityDef.isTree());
		List<EEntityItemDef> allItems = entityDefService.fetchAllItems(pkg, entityCls);
		for (EEntityItemDef entityItemDef : allItems) {
			EUIItemDef itemDef = create(entityItemDef);
			//			String field = entityItemDef.getField();
			//设置默认显示类型
			if ("String".equals(entityItemDef.getValCls())) {
				itemDef.setType(EUIDataTableDef.TYPE_Text + "");
			} else if ("java.util.Date".equals(entityItemDef.getValCls())) {
				itemDef.setType(EUIDataTableDef.TYPE_DateTime + "");
			} else {
				itemDef.setType(EUIDataTableDef.TYPE_Hidden + "");
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
	public EUIDataGridDef buildUIDataGridDef(String pkg, String entityCls) {
		EUIDataGridDef dataGridDef = new EUIDataGridDef();
		EUIDataTableDef dataTableDef = new EUIDataTableDef();
		EEntityDef entityDef = entityDefService.fetch(pkg, entityCls);
		dataGridDef.init(entityDef.getName(), entityDef.getComments() + "表格", entityCls, entityDef.isTree());
		List<EEntityItemDef> allItems = entityDefService.fetchAllItems(pkg, entityCls);
		for (EEntityItemDef entityItemDef : allItems) {
			EUIItemDef itemDef = create(entityItemDef);
			String field = entityItemDef.getField();
			//设置默认显示类型
			if ("String".equals(entityItemDef.getValCls())) {
				itemDef.setType(EUIDataTableDef.TYPE_Text + "");
			} else if ("java.util.Date".equals(entityItemDef.getValCls())) {
				itemDef.setType(EUIDataTableDef.TYPE_DateTime + "");
			} else {
				itemDef.setType(EUIDataTableDef.TYPE_Hidden + "");
			}

			if (field.startsWith("dataTable.")) {
				if ("dataTable.id".equals(field) || "dataTable.name".equals(field) || "dataTable.outDir".equals(field) || "dataTable.action".equals(field) || "dataTable.comment".equals(field)) {} else {
					dataTableDef.addPropDef(itemDef);
				}
			} else {
				if (field.indexOf(".") > 0) {
					itemDef.setType(EUIDataTableDef.TYPE_Hidden + "");
				}
				dataTableDef.addPropDef(itemDef);
				dataGridDef.addPropDef(itemDef);
			}
		}
		dataTableDef.upDefSorts();
		dataGridDef.setDataTable(dataTableDef);
		dataGridDef.upDefSorts();
		return dataGridDef;
	}

	/**
	 * 根据EntityDef生成对应Entity的DataGrid vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataGrid(EUIDataGridDef dataGridDef) {
		StrObj model = new StrObj();
		model.put("uiDef", dataGridDef);
		CodeGenUtils.genFtlCode("/ui/DataGridList.Vue.ftl", model, dataGridDef.getOutDir(), dataGridDef.getAction() + "List.vue");
	}

	/**
	 * 根据EntityDef生成对应Entity的DataEditForm vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataEditForm(EUIFormDef formDef) {
		StrObj model = new StrObj();
		System.out.println("--uiDefJson--");
		System.out.println("--uiDef--");
		System.out.println(WJsonUtils.toJson(formDef));
		System.out.println("--uiDef.defGroups--");
		System.out.println(WJsonUtils.toJson(formDef.getDefGroups()));
		for (EUIItemDef itemDef : formDef.getDefs()) {
			System.out.println("--item.defGroupItem--");
			System.out.println(itemDef.isDefGroupItem());
			System.out.println("--item.defGroupName--");
			System.out.println(itemDef.getDefGroupName());
			System.out.println("--item.defGroupField--");
			System.out.println(itemDef.getDefGroupField());
		}
		model.put("uiDef", formDef);
		if (formDef.isTree()) {
			CodeGenUtils.genFtlCode("/ui/Tree.Vue.ftl", model, formDef.getOutDir(), formDef.getAction() + "List.vue");
		} else {
			CodeGenUtils.genFtlCode("/ui/DataEditForm.Vue.ftl", model, formDef.getOutDir(), formDef.getAction() + "Form.vue");
		}
	}

	/**
	 * 根据EntityDef生成对应Entity的DataView vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataTable(EUIDataTableDef dataTableDef) {
		StrObj model = new StrObj();
		model.put("uiDef", dataTableDef);
		CodeGenUtils.genFtlCode("/ui/DataTableList.Vue.ftl", model, dataTableDef.getOutDir(), dataTableDef.getAction() + "List.vue");

	}

	public void genActionsJs(String action, String outDir, boolean isTree) {
		StrObj model = new StrObj();
		model.put("action", action);
		if (isTree) {
			CodeGenUtils.genFtlCode("/ui/ActionsTree.js.ftl", model, outDir, action + "Actions.js");
		} else {
			CodeGenUtils.genFtlCode("/ui/Actions.js.ftl", model, outDir, action + "Actions.js");
		}
	}
}
