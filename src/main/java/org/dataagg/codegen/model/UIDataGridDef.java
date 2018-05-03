package org.dataagg.codegen.model;

import org.dataagg.util.WJsonExclued;

/**
 *
 * DataGrid定义
 *
 * DataAgg
 */
public class UIDataGridDef extends UIFormDef {
	private static final long serialVersionUID = 3661703438949172956L;
	private Long dataTableId;
	private UIDataTableDef dataTable;

	//##CodeMerger.code:_CustomFields
	public UIDataGridDef(String name) {
		super(name);
	}

	public UIDataGridDef(EntityDef entityDef) {
		super(entityDef);
	}

	public void initDataTable() {
		//		if (dataTable != null && dataTable.getAction() == null) {
		dataTable.setAction(getAction());
		//		}
		//		if (dataTable != null && dataTable.getName() == null) {
		dataTable.name = name;
		//		}
		//		if (dataTable != null && dataTable.getCfg() == null) {
		dataTable.setCfg(getCfg());
		//		}
		//		if (dataTable != null && dataTable.getComment() == null) {
		dataTable.comments = comments;
		//		}
		//		if (dataTable != null && dataTable.getOutDir() == null) {
		dataTable.setOutDir(getOutDir());
		//		}
	}

	//是否是树形
	@Override
	@WJsonExclued
	public boolean isTree() {
		return "true".equals(getCfg("isTree"));
	}

	@Override
	public void isTree(boolean value) {
		addCfg("isTree", value ? "true" : "false");
	}
	//##CodeMerger.code

	//--------------------setter & getter-----------------------------------
	public Long getDataTableId() {
		return dataTableId;
	}

	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}

	public UIDataTableDef getDataTable() {
		return dataTable;
	}

	public void setDataTable(UIDataTableDef dataTable) {
		this.dataTable = dataTable;
	}

}
