package org.datasays.util.codegen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;

import org.datasays.commons.base.ILongIdEntity;
import org.datasays.util.Constans;
import org.datasays.util.collection.StrObj;
import org.datasays.util.props.PropDef;
import org.datasays.util.props.PropStrObj;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jodd.util.StringUtil;

public abstract class AEntityDefBase<I extends AEntityItemDefBase> implements PropStrObj<I>, ILongIdEntity {
	private static final long serialVersionUID = 3575924190406837306L;

	private StrObj values;

	public Set<String> pkeys;// 主键字段名
	@Deprecated
	public Set<String> ukeys;// unique字段名
	public Set<String> fkeys;// 外键字段名

	public List<StrObj> relations = new ArrayList<StrObj>();//存储关联关系

	public boolean isCreateBy = false;
	public boolean isCreateDate = false;
	public boolean isUpdateBy = false;
	public boolean isUpdateDate = false;
	public boolean isDelFlag = false;

	public abstract StrObj getCfg();

	public abstract void setCfg(StrObj cfg);

	public abstract String getPkg();

	public abstract void setPkg(String pkg);

	public abstract String getProject();

	public abstract void setProject(String project);

	public abstract String getEntityCls();

	public abstract void setEntityCls(String entityCls);

	@Override
	public abstract List<I> getDefs();

	@Override
	public abstract void setDefs(List<I> defs);

	@Override
	public StrObj getValues() {
		return values;
	}

	@Override
	public void setValues(StrObj values) {
		this.values = values;
	}

	// 添加一个Long类型的主键
	public I addPkDef(String key, String title, String cls) {
		I def = addPropDef(key, title, cls);
		def.setType(1 + "");
		def.isPK(true);
		pkeys = StrObj.add4Set(pkeys, key);
		return def;
	}

	// 添加一个@Name注解类型的unique
	@Deprecated
	public I addUniqueDef(String key, String title, String cls) {
		I def = addPropDef(key, title, cls);
		def.setType(10 + "");
		def.isUnique(true);
		ukeys = StrObj.add4Set(ukeys, key);
		return def;
	}

	// 获取实体的主键过滤条件
	@JsonIgnore
	public Cnd getPKCnd() {
		if (pkeys != null && pkeys.size() > 0) {
			Cnd cnd = null;
			int i = 0;
			for (String pkField : pkeys) {
				Object pkValue = val(pkField);
				if (pkValue == null) { throw new IllegalArgumentException("主键" + pkField + "的值不能为null!"); }
				if (i == 0) {
					cnd = Cnd.where(pkField, "=", pkValue);
				} else {
					cnd.and(pkField, "=", pkValue);
				}
				i++;
			}
			return cnd;
		}
		throw new IllegalArgumentException("实体定义中缺少主键设置!");
	}

	// 根据实体的非主键属性值生成对应的set语句
	@JsonIgnore
	public Chain getNoPkChain() {
		Chain chain = null;
		if (values != null && values.size() > 0) {
			for (String field : values.keySet()) {
				if (pkeys == null || !pkeys.contains(field)) {
					Object value = val(field);
					if (value != null) {
						if (chain == null) {
							chain = Chain.make(field, value);
						} else {
							chain.add(field, value);
						}
					}
				}
			}
		}
		if (chain == null) { throw new IllegalArgumentException("实体属性值中缺少可用值!"); }
		return chain;
	}

	@JsonIgnore
	public boolean isPKey(String key) {
		return pkeys != null && pkeys.contains(key);
	}

	@JsonIgnore
	public boolean isUKey(String key) {
		return ukeys != null && ukeys.contains(key);
	}

	@JsonIgnore
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
		I def = addPropDef(key, title, valCls);
		def.setRelation(Constans.EntityType.One2One);
		def.setRelationFrom(field);
		fkeys = StrObj.add4Set(fkeys, field);
		return def;
	}

	public I addOne2ManyDef(String key, String title, String valCls, String mainKey) {
		I def = addListDef(key, title, valCls);
		def.setRelation(Constans.EntityType.One2Many);
		def.setRelationFrom(mainKey);
		fkeys = StrObj.add4Set(fkeys, mainKey);
		return def;
	}

	public PropDef addMany2ManyDef(String entityCls, String key, String title, String relation, String from, String to) {
		I def = addListDef(key, title, entityCls);
		def.setRelation(relation);
		def.setRelationFrom(from);
		def.setRelationTo(to);
		fkeys = StrObj.add4Set(fkeys, from);
		fkeys = StrObj.add4Set(fkeys, to);
		return def;
	}

	@Override
	public void rebuild() {
		List<I> defs = getDefs();
		if (defs == null) { return; }
		StrObj relation = new StrObj();
		for (I def : defs) {
			if (def.isPK()) {
				pkeys = StrObj.add4Set(pkeys, def.getField());
			}
			if (def.isUnique()) {
				ukeys = StrObj.add4Set(ukeys, def.getField());
			}
			if (def.isOne2One()) {
				fkeys = StrObj.add4Set(fkeys, def.getField());
				relation = new StrObj();
				relation.add4Set("relationType", Constans.EntityType.One2One);
				relation.add4Set("entityItemDef", def);
				relations.add(relation);
			} else if (def.isOne2Many()) {
				fkeys = StrObj.add4Set(fkeys, def.getField());
				relation = new StrObj();
				relation.add4Set("relationType", Constans.EntityType.One2Many);
				relation.add4Set("entityItemDef", def);
				relations.add(relation);
			} else if (def.isMany2Many()) {
				fkeys = StrObj.add4Set(fkeys, def.getRelationFrom());
				fkeys = StrObj.add4Set(fkeys, def.getRelationTo());
				relation = new StrObj();
				relation.add4Set("relationType", Constans.EntityType.Many2Many);
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

	public StrObj addCfg(String name, Object value) {
		StrObj cfg = getCfg();
		if (value == null) { return cfg; }
		if (cfg == null) {
			cfg = new StrObj(name, value);
			setCfg(cfg);
		} else {
			cfg.put(name, value);
		}
		return cfg;
	}

	public Object getCfg(String name) {
		StrObj cfg = getCfg();
		return cfg == null ? null : cfg.get(name);
	}

	// actionUrl
	@JsonIgnore
	public String getActionUrl() {
		String actionUrl = (String) getCfg("actionUrl");
		return StringUtil.isBlank(actionUrl) ? "" : actionUrl;
	}

	//是否是树形
	@JsonIgnore
	public boolean isTree() {
		return getCfg("isTree") != null && getCfg("isTree").equals(true);
	}

	//master类名, 如果没有返回null
	@JsonIgnore
	public String getDetailCls() {
		return (String) getCfg("detailCls");
	}

	//是否是Detail类
	@JsonIgnore
	public boolean isDetail() {
		return getCfg("isDetail") != null && getCfg("isDetail").equals(true);
	}

	public void isTree(boolean value) {
		addCfg("isTree", value);
	}

	public String getOutDir() {
		String pkg = getPkg();
		String project = getProject();
		String outDir = pkg.substring(0, pkg.lastIndexOf("."));
		outDir = StringUtil.replace(outDir, ".", "\\");
		return String.format("..\\%s\\src\\main\\java\\%s", project, outDir);
	}
}
