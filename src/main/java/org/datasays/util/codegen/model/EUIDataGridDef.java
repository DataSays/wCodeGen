package org.datasays.util.codegen.model;
import java.util.*;
import org.nutz.dao.entity.annotation.*;
import org.datasays.commons.base.*;
import org.datasays.util.codegen.model.EUIDataTableDef;
//##JavaCodeMerger.import
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 *
 * DataGrid定义
 *
 * EntityDefBuilder
 */
@Table("da_ui_datagrid_def")
@Comment("DataGrid定义")
public class EUIDataGridDef extends EUIFormDef{
	private static final long serialVersionUID = 3661703438949172956L;
	@Column()
	@Comment("dataTableId")
	@ColDefine(type = ColType.INT , width = 16)
	private Long dataTableId;

	@One(field = "dataTableId")
	private EUIDataTableDef dataTable;

//##JavaCodeMerger.code:0
	public EUIDataGridDef() {
		super(null);
	}

	public EUIDataGridDef(String name) {
		super(name);
	}

	public void initDataTable() {
		//		if (dataTable != null && dataTable.getAction() == null) {
		dataTable.setAction(getAction());
		//		}
		//		if (dataTable != null && dataTable.getName() == null) {
		dataTable.setName(getName());
		//		}
		//		if (dataTable != null && dataTable.getCfg() == null) {
		dataTable.setCfg(getCfg());
		//		}
		//		if (dataTable != null && dataTable.getComment() == null) {
		dataTable.setComment(getComment());
		//		}
		//		if (dataTable != null && dataTable.getOutDir() == null) {
		dataTable.setOutDir(getOutDir());
		//		}
	}

	//是否是树形
	@Override
	@JsonIgnore
	public boolean isTree() {
		return "true".equals(getCfg("isTree"));
	}

	@Override
	public void isTree(boolean value) {
		addCfg("isTree", value ? "true" : "false");
	}
//##JavaCodeMerger.code

	//--------------------setter & getter-----------------------------------
	public Long getDataTableId() {
		return dataTableId;
	}

	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}

	public EUIDataTableDef getDataTable() {
		return dataTable;
	}

	public void setDataTable(EUIDataTableDef dataTable) {
		this.dataTable = dataTable;
	}

}
