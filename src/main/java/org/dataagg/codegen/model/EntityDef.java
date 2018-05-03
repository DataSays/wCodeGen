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
	public String entityCls;//entity类额外配置
	public String applictionPkg;

	public Set<String> pkeys;// 主键字段名
	public Set<String> fkeys;// 外键字段名

	public List<StrObj> relations = new ArrayList<StrObj>();//存储关联关系

	public EntityDef(String name) {
		super(name);
	}

	public EntityDef(String name, String applictionPkg, String entityCls) {
		this(name);
		this.entityCls = entityCls;
		this.applictionPkg = applictionPkg;
	}

	@Override
	public EntityItemDef newDef(String key, String title) {
		return new EntityItemDef(key, title);
	}

	public String getFullCls() {
		return pkg + "." + entityCls;
	}

	public String getSimpleName() {
		return ADefBase.genName(entityCls);
	}

	public String getEntityNameU() {
		return ADefBase.genNameU(entityCls);
	}

	public String getEntityNameL() {
		return ADefBase.genNameL(entityCls);
	}

	// 添加一个Long类型的主键
	public EntityItemDef addPkDef(String key, String title, String cls) {
		EntityItemDef def = addPropDef(key, title, cls);
		def.type = 1 + "";
		def.isPK(true);
		addCfg("pkType", cls);
		addCfg("pkField", key);
		pkeys = StrObj.add4Set(pkeys, key);
		return def;
	}

	public boolean isPKey(String key) {
		return pkeys != null && pkeys.contains(key);
	}

	public boolean isFKey(String key) {
		return fkeys != null && fkeys.contains(key);
	}

	public PropDef addOne2OneDef(String key, String title, String valCls, String field, String fkJavafiled) {
		EntityItemDef def = addPropDef(key, title, valCls);
		def.setRelation(EntityDef.One2One);
		def.setRelationFrom(field);
		def.addCfg("relationFromField", fkJavafiled);
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

	public EntityItemDef find(String field) {
		for (EntityItemDef item : defs) {
			if (item.field.equals(field)) { return item; }
		}
		return null;
	}

	public void build() {
		List<EntityItemDef> defs = getDefs();
		if (defs == null) { return; }
		StrObj relation = new StrObj();
		for (EntityItemDef def : defs) {
			if (def.isPK()) {
				pkeys = StrObj.add4Set(pkeys, def.field);
			}
			if (def.isOne2One()) {
				fkeys = StrObj.add4Set(fkeys, def.field);
				relation = new StrObj();
				relation.add4Set("relationType", EntityDef.One2One);
				relation.add4Set("entityItemDef", def);
				relations.add(relation);
			} else if (def.isOne2Many()) {
				fkeys = StrObj.add4Set(fkeys, def.field);
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
		}
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

	public String pkType() {
		return cfg.strVal("pkType");
	}

	public String pkField() {
		return cfg.strVal("pkField");
	}
}
