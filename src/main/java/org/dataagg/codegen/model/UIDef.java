package org.dataagg.codegen.model;

import org.dataagg.codegen.base.ADefBase;
import org.dataagg.codegen.util.ElementUI;
import org.dataagg.util.WJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UIDef extends ADefBase<UIItemDef> {
	private static final Logger LOG = LoggerFactory.getLogger(UIDef.class);
	private static final long serialVersionUID = -4373891894076468347L;
	public static final int TYPE_Form = 0;//表单类型的UI定义，包含一个uiSet集合
	public static final int TYPE_DataGrid = 1;//DataGrid类型的UI定义，包含两个uiSet集合：dataTable，queryForm
	public static final int TYPE_TreeForm = 2;//树型结构类型的UI定义，包含一个uiSet集合

	public static final String CFG_ColProfile = "_ColProfile";//el-col
	public static final String CFG_InnerHtml = "_InnerHtml";//在子组件之前的innerHTML代码
	public static final String CFG_InnerHtml2 = "_InnerHtml2";//在子组件之后的innerHTML代码

	public EntityDef entityDef;//关联实体对象定义
	public String outDir;//目标文件夹
	public String action;//action类
	public int type = -1;

	//当前group的索引
	public int groupIndex = -1;
	//当前group定义
	public UIItemDef groupDef;

	public List<ValidateDef> validates = new ArrayList<>();

	private UIDef(String name) {
		super(name);
	}

	public UIDef(EntityDef entityDef, int type) {
		super(genNameU(entityDef.entityCls));
		common(entityDef.project, entityDef.pkg, entityDef.comments);
		this.entityDef = entityDef;
		this.type = type;
		action = entityDef.getActionUrl();
		outDir = "views\\" + project + "\\" + entityDef.getActionUrl() + "\\";

	}

	public static UIDef form(EntityDef entityDef) {
		return new UIDef(entityDef, TYPE_Form);
	}

	public static UIDef dataGrid(EntityDef entityDef) {
		return new UIDef(entityDef, TYPE_DataGrid);
	}

	public static UIDef treeForm(EntityDef entityDef) {
		return new UIDef(entityDef, TYPE_TreeForm);
	}

	//-----------------------------------------------------------
	public void addGroupDef(UIItemDef groupDef) {
		groupIndex++;
		this.groupDef = groupDef;
		addPropDef(groupDef);
	}

	/**
	 * 切换到下一个group
	 */
	public void switchGroup(int index) {
		groupIndex = index;
		groupDef = defs.get(groupIndex);
	}

	public UIDef addItemDef(UIItemDef item) {
		groupDef.addChild(item);
		return this;
	}

	public UIItemDef fetchItem(String field) {
		for (UIItemDef item : groupDef.getItems()) {
			if (field.equals(item.field)) { return item; }
		}
		LOG.warn("未找到字段：" + field);
		return null;
	}

	@Override
	public UIItemDef newDef(String key, String title) {
		UIItemDef item = new UIItemDef(key, title);
		return item;
	}
}
