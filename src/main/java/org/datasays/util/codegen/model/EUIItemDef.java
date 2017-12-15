package org.datasays.util.codegen.model;
import java.util.*;
import org.nutz.dao.entity.annotation.*;
import org.datasays.commons.base.*;
//##JavaCodeMerger.import
import org.datasays.util.props.PropDef;


/**
 *
 * UI定义明细项
 *
 * EntityDefBuilder
 */
@Table("da_ui_item_def")
@Comment("UI定义明细项")
public class EUIItemDef extends PropDef{
	private static final long serialVersionUID = 120331974246389725L;
	@Column()
	@Comment("主表Id")
	@ColDefine(type = ColType.INT , width = 16)
	private Long masterId;

	@Column()
	@Comment("主表类型")
	@ColDefine(type = ColType.CHAR , width = 1, notNull = true)
	private String masterType;

//##JavaCodeMerger.code:0
	public EUIItemDef() {
		super(null, null);
	}

	public EUIItemDef(Integer sort, String field, String type, String title) {
		super(sort, field, type, title);
	}

	public EUIItemDef(String field, String title) {
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
//##JavaCodeMerger.code

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
