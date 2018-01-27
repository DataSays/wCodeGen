package org.dataagg.codegen;

import org.dataagg.codegen.model.UIDataGridDef;
import org.dataagg.codegen.model.UIDataTableDef;
import org.dataagg.codegen.model.UIFormDef;
import org.dataagg.codegen.model.UIItemDef;
import org.dataagg.codegen.util.CodeGenUtils;
import org.dataagg.util.WJsonUtils;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UICodeGen {
	private static final Logger LOG = LoggerFactory.getLogger(UICodeGen.class);
	public String baseDir = "..\\..\\pscWeb\\src\\";

	/**
	 * 根据EntityDef生成对应Entity的DataGrid vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataGrid(UIDataGridDef dataGridDef) {
		StrObj model = new StrObj();
		model.put("uiDef", dataGridDef);
		CodeGenUtils.genFtlCode("/ui/DataGridList.Vue.ftl", model, dataGridDef.getOutDir(), dataGridDef.getAction() + "List.vue");
	}

	/**
	 * 根据EntityDef生成对应Entity的DataEditForm vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataEditForm(UIFormDef formDef) {
		StrObj model = new StrObj();
		LOG.debug("--uiDefJson--");
		LOG.debug("--uiDef--");
		LOG.debug(WJsonUtils.toJson(formDef));
		LOG.debug("--uiDef.defGroups--");
		LOG.debug(WJsonUtils.toJson(formDef.getDefGroups()));
		for (UIItemDef itemDef : formDef.getDefs()) {
			LOG.debug("--item.defGroupItem--");
			LOG.debug(itemDef.isDefGroupItem() ? "true" : "false");
			LOG.debug("--item.defGroupName--");
			LOG.debug(itemDef.getDefGroupName());
			LOG.debug("--item.defGroupField--");
			LOG.debug(itemDef.getDefGroupField());
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
	public void genVueDataTable(UIDataTableDef dataTableDef) {
		StrObj model = new StrObj();
		model.put("uiDef", dataTableDef);
		CodeGenUtils.genFtlCode("/ui/DataTableList.Vue.ftl", model, dataTableDef.getOutDir(), dataTableDef.getAction() + "List.vue");

	}
}
