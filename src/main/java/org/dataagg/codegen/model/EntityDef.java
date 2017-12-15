package org.dataagg.codegen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dataagg.codegen.base.ADefBase;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.props.PropDef;

import jodd.util.StringUtil;

/**
 *
 * Entity对象定义
 *
 * DataAgg
 */
public class EntityDef extends ADefBase<EntityItemDef> {
	private static final long serialVersionUID = -62122864223757115L;
	public static final String One2One = "__One2One__";
	public static final String One2Many = "__One2Many__";
	public static final String Many2Many = "__Many2Many__";
	private String entityCls;//entity类额外配置

	public Set<String> pkeys;// 主键字段名
	public Set<String> fkeys;// 外键字段名

	public List<StrObj> relations = new ArrayList<StrObj>();//存储关联关系

	public boolean isCreateBy = false;
	public boolean isCreateDate = false;
	public boolean isUpdateBy = false;
	public boolean isUpdateDate = false;
	public boolean isDelFlag = false;

	public EntityDef(String name) {
		super(name);
	}

	public EntityDef(String name, String entityCls) {
		this(name);
		this.entityCls = entityCls;
	}

	@Override
	public StrObj buildModel() {
		StrObj model = super.buildModel();
		String nameU = genName(entityCls);
		model.put("rootPkg", getRootPkg());
		model.put("name", name);
		model.put("project", project);
		model.put("pkg", pkg);
		model.put("entityCls", entityCls);
		model.put("comments", comments);
		model.put("nameU", nameU);
		model.put("nameL", genNameL(nameU));

		// actionUrl
		String actionUrl = getActionUrl();
		if (StringUtil.isBlank(actionUrl)) {
			actionUrl = genNameL(entityCls);
			addCfg("actionUrl", actionUrl);
		}
		model.put("actionUrl", actionUrl);

		String applictionCls = getProject();
		applictionCls = applictionCls.substring(0, 1).toUpperCase() + applictionCls.substring(1) + "Application";

		model.put("applictionCls", applictionCls);
		model.put("applictionPkg", "com.dataagg");

		// parentCls
		String parentCls = (String) getCfg("parentCls");
		if (parentCls == null) {
			parentCls = "implements ILongIdEntity";
			if (isTree()) {
				parentCls = "implements ITreeLongIdEntity<" + getEntityCls() + ">";
			}
			addCfg("parentCls", parentCls);
		}

		model.put("entityDef", this);
		return model;
	}

	@Override
	public EntityItemDef newDef(String key, String title) {
		return new EntityItemDef(key, title);
	}

	public String getFullCls() {
		return getPkg() + "." + getEntityCls();
	}

	public String getSimpleName() {
		return ADefBase.genName(getEntityCls());
	}

	public String getEntityNameU() {
		return ADefBase.genNameU(getEntityCls());
	}

	public String getEntityNameL() {
		return ADefBase.genNameL(getEntityCls());
	}

	// 添加一个Long类型的主键
	public EntityItemDef addPkDef(String key, String title, String cls) {
		EntityItemDef def = addPropDef(key, title, cls);
		def.setType(1 + "");
		def.isPK(true);
		pkeys = StrObj.add4Set(pkeys, key);
		return def;
	}

	public boolean isPKey(String key) {
		return pkeys != null && pkeys.contains(key);
	}

	public boolean isFKey(String key) {
		return fkeys != null && fkeys.contains(key);
	}

	// 暂未用到该方法
	public void addTreeDef(String parent, String parentIds, String items) {
		String entityCls = getEntityCls();
		addPropDef(parent == null ? parent : "parent", "上级节点", entityCls);
		addPropDef(parentIds == null ? parentIds : "parentIds", "上级节点", "String");
		addListDef(items == null ? items : "items", "子节点", entityCls);
	}

	public PropDef addOne2OneDef(String key, String title, String valCls, String field) {
		EntityItemDef def = addPropDef(key, title, valCls);
		def.setRelation(EntityDef.One2One);
		def.setRelationFrom(field);
		fkeys = StrObj.add4Set(fkeys, field);
		return def;
	}

	public EntityItemDef addOne2ManyDef(String key, String title, String valCls, String mainKey) {
		EntityItemDef def = addListDef(key, title, valCls);
		def.setRelation(EntityDef.One2Many);
		def.setRelationFrom(mainKey);
		fkeys = StrObj.add4Set(fkeys, mainKey);
		return def;
	}

	public PropDef addMany2ManyDef(String entityCls, String key, String title, String relation, String from, String to) {
		EntityItemDef def = addListDef(key, title, entityCls);
		def.setRelation(relation);
		def.setRelationFrom(from);
		def.setRelationTo(to);
		fkeys = StrObj.add4Set(fkeys, from);
		fkeys = StrObj.add4Set(fkeys, to);
		return def;
	}

	public void rebuild() {
		List<EntityItemDef> defs = getDefs();
		if (defs == null) { return; }
		StrObj relation = new StrObj();
		for (EntityItemDef def : defs) {
			if (def.isPK()) {
				pkeys = StrObj.add4Set(pkeys, def.getField());
			}
			if (def.isOne2One()) {
				fkeys = StrObj.add4Set(fkeys, def.getField());
				relation = new StrObj();
				relation.add4Set("relationType", EntityDef.One2One);
				relation.add4Set("entityItemDef", def);
				relations.add(relation);
			} else if (def.isOne2Many()) {
				fkeys = StrObj.add4Set(fkeys, def.getField());
				relation = new StrObj();
				relation.add4Set("relationType", EntityDef.One2Many);
				relation.add4Set("entityItemDef", def);
				relations.add(relation);
			} else if (def.isMany2Many()) {
				fkeys = StrObj.add4Set(fkeys, def.getRelationFrom());
				fkeys = StrObj.add4Set(fkeys, def.getRelationTo());
				relation = new StrObj();
				relation.add4Set("relationType", EntityDef.Many2Many);
				relation.add4Set("entityItemDef", def);
				relations.add(relation);
			}
			if (!isCreateBy && def.isCreateBy()) {
				isCreateBy = true;
			}
			if (!isCreateDate && def.isCreateDate()) {
				isCreateDate = true;
			}
			if (!isUpdateBy && def.isUpdateBy()) {
				isUpdateBy = true;
			}
			if (!isUpdateDate && def.isUpdateDate()) {
				isUpdateDate = true;
			}
			if (!isDelFlag && def.isDelFlag()) {
				isDelFlag = true;
			}
		}
		//		System.out.println(this.entityCls+":::"+ isCreateBy+ "" +isCreateDate+ "" +isUpdateBy+ "" +isUpdateDate+ "" +isDelFlag);
	}

	// actionUrl
	public String getActionUrl() {
		String actionUrl = (String) getCfg("actionUrl");
		return StringUtil.isBlank(actionUrl) ? "" : actionUrl;
	}

	//是否是树形
	public boolean isTree() {
		return getCfg("isTree") != null && getCfg("isTree").equals(true);
	}

	//master类名, 如果没有返回null
	public String getDetailCls() {
		return (String) getCfg("detailCls");
	}

	//是否是Detail类
	public boolean isDetail() {
		return getCfg("isDetail") != null && getCfg("isDetail").equals(true);
	}

	public void isTree(boolean value) {
		addCfg("isTree", value);
	}

	//--------------------setter & getter-----------------------------------
	public String getEntityCls() {
		return entityCls;
	}

	public void setEntityCls(String entityCls) {
		this.entityCls = entityCls;
	}
}
