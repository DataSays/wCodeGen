package org.datasays.util.codegen.model;
import java.util.*;
import org.nutz.dao.entity.annotation.*;
import org.datasays.commons.base.*;
import org.datasays.util.codegen.model.EEntityItemDef;
import org.datasays.util.collection.StrObj;


/**
 *
 * Entity对象定义
 *
 * EntityDefBuilder
 */
@Table("da_entity_def")
@Comment("Entity对象定义")
public class EEntityDef extends AEntityDefBase<EEntityItemDef>{
	private static final long serialVersionUID = -62122864223757115L;
	@Id
	@Comment("Id")
	private Long id;

	@Column()
	@Comment("名称")
	@ColDefine(type = ColType.VARCHAR , width = 200)
	private String name;

	@Column()
	@Comment("所属项目")
	@ColDefine(type = ColType.VARCHAR , width = 200)
	private String project;

	@Column()
	@Comment("包名")
	@ColDefine(type = ColType.VARCHAR , width = 200)
	private String pkg;

	@Column()
	@Comment("实体类")
	@ColDefine(type = ColType.VARCHAR , width = 200)
	private String entityCls;

	@Column()
	@Comment("额外配置")
	@ColDefine(customType = "LONGTEXT" )
	private StrObj cfg;

	@Many(field = "masterId")
	@Comment("Entity对象Item定义")
	private List<EEntityItemDef> defs;

	@Column()
	@Comment("备注")
	@ColDefine(customType = "LONGTEXT" )
	private String comments;

//##JavaCodeMerger.code:0
	public EEntityDef() {
		this(null);
	}

	public EEntityDef(String name) {
		this.name = name;
	}

	public EEntityDef(String name, String pkg, String entityCls, String comment) {
		this(name);
		comments = comment;
		this.pkg = pkg;
		this.entityCls = entityCls;
	}

	@Override
	public EEntityItemDef newDef(String key, String title) {
		return new EEntityItemDef(key, title);
	}

	public String getFullCls() {
		return getPkg() + "." + getEntityCls();
	}
//##JavaCodeMerger.code

	//--------------------setter & getter-----------------------------------
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getEntityCls() {
		return entityCls;
	}

	public void setEntityCls(String entityCls) {
		this.entityCls = entityCls;
	}

	public StrObj getCfg() {
		return cfg;
	}

	public void setCfg(StrObj cfg) {
		this.cfg = cfg;
	}

	public List<EEntityItemDef> getDefs() {
		return defs;
	}

	public void setDefs(List<EEntityItemDef> defs) {
		this.defs = defs;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}
