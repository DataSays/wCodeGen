package org.dataagg.codegen.model;

import org.dataagg.util.props.PropDef;

/**
 *
 * UI定义明细项
 *
 * DataAgg
 */
public class UIItemDef extends PropDef {
	private static final long serialVersionUID = 120331974246389725L;
	private Long masterId;
	private String masterType;//主表类型

	//##CodeMerger.code:_CustomFields
	public UIItemDef() {
		super(null, null);
	}

	public UIItemDef(Integer sort, String field, String type, String title) {
		super(sort, field, type, title);
	}

	public UIItemDef(String field, String title) {
		super(field, title);
	}

	//关联字典名称
	public String getDictName() {
		return (String) getCfg("dictName");
	}

	public void setDictName(String value) {
		addCfg("dictName", value);
	}

	public boolean isDefGroupItem() {
		return super.getField().contains("[].");
	}

	public String getDefGroupName() {
		String f = null;
		if (isDefGroupItem()) {
			f = super.getField();
			f = f.substring(0, f.indexOf("[]."));
		}
		return f;
	}

	public String getDefGroupField() {
		if (isDefGroupItem()) {
			String f = super.getField();
			return f.substring(f.indexOf("[].") + 3);
		}
		return null;
	}
	//##CodeMerger.code

	//--------------------setter & getter-----------------------------------
	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

	public String getMasterType() {
		return masterType;
	}

	public void setMasterType(String masterType) {
		this.masterType = masterType;
	}

}
