package org.datasays.util.codegen.model;
import java.util.*;
import org.nutz.dao.entity.annotation.*;
import org.datasays.commons.base.*;

/**
 *
 * DataTable定义
 *
 * EntityDefBuilder
 */
@Table("da_ui_datatable_def")
@Comment("DataTable定义")
public class EUIDataTableDef extends AUIDefSet{
	private static final long serialVersionUID = -6613624606629187549L;
//##JavaCodeMerger.code:0
	public static final int TYPE_Hidden = 1;//隐藏字段
	public static final int TYPE_Text = 10;//普通文本
	public static final int TYPE_ELText = 200;//表达式定义的文本
	public static final int TYPE_Date = 50;//日期
	public static final int TYPE_Time = 55;//时间
	public static final int TYPE_DateTime = 56;//日期时间
	public static final int TYPE_Upload = 60;//上传文件

	public EUIDataTableDef() {
		super(null);
	}

	public EUIDataTableDef(String name) {
		super(name);
	}
//##JavaCodeMerger.code

	//--------------------setter & getter-----------------------------------
}
