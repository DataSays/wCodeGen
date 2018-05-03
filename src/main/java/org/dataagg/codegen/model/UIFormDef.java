package org.dataagg.codegen.model;

import java.util.HashSet;
import java.util.Set;

import org.dataagg.codegen.base.AUIDefSet;
import org.dataagg.util.WJsonExclued;
import org.dataagg.util.props.PropDef;

/**
 *
 * Form定义
 *
 * DataAgg
 */
public class UIFormDef extends AUIDefSet {
	private static final long serialVersionUID = 4073853305310495827L;

	//##CodeMerger.code:_CustomFields
	/**
	 * cfg:
	 * 	backAction: 完成action后跳转的url
	 * 	colSpan: 在一行内占多少格,最大值是4, 默认值是2
	 * 	required:boolean 是否是必填项, 默认false
	 *  disabled	禁用	boolean	—	false
	 *  extAttribute: String 其他配置属性值
	 *
	 */
	public static final int TYPE_Hidden = 1;//隐藏字段
	public static final int TYPE_Text = 10;//普通文本
	/**
	 * cfg:
	 * 	rows	输入框行数，只对 type="textarea" 有效	number	—	2
	 */
	public static final int TYPE_TextArea = 15;//多行文本
	/**
	 * cfg:
	 * 	editorOption:
	 */
	public static final int TYPE_RiceText = 16;//富文本

	public static final int TYPE_Password = 17;//密码类型
	public static final int TYPE_InputNumber = 20;//计数器
	public static final int TYPE_Slider = 26;//滑块
	public static final int TYPE_Switch = 27;//开关
	public static final int TYPE_Rate = 28;//评分
	public static final int TYPE_Color = 29;//颜色
	public static final int TYPE_CheckBox = 30;//多选框
	public static final int TYPE_Radio = 35;//单选
	public static final int TYPE_Select = 40;//选择器
	public static final int TYPE_Cascader = 45;//级联选择器
	public static final int TYPE_Date = 50;//日期
	public static final int TYPE_Time = 55;//时间
	public static final int TYPE_DateTime = 56;//日期时间
	public static final int TYPE_Upload = 60;//文件上传

	public Set<String> pKeys;
	public Set<String> linkEntities; //已连接的实体, 实体全名
	public Set<String> linkFields; //已连接的实体字段, 实体全民:实体字段

	public UIFormDef(String name) {
		super(name);
	}

	public UIFormDef(EntityDef entityDef) {
		super(entityDef);
	}

	//step1. 添加关联实体的所有字段定义信息
	public void addEntityDef(EntityDef entityDef, String... fields) {
		//		for (PropDef def : entityDef.defs) {
		//			PropDef def1 = def.clone();
		//			def1.key = entityDef.name + "." + def1.key;
		//			if (entityDef.isPKey(def1.key)) {
		//				//主键默认生效,并且使用隐藏字段显示
		//				def1.enabled = true;
		//				def1.type = PropDef.TYPE_Hidden;
		//			} else {
		//				def1.enabled = false;
		//			}
		//			addPropDef(def1);
		//		}
		if (pKeys == null) {
			pKeys = new HashSet<>();
		}
		if (linkEntities == null) {
			linkEntities = new HashSet<>();
		}
		if (linkFields == null) {
			linkFields = new HashSet<>();
		}
		for (String field : fields) {
			linkEntities.add(entityDef.entityCls);
			if (!field.contains(".")) {
				PropDef def = getPropDef(field);
				if (def != null && Integer.parseInt(def.type) < 20) {
					//非对象类型
					linkFields.add(entityDef.entityCls + ":" + field);
					if ("10".equals(def.type)) {
						//主键字段
						pKeys.add(field);
					}
					//FIXME
					//addPropDef(def.clone());
				} else {
					throw new IllegalArgumentException(field + "不能添加此字段!");
				}
			} else {//嵌套字段
				String field0 = field.substring(0, field.indexOf("."));
				String field1 = field.substring(field.indexOf(".") + 1);
				PropDef def = getPropDef(field0);
				if (def != null && Integer.parseInt(def.type) >= 20) {
					EntityDef subEntityDef = fetchEntityDef(def.valCls);
					addEntityDef(subEntityDef, field1);
				} else {
					throw new IllegalArgumentException(field + "不能添加此字段!");
				}
			}
		}
	}

	private EntityDef fetchEntityDef(String valCls) {
		return null;
	}

	//是否是树形
	@WJsonExclued
	public boolean isTree() {
		return "true".equals(getCfg("isTree"));
	}

	public void isTree(boolean value) {
		addCfg("isTree", value ? "true" : "false");
	}
	//##CodeMerger.code

	//--------------------setter & getter-----------------------------------
}
