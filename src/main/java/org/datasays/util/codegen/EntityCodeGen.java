package org.datasays.util.codegen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.nutz.dao.Cnd;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Mirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.datasays.util.WJsonUtils;
import org.datasays.util.codegen.model.EEntityDef;
import org.datasays.util.codegen.model.EEntityItemDef;
import org.datasays.util.codegen.model.EUIFormDef;
import org.datasays.util.codegen.service.EntityDefService;
import org.datasays.util.codegen.vo.EntityInfo;
import org.datasays.util.collection.StrObj;

import jodd.util.ArraysUtil;
import jodd.util.StringUtil;

public class EntityCodeGen {
	private static final Logger LOG = LoggerFactory.getLogger(EntityCodeGen.class);

	private EntityDefService entityDefService;

	private UICodeGen uiCodeGen;

	private final String webDir = "..\\..\\pscWeb\\src\\";

	/**
	 * 扫描srcDir目录下pkg包下的所有实体类, 转化为EntityDef添加到定义库中
	 *
	 * @param srcDir
	 * @param pkg
	 * @deprecated org.datasays.util.codegen.EntityCodeGen.saveEntityDef(EntityDefBuilder)
	 */
	@Deprecated
	public void scanPkg(String project, String pkg) {
		String srcDir = EntityInfo.genProjectSrcMainPath(project);
		CodeGenUtils.findClassByAnnotation(srcDir, pkg, Table.class, (clsModel, a) -> {
			genEntityInfo(srcDir, project, pkg, clsModel, a);
		});
	}

	/**
	 * 扫描指定的实体类, 转化为EntityDef添加到定义库中
	 *
	 * @param srcDir
	 * @param pkg
	 * @param entityClsName
	 * @deprecated org.datasays.util.codegen.EntityCodeGen.saveEntityDef(EntityDefBuilder)
	 */
	@Deprecated
	public void scanPkg(String project, String pkg, String entityClsName) {
		String srcDir = EntityInfo.genProjectSrcMainPath(project);
		CodeGenUtils.findClassByAnnotation(srcDir, pkg, Table.class, (clsModel, a) -> {
			if (StringUtil.isNotBlank(entityClsName) && StringUtil.equals(entityClsName, clsModel.getSimpleName())) {
				genEntityInfo(srcDir, project, pkg, clsModel, a);
			}
		});
	}

	@Deprecated
	public void genEntityInfo(String srcDir, String project, String pkg, Class<Table> clsModel, Table a) throws Exception {
		boolean updateFlag = false;
		EEntityDef entityDef = entityDefService.fetchWithItems(pkg, clsModel.getSimpleName());
		List<EEntityItemDef> oldDefs = null;
		EEntityDef oldEntityDef = entityDef;
		if (entityDef == null) {
			entityDef = new EEntityDef(a.value(), clsModel.getPackage().getName(), clsModel.getSimpleName(), "");
		} else {
			updateFlag = true;
			oldDefs = entityDef.getDefs();
			entityDef.setDefs(null);
		}
		Mirror<?> mirror = Mirror.me(clsModel);
		// comment
		Comment comment = mirror.getAnnotation(Comment.class);
		if (comment != null) {
			entityDef.setComments(comment.value());
		}
		// all fields
		Field[] fields = mirror.getFields();

		for (Field field : fields) {
			EEntityItemDef entityItemDef = null;

			// comment
			comment = field.getAnnotation(Comment.class);
			String commentText = field.getName();
			if (comment != null) {
				commentText = comment.value();
			}

			if (field.getAnnotation(Id.class) != null) {
				// Id
				entityItemDef = entityDef.addPkDef(field.getName(), commentText, field.getType().getName());
			} else if (field.getAnnotation(Name.class) != null) {
				if (checkEntityExistIdAndName(fields)) {
					// id Name
					entityItemDef = entityDef.addUniqueDef(field.getName(), commentText, field.getType().getName());
				} else {
					// Name
					entityItemDef = entityDef.addPkDef(field.getName(), commentText, field.getType().getName());
				}
			} else if (field.getAnnotation(PK.class) != null) {
				// FIXME PK
				entityItemDef = entityDef.addPkDef(field.getName(), commentText, field.getType().getName());
			}

			// Column
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				if (entityItemDef == null) {
					entityItemDef = entityDef.addPropDef(field.getName(), commentText, field.getType().getName());
				}
				if (StringUtil.equals(field.getName(), "parentId")) {
					entityDef.isTree(true);
				}
				entityItemDef.setColName(column.value());
			}
			// One
			One one = field.getAnnotation(One.class);
			if (one != null) {
				if (entityItemDef == null) {
					entityItemDef = (EEntityItemDef) entityDef.addOne2OneDef(field.getName(), commentText, field.getType().getName(), one.field());
				}
			}
			// Many
			Many many = field.getAnnotation(Many.class);
			if (many != null) {
				if (entityItemDef == null) {
					entityItemDef = entityDef.addOne2ManyDef(field.getName(), commentText, Mirror.getGenericTypes(field)[0].getName(), many.field());
					if (StringUtil.equals(clsModel.getSimpleName(), "EUIDataGridDef")) {
						entityItemDef.setRelationPrefix("G");
					} else if (StringUtil.equals(clsModel.getSimpleName(), "EUIDataTableDef")) {
						entityItemDef.setRelationPrefix("T");
					} else if (StringUtil.equals(clsModel.getSimpleName(), "EUIFormDef")) {
						entityItemDef.setRelationPrefix("F");
					}
				}
			}
			// ManyMany
			ManyMany manyMany = field.getAnnotation(ManyMany.class);
			if (manyMany != null) {
				if (entityItemDef == null) {
					entityItemDef = (EEntityItemDef) entityDef.addMany2ManyDef(Mirror.getGenericTypes(field)[0].getName(), field.getName(), commentText, manyMany.relation(), manyMany.from(), manyMany.to());
				}
			}
			// ColDefine
			ColDefine colDefine = field.getAnnotation(ColDefine.class);
			if (colDefine != null && entityItemDef != null) {
				entityItemDef.setWidth(colDefine.width());
				if (colDefine.precision() > 0) {
					entityItemDef.setPrecision(colDefine.precision());
				}
				if (entityItemDef.notNull()) {
					entityItemDef.notNull(true);
				}
				entityItemDef.setDataType(colDefine.type().name());
				if (colDefine.type() == ColType.INT || colDefine.type() == ColType.FLOAT) {
					entityItemDef.setType(EUIFormDef.TYPE_InputNumber + "");//计数器
				} else if (colDefine.type() == ColType.VARCHAR || colDefine.type() == ColType.CHAR) {
					entityItemDef.setType(EUIFormDef.TYPE_Text + "");
				} else if (colDefine.type() == ColType.BOOLEAN) {
					entityItemDef.setType(EUIFormDef.TYPE_Switch + "");//开关
				} else if (colDefine.type() == ColType.DATE) {
					entityItemDef.setType(EUIFormDef.TYPE_Date + "");//日期
				} else if (colDefine.type() == ColType.TIME) {
					entityItemDef.setType(EUIFormDef.TYPE_Time + "");//时间
				} else if (colDefine.type() == ColType.DATETIME) {
					entityItemDef.setType(EUIFormDef.TYPE_DateTime + "");//日期时间
				}
			}

			// 修改实体定义信息时，暂不覆盖cfg carlos
			if (updateFlag && oldDefs != null && !oldDefs.isEmpty()) {
				for (EEntityItemDef def : oldDefs) {
					if (StringUtil.equals(def.getField(), field.getName())) {
						if (entityItemDef != null) {
							entityItemDef.setCfg(def.getCfg());
						}
						break;
					}
				}
			}
			if (entityItemDef != null) {
				LOG.info(entityDef.getName() + ":" + entityItemDef.getField());
			} else {
				LOG.debug(entityDef.getName() + "的字段" + field.getName() + "没有关联注释");
			}
		}

		entityDef.setProject(project);
		// 修改实体定义信息时，暂不覆盖cfg carlos
		if (updateFlag && oldEntityDef != null && oldEntityDef.getCfg() != null) {
			entityDef.setCfg(oldEntityDef.getCfg());
		}

		entityDef.addCfg("actionUrl", EntityInfo.genControllerUrl(clsModel.getSimpleName()));

		// 清理旧数据items
		if (updateFlag && oldDefs != null && !oldDefs.isEmpty()) {
			entityDefService.deleteItemByMasterId(entityDef.getId());
		}
		entityDef.upDefSorts();
		entityDefService.save(entityDef);
		LOG.info(WJsonUtils.toJson(entityDef, true));
	}

	@Deprecated
	public boolean checkEntityExistIdAndName(Field[] fields) {
		int i = 0;
		for (Field field : fields) {
			if (field.getAnnotation(Id.class) != null) {
				i++;
			}
			if (field.getAnnotation(Name.class) != null) {
				i++;
			}
			if (i == 2) { return true; }
		}
		return false;
	}

	public StrObj buildModel(Map<String, EEntityDef> mapEntityDefs, EEntityDef entityDef) {
		// 初始化
		entityDef.rebuild();
		StrObj model = new StrObj();

		String entityCls = entityDef.getEntityCls();
		String entityPkg = entityDef.getPkg();
		String rootPkg = entityPkg.substring(0, entityPkg.lastIndexOf("."));
		String nameU = EntityInfo.genName(entityCls);
		model.put("rootPkg", rootPkg);
		model.put("nameU", nameU);
		model.put("nameL", EntityInfo.genNameL(nameU));

		// actionUrl
		String actionUrl = entityDef.getActionUrl();
		if (StringUtil.isBlank(actionUrl)) {
			actionUrl = EntityInfo.genControllerUrl(entityCls);
			entityDef.addCfg("actionUrl", actionUrl);
		}
		model.put("actionUrl", actionUrl);

		String applictionCls = EntityInfo.genProjectApplication(entityDef.getProject());
		model.put("applictionCls", applictionCls);
		model.put("applictionPkg", "com.dataagg");

		model.put("entityDef", entityDef);

		//relations
		List<StrObj> relations = new ArrayList<>();
		if (entityDef.relations != null && !entityDef.relations.isEmpty()) {
			relations = entityDef.relations;
			String valCls = "";
			EEntityDef tmpEntityDef = null;
			EEntityItemDef entityItemDef = null;
			for (StrObj obj : relations) {
				entityItemDef = (EEntityItemDef) obj.get("entityItemDef");
				valCls = entityItemDef.getValCls();
				entityPkg = StringUtil.substring(valCls, 0, StringUtil.lastIndexOfIgnoreCase(valCls, "."));
				entityCls = StringUtil.replace(valCls, entityPkg + ".", "");
				tmpEntityDef = mapEntityDefs.get(entityPkg + "." + entityCls);
				obj.putAll(buildModel(mapEntityDefs, tmpEntityDef));
				obj.put("", valCls);
				obj.put("itemCls", tmpEntityDef.getEntityCls());
				obj.put("itemNameU", obj.strVal("nameU"));
				obj.put("itemNameL", EntityInfo.genNameL(obj.strVal("nameU")));
				obj.put("field", entityItemDef.getField());
				obj.put("fieldU", EntityInfo.genNameU(entityItemDef.getField()));
				obj.put("relationTable", entityItemDef.getCfg("relation") != null ? entityItemDef.getCfg("relation") : "");
				String relationFrom = entityItemDef.getCfg("relationFrom") != null ? entityItemDef.getCfg("relationFrom").toString() : "";
				obj.put("relationFrom", relationFrom);
				obj.put("relationFromU", EntityInfo.genNameU(relationFrom));
				obj.put("relationTo", entityItemDef.getCfg("relationTo") != null ? entityItemDef.getCfg("relationTo") : "");
			}

			//移除重复关联
			Set<String> usedRelation = new HashSet<>();
			relations = relations.stream().filter(relation -> {
				EEntityItemDef entityItemDef1 = (EEntityItemDef) relation.get("entityItemDef");
				if (entityItemDef1 != null && entityItemDef1.getField() != null) {
					String relationNameU = entityItemDef1.getField();
					if (usedRelation.contains(relationNameU)) { return false; }
					usedRelation.add(relationNameU);
				}
				return true;
			}).collect(Collectors.toList());
		}
		model.put("relations", relations);
		return model;
	}

	/**
	 * 生成
	 *
	 * @param genVo
	 * @param entityDef
	 * @return
	 */

	public StrObj preGen(Map<String, EEntityDef> mapEntityDefs, EEntityDef entityDef) {
		// 初始化
		entityDef.rebuild();
		StrObj model = new StrObj();

		//特殊配置
		model.put("relations", new ArrayList<StrObj>());

		// 设置主键  //暂未在模版中使用
		//		if (entityDef.pkeys != null && !entityDef.pkeys.isEmpty()) {
		//			for (String pk : entityDef.pkeys) {
		//				model.put(pk, pk);
		//			}
		//		}

		// 设置外键  //暂未在模版中使用
		//		if (entityDef.fkeys != null && !entityDef.fkeys.isEmpty()) {
		//			for (String fk : entityDef.fkeys) {
		//				model.put("fk_"+fk, fk);
		//			}
		//		}

		//文件基本注释内容
		model.put("entityDef", entityDef);
		model.put("clsDesc", entityDef.getComments());
		model.put("clsAuthor", "DataAgg");

		//dao  service controller基础信息
		String entityCls = entityDef.getEntityCls();
		String entityPkg = entityDef.getPkg();
		String rootPkg = entityPkg.substring(0, entityPkg.lastIndexOf("."));
		String controllerPkg = rootPkg + ".controller";
		String servicePkg = rootPkg + ".service";
		String daoPkg = rootPkg + ".dao";
		String testPkg = rootPkg + ".service";

		// actionUrl
		String actionUrl = entityDef.getActionUrl();
		if (StringUtil.isBlank(actionUrl)) {
			actionUrl = EntityInfo.genControllerUrl(entityCls);
		}
		model.put("actionUrl", actionUrl);

		// entity
		model.put("entityCls", entityCls);
		model.put("entityClsName", actionUrl);
		model.put("entityPkg", entityPkg);

		// dao
		model.put("daoCls", EntityInfo.genClassName(entityCls, "Dao"));
		model.put("daoClsName", EntityInfo.genClassName(actionUrl, "Dao"));
		model.put("daoPkg", daoPkg);

		// service
		model.put("serviceCls", EntityInfo.genClassName(entityCls, "Service"));
		model.put("serviceClsName", EntityInfo.genClassName(actionUrl, "Service"));
		model.put("servicePkg", servicePkg);

		// controller
		String controllerCls = EntityInfo.genClassName(entityCls, "Controller");
		model.put("controllerCls", controllerCls);
		model.put("controllerPkg", controllerPkg);

		// test
		model.put("testCls", EntityInfo.genClassName(entityCls, "ServiceTest"));
		model.put("testPkg", testPkg);

		// test application
		String applictionCls = EntityInfo.genProjectApplication(entityDef.getProject());
		model.put("applictionCls", applictionCls);
		model.put("applictionPkg", "com.dataagg");

		//创建和修改信息
		model.put("createBy", entityDef.isCreateBy);
		model.put("createDate", entityDef.isCreateDate);
		model.put("updateBy", entityDef.isUpdateBy);
		model.put("updateDate", entityDef.isUpdateDate);
		//删除信息
		model.put("delFlag", entityDef.isDelFlag);

		if (!entityDef.relations.isEmpty()) {
			String valCls = "";
			EEntityDef tmpEntityDef = null;
			EEntityItemDef entityItemDef = null;
			for (StrObj obj : entityDef.relations) {
				entityItemDef = (EEntityItemDef) obj.get("entityItemDef");
				//				LOG.warn("entityItemDef:::" + ((EEntityItemDef) obj.get("entityItemDef")).toString());
				valCls = entityItemDef.getValCls();
				entityPkg = StringUtil.substring(valCls, 0, StringUtil.lastIndexOfIgnoreCase(valCls, "."));
				entityCls = StringUtil.replace(valCls, entityPkg + ".", "");
				//				tmpEntityDef = entityDefService.fetchWithItems(entityPkg, entityCls);
				tmpEntityDef = mapEntityDefs.get(entityPkg + "." + entityCls);
				obj.add4Set("relationEntityDef", preGen2(mapEntityDefs, tmpEntityDef));
				//				LOG.warn("relationEntityDef:::" + obj.get("relationEntityDef").toString());
				StrObj specialAttributes = new StrObj();

				specialAttributes.add4Set("relationPrefix", entityItemDef.getRelationPrefix());
				specialAttributes.add4Set("relationFrom", entityItemDef.getRelationFrom());
				specialAttributes.add4Set("relationTo", entityItemDef.getRelationTo());
				specialAttributes.add4Set("getItemsMethod", EntityInfo.genGSMethod("get", entityItemDef.getField()));
				specialAttributes.add4Set("setItemsMethod", EntityInfo.genGSMethod("set", entityItemDef.getField()));
				specialAttributes.add4Set("getMasterIdMethod", EntityInfo.genGSMethod("get", entityItemDef.getRelationFrom()));
				specialAttributes.add4Set("setMasterIdMethod", EntityInfo.genGSMethod("set", entityItemDef.getRelationFrom()));
				obj.add4Set("specialAttributes", specialAttributes);
				//				LOG.warn("specialAttributes:::" + obj.get("specialAttributes").toString());
			}

			//移除重复关联
			Set<String> usedRelation = new HashSet<>();
			entityDef.relations = entityDef.relations.stream().filter(relation -> {
				StrObj relationEntityDef = (StrObj) relation.get("relationEntityDef");
				if (relationEntityDef != null && relationEntityDef.get("daoClsName") != null) {
					String daoClsName = relationEntityDef.get("daoClsName").toString();
					if (usedRelation.contains(daoClsName)) { return false; }
					usedRelation.add(daoClsName);
				}
				return true;
			}).collect(Collectors.toList());

			model.put("relations", entityDef.relations);
		}
		return model;
	}

	public StrObj preGen2(Map<String, EEntityDef> mapEntityDefs, EEntityDef entityDef) {
		// 初始化
		entityDef.rebuild();
		StrObj model = new StrObj();

		//特殊配置
		model.put("haveRelations", "false");

		// 设置主键  //暂未在模版中使用
		//		if (entityDef.pkeys != null && !entityDef.pkeys.isEmpty()) {
		//			for (String pk : entityDef.pkeys) {
		//				model.put(pk, pk);
		//			}
		//		}

		// 设置外键  //暂未在模版中使用
		//		if (entityDef.fkeys != null && !entityDef.fkeys.isEmpty()) {
		//			for (String fk : entityDef.fkeys) {
		//				model.put("fk_"+fk, fk);
		//			}
		//		}

		//文件基本注释内容
		model.put("entityDef", entityDef);
		model.put("clsDesc", entityDef.getComments());
		model.put("clsAuthor", "DataAgg");

		//dao  service controller基础信息
		String entityCls = entityDef.getEntityCls();
		String entityPkg = entityDef.getPkg();
		String rootPkg = entityPkg.substring(0, entityPkg.lastIndexOf("."));
		String controllerPkg = rootPkg + ".controller";
		String servicePkg = rootPkg + ".service";
		String daoPkg = rootPkg + ".dao";
		String testPkg = rootPkg + ".service";

		// actionUrl
		String actionUrl = entityDef.getActionUrl();
		if (StringUtil.isBlank(actionUrl)) {
			actionUrl = EntityInfo.genControllerUrl(entityCls);
		}
		model.put("actionUrl", actionUrl);

		// entity
		model.put("entityCls", entityCls);
		model.put("entityClsName", actionUrl);
		model.put("entityPkg", entityPkg);

		// dao
		model.put("daoCls", EntityInfo.genClassName(entityCls, "Dao"));
		model.put("daoClsName", EntityInfo.genClassName(actionUrl, "Dao"));
		model.put("daoPkg", daoPkg);

		// service
		model.put("serviceCls", EntityInfo.genClassName(entityCls, "Service"));
		model.put("serviceClsName", EntityInfo.genClassName(actionUrl, "Service"));
		model.put("servicePkg", servicePkg);

		// controller
		String controllerCls = EntityInfo.genClassName(entityCls, "Controller");
		model.put("controllerCls", controllerCls);
		model.put("controllerPkg", controllerPkg);

		// test
		model.put("testCls", EntityInfo.genClassName(entityCls, "ServiceTest"));
		model.put("testPkg", testPkg);

		// test application
		String applictionCls = EntityInfo.genProjectApplication(entityDef.getProject());
		model.put("applictionCls", applictionCls);
		model.put("applictionPkg", "com.dataagg");

		//创建和修改信息
		model.put("createBy", entityDef.isCreateBy);
		model.put("createDate", entityDef.isCreateDate);
		model.put("updateBy", entityDef.isUpdateBy);
		model.put("updateDate", entityDef.isUpdateDate);
		//删除信息
		model.put("delFlag", entityDef.isDelFlag);

		model.put("haveRelations", entityDef.relations.isEmpty() ? "false" : "true");
		return model;
	}

	/**
	 * 根据EntityDef生成对应Entity的Dao对象
	 *
	 * @param genVo
	 * @param entityDef
	 */
	public void genDao(StrObj model, EEntityDef entityDef) throws Exception {
		LOG.debug("entityDef:::" + model.toString());
		String javaFile = String.format("%s/dao/%s.java", entityDef.getOutDir(), model.get("nameU") + "Dao");

		upJavaCodeMerger(model, javaFile, 2);

		if (entityDef.isTree()) {
			CodeGenUtils.genFtlCode("entity/treeDao.ftl", model, javaFile);
		} else {
			CodeGenUtils.genFtlCode("entity/simpleDao.ftl", model, javaFile);
		}
	}

	/**
	 * 根据EntityDef生成对应Entity的Service对象
	 *
	 * @param genVo
	 * @param entityDef
	 */
	public void genService(StrObj model, EEntityDef entityDef) throws Exception {
		String javaFile = String.format("%s/service/%s.java", entityDef.getOutDir(), model.get("nameU") + "Service");

		upJavaCodeMerger(model, javaFile, 2);
		if (entityDef.isTree()) {
			CodeGenUtils.genFtlCode("entity/treeService.ftl", model, javaFile);
		} else {
			CodeGenUtils.genFtlCode("entity/simpleService.ftl", model, javaFile);
		}
	}

	/**
	 * 根据EntityDef生成对应Entity的Controller对象
	 *
	 * @param srcDir
	 * @param pkg
	 */
	public void genController(StrObj model, EEntityDef entityDef) throws Exception {
		//uiCodeGen.genActionsJs(entityDef.getCfg("actionUrl").toString(), webDir + "actions\\", entityDef.isTree());
		String javaFile = String.format("%s/controller/%s.java", entityDef.getOutDir(), model.get("nameU") + "Controller");

		upJavaCodeMerger(model, javaFile, 3);

		if (entityDef.isTree()) {
			CodeGenUtils.genFtlCode("entity/treeController.ftl", model, javaFile);
		} else {
			CodeGenUtils.genFtlCode("entity/simpleController.ftl", model, javaFile);
		}
	}

	/**
	 * 根据EntityDef生成对应Entity的测试用例
	 *
	 * @param srcDir
	 * @param pkg
	 */
	public void genTest(StrObj model, EEntityDef entityDef) throws Exception {
		String testServicePath = StringUtil.replace(entityDef.getOutDir(), "\\main\\", "\\test\\") + "\\service";
		String javaFile = String.format("%s/%s.java", testServicePath, model.get("nameU") + "ServiceTest");

		upJavaCodeMerger(model, javaFile, 2);

		if (entityDef.isTree()) {
			CodeGenUtils.genFtlCode("entity/treeTest.ftl", model, javaFile);
		} else {
			CodeGenUtils.genFtlCode("entity/simpleTest.ftl", model, javaFile);
		}
	}

	/**
	 * 根据EntityDef生成对应的Entity对象
	 *
	 * @param driver
	 *            数据库类型,暂时只支持mysql
	 * @param ddlFile
	 *            生成文件路径
	 */
	public void genDDL(String driver, String ddlFile) {

	}

	/**
	 * 根据EntityDefBuilder保存对应的实体定义数据到数据库
	 * @param entityDefBuilder
	 */
	public void saveEntityDef(EntityDefBuilder entityDefBuilder) {
		try {
			entityDefService.saveAll(entityDefBuilder.main);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 根据EntityDefBuilder生成对应的实体类,包含NutDao相关注解,并保留定制的代码
	 * @param entityDefBuilder
	 */
	public void genEntityCls(EntityDefBuilder entityDefBuilder) {
		try {
			CodeGenHelper codeGenHelper = new CodeGenHelper();
			EEntityDef main = entityDefBuilder.main;
			codeGenHelper.appendln2("package %s;", main.getPkg());
			codeGenHelper.appendln2("import java.util.*;");
			codeGenHelper.appendln2("import org.nutz.dao.entity.annotation.*;");
			codeGenHelper.appendln2("import org.datasays.commons.base.*;");

			int importOffset = codeGenHelper.offset();
			Set<String> imports = new HashSet<>();

			String parentCls = "implements ILongIdEntity";
			boolean isTree = main.getCfg("isTree") != null && "true".equals(main.getCfg("isTree").toString());
			if (isTree) {
				parentCls = "implements ITreeLongIdEntity<" + main.getEntityCls() + ">";
				imports.add("org.datasays.util.collection.ITreeLongIdEntity");
			}
			if (main.getCfg("parentCls") != null) {
				parentCls = main.getCfg("parentCls").toString();
			}

			codeGenHelper.appendln2("");
			codeGenHelper.appendln2("/**");
			codeGenHelper.appendln2(" *");
			codeGenHelper.appendln2(" * %s", main.getComments());
			codeGenHelper.appendln2(" *");
			codeGenHelper.appendln2(" * EntityDefBuilder");
			codeGenHelper.appendln2(" */");
			codeGenHelper.appendln2("@Table(\"%s\")", main.getName());
			codeGenHelper.appendln2("@Comment(\"%s\")", main.getComments());
			codeGenHelper.appendln2("public class %s %s{", main.getEntityCls(), parentCls);
			codeGenHelper.beginIndent();
			codeGenHelper.appendln2("private static final long serialVersionUID = %dL;", main.getCfg("serialVersionUID"));

			//fields
			if (main.getDefs() != null) {
				for (EEntityItemDef item : main.getDefs()) {
					String itemType = (String) item.getCfg("itemType");
					//跳过生成父类的字段
					if (item.getCfg("parentDef") != null || "true".equals(item.getCfg("noGen"))) {
						continue;
					}
					if ("LongId".equals(itemType)) {
						codeGenHelper.appendln2("@Id");
						codeGenHelper.appendln2("@Comment(\"Id\")");
					} else if ("One".equals(itemType)) {
						codeGenHelper.appendln2("@One(field = \"%sId\")", item.getField());
					} else if ("Many".equals(itemType)) {
						codeGenHelper.appendln2("@Many(field = \"%s\")", item.getCfg("relationFrom"));
						codeGenHelper.appendln2("@Comment(\"%s\")", item.getTitle());

						codeGenHelper.appendln2(lstFieldDef(item));
						codeGenHelper.appendln("");
						continue;
					} else if ("Many2Many".equals(itemType)) {
						codeGenHelper.appendln2("@ManyMany(relation = \"%s\", from = \"%s\", to = \"%s\")", item.getCfg("relation"), item.getCfg("relationFrom"), item.getCfg("relationTo"));

						codeGenHelper.appendln2(lstFieldDef(item));
						codeGenHelper.appendln("");
						continue;
					} else {
						String[] allColType = new String[] { "CHAR", "BOOLEAN", "VARCHAR", "TEXT", "BINARY", "TIMESTAMP", "DATETIME", "DATE", "TIME", "FLOAT", "PSQL_JSON", "PSQL_ARRAY", "MYSQL_JSON", "AUTO" };
						if (ArraysUtil.contains(allColType, item.getDataType())) {
							codeGenHelper.appendln2("@Column()");
							codeGenHelper.appendln2("@Comment(\"%s\")", item.getTitle());
							codeGenHelper.appendln2("@ColDefine(type = ColType.%s %s)", item.getDataType(), extColDefine(item));
						} else {
							if (StringUtil.equals("BIGINT", item.getDataType())) {
								codeGenHelper.appendln2("@Column()");
								codeGenHelper.appendln2("@Comment(\"%s\")", item.getTitle());
								codeGenHelper.appendln2("@ColDefine(type = ColType.%s %s)", "INT", extColDefine(item));
							} else {
								codeGenHelper.appendln2("@Column()");
								codeGenHelper.appendln2("@Comment(\"%s\")", item.getTitle());
								codeGenHelper.appendln2("@ColDefine(customType = \"%s\" %s)", item.getDataType(), extColDefine(item));
							}
						}
					}
					codeGenHelper.appendln2(fieldDef(item));
					codeGenHelper.appendln("");
				}
			}
			if (isTree) {
				codeGenHelper.appendln2("private List<%s> items;", main.getEntityCls());
			}

			int codeOffset = codeGenHelper.offset();

			//setter & getter
			if (main.getDefs() != null) {
				codeGenHelper.appendln2("//--------------------setter & getter-----------------------------------");
				for (EEntityItemDef item : main.getDefs()) {
					//跳过生成父类的字段
					if (item.getCfg("parentDef") != null || "true".equals(item.getCfg("noGen"))) {
						continue;
					}
					String itemType = (String) item.getCfg("itemType");
					String ufield = StringUtil.capitalize(item.getField());
					String fieldType = item.getValCls();
					if (fieldType.indexOf(".") > 0) {
						imports.add(fieldType);
						fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
					}
					if ("Boolean".equals(itemType)) {

						String method1 = "get" + ufield;
						String method2 = "set" + ufield;
						if (ufield.startsWith("is")) {
							ufield = ufield.substring(2);
							ufield = StringUtil.capitalize(ufield);
							method1 = "is" + ufield;
							method2 = "set" + ufield.substring(2);
						}
						codeGenHelper.appendln2("public %s %s() {", fieldType, method1);
						codeGenHelper.beginIndent();
						codeGenHelper.appendln2("return %s;", item.getField());
						codeGenHelper.endIndent();
						codeGenHelper.appendln2("}");
						codeGenHelper.appendln("");

						codeGenHelper.appendln2("public void %s(%s %s) {", method2, fieldType, item.getField());
						codeGenHelper.beginIndent();
						codeGenHelper.appendln2("this.%s = %s;", item.getField(), item.getField());
						codeGenHelper.endIndent();
						codeGenHelper.appendln2("}");
						codeGenHelper.appendln("");

					} else if ("Many".equals(itemType) || "Many2Many".equals(itemType)) {
						codeGenHelper.appendln2("public List<%s> get%s() {", fieldType, ufield);
						codeGenHelper.beginIndent();
						codeGenHelper.appendln2("return %s;", item.getField());
						codeGenHelper.endIndent();
						codeGenHelper.appendln2("}");
						codeGenHelper.appendln("");

						codeGenHelper.appendln2("public void set%s(List<%s> %s) {", ufield, fieldType, item.getField());
						codeGenHelper.beginIndent();
						codeGenHelper.appendln2("this.%s = %s;", item.getField(), item.getField());
						codeGenHelper.endIndent();
						codeGenHelper.appendln2("}");
						codeGenHelper.appendln("");
					} else {
						codeGenHelper.appendln2("public %s get%s() {", fieldType, ufield);
						codeGenHelper.beginIndent();
						codeGenHelper.appendln2("return %s;", item.getField());
						codeGenHelper.endIndent();
						codeGenHelper.appendln2("}");
						codeGenHelper.appendln("");

						codeGenHelper.appendln2("public void set%s(%s %s) {", ufield, fieldType, item.getField());
						codeGenHelper.beginIndent();
						codeGenHelper.appendln2("this.%s = %s;", item.getField(), item.getField());
						codeGenHelper.endIndent();
						codeGenHelper.appendln2("}");
						codeGenHelper.appendln("");
					}
				}
			}
			if (isTree) {
				codeGenHelper.appendln2("@Override");
				codeGenHelper.appendln2("public List<%s> getItems() {", main.getEntityCls());
				codeGenHelper.appendln2("\treturn this.items;");
				codeGenHelper.appendln2("}");
				codeGenHelper.appendln2("");

				codeGenHelper.appendln2("@Override");
				codeGenHelper.appendln2("public void setItems(List<%s> items) {", main.getEntityCls());
				codeGenHelper.appendln2("\tthis.items = items;");
				codeGenHelper.appendln2("}");
			}
			codeGenHelper.endIndent();

			String file = String.format("..\\%s\\src\\main\\java\\%s\\%s.java", main.getProject(), StringUtil.replace(main.getPkg(), ".", File.separator), main.getEntityCls());
			LOG.info(file);

			//合并定制化的代码

			JavaCodeMerger javaCodeMerger = JavaCodeMerger.parseFile(file);

			String importCode = javaCodeMerger.getImportCodes();
			codeGenHelper.insert(importOffset, importCode);

			codeOffset += importCode.length();
			codeGenHelper.insert(codeOffset, javaCodeMerger.getJavaCodes(0));

			for (String importcls : imports) {
				codeGenHelper.insert(importOffset, String.format("import %s;%n", importcls));
			}
			codeGenHelper.appendln("}");
			codeGenHelper.writeFile(file);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static String extColDefine(EEntityItemDef item) {
		String colDef = "";
		if (item.getWidth() != null && item.getWidth() > 0) {
			colDef += ", width = " + item.getWidth();
		}
		if (item.getPrecision() != null && item.getPrecision() > 0) {
			colDef += ", precision = " + item.getPrecision();
		}
		if ("true".equals(item.getCfg("notNull"))) {
			colDef += ", notNull = true";
		}
		return colDef;
	}

	private static String fieldDef(EEntityItemDef item) {
		String fieldType = item.getValCls();
		if (fieldType.indexOf(".") > 0) {
			fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
		}
		if (item.getDefaultVal() == null) {
			return String.format("private %s %s;", fieldType, item.getField());
		} else {
			String defaultVal = item.getDefaultVal().toString();
			if (item.getDefaultVal() instanceof Number) {
				if ("Long".equals(item.getValCls()) || "long".equals(item.getValCls())) {
					defaultVal += "L";
				}
				return String.format("private %s %s=%s;", fieldType, item.getField(), defaultVal);
			} else if (item.getDefaultVal() instanceof Character) {
				return String.format("private %s %s='%s';", fieldType, item.getField(), defaultVal);
			} else if (item.getDefaultVal() instanceof Boolean) {
				return String.format("private %s %s=%s;", fieldType, item.getField(), ((Boolean) item.getDefaultVal()).booleanValue() ? "true" : "false");
			} else {
				return String.format("private %s %s=\"%s\";", fieldType, item.getField(), defaultVal);
			}
		}
	}

	private static String lstFieldDef(EEntityItemDef item) {
		String fieldType = item.getValCls();
		if (fieldType.indexOf(".") > 0) {
			fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
		}
		if (item.getDefaultVal() == null) {
			return String.format("private List<%s> %s;", fieldType, item.getField());
		} else {
			String defaultVal = item.getDefaultVal().toString();
			if (item.getDefaultVal() instanceof Number) {
				return String.format("private List<%s> %s=%s;", fieldType, item.getField(), defaultVal);
			} else if (item.getDefaultVal() instanceof Character) {
				return String.format("private List<%s> %s='%s';", fieldType, item.getField(), defaultVal);
			} else if (item.getDefaultVal() instanceof Boolean) {
				return String.format("private List<%s> %s=%s;", fieldType, item.getField(), ((Boolean) item.getDefaultVal()).booleanValue() ? "true" : "false");
			} else {
				return String.format("private List<%s> %s=\"%s\";", fieldType, item.getField(), defaultVal);
			}
		}
	}

	/**
	 * 根据entityDef数据生成对应的AllEntityDefs文件,初始化所有EntityDefBuilder
	 * @param projectCode
	 */
	public void genEntityDefBuilder(String projectCode) {
		try {
			List<EEntityDef> all = entityDefService.query(Cnd.where("1", "=", "1").asc("project, pkg, name"), EntityDefService.ProfileFull);
			Map<String, Set<String>> projectEntityDefBuilder = new LinkedHashMap<>();
			CodeGenHelper codeGen = new CodeGenHelper();
			codeGen.appendln("package com.dataagg;");
			codeGen.appendln("import static org.datasays.util.codegen.EntityDefBuilder.newEntityDef;");
			codeGen.appendln("import org.datasays.util.codegen.EntityDefBuilder;");
			codeGen.appendln("");
			codeGen.appendln("public class AllEntityDefs {");
			codeGen.appendln("//@formatter:off");
			codeGen.beginIndent();
			for (EEntityDef entityDef : all) {
				Map<String, EEntityItemDef> allItems = new Hashtable<>();
				Set<String> lstFields = new HashSet<>();
				for (EEntityItemDef entityItemDef : entityDef.getDefs()) {
					allItems.put(entityItemDef.getField(), entityItemDef);
				}

				Set<String> pEntityDefBuilder = projectEntityDefBuilder.get(entityDef.getProject());
				if (pEntityDefBuilder == null) {
					pEntityDefBuilder = new LinkedHashSet<>();
					projectEntityDefBuilder.put(entityDef.getProject(), pEntityDefBuilder);
				}
				pEntityDefBuilder.add(entityDef.getName());

				codeGen.appendln2("//" + entityDef.getName() + "--" + entityDef.getComments());
				codeGen.appendln2("public static EntityDefBuilder %s = newEntityDef(\"%s\",\"%s\", \"%s\", \"%s\", \"%s\")", entityDef.getName(), entityDef.getProject(), entityDef.getName(), entityDef.getPkg(), entityDef.getEntityCls(), entityDef.getComments());
				codeGen.beginIndent();
				codeGen.append("\t\t.actionUrl(\"%s\")", entityDef.getActionUrl());
				boolean isTree = "true".equals(entityDef.getCfg("isTree"));
				if (isTree) {
					codeGen.append(".isTree()");
				}
				String isMasterDetail = (String) entityDef.getCfg("isMasterDetail");
				if (isMasterDetail != null) {
					codeGen.append(".isMasterDetail(%s)", isMasterDetail);
				}
				codeGen.appendln("");

				for (EEntityItemDef entityItemDef : entityDef.getDefs()) {
					String field = entityItemDef.getField();
					if (lstFields.contains(field)) {
						continue;
					}
					if ("id".equals(field)) {
						codeGen.appendln2(".addLongId()");
					} else if ("cfg".equals(field)) {
						codeGen.appendln2(".addStrObj(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					} else if ("enabled".equals(field) || field.endsWith("Flag")) {
						codeGen.appendln2(".addFlag(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					} else if ("description".equals(field) || "summary".equals(field) || "remarks".equals(field) || "comment".equals(field)) {
						codeGen.appendln2(".addLongText(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					} else if ("weight".equals(field) || "sort".equals(field)) {
						codeGen.appendln2(".addInt(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					} else if (field.endsWith("Price")) {
						codeGen.appendln2(".addMoney(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					} else if ("createBy".equals(field) || "updateBy".equals(field)) {
						codeGen.appendln2(".addLongKey(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					} else if ("createDate".equals(field) || "updateDate".equals(field) || field.endsWith("Date") || field.endsWith("Time")) {
						codeGen.appendln2(".addDate(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					} else if ("parentId".equals(field)) {
						codeGen.appendln2(".addLongKey(\"%s\", \"%s\")", field, entityItemDef.getTitle());
						if (!isTree) {
							LOG.warn(entityDef.getName() + "未设置为isTree!");
						}
					} else if ("parentIds".equals(field)) {
						codeGen.appendln2(".addString(\"%s\", \"%s\")", field, entityItemDef.getTitle());
						if (!isTree) {
							LOG.warn(entityDef.getName() + "未设置为isTree!");
						}
					} else if (field.endsWith("Id") && !"parentId".equals(field) && !"masterId".equals(field)) {
						String oneEntityName = field.substring(0, field.length() - 2);
						EEntityItemDef oneEntity = allItems.get(oneEntityName);
						if (oneEntity != null) {
							StrObj oneEntitycfg = oneEntity.getCfg();
							if ("__One2One__".equals(oneEntitycfg.strVal("relation")) && field.equals(oneEntitycfg.strVal("relationFrom"))) {
								codeGen.appendln2(".addOne(\"%s\", \"%s\", \"%s\")", oneEntityName, oneEntity.getTitle(), oneEntity.getValCls());
								lstFields.add(oneEntityName);
							}
						} else {
							codeGen.appendln2(".addLongKey(\"%s\", \"%s\")", field, entityItemDef.getTitle());
							if (!isTree) {
								LOG.warn(String.format("%s中的%s没有关联对象!", entityDef.getName(), field));
							}
						}
					} else if ("__One2Many__".equals(entityItemDef.getCfg("relation"))) {
						codeGen.appendln2(".addMany(\"%s\", \"%s\", \"%s\", \"%s\")", field, entityItemDef.getCfg("relationFrom"), entityItemDef.getTitle(), entityItemDef.getValCls());
					} else if (entityItemDef.getCfg("relation") != null && !"__One2One__".equals(entityItemDef.getCfg("relation")) && !"__One2Many__".equals(entityItemDef.getCfg("relation"))) {
						codeGen.appendln2(".addMany2Many(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\")", field, entityItemDef.getCfg("relation"), entityItemDef.getCfg("relationFrom"), entityItemDef.getCfg("relationTo"), entityItemDef.getTitle(), entityItemDef.getValCls());
					} else {
						codeGen.appendln2(".addString(\"%s\", \"%s\")", field, entityItemDef.getTitle());
					}
					lstFields.add(field);
				}
				codeGen.appendln2(";");
				codeGen.endIndent();
				codeGen.appendln("");
			}
			codeGen.endIndent();
			codeGen.appendln2("//@formatter:on");

			//projectEntityDefBuilder
			codeGen.beginIndent();
			for (String project : projectEntityDefBuilder.keySet()) {
				codeGen.append("public static EntityDefBuilder[] %s = new EntityDefBuilder[] { ", project);
				Set<String> pEntityDefBuilder = projectEntityDefBuilder.get(project);
				int i = 0;
				for (String entityDefBuilder : pEntityDefBuilder) {
					codeGen.append(" %s%s", entityDefBuilder, (i < pEntityDefBuilder.size() - 1) ? "," : "");
					i++;
				}
				codeGen.appendln2("};");
			}
			codeGen.endIndent();

			codeGen.appendln2("}");
			codeGen.appendln("");
			codeGen.writeFile("..\\" + projectCode + "\\src\\test\\java\\com\\dataagg\\AllEntityDefs.java");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void upJavaCodeMerger(StrObj model, String javaFile, int initSize) {
		JavaCodeMerger javaCodeMerger = JavaCodeMerger.parseFile(javaFile);
		model.put("JavaImports", javaCodeMerger.getImportCodes());
		String[] javaCodes = javaCodeMerger.getAllJavaCodes(initSize);
		if (initSize != javaCodes.length) {
			LOG.warn(javaFile + "中的JavaCodes数量不匹配![期望:" + initSize + ",实际:" + javaCodes.length + "]");
		}
		//DEBUG for genCotroller
		//if (initSize == 3) {
		//if (javaFile.endsWith("Dao.java")) {
		//if (javaFile.endsWith("Service.java")) {
		//if (javaFile.endsWith("ServiceTest.java")) {
		//if (javaFile.endsWith("Controller.java")) {
		//	javaCodes[1] = "";
		//}
		model.put("JavaCodes", javaCodes);
	}

	public static void genAllEntityCodes(List<EntityDefBuilder> allEntityDefs) {
		EntityCodeGen entityCodeGen = new EntityCodeGen();
		entityCodeGen.uiCodeGen = new UICodeGen();

		Map<String, EEntityDef> mapEntityDefs = new Hashtable<>();
		allEntityDefs.forEach(entityDefBuilder -> {
			EEntityDef entityDef = entityDefBuilder.main;
			mapEntityDefs.put(entityDef.getFullCls(), entityDef);
		});
		for (EntityDefBuilder entityDefBuilder : allEntityDefs) {
			EEntityDef entityDef = entityDefBuilder.main;
			//if (entityDef.getEntityCls().startsWith("EEntity") || entityDef.getEntityCls().startsWith("EUI")) {
			//	continue;
			//}
			entityCodeGen.genEntityCls(entityDefBuilder);

			if (entityDef.isTree()) {
				continue;
			}
			//生成class基础的信息
			//			StrObj model = entityCodeGen.preGen(mapEntityDefs, entityDef);
			StrObj model = entityCodeGen.buildModel(mapEntityDefs, entityDef);
			CodeGenUtils.genJson("../codeGen/tmp/" + entityDef.getName() + ".json", model);
			try {
				entityCodeGen.genDao(model, entityDef);
				if (!entityDef.isDetail()) {
					entityCodeGen.genService(model, entityDef);
					entityCodeGen.genTest(model, entityDef);
					entityCodeGen.genController(model, entityDef);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
}
