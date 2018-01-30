package org.dataagg.codegen;

import static org.dataagg.codegen.base.ADefBase.genNameL;
import static org.dataagg.codegen.base.ADefBase.genNameU;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.dataagg.codegen.base.ACodeGenBase;
import org.dataagg.codegen.model.EntityDef;
import org.dataagg.codegen.model.EntityDefBuilder;
import org.dataagg.codegen.model.EntityItemDef;
import org.dataagg.codegen.util.CodeGenUtils;
import org.dataagg.codegen.util.JCodeMerger;
import org.dataagg.codegen.util.JCoder;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.util.ArraysUtil;
import jodd.util.StringUtil;

public class EntityCodeGen extends ACodeGenBase<EntityDef> {
	private static final Logger LOG = LoggerFactory.getLogger(EntityCodeGen.class);
	private Map<String, EntityDef> mapEntityDefs = null;
	private StrObj model;

	public EntityCodeGen() {
		super();
	}

	private StrObj buildModel(EntityDef entityDef) {
		StrObj model = entityDef.buildModel();
		String entityPkg = entityDef.getPkg();
		String entityCls = entityDef.getEntityCls();

		//relations
		List<StrObj> relations = new ArrayList<>();
		if (entityDef.relations != null && !entityDef.relations.isEmpty()) {
			relations = entityDef.relations;
			String valCls = "";
			EntityDef tmpEntityDef = null;
			EntityItemDef entityItemDef = null;
			for (StrObj obj : relations) {
				entityItemDef = (EntityItemDef) obj.get("entityItemDef");
				valCls = entityItemDef.getValCls();
				entityPkg = StringUtil.substring(valCls, 0, StringUtil.lastIndexOfIgnoreCase(valCls, "."));
				entityCls = StringUtil.replace(valCls, entityPkg + ".", "");
				tmpEntityDef = mapEntityDefs.get(entityPkg + "." + entityCls);
				if (tmpEntityDef == null) {
					LOG.error("不能找到" + entityPkg + "." + entityCls + "的定义配置!");
					continue;
				}
				obj.putAll(buildModel(tmpEntityDef));
				obj.put("valCls", valCls);
				obj.put("itemCls", tmpEntityDef.getEntityCls());
				obj.put("itemNameU", obj.strVal("nameU"));
				obj.put("itemNameL", genNameL(obj.strVal("nameU")));
				obj.put("field", entityItemDef.getField());
				obj.put("fieldU", genNameU(entityItemDef.getField()));
				obj.put("relationTable", entityItemDef.getCfg("relation") != null ? entityItemDef.getCfg("relation") : "");
				String relationFrom = entityItemDef.getCfg("relationFrom") != null ? entityItemDef.getCfg("relationFrom").toString() : "";
				obj.put("relationFrom", relationFrom);
				obj.put("relationFromU", genNameU(relationFrom));
				obj.put("relationTo", entityItemDef.getCfg("relationTo") != null ? entityItemDef.getCfg("relationTo") : "");
			}

			//移除重复关联
			Set<String> usedRelation = new HashSet<>();
			relations = relations.stream().filter(relation -> {
				EntityItemDef entityItemDef1 = (EntityItemDef) relation.get("entityItemDef");
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
	 * 根据EntityDef生成对应Entity的Dao对象
	 *
	 * @param genVo
	 * @param entityDef
	 */
	public void genDao() throws Exception {
		String javaFile = def.javaFile(def.getRootPkg() + ".dao", def.getSimpleName() + "Dao");
		genByTpl(model, "tpls/Dao.ftl", javaFile, 2);
	}

	/**
	 * 根据EntityDef生成对应Entity的Service对象
	 *
	 * @param genVo
	 * @param entityDef
	 */
	public void genService() throws Exception {
		String javaFile = def.javaFile(def.getRootPkg() + ".service", def.getSimpleName() + "Service");
		genByTpl(model, "tpls/Service.ftl", javaFile, 2);
	}

	/**
	 * 根据EntityDef生成对应Entity的测试用例
	 *
	 * @param srcDir
	 * @param pkg
	 */
	public void genServiceTest() throws Exception {
		String javaFile = def.testJavaFile(def.getRootPkg() + ".service", def.getSimpleName() + "ServiceTest");
		genByTpl(model, "tpls/ServiceTest.ftl", javaFile, 5);
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
	 * 根据EntityDefBuilder生成对应的实体类,包含NutDao相关注解,并保留定制的代码
	 * @param entityDefBuilder
	 */
	public void genEntityCls() {
		try {
			String javaFile = def.javaFile(def.getPkg(), def.getEntityCls());
			LOG.info(javaFile);

			JCoder coder = new JCoder(javaFile, mergeCode);
			coder.appendln2("package %s;", def.getPkg());
			coder.appendln2("import java.util.*;");
			coder.appendln2("import com.google.common.collect.Lists;");

			coder.appendln2("import org.nutz.dao.entity.annotation.*;");
			coder.appendln2("import com.dataagg.commons.base.*;");

			coder.markImportCodeOffset();

			coder.appendln2("");
			coder.append(JCoder.longComment(def.getComments(), null));
			//NutzDao的实体注解
			coder.appendln2("@Table(\"%s\")", def.getName());
			coder.appendln2("@Comment(\"%s\")", def.getComments());

			//类定义的implements和extends
			String parentCls = "implements ILongIdEntity";
			if (def.getCfg("parentCls") != null) {
				parentCls = def.getCfg("parentCls").toString();
			} else {
				parentCls = "implements ILongIdEntity";
				if (def.isTree()) {
					parentCls = "implements ITreeLongIdEntity<" + def.getEntityCls() + ">";
				}
			}
			coder.appendln2(JCoder.publicClsDef(def.getEntityCls(), parentCls));
			coder.beginIndent();
			coder.appendln2(JCoder.serialVersionUID((Long) def.getCfg("serialVersionUID")));

			//字段定义部分
			if (def.getDefs() != null) {
				for (EntityItemDef item : def.getDefs()) {
					String itemType = (String) item.getCfg("itemType");
					//跳过生成父类的字段
					if (item.getCfg("parentDef") != null || "true".equals(item.getCfg("noGen"))) {
						continue;
					}
					String fieldType = item.getValCls();
					if (fieldType.indexOf(".") > 0) {
						if (!fieldType.startsWith("java.util.")) {
							coder.addImportCls(fieldType);
						}
						fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
					}
					if ("LongId".equals(itemType)) {
						coder.appendln2("@Id");
						coder.appendln2("@Comment(\"%s\")", item.getTitle());
						coder.appendln2(JCoder.fieldDef(fieldType, item.getField(), item.getDefaultVal()));
					} else if ("One".equals(itemType)) {
						coder.appendln2("@One(field = \"%sId\")", item.getField());
						coder.appendln2(JCoder.fieldDef(fieldType, item.getField(), item.getDefaultVal()));
					} else if ("Many".equals(itemType)) {
						coder.appendln2("@Many(field = \"%s\")", item.getCfg("relationFrom"));
						coder.appendln2("@Comment(\"%s\")", item.getTitle());
						coder.appendln2(JCoder.fieldDef("List<" + fieldType + ">", item.getField(), item.getDefaultVal()));
						coder.appendln("");
						continue;
					} else if ("Many2Many".equals(itemType)) {
						coder.appendln2("@ManyMany(relation = \"%s\", from = \"%s\", to = \"%s\")", item.getCfg("relation"), item.getCfg("relationFrom"), item.getCfg("relationTo"));
						coder.appendln2(JCoder.fieldDef("List<" + fieldType + ">", item.getField(), item.getDefaultVal()));
						coder.appendln("");
						continue;
					} else {
						//ColDefine的附加信息
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
						coder.appendln2("@Column()");
						coder.appendln2("@Comment(\"%s\")", item.getTitle());
						String[] allColType = new String[] { "CHAR", "BOOLEAN", "VARCHAR", "TEXT", "BINARY", "TIMESTAMP", "DATETIME", "DATE", "TIME", "FLOAT", "PSQL_JSON", "PSQL_ARRAY", "MYSQL_JSON", "AUTO" };
						if (ArraysUtil.contains(allColType, item.getDataType())) {
							coder.appendln2("@ColDefine(type = ColType.%s %s)", item.getDataType(), colDef);
						} else {
							if (StringUtil.equals("BIGINT", item.getDataType())) {
								coder.appendln2("@ColDefine(type = ColType.%s %s)", "INT", colDef);
							} else {
								coder.appendln2("@ColDefine(customType = \"%s\" %s)", item.getDataType(), colDef);
							}
						}
						coder.appendln2(JCoder.fieldDef(fieldType, item.getField(), item.getDefaultVal()));
					}
					coder.appendln("");
				}
			}

			//定制部分代码
			coder.insertMergedCodes("_CustomFields");

			//setter & getter
			if (def.getDefs() != null) {
				coder.appendln2("//--------------------setter & getter-----------------------------------");
				for (EntityItemDef item : def.getDefs()) {
					//跳过生成父类的字段
					if (item.getCfg("parentDef") != null || "true".equals(item.getCfg("noGen"))) {
						continue;
					}
					String itemType = (String) item.getCfg("itemType");
					String fieldType = item.getValCls();
					if (fieldType.indexOf(".") > 0) {
						fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
					}
					if ("Many".equals(itemType) || "Many2Many".equals(itemType)) {
						coder.appendFieldGSetter("List<" + fieldType + ">", item.getField());
					} else {
						coder.appendFieldGSetter(fieldType, item.getField());
					}
				}
			}
			coder.endIndent();
			coder.appendln("}");

			//插入ImportCodes
			coder.insertImportCodes();
			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void genAllCode() {
		try {
			if (def.isTree()) { return; }
			genEntityCls();
			model = buildModel(def);
			genDebugModelJson(model.strVal("nameL"), model);
			//genEntity();
			genDao();
			if (!def.isDetail()) {
				genService();
				genServiceTest();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void genByTpl(StrObj model, String template, String outFile, int javaCodeSize) {
		outFile = StringUtil.replace(outFile, "\\", File.separator);
		outFile = StringUtil.replace(outFile, "/", File.separator);
		template = StringUtil.replace(template, "\\", "/");
		if (mergeCode) {
			upJavaCodeMerger(model, outFile, javaCodeSize);
		}
		CodeGenUtils.genFtlCode(template, model, outFile);
	}

	private void upJavaCodeMerger(StrObj model, String javaFile, int javaCodeSize) {
		JCodeMerger javaCodeMerger = new JCodeMerger(javaFile);
		javaCodeMerger.setMergeCode(mergeCode);
		model.put("JavaImports", javaCodeMerger.getImportCodes());
		Map<String, String> javaCodes = javaCodeMerger.getAllCodes();
		if (javaCodeSize != javaCodes.size()) {
			LOG.warn(javaFile + "中的JavaCodes数量不匹配![期望:" + javaCodeSize + ",实际:" + javaCodes.size() + "]");
		}
		model.put("JavaCodes", javaCodes);
	}

	public void addAllDef(EntityDefBuilder[] allEntityDefs) {
		if (mapEntityDefs == null) {
			mapEntityDefs = new Hashtable<>();
		}
		if (allEntityDefs != null) {
			for (EntityDefBuilder entityDefBuilder : allEntityDefs) {
				EntityDef entityDef = entityDefBuilder.main;
				mapEntityDefs.put(entityDef.getFullCls(), entityDef);
			}
		}
	}
}
