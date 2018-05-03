package org.dataagg.codegen.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jodd.util.StringUtil;
import org.dataagg.util.collection.ITreeNode;
import org.dataagg.util.props.PropDef;

/**
 * UI定义明细项
 * <p>
 * DataAgg
 */
public class UIItemDef extends PropDef implements ITreeNode<UIItemDef, String> {
	private static final long serialVersionUID = 120331974246389725L;
	private String parentId;
	private List<UIItemDef> items;
	public List<ValidateDef> validates = new ArrayList<>();

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

	public String jsDefaultVal() {
		if (defaultVal != null) {
			return StringUtil.toPrettyString(defaultVal);
		} else {
			return null;
		}
	}

	//##CodeMerger.code

	//--------------------setter & getter-----------------------------------
	@Override
	public String getId() {
		return field;
	}

	@Override
	public void setId(String id) {
		field = id;
	}

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public List<UIItemDef> getItems() {
		return items;
	}

	@Override
	public void setItems(List<UIItemDef> items) {
		this.items = items;
	}
}
