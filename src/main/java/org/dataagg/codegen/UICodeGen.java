package org.dataagg.codegen;

import org.dataagg.codegen.base.ACodeGenBase;
import org.dataagg.codegen.base.AUIDefSet;
import org.dataagg.codegen.model.ActionDef;
import org.dataagg.codegen.model.UIDataGridDef;
import org.dataagg.codegen.model.UIDataTableDef;
import org.dataagg.codegen.model.UIFormDef;
import org.dataagg.codegen.model.UIItemDef;
import org.dataagg.codegen.util.CodeGenUtils;
import org.dataagg.util.WJsonUtils;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UICodeGen extends ACodeGenBase<AUIDefSet> {
	private static final Logger LOG = LoggerFactory.getLogger(UICodeGen.class);
	public String baseDir = "..\\..\\ynfhWeb\\src\\";

	/**
	 * 根据EntityDef生成对应Entity的DataGrid vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataGrid(UIDataGridDef dataGridDef) {
		StrObj model = new StrObj();
		model.put("uiDef", dataGridDef);
		genDebugModelJson(dataGridDef.getAction() + "List", model);
		CodeGenUtils.genFtlCode("/ui/DataGridList.Vue.ftl", model, baseDir, dataGridDef.getOutDir(), dataGridDef.getAction() + "List.vue");
	}

	/**
	 * 根据EntityDef生成对应Entity的DataEditForm vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataEditForm(UIFormDef formDef) {
		StrObj model = new StrObj();
		for (UIItemDef itemDef : formDef.getDefs()) {
			LOG.debug("--item.defGroupItem--");
			LOG.debug(itemDef.isDefGroupItem() ? "true" : "false");
			LOG.debug("--item.defGroupName--");
			LOG.debug(itemDef.getDefGroupName());
			LOG.debug("--item.defGroupField--");
			LOG.debug(itemDef.getDefGroupField());
		}
		model.put("uiDef", formDef);
		genDebugModelJson(formDef.getAction() + "Form", model);
		if (formDef.isTree()) {
			CodeGenUtils.genFtlCode("/ui/Tree.Vue.ftl", model, baseDir, formDef.getOutDir(), formDef.getAction() + "List.vue");
		} else {
			CodeGenUtils.genFtlCode("/ui/DataEditForm.Vue.ftl", model, baseDir, formDef.getOutDir(), formDef.getAction() + "Form.vue");
		}
	}

	/**
	 * 根据EntityDef生成对应Entity的DataView vue文件
	 * @param codeFile 生成文件路径
	 */
	public void genVueDataTable(UIDataTableDef dataTableDef) {
		StrObj model = new StrObj();
		model.put("uiDef", dataTableDef);
		genDebugModelJson(dataTableDef.getAction() + "Table", model);
		CodeGenUtils.genFtlCode("/ui/DataTableList.Vue.ftl", model, baseDir, dataTableDef.getOutDir(), dataTableDef.getAction() + "List.vue");

	}

	@Override
	public void genAllCode() {
		if (def instanceof UIDataTableDef) {
			genVueDataTable((UIDataTableDef) def);
		} else if (def instanceof UIDataGridDef) {
			genVueDataGrid((UIDataGridDef) def);
		} else if (def instanceof UIFormDef) {
			genVueDataEditForm((UIFormDef) def);
		}
	}
}
