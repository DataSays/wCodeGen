package org.dataagg.codegen.model;

import org.dataagg.util.props.PropDef;

import jodd.util.StringUtil;

/**
 *
 * Entity对象item定义
 *
 * DataAgg
 */
public class EntityItemDef extends PropDef {
	private static final long serialVersionUID = 2311828431502009131L;
	public static final int TYPE_Object = 1;//Object类型
	public static final int TYPE_PKey = 10;//Long类型主键
	public static final int TYPE_FKey = 15;//外键字段
	public static final int TYPE_POJO = 20;//Pojo类型
	public static final int TYPE_List4One2Many = 25;//1:n的List<Pojo>类型
	public static final int TYPE_List4Many2Many = 26;//n:n的List<Pojo>类型

	public EntityItemDef() {
		super();
	}

	public EntityItemDef(Integer sort, String field, String type, String title) {
		super(sort, field, type, title);
	}

	public EntityItemDef(String field, String title) {
		super(field, title);
	}

	//数据存储宽度/长度
	public Integer getWidth() {
		return (Integer) getCfg("width");
	}

	public void setWidth(Integer value) {
		addCfg("width", value);
	}

	//数据存储精度,小数点后多少位,默认是2
	public Integer getPrecision() {
		return (Integer) getCfg("precision");
	}

	public void setPrecision(Integer value) {
		addCfg("precision", value);
	}

	//数据存储类型
	public String getDataType() {
		return (String) getCfg("dataType");
	}

	public void setDataType(String value) {
		addCfg("dataType", value);
	}

	//数据库字段名
	public String getColName() {
		return (String) getCfg("colName");
	}

	public void setColName(String value) {
		addCfg("colName", value);
	}

	//notNull
	public boolean notNull() {
		return "true".equals(getCfg("notNull"));
	}

	public void notNull(boolean value) {
		addCfg("notNull", value ? "true" : "false");
	}

	//是否是主键
	public boolean isPK() {
		return "true".equals(getCfg("isPK"));
	}

	public void isPK(boolean value) {
		addCfg("isPK", value ? "true" : "false");
	}

	//关联关系:__One2One__,__One2Many__,<table>
	public String getRelation() {
		String s = (String) getCfg("relation");
		return StringUtil.isBlank(s) ? "" : s;
	}

	public void setRelation(String value) {
		addCfg("relation", value);
	}

	//关联关系-关联字段前缀
	public String getRelationPrefix() {
		String s = (String) getCfg("relationPrefix");
		return StringUtil.isBlank(s) ? "" : s;
	}

	public void setRelationPrefix(String value) {
		addCfg("relationPrefix", value);
	}

	//关联字段1
	public String getRelationFrom() {
		String s = (String) getCfg("relationFrom");
		return StringUtil.isBlank(s) ? "" : s;
	}

	public void setRelationFrom(String value) {
		addCfg("relationFrom", value);
	}

	//关联字段2
	public String getRelationTo() {
		String s = (String) getCfg("relationTo");
		return StringUtil.isBlank(s) ? "" : s;
	}

	public void setRelationTo(String value) {
		addCfg("relationTo", value);
	}

	public boolean isOne() {
		return StringUtil.isBlank(getRelation());
	}

	public boolean isOne2One() {
		return EntityDef.One2One.equals(getRelation());
	}

	public boolean isOne2Many() {
		return EntityDef.One2Many.equals(getRelation());
	}

	public boolean isMany2Many() {
		String relation = getRelation();
		return relation != null && relation != "" && !EntityDef.One2One.equals(relation) && !EntityDef.One2Many.equals(relation);
	}

	public boolean isCreateBy() {
		return StringUtil.equals(getField(), "createBy");
	}

	public boolean isCreateDate() {
		return StringUtil.equals(getField(), "createDate");
	}

	public boolean isUpdateBy() {
		return StringUtil.equals(getField(), "updateBy");
	}

	public boolean isUpdateDate() {
		return StringUtil.equals(getField(), "updateDate");
	}

	public boolean isDelFlag() {
		return StringUtil.equals(getField(), "delFlag");
	}
}
