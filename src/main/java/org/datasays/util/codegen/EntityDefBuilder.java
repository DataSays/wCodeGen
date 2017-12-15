package org.datasays.util.codegen;

import org.datasays.util.codegen.model.EEntityDef;
import org.datasays.util.codegen.model.EEntityItemDef;
import org.datasays.util.collection.StrObj;

public class EntityDefBuilder {
	public EEntityDef main;

	public static EntityDefBuilder newEntityDef(String project, String name, String pkg, String entityCls, String comment) {
		EntityDefBuilder builder = new EntityDefBuilder();
		builder.main = new EEntityDef(name, pkg, entityCls, comment);
		builder.main.setProject(project);
		return builder;
	}

	public static EntityDefBuilder newParentDef(String project, String pkg, String entityCls, String comment) {
		EntityDefBuilder builder = new EntityDefBuilder();
		builder.main = new EEntityDef(entityCls, pkg, entityCls, comment);
		builder.main.setProject(project);
		return builder;
	}

	public EntityDefBuilder isTree() {
		main.addCfg("isTree", true);
		return this;
	}

	public EntityDefBuilder isMasterDetail(EntityDefBuilder detail) {
		main.addCfg("detailCls", detail.main.getEntityCls());
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
		EEntityDef parent = parentBuilder.main;
		for (EEntityItemDef item : parent.getDefs()) {
			item.addCfg("parentDef", parent.getEntityCls());
			main.addPropDef(item);
		}
		return this;
	}

	private EEntityItemDef lastItem() {
		return main.getDefs().get(main.getDefs().size() - 1);
	}

	public EntityDefBuilder addLongId() {
		EEntityItemDef item = main.addPkDef("id", "ID", "Long");
		item.addCfg("itemType", "LongId");
		item.addCfg("dataType", "BIGINT");
		item.addCfg("width", 16);
		return this;
	}

	public EntityDefBuilder addString(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "String");
		item.addCfg("itemType", "String");
		item.addCfg("dataType", "VARCHAR");
		item.addCfg("width", 200);
		return this;
	}

	public EntityDefBuilder addLong(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "Long");
		item.addCfg("itemType", "Long");
		item.addCfg("dataType", "BIGINT");
		item.addCfg("width", 16);
		return this;
	}

	public EntityDefBuilder addObj(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "Object");
		item.addCfg("itemType", "Object");
		item.addCfg("dataType", "VARCHAR");
		item.addCfg("width", 200);
		return this;
	}

	public EntityDefBuilder addLongText(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "String");
		item.addCfg("itemType", "LongText");
		item.addCfg("dataType", "LONGTEXT");
		return this;
	}

	public EntityDefBuilder addDate(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "java.util.Date");
		item.addCfg("itemType", "Date");
		item.addCfg("dataType", "DATETIME");
		return this;
	}

	public EntityDefBuilder addFile(String field, String comment) {
		addString(field, comment).width(64);
		return this;
	}

	public EntityDefBuilder addMoney(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "java.math.BigDecimal");
		item.addCfg("itemType", "Money");
		item.addCfg("dataType", "FLOAT");
		item.addCfg("width", 10);
		item.addCfg("precision", 2);
		return this;
	}

	public EntityDefBuilder addBoolean(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "Boolean");
		item.addCfg("itemType", "Boolean");
		item.addCfg("dataType", "TINYINT");
		item.addCfg("width", 1);
		return this;
	}

	public EntityDefBuilder addInt(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "Integer");
		item.addCfg("itemType", "Int");
		item.addCfg("dataType", "INT");
		item.addCfg("width", 11);
		return this;
	}

	public EntityDefBuilder addFloat(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "Float");
		item.addCfg("itemType", "Float");
		item.addCfg("dataType", "FLOAT");
		item.addCfg("width", 10);
		return this;
	}

	public EntityDefBuilder addDouble(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "Double");
		item.addCfg("itemType", "Double");
		item.addCfg("dataType", "DOUBLE");
		item.addCfg("width", 10);
		return this;
	}

	public EntityDefBuilder addLongKey(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "Long");
		item.addCfg("itemType", "LongKey");
		item.addCfg("dataType", "BIGINT");
		item.addCfg("width", 16);
		return this;
	}

	public EntityDefBuilder addStrObj(String field, String comment) {
		addLongText(field, comment);
		EEntityItemDef item = lastItem();
		item.setValCls(StrObj.class.getName());
		item.addCfg("itemType", "StrObj");
		item.addCfg("dataType", "LONGTEXT");
		return this;
	}

	public EntityDefBuilder addFlag(String field, String comment) {
		EEntityItemDef item = main.addPropDef(field, comment, "String");
		item.addCfg("itemType", "Flag");
		item.addCfg("dataType", "CHAR");
		item.addCfg("width", 1);
		notNull();
		return this;
	}

	public EntityDefBuilder addDict(String field, String dictName, String comment) {
		addString(field, comment);
		EEntityItemDef item = lastItem();
		item.addCfg("itemType", "Dict");
		item.addCfg("dictName", dictName);
		item.addCfg("width", 50);
		return this;
	}

	public EntityDefBuilder addCategory(String field, String dictType, String categoryType, String comment) {
		addLongKey(field, comment);
		EEntityItemDef item = lastItem();
		item.addCfg("itemType", "Category");
		item.addCfg("dictType", dictType);
		item.addCfg("categoryType", categoryType);
		return this;
	}

	public EntityDefBuilder notNull() {
		EEntityItemDef item = lastItem();
		item.addCfg("notNull", "true");
		return this;
	}

	public EntityDefBuilder defaultVal(Object defaultVal) {
		EEntityItemDef item = lastItem();
		item.setDefaultVal(defaultVal);
		return this;
	}

	public EntityDefBuilder width(int width) {
		EEntityItemDef item = lastItem();
		item.addCfg("width", width);
		return this;
	}

	public EntityDefBuilder noGen() {
		EEntityItemDef item = lastItem();
		item.addCfg("noGen", "true");
		return this;
	}

	public EntityDefBuilder addOne(String field, String comment, String cls) {
		addLongKey(field + "Id", field + "Id");
		main.addOne2OneDef(field, comment, cls, field + "Id");
		EEntityItemDef item = lastItem();
		item.addCfg("itemType", "One");
		return this;
	}

	public EntityDefBuilder addMany(String field, String mainKey, String comment, String cls) {
		main.addOne2ManyDef(field, comment, cls, mainKey);
		EEntityItemDef item = lastItem();
		item.addCfg("itemType", "Many");
		return this;
	}

	public EntityDefBuilder addMany2Many(String field, String relation, String from, String to, String comment, String cls) {
		main.addMany2ManyDef(cls, field, comment, relation, from, to);
		EEntityItemDef item = lastItem();
		item.addCfg("itemType", "Many2Many");
		return this;
	}
}
