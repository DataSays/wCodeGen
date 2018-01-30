package org.dataagg.codegen.model;

import org.dataagg.codegen.base.AUIDefSet;

/**
 *
 * DataTable定义
 *
 * DataAgg
 */
public class UIDataTableDef extends AUIDefSet {
	private static final long serialVersionUID = -6613624606629187549L;

	//##CodeMerger.code:_CustomFields
	public static final int TYPE_Hidden = 1;//隐藏字段
	public static final int TYPE_Text = 10;//普通文本
	public static final int TYPE_DictText = 11;//字典数据文本
	public static final int TYPE_ELText = 200;//表达式定义的文本j
	public static final int TYPE_Date = 50;//日期
	public static final int TYPE_Time = 55;//时间
	public static final int TYPE_DateTime = 56;//日期时间
	public static final int TYPE_Upload = 60;//上传文件

	public UIDataTableDef(String name) {
		super(name);
	}

	public UIDataTableDef(EntityDef entityDef) {
		super(entityDef);
	}
	//##CodeMerger.code

	//--------------------setter & getter-----------------------------------
}
