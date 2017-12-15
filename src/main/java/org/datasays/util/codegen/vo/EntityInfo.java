package org.datasays.util.codegen.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.datasays.util.Constans;
import org.datasays.util.codegen.model.EEntityDef;
import org.datasays.util.codegen.model.EEntityItemDef;
import org.datasays.util.collection.StrObj;

import jodd.util.StringUtil;

public class EntityInfo {
	private static final Logger LOG = LoggerFactory.getLogger(EntityInfo.class);
	private EEntityDef entityDef = null;
	private Set<String> pkeys;// 主键字段名
	private Set<String> ukeys;// unique字段名
	private Set<String> fkeys;// 外键字段名

	private List<StrObj> relations = new ArrayList<StrObj>();//存储关联关系

	private boolean isCreateBy = false;
	private boolean isCreateDate = false;
	private boolean isUpdateBy = false;
	private boolean isUpdateDate = false;
	private boolean isDelFlag = false;
	private String clsName;
	private boolean clsTree;
	private String clsDesc;
	private String clsAuthor;
	private String entityCls;
	private String entityPkg;
	private String actionUrl;
	private String entityClsName;
	private String daoCls;
	private String daoClsName;
	private String serviceCls;
	private String serviceClsName;
	private String controllerCls;
	private String testCls;
	private String applictionCls;
	private String applictionPkg;
	private String rootPkg;
	private String controllerPkg;
	private String servicePkg;
	private String daoPkg;
	private String testPkg;

	private String importCodes;

	private String[] javaCodes;

	public EntityInfo(EEntityDef entityDef) {
		super();
		this.entityDef = entityDef;
	}

	private void rebuild() {
		List<EEntityItemDef> defs = entityDef.getDefs();
		if (defs == null) { return; }
		StrObj relation = new StrObj();
		for (EEntityItemDef def : defs) {
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

	public static String genClassName(String entityCls, String name) {
		return genName(entityCls) + name;
	}

	public static String genName(String entityCls) {
		String cls = entityCls;
		if (cls.startsWith("Entity")) {

		} else if (cls.startsWith("E")) {
			cls = cls.substring(1);
		} else if (cls.startsWith("EUI")) {
			cls = "UI" + cls.substring(3);
		} else if (cls.startsWith("UI")) {
			cls = "UI" + cls.substring(2);
		}
		return cls;
	}

	public static String genNameL(String entityCls) {
		String cls = genName(entityCls);
		cls = StringUtil.uncapitalize(cls);
		if (cls.startsWith("uI")) {
			cls = "ui" + cls.substring(2);
		}
		return cls;
	}

	public static String genNameU(String entityCls) {
		String cls = genName(entityCls);
		cls = StringUtil.capitalize(cls);
		if (cls.startsWith("Ui")) {
			cls = "UI" + cls.substring(2);
		}
		return cls;
	}

	/**
	 * 拼接pre和field，且field首字母大写
	 */
	public static String genGSMethod(String pre, String field) {
		return pre + field.substring(0, 1).toUpperCase() + field.substring(1);
	}

	/**
	 * 根据项目生成代码路径 main
	 */
	public static String genProjectSrcMainPath(String project) {
		return "..\\" + project + "\\src\\main\\java";
	}

	/**
	 * 根据项目生成代码路径 test
	 */
	public static String genProjectSrcTestPath(String project) {
		return "..\\" + project + "\\src\\test\\java";
	}

	/**
	 * 根据实体名称生成路径
	 */
	public static String genControllerUrl(String entityCls) {
		return genNameL(entityCls);
	}

	/**
	 * 根据项目生成路径，来生成Application的相关信息
	 * 比如配置的commons项目的下entity配置的cfg  project
	 */
	public static String genProjectApplication(Object project) {
		String p = project.toString();
		return p.substring(0, 1).toUpperCase() + p.substring(1) + "Application";
	}

	//-------------------------------getter & setter -------------------------------------

	public EEntityDef getEntityDef() {
		return entityDef;
	}

	public void setEntityDef(EEntityDef entityDef) {
		this.entityDef = entityDef;
	}

	public Set<String> getPkeys() {
		return pkeys;
	}

	public void setPkeys(Set<String> pkeys) {
		this.pkeys = pkeys;
	}

	public Set<String> getUkeys() {
		return ukeys;
	}

	public void setUkeys(Set<String> ukeys) {
		this.ukeys = ukeys;
	}

	public Set<String> getFkeys() {
		return fkeys;
	}

	public void setFkeys(Set<String> fkeys) {
		this.fkeys = fkeys;
	}

	public List<StrObj> getRelations() {
		return relations;
	}

	public void setRelations(List<StrObj> relations) {
		this.relations = relations;
	}

	public boolean isCreateBy() {
		return isCreateBy;
	}

	public void setCreateBy(boolean isCreateBy) {
		this.isCreateBy = isCreateBy;
	}

	public boolean isCreateDate() {
		return isCreateDate;
	}

	public void setCreateDate(boolean isCreateDate) {
		this.isCreateDate = isCreateDate;
	}

	public boolean isUpdateBy() {
		return isUpdateBy;
	}

	public void setUpdateBy(boolean isUpdateBy) {
		this.isUpdateBy = isUpdateBy;
	}

	public boolean isUpdateDate() {
		return isUpdateDate;
	}

	public void setUpdateDate(boolean isUpdateDate) {
		this.isUpdateDate = isUpdateDate;
	}

	public boolean isDelFlag() {
		return isDelFlag;
	}

	public void setDelFlag(boolean isDelFlag) {
		this.isDelFlag = isDelFlag;
	}

	public String getClsName() {
		return clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}

	public boolean isClsTree() {
		return clsTree;
	}

	public void setClsTree(boolean clsTree) {
		this.clsTree = clsTree;
	}

	public String getClsDesc() {
		return clsDesc;
	}

	public void setClsDesc(String clsDesc) {
		this.clsDesc = clsDesc;
	}

	public String getClsAuthor() {
		return clsAuthor;
	}

	public void setClsAuthor(String clsAuthor) {
		this.clsAuthor = clsAuthor;
	}

	public String getEntityCls() {
		return entityCls;
	}

	public void setEntityCls(String entityCls) {
		this.entityCls = entityCls;
	}

	public String getEntityPkg() {
		return entityPkg;
	}

	public void setEntityPkg(String entityPkg) {
		this.entityPkg = entityPkg;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public String getEntityClsName() {
		return entityClsName;
	}

	public void setEntityClsName(String entityClsName) {
		this.entityClsName = entityClsName;
	}

	public String getDaoCls() {
		return daoCls;
	}

	public void setDaoCls(String daoCls) {
		this.daoCls = daoCls;
	}

	public String getDaoClsName() {
		return daoClsName;
	}

	public void setDaoClsName(String daoClsName) {
		this.daoClsName = daoClsName;
	}

	public String getServiceCls() {
		return serviceCls;
	}

	public void setServiceCls(String serviceCls) {
		this.serviceCls = serviceCls;
	}

	public String getServiceClsName() {
		return serviceClsName;
	}

	public void setServiceClsName(String serviceClsName) {
		this.serviceClsName = serviceClsName;
	}

	public String getControllerCls() {
		return controllerCls;
	}

	public void setControllerCls(String controllerCls) {
		this.controllerCls = controllerCls;
	}

	public String getTestCls() {
		return testCls;
	}

	public void setTestCls(String testCls) {
		this.testCls = testCls;
	}

	public String getApplictionCls() {
		return applictionCls;
	}

	public void setApplictionCls(String applictionCls) {
		this.applictionCls = applictionCls;
	}

	public String getApplictionPkg() {
		return applictionPkg;
	}

	public void setApplictionPkg(String applictionPkg) {
		this.applictionPkg = applictionPkg;
	}

	public String getRootPkg() {
		return rootPkg;
	}

	public void setRootPkg(String rootPkg) {
		this.rootPkg = rootPkg;
	}

	public String getControllerPkg() {
		return controllerPkg;
	}

	public void setControllerPkg(String controllerPkg) {
		this.controllerPkg = controllerPkg;
	}

	public String getServicePkg() {
		return servicePkg;
	}

	public void setServicePkg(String servicePkg) {
		this.servicePkg = servicePkg;
	}

	public String getDaoPkg() {
		return daoPkg;
	}

	public void setDaoPkg(String daoPkg) {
		this.daoPkg = daoPkg;
	}

	public String getTestPkg() {
		return testPkg;
	}

	public void setTestPkg(String testPkg) {
		this.testPkg = testPkg;
	}

	public String getImportCodes() {
		return importCodes;
	}

	public void setImportCodes(String importCodes) {
		this.importCodes = importCodes;
	}

	public String[] getJavaCodes() {
		return javaCodes;
	}

	public void setJavaCodes(String[] javaCodes) {
		this.javaCodes = javaCodes;
	}
}
