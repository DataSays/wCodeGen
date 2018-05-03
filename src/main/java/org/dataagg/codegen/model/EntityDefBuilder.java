package org.dataagg.codegen.model;

import static org.dataagg.codegen.util.ElementUI.*;

import org.dataagg.codegen.base.ADefBuilderBase;
import org.dataagg.util.collection.StrObj;

public class EntityDefBuilder extends ADefBuilderBase<EntityDef, EntityItemDef> {

	public EntityDefBuilder(EntityDef m) {
		super(m);
	}

	public static EntityDefBuilder newEntityDef(String project, String applictionPkg, String name, String pkg, String entityCls, String comment) {
		EntityDef entityDef = new EntityDef(name, applictionPkg, entityCls);
		entityDef.common(project, pkg, comment);
		EntityDefBuilder builder = new EntityDefBuilder(entityDef);
		return builder;
	}

	public static EntityDefBuilder newParentDef(String project, String applictionPkg, String pkg, String entityCls, String comment) {
		EntityDef entityDef = new EntityDef(entityCls, applictionPkg, entityCls);
		entityDef.common(project, pkg, comment);
		EntityDefBuilder builder = new EntityDefBuilder(entityDef);
		return builder;
	}

	/**
	 * 树状数据结构
	 * @param idField 主键字段名
	 * @param parentField 父节点关联字段名
	 * @return
	 */
	public EntityDefBuilder isTree(String idField, String parentField) {
		main.addCfg("isTree", true);
		main.addCfg("idField", idField);
		main.addCfg("parentField", parentField);
		return this;
	}

	public EntityDefBuilder isMasterDetail(EntityDefBuilder detail) {
		main.addCfg("detailCls", detail.main.entityCls);
		detail.main.addCfg("isDetail", true);
		return this;
	}

	public EntityDefBuilder actionUrl(String actionUrl) {
		main.addCfg("actionUrl", actionUrl);
		return this;
	}

	public EntityDefBuilder serialVersionUID(long serialVersionUID) {
		main.addCfg("serialVersionUID", serialVersionUID);
		return this;
	}

	public EntityDefBuilder parentCls(String parentCls) {
		main.addCfg("parentCls", parentCls);
		return this;
	}

	public EntityDefBuilder parentDef(EntityDefBuilder parentBuilder) {
		main.addCfg("isParentDef", true);
		EntityDef parent = parentBuilder.main;
		for (EntityItemDef item : parent.getDefs()) {
			item.addCfg("parentDef", parent.entityCls);
			main.addPropDef(item);
		}
		return this;
	}

	public EntityDefBuilder serviceInterface(String serviceInterface) {
		main.addCfg("serviceInterface", serviceInterface);
		return this;
	}

	public EntityDefBuilder addLongPk() {
		return addLongPk("id", 16);
	}

	public EntityDefBuilder addLongPk(String key, int width) {
		EntityItemDef item = main.addPkDef(key, "ID", "Long");
		item.addCfg("itemType", "LongId");
		item.addCfg("dataType", "BIGINT");
		item.addCfg("width", width);
		return this;
	}

	public EntityDefBuilder addStrPk(String key, int width) {
		EntityItemDef item = main.addPkDef(key, "ID", "String");
		item.addCfg("itemType", "StringId");
		item.addCfg("dataType", "VARCHAR");
		item.addCfg("width", width);
		return this;
	}

	public EntityDefBuilder addString(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "String");
		item.addCfg("itemType", "String");
		item.addCfg("dataType", "VARCHAR");
		item.addCfg("width", 200);
		ui(create(TYPE_Text));
		return this;
	}

	public EntityDefBuilder addLong(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "Long");
		item.addCfg("itemType", "Long");
		item.addCfg("dataType", "BIGINT");
		item.addCfg("width", 16);
		return this;
	}

	public EntityDefBuilder addObj(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "Object");
		item.addCfg("itemType", "Object");
		item.addCfg("dataType", "VARCHAR");
		item.addCfg("width", 200);
		return this;
	}

	public EntityDefBuilder addLongText(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "String");
		item.addCfg("itemType", "LongText");
		item.addCfg("dataType", "VARCHAR");
		ui(create(TYPE_TextArea, "_colProfile", "1"));
		return this;
	}

	public EntityDefBuilder addDate(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "java.util.Date");
		item.addCfg("itemType", "Date");
		item.addCfg("dataType", "TIMESTAMP");
		ui(create(TYPE_DateTime));
		return this;
	}

	/**
	 * 日期范围
	 *
	 * @param field1
	 * @param field2
	 * @param comment1
	 * @param comment2
	 * @return
	 */
	public EntityDefBuilder addDateRange(String field1, String field2, String comment1, String comment2) {
		addDate(field1, comment1);
		ui(create(TYPE_Date));
		addDate(field2, comment2);
		ui(create(TYPE_Date));
		return this;
	}

	/**
	 * 时间范围
	 *
	 * @param field1
	 * @param field2
	 * @param comment1
	 * @param comment2
	 * @return
	 */
	public EntityDefBuilder addTimeRange(String field1, String field2, String comment1, String comment2) {
		addDate(field1, comment1);
		ui(create(TYPE_DateTime));
		addDate(field2, comment2);
		ui(create(TYPE_DateTime));
		return this;
	}

	public EntityDefBuilder addFile(String field, String comment) {
		addString(field, comment).width(64);
		ui(custom("upload-file", ":grouping", "entity." + field, "_colProfile", "1"));
		return this;
	}

	//排序字段
	public EntityDefBuilder addSort(String comment) {
		addInt("sort", comment).defaultVal(10).notNull().ui(create(TYPE_InputNumber, ":min", "1"));
		return this;
	}

	public EntityDefBuilder addMoney(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "java.math.BigDecimal");
		item.addCfg("itemType", "Money");
		item.addCfg("dataType", "FLOAT");
		item.addCfg("width", 10);
		item.addCfg("precision", 2);
		return this;
	}

	public EntityDefBuilder addBoolean(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "Boolean");
		item.addCfg("itemType", "Boolean");
		item.addCfg("dataType", "SMALLINT");
		item.addCfg("width", 1);
		return this;
	}

	public EntityDefBuilder addInt(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "Integer");
		item.addCfg("itemType", "Int");
		item.addCfg("dataType", "INT");
		item.addCfg("width", 11);
		return this;
	}

	public EntityDefBuilder addFloat(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "Float");
		item.addCfg("itemType", "Float");
		item.addCfg("dataType", "FLOAT");
		item.addCfg("width", 10);
		return this;
	}

	public EntityDefBuilder addDouble(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "Double");
		item.addCfg("itemType", "Double");
		item.addCfg("dataType", "DOUBLE");
		item.addCfg("width", 10);
		return this;
	}

	public EntityDefBuilder addLongKey(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "Long");
		item.addCfg("itemType", "LongKey");
		item.addCfg("dataType", "BIGINT");
		item.addCfg("width", 16);
		return this;
	}

	public EntityDefBuilder addStrKey(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "String");
		item.addCfg("itemType", "StringKey");
		item.addCfg("dataType", "VARCHAR");
		item.addCfg("width", 30);
		return this;
	}

	public EntityDefBuilder addStrObj(String field, String comment) {
		addLongText(field, comment);
		EntityItemDef item = lastItem();
		item.valCls = StrObj.class.getName();
		item.addCfg("itemType", "StrObj");
		item.addCfg("dataType", "VARCHAR");
		item.addCfg("width", 30000);
		return this;
	}

	public EntityDefBuilder addFlag(String field, String comment) {
		EntityItemDef item = main.addPropDef(field, comment, "String");
		item.addCfg("itemType", "Flag");
		item.addCfg("dataType", "CHAR");
		item.addCfg("width", 1);
		notNull();
		return this;
	}

	//删除标识
	public EntityDefBuilder addDelFlag(String comment) {
		addFlag("delFlag", comment).defaultVal("0").notNull().ui(create(TYPE_Switch, "active-text", "未删除", "inactive-text", "已删除"));
		return this;
	}

	//启停标识
	public EntityDefBuilder addEnableFlag(String comment) {
		addBoolean("enableFlag", comment).defaultVal(true).notNull().ui(create(TYPE_Switch, "active-text", "启用", "inactive-text", "停用"));
		return this;
	}

	public EntityDefBuilder addDict(String field, String dictName, String comment) {
		addString(field, comment);
		EntityItemDef item = lastItem();
		item.addCfg("itemType", "Dict");
		item.addCfg("dictName", dictName);
		item.addCfg("width", 50);
		ui(select(dictName));
		return this;
	}

	public EntityDefBuilder addCategory(String field, String dictType, String categoryType, String comment) {
		addLongKey(field, comment);
		EntityItemDef item = lastItem();
		item.addCfg("itemType", "Category");
		item.addCfg("dictType", dictType);
		item.addCfg("categoryType", categoryType);
		return this;
	}

	public EntityDefBuilder notNull() {
		EntityItemDef item = lastItem();
		item.addCfg("notNull", "true");
		return this;
	}

	public EntityDefBuilder defaultVal(Object defaultVal) {
		EntityItemDef item = lastItem();
		item.defaultVal = defaultVal;
		return this;
	}

	public EntityDefBuilder width(int width) {
		EntityItemDef item = lastItem();
		item.addCfg("width", width);
		return this;
	}

	public EntityDefBuilder noGen() {
		EntityItemDef item = lastItem();
		item.addCfg("noGen", "true");
		return this;
	}

	public EntityDefBuilder addOne(String field, String comment, String cls) {
		return addOne(field + "Id", field + "Id", field, comment, cls);
	}

	public EntityDefBuilder addOne(String fkDbField, String fkJavafiled, String field, String comment, String cls) {
		addLongKey(fkDbField, fkDbField);
		main.addOne2OneDef(field, comment, cls, fkDbField, fkJavafiled);
		EntityItemDef item = lastItem();
		item.addCfg("itemType", "One");
		return this;
	}

	public EntityDefBuilder addStrOne(String fkDbField, String fkJavafiled, String field, String comment, String cls) {
		addStrKey(fkDbField, fkDbField);
		main.addOne2OneDef(field, comment, cls, fkDbField, fkJavafiled);
		EntityItemDef item = lastItem();
		item.addCfg("itemType", "One");
		return this;
	}

	public EntityDefBuilder addMany(String field, String mainKey, String comment, String cls) {
		main.addOne2ManyDef(field, comment, cls, mainKey);
		EntityItemDef item = lastItem();
		item.addCfg("itemType", "Many");
		return this;
	}

	public EntityDefBuilder addMany2Many(String field, String relation, String from, String to, String comment, String cls) {
		main.addMany2ManyDef(cls, field, comment, relation, from, to);
		EntityItemDef item = lastItem();
		item.addCfg("itemType", "Many2Many");
		return this;
	}

	/**
	 * 配置默认使用UI组件显示字段时的组件参数，可一次设置多个，使用时默认使用第一个，也可指定使用第几套方案
	 *
	 * @param uiDef
	 * @return
	 */
	public EntityDefBuilder ui(UIItemDef uiDef) {
		EntityItemDef item = lastItem();
		if (uiDef != null) {
			uiDef.field = item.field;
			uiDef.title = item.title;
			item.uiDef = uiDef;
		}
		return this;
	}
}
