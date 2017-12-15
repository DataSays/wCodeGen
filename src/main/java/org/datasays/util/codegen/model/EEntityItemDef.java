package org.datasays.util.codegen.model;
import java.util.*;
import org.nutz.dao.entity.annotation.*;
import org.datasays.commons.base.*;

/**
 *
 * Entity对象item定义
 *
 * EntityDefBuilder
 */
@Table("da_entity_item_def")
@Comment("Entity对象item定义")
public class EEntityItemDef extends AEntityItemDefBase{
	private static final long serialVersionUID = 2311828431502009131L;
	@Column()
	@Comment("主表Id")
	@ColDefine(type = ColType.INT , width = 16)
	private Long masterId;

//##JavaCodeMerger.code:0
	public EEntityItemDef() {
		super();
	}

	public EEntityItemDef(Integer sort, String field, String type, String title) {
		super(sort, field, type, title);
	}

	public EEntityItemDef(String field, String title) {
		super(field, title);
	}
//##JavaCodeMerger.code

	//--------------------setter & getter-----------------------------------
	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

}
