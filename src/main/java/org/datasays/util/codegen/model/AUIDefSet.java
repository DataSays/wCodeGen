package org.datasays.util.codegen.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;

import org.datasays.commons.base.ILongIdEntity;
import org.datasays.util.collection.StrObj;
import org.datasays.util.props.PropDef;
import org.datasays.util.props.PropStrObj;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jodd.util.StringUtil;

public abstract class AUIDefSet implements PropStrObj<EUIItemDef>, ILongIdEntity {
	private static final long serialVersionUID = -5931083090046947541L;

	@Id
	@Comment("Id")
	private Long id;
	@Name
	@Comment("属性集名称")
	private String name;

	@Column
	@Comment("目标文件夹")
	private String outDir;

	@Column
	@Comment("action类")
	private String action;

	@Many(field = "masterId")
	@Comment("属性定义")
	private List<EUIItemDef> defs;

	private StrObj values;

	@Column
	@Comment("备注")
	private String comment;

	//额外配置信息
	@Column
	@Comment("额外配置")
	private StrObj cfg;

	public AUIDefSet(String name) {

	}

	@Override
	public EUIItemDef newDef(String key, String title) {
		EUIItemDef item = new EUIItemDef(key, title);
		return item;
	}

	public void init(String name, String title, String cls,boolean isTree) {
		setName(title);
		String niceName = StringUtil.cutPrefix(cls, "E");
		if (niceName.startsWith("UI")) {
			niceName = "ui" + niceName.substring(2);
		} else {
			niceName = StringUtil.uncapitalize(niceName);
		}
		setAction(niceName);
		addCfg("backAction", "/" + niceName + "/list");
		addCfg("isTree", isTree?"true":"false");
		setOutDir("views\\base\\" + niceName + "\\");
	}

	//关联字典名称
	public String getDictName(PropDef def) {
		return (String) def.getCfg("dictName");
	}

	public void setDictName(PropDef def, String value) {
		def.addCfg("dictName", value);
	}

	public EUIItemDef getItemByField(String field) {
		if (defs != null) {
			for (EUIItemDef item : defs) {
				if (item.getField().equals(field)) { return item; }
			}
		}
		return null;
	}

	@Override
	public List<EUIItemDef> getDefs() {
		return defs;
	}

	@Override
	public void setDefs(List<EUIItemDef> defs) {
		this.defs = defs;
	}

	@Override
	public StrObj getValues() {
		return values;
	}

	@Override
	public void setValues(StrObj values) {
		this.values = values;
	}

	public StrObj addCfg(String name, Object value) {
		if (value == null) { return cfg; }
		if (cfg == null) {
			cfg = new StrObj(name, value);
		} else {
			cfg.put(name, value);
		}
		return cfg;
	}

	public Object getCfg(String name) {
		return cfg == null ? null : cfg.get(name);
	}

	/**
	 * 从defs里解析分组的定义集合
	 * @return
	 */
	@JsonIgnore
	public Map<String, List<EUIItemDef>> getDefGroups() {
		Map<String, List<EUIItemDef>> defGroups = new Hashtable<>();
		if (defs != null) {
			for (EUIItemDef def : defs) {
				if (def.isDefGroupItem()) {
					String groupName = def.getDefGroupName();
					List<EUIItemDef> defGItems = defGroups.get(groupName);
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

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public StrObj getCfg() {
		return cfg;
	}

	public void setCfg(StrObj cfg) {
		this.cfg = cfg;
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
