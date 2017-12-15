package org.dataagg.codegen.base;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.dataagg.codegen.model.EntityDef;
import org.dataagg.codegen.model.UIItemDef;
import org.dataagg.util.props.PropDef;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jodd.util.StringUtil;

public abstract class AUIDefSet extends ADefBase<UIItemDef> {
	private static final long serialVersionUID = -5931083090046947541L;
	public EntityDef entityDef;//关联实体对象定义
	private String outDir;//目标文件夹
	private String action;//action类

	public AUIDefSet(String name) {
		super(name);
	}

	public AUIDefSet(EntityDef entityDef) {
		super(genNameU(entityDef.getEntityCls()));
		common(entityDef.getProject(), entityDef.getPkg(), entityDef.getComments());
		this.entityDef = entityDef;
	}

	@Override
	public UIItemDef newDef(String key, String title) {
		UIItemDef item = new UIItemDef(key, title);
		return item;
	}

	public void init(String name, String title, String cls, boolean isTree) {
		setName(title);
		String niceName = StringUtil.cutPrefix(cls, "E");
		if (niceName.startsWith("UI")) {
			niceName = "ui" + niceName.substring(2);
		} else {
			niceName = StringUtil.uncapitalize(niceName);
		}
		setAction(niceName);
		addCfg("backAction", "/" + niceName + "/list");
		addCfg("isTree", isTree ? "true" : "false");
		setOutDir("views\\base\\" + niceName + "\\");
	}

	//关联字典名称
	public String getDictName(PropDef def) {
		return (String) def.getCfg("dictName");
	}

	public void setDictName(PropDef def, String value) {
		def.addCfg("dictName", value);
	}

	public UIItemDef getItemByField(String field) {
		if (defs != null) {
			for (UIItemDef item : defs) {
				if (item.getField().equals(field)) { return item; }
			}
		}
		return null;
	}

	/**
	 * 从defs里解析分组的定义集合
	 * @return
	 */
	@JsonIgnore
	public Map<String, List<UIItemDef>> getDefGroups() {
		Map<String, List<UIItemDef>> defGroups = new Hashtable<>();
		if (defs != null) {
			for (UIItemDef def : defs) {
				if (def.isDefGroupItem()) {
					String groupName = def.getDefGroupName();
					List<UIItemDef> defGItems = defGroups.get(groupName);
					if (defGItems == null) {
						defGItems = new ArrayList<>();
					}
					defGItems.add(def);
					defGroups.put(groupName, defGItems);
				}
			}
		}
		return defGroups;
	}

	public String getOutDir() {
		return outDir;
	}

	public void setOutDir(String outDir) {
		this.outDir = outDir;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
