package org.dataagg.codegen;

import jodd.util.ArraysUtil;
import jodd.util.StringUtil;
import org.dataagg.codegen.base.ACodeGenBase;
import org.dataagg.codegen.model.EntityDef;
import org.dataagg.codegen.model.EntityDefBuilder;
import org.dataagg.codegen.model.EntityItemDef;
import org.dataagg.codegen.util.JCoder;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityCodeGen extends ACodeGenBase<EntityDef> {
	private static final Logger LOG = LoggerFactory.getLogger(EntityCodeGen.class);
	private Map<String, EntityDef> mapEntityDefs = null;
	private JCoder coder = null;

	public EntityCodeGen(String baseDir, boolean mergeCode) {
		super(baseDir, mergeCode);
	}

	protected void appendCode(String line) {
		coder.appendln2(mapTplHelper.parse(line));
	}

	/**
	 * 根据EntityDef生成对应Entity的Dao对象
	 */
	public void genDao() throws Exception {
		try {
			String javaFile = baseDir + def.javaFile(def.getRootPkg() + ".dao", def.getSimpleName() + "Dao");
			LOG.info(javaFile);

			coder = new JCoder(javaFile, mergeCode);
			coder.appendln2("package %s.dao;", def.getRootPkg());
			coder.appendln2("import static com.dataagg.util.CndUtils.*;");
			coder.appendln2("import org.slf4j.*;");
			coder.appendln2("import java.util.*;");
			coder.appendln2("import jodd.util.*;");
			coder.appendln2("import com.dataagg.util.*;");
			coder.appendln2("import org.dataagg.util.collection.*;");
			coder.appendln2("import org.dataagg.util.lang.*;");
			coder.appendln2("import com.dataagg.commons.base.*;");
			coder.appendln2("import com.dataagg.commons.domain.*;");
			coder.appendln2("");
			coder.appendln2("import javax.sql.DataSource;");
			coder.appendln2("");
			coder.appendln2("import org.nutz.dao.*;");
			coder.appendln2("import org.nutz.dao.util.cri.*;");
			coder.appendln2("import org.nutz.dao.pager.Pager;");
			coder.appendln2("import org.nutz.dao.impl.NutDao;");
			coder.appendln2("import org.springframework.beans.factory.annotation.Autowired;");
			coder.appendln2("import org.springframework.cache.annotation.Cacheable;");
			coder.appendln2("import org.springframework.stereotype.Component;");
			coder.appendln2("");
			List<StrObj> relations = def.relations;
			if (relations != null && !relations.isEmpty()) {
				for (StrObj relation : relations) {
					EntityDef relationEntityDef = mapEntityDefs.get(relation.strVal("relationEntity"));
					coder.appendln2("import %s.dao.%sDao;", relationEntityDef.getRootPkg(), EntityDef.genName(relationEntityDef.entityCls));
					coder.appendln2("import %s.%s;", relationEntityDef.pkg, relationEntityDef.entityCls);
				}
			}
			coder.appendln2("import %s.%s;", def.pkg, def.entityCls);
			coder.appendln2("");
			coder.markImportCodeOffset();

			coder.appendln2("");
			coder.append(JCoder.longComment(def.comments, null));
			coder.appendln2("@Component");
			coder.appendln2("public class %sDao extends %s<%s> {", EntityDef.genNameU(def.entityCls), ("String".equals(def.getCfg("pkType"))
					? "AStringPKEntityDao"
					: "ALongPKEntityDao"), def.entityCls);
			coder.beginIndent();

			String subParams = "";
			String subDefs = "";
			String pkType = def.cfg.strVal("pkType");
			if (relations != null && !relations.isEmpty()) {
				for (StrObj relation : relations) {
					EntityItemDef relationField = def.find(relation.strVal("relationField"));
					EntityDef relationEntity = mapEntityDefs.get(relation.strVal("relationEntity"));
					String fieldU = ufield(relationField.field);
					String relationNameU = EntityDef.genNameU(relationEntity.entityCls);
					String relationNameL = EntityDef.genNameL(relationEntity.entityCls);

					if ("__One2Many__".equals(relation.strVal("relationType"))) {
						coder.appendln2("public One2ManyDaoHelper<%s, %s, %s> %sHelper;", def.entityCls, relationEntity.entityCls, pkType, relationField.field);
						subParams = subParams + ", @Autowired " + relationNameU + "Dao " + relationNameL + "Dao";
						subDefs += relationField.field + "Helper = new One2ManyDaoHelper<" + def.entityCls + ", " + relationEntity.entityCls + ", " + pkType + ">(this, " + relationNameL + "Dao, \"" + relation.strVal("relationFrom") + "\") {\n";
						subDefs += "@Override \n";
						subDefs += "public void upMasterInfo(" + relationEntity.entityCls + " item, " + pkType + " id) {\n";
						subDefs += "	item.set" + ufield(relation.strVal("relationFrom")) + "(id);\n";
						subDefs += "}\n";
						subDefs += "\n";
						subDefs += "@Override \n";
						subDefs += "public void setSubItems(" + def.entityCls + " entity, List<" + relationEntity.entityCls + "> allItems) {\n";
						subDefs += "	entity.set" + fieldU + "(allItems);\n";
						subDefs += "};\n";
						subDefs += "};\n";
					} else if ("__Many2Many__".equals(relation.strVal("relationType"))) {
						coder.appendln2("public Many2ManyDaoHelper<" + def.entityCls + ", " + relationEntity.entityCls + "> " + relation.strVal("relationField") + "Helper;");
						subParams = subParams + ", @Autowired " + relationNameU + "Dao " + relationNameL + "Dao";
						subDefs += "" + relationField.field + "Helper = new Many2ManyDaoHelper<" + def.entityCls + ", " + relationEntity.entityCls + ">(this, " + relationNameL + "Dao, \"" + relationField.field + "\", \"" + def.name + "\", \"" + relation.mapVal("entityDef").strVal("name") + "\", \"" + relation.strVal("relationFrom") + "\", \"" + relation.strVal("relationTo") + "\", \"" + relation.strVal("relationTable") + "\") {\n";
						subDefs += "	@Override \n";
						subDefs += "	public void setSubItems(" + def.entityCls + " entity, List<" + relationEntity.entityCls + "> allItems) {\n";
						subDefs += "		entity.set" + fieldU + "(allItems);\n";
						subDefs += "	};\n";
						subDefs += "};\n";
					}
				}
			}

			if (def.isTree()) {
				coder.appendln2("public TreeDaoHelper<" + def.entityCls + ", " + def.getCfg("pkType") + "> treeHelper;");
				String otherFileds = "";
				String tmpcodes = "";
				String pkField = def.getCfg("pkField") != null ? def.getCfg("pkField").toString() : "";
				String parentField = def.getCfg("parentField") != null ? def.getCfg("parentField").toString() : "";
				for (EntityItemDef d : def.getDefs()) {
					String dbField = d.getCfg("dbField") != null ? d.getCfg("dbField").toString() : "";
					if (!d.field.equalsIgnoreCase(pkField) && !d.field.equalsIgnoreCase(parentField) && !dbField.equalsIgnoreCase(pkField) && !dbField.equalsIgnoreCase(parentField) && d.getCfg("relation") == null) {
						otherFileds = otherFileds + ", \"" + d.field + "\"";
					}
				}
				tmpcodes = "treeHelper = new TreeDaoHelper<>(this, " + def.getCfg("pkType") + ".class, \"" + def.name + "\", \"" + def.getCfg("pkField") + "\", \"" + def.getCfg("parentField") + "\"" + otherFileds + ");";
				subDefs = subDefs + tmpcodes;
			}

			//定制部分代码
			coder.insertMergedCodes("_CustomFields");

			coder.appendln2("@Autowired");
			coder.appendln2("public " + EntityDef.genNameU(def.entityCls) + "Dao(@Autowired NutDao nutDao" + subParams + ") {");
			coder.appendln2("	super(nutDao);");
			coder.appendln2("	" + subDefs);
			coder.appendln2("}");

			if (def.isTree()) {
				coder.appendln2("/**");
				coder.appendln2("    * 根据指定节点查询所有关联节点,并重新构建树结构,可包含多个父节点");
				coder.appendln2("    */");
				coder.appendln2("   public List<" + def.entityCls + "> queryRelations1(" + def.getCfg("pkType") + " id, int mode, String queryCnd, StrObj queryParams) {");
				coder.appendln2("      return treeHelper.queryRelations1(id, mode, queryCnd, queryParams);");
				coder.appendln2("   }");
				coder.appendln2("");
				coder.appendln2("   /**");
				coder.appendln2("    * 根据指定节点查询所有关联节点,并重新构建树结构,只包含一个父节点,其余部分删除");
				coder.appendln2("    */");
				coder.appendln2("   public " + def.entityCls + " queryRelations2(" + def.getCfg("pkType") + " id, int mode, String queryCnd, StrObj queryParams) {");
				coder.appendln2("      return treeHelper.queryRelations2(id, mode, queryCnd, queryParams);");
				coder.appendln2("}");
			}

			//定制部分代码
			coder.insertMergedCodes("_CustomMethods");

			coder.endIndent();
			coder.appendln("}");

			//插入ImportCodes
			coder.insertImportCodes();
			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 根据EntityDef生成对应Entity的Service对象
	 */
	@SuppressWarnings("unchecked")
	public void genService() throws Exception {
		try {
			String javaFile = baseDir + def.javaFile(def.getRootPkg() + ".service", def.getSimpleName() + "Service");
			LOG.info(javaFile);

			coder = new JCoder(javaFile, mergeCode);
			String nameU = EntityDef.genNameU(def.entityCls);
			//String nameL = EntityDef.genNameL(def.entityCls);

			appendCode("package ${rootPkg}.service;");
			appendCode("");
			appendCode("import static com.dataagg.util.CndUtils.*;");
			appendCode("import org.slf4j.*;");
			appendCode("import java.util.*;");
			appendCode("import jodd.util.*;");
			appendCode("import com.dataagg.util.*;");
			appendCode("import org.dataagg.util.collection.*;");
			appendCode("import org.dataagg.util.lang.*;");
			appendCode("import com.dataagg.commons.base.*;");
			appendCode("");
			appendCode("import org.nutz.dao.*;");
			appendCode("import org.nutz.dao.util.cri.*;");
			appendCode("import org.nutz.dao.pager.Pager;");
			appendCode("import org.nutz.dao.impl.NutDao;");
			appendCode("import org.springframework.beans.factory.annotation.Autowired;");
			appendCode("import org.springframework.cache.annotation.Cacheable;");
			appendCode("import org.springframework.stereotype.Service;");
			appendCode("");
			appendCode("import " + def.getRootPkg() + ".dao." + nameU + "Dao;");
			appendCode("import " + def.pkg + "." + def.entityCls + ";");
			appendCode("");
			String allItemFetchItems = "";
			String allItemSaveItems = "";
			String allItemDef = "";
			String pkType = def.cfg.strVal("pkType");
			List<StrObj> relations = (List<StrObj>) mapTplHelper.val("relations");
			for (StrObj relation : relations) {
				EntityItemDef relationField = def.find(relation.strVal("relationField"));
				EntityDef relationEntity = mapEntityDefs.get(relation.strVal("relationEntity"));
				String fieldU = ufield(relationField.field);
				String relationNameU = EntityDef.genNameU(relationEntity.entityCls);
				//String relationNameL = EntityDef.genNameL(relationEntity.entityCls);
				if ("__One2Many__".equals(relation.strVal("relationType"))) {
					String daoHelper = relationField.field + "Helper";
					allItemFetchItems = allItemFetchItems + "dao." + daoHelper + ".fetchItems(entity);\n";
					allItemSaveItems = allItemSaveItems + "dao." + daoHelper + ".saveItems(newEntity, " + relationField.field + ");";
					allItemDef = allItemDef + "List<" + relationEntity.entityCls + "> " + relationField.field + " = entity.get" + fieldU + "();";
					appendCode("import " + relationEntity.getRootPkg() + ".dao." + relationNameU + "Dao;");
					appendCode("import " + relationEntity.pkg + "." + relationEntity.entityCls + ";");
				} else if ("__Many2Many__".equals(relation.strVal("relationType"))) {
					String daoHelper = relationField.field + "Helper";
					allItemFetchItems = allItemFetchItems + "dao." + daoHelper + ".fetchItems(entity);\n";
					allItemSaveItems = allItemSaveItems + "dao." + daoHelper + ".saveItems(newEntity, " + relationField.field + ");";
					allItemDef = allItemDef + "List<" + relationEntity.entityCls + "> " + relationField.field + " = entity.get" + fieldU + "();";
					appendCode("import " + relationEntity.getRootPkg() + ".dao." + relationNameU + "Dao;");
					appendCode("import " + relationEntity.pkg + "." + relationEntity.entityCls + ";");
				}
			}
			appendCode("");
			coder.markImportCodeOffset();
			appendCode("");
			appendCode("/**");
			appendCode(" * " + def.comments);
			appendCode(" *");
			appendCode(" * DataAgg");
			appendCode(" *");
			appendCode(" */");
			appendCode("@Service");
			String serviceInterface = def.cfg.strVal("serviceInterface");
			if (StringUtil.isNotBlank(serviceInterface)) {
				serviceInterface = "implements " + serviceInterface;
			} else {
				serviceInterface = "";
			}
			appendCode("public class " + nameU + "Service extends AEntityServiceBase<" + nameU + "Dao, " + def.entityCls + ", " + pkType + "> " + serviceInterface + "{");
			appendCode("	@Autowired");
			appendCode("	public " + nameU + "Service(" + nameU + "Dao dao) {");
			appendCode("		super(dao);");
			appendCode("	}");
			appendCode("");
			//定制部分代码
			coder.insertMergedCodes("_CustomFields");
			appendCode("");
			//定制部分代码
			coder.startMergedCodes("_CustomMethods");
			appendCode("	@Override");
			appendCode("	public void fetchItems(" + def.entityCls + " entity, String profile) {");
			appendCode("		if (ProfileFull.equalsIgnoreCase(profile)) {");
			appendCode("			" + allItemFetchItems);
			appendCode("		} else if (ProfileList.equalsIgnoreCase(profile)) {");
			appendCode("");
			appendCode("		} else if (ProfileView.equalsIgnoreCase(profile)) {");
			appendCode("			" + allItemFetchItems);
			appendCode("		} else if (ProfileEdit.equalsIgnoreCase(profile)) {");
			appendCode("			" + allItemFetchItems);
			appendCode("		}");
			appendCode("	}");
			appendCode("");
			appendCode("	public " + def.entityCls + " saveAll(" + def.entityCls + " entity) throws Exception {");
			appendCode("		" + allItemDef);
			appendCode("		" + def.entityCls + " newEntity = super.save(entity);");
			appendCode("		" + allItemSaveItems);
			appendCode("		return newEntity;");
			appendCode("	}");
			appendCode("");
			if (def.isTree()) {
				coder.appendln2("	public List<%s> queryRelations1(%s id, int mode, String queryCnd, StrObj queryParams) {", def.entityCls, pkType);
				appendCode("		return dao.treeHelper.queryRelations1(id, mode, queryCnd, queryParams);");
				appendCode("	}");
				appendCode("");
				coder.appendln2("	public %s queryRelations2(%s id, int mode, String queryCnd, StrObj queryParams) {", def.entityCls, pkType);
				appendCode("		return dao.treeHelper.queryRelations2(id, mode, queryCnd, queryParams);");
				appendCode("	}");
			}
			coder.endMergedCodes("_CustomMethods");

			coder.endIndent();
			coder.appendln("}");

			//插入ImportCodes
			coder.insertImportCodes();
			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 根据EntityDef生成对应Entity的测试用例
	 */
	public void genServiceTest() throws Exception {
		try {
			String javaFile = baseDir + def.testJavaFile(def.getRootPkg() + ".service", def.getSimpleName() + "ServiceTest");
			LOG.info(javaFile);

			coder = new JCoder(javaFile, mergeCode);
			String nameU = EntityDef.genNameU(def.entityCls);
			String nameL = EntityDef.genNameL(def.entityCls);

			String applictionCls = def.project;
			applictionCls = applictionCls.substring(0, 1).toUpperCase() + applictionCls.substring(1) + "Application";

			appendCode("package ${rootPkg}.service;");
			appendCode("");
			appendCode("import static org.junit.Assert.*;");
			appendCode("");
			appendCode("import java.util.*;");
			appendCode("");
			appendCode("import org.junit.*;");
			appendCode("import org.junit.runner.RunWith;");
			appendCode("import org.slf4j.*;");
			appendCode("import org.springframework.beans.factory.annotation.Autowired;");
			appendCode("import org.springframework.boot.test.context.SpringBootTest;");
			appendCode("import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;");
			appendCode("");
			appendCode("import ${applictionPkg}." + applictionCls + ";");
			appendCode("import " + def.pkg + "." + def.entityCls + ";");
			coder.markImportCodeOffset();
			appendCode("");
			appendCode("/**");
			appendCode(" * " + def.comments);
			appendCode(" *");
			appendCode(" * DataAgg");
			appendCode(" *");
			appendCode(" */");
			appendCode("@RunWith(SpringJUnit4ClassRunner.class)");
			appendCode("@SpringBootTest(classes = " + applictionCls + ".class)");
			appendCode("public class " + nameU + "ServiceTest {");
			appendCode("	private static final Logger LOG = LoggerFactory.getLogger(" + nameU + "ServiceTest.class);");
			appendCode("");
			appendCode("	@Autowired");
			appendCode("	public " + nameU + "Service " + nameL + "Service;");
			appendCode("");
			//定制部分代码
			coder.insertMergedCodes("_CustomFields");
			appendCode("");
			appendCode("	@Before");
			appendCode("	public void setUp() throws Exception {");
			coder.insertMergedCodes("_CustomsetUp");
			appendCode("	}");
			appendCode("");
			appendCode("	@After");
			appendCode("	public void tearDown() throws Exception {");
			coder.insertMergedCodes("_CustomtearDown");
			appendCode("	}");
			appendCode("");
			appendCode("	@Test");
			appendCode("	public void test() {");
			appendCode("		try {");
			coder.startMergedCodes("test");
			appendCode("			List<" + def.entityCls + "> " + nameL + "List = " + nameL + "Service.query(null);");
			appendCode("			LOG.warn(\"数据条数：\" + " + nameL + "List.size());");
			coder.endMergedCodes("test");
			appendCode("		} catch (Exception e) {");
			appendCode("			LOG.error(e.getMessage(), e);");
			appendCode("			fail(e.getMessage());");
			appendCode("		}");
			appendCode("	}");
			appendCode("");
			//定制部分代码
			coder.insertMergedCodes("_CustomMethods");

			coder.endIndent();
			coder.appendln("}");

			//插入ImportCodes
			coder.insertImportCodes();
			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 根据EntityDef生成对应的Entity对象
	 *
	 * @param driver  数据库类型,暂时只支持mysql
	 * @param ddlFile 生成文件路径
	 */
	public void genDDL(String driver, String ddlFile) {

	}

	/**
	 * 根据EntityDefBuilder生成对应的实体类,包含NutDao相关注解,并保留定制的代码
	 */
	public void genEntityCls() {
		try {
			String javaFile = baseDir + def.javaFile(def.pkg, def.entityCls);
			LOG.info(javaFile);

			coder = new JCoder(javaFile, mergeCode);
			coder.appendln2("package %s;", def.pkg);
			coder.appendln2("import java.util.*;");
			coder.appendln2("import com.google.common.collect.Lists;");

			coder.appendln2("import org.nutz.dao.entity.annotation.*;");
			coder.appendln2("import com.dataagg.commons.base.*;");
			coder.appendln2("import org.dataagg.util.collection.*;");

			coder.markImportCodeOffset();

			coder.appendln2("");
			coder.append(JCoder.longComment(def.comments, null));
			//NutzDao的实体注解
			coder.appendln2("@Table(\"%s\")", def.name);
			coder.appendln2("@Comment(\"%s\")", def.comments);

			//类定义的implements和extends
			String parentCls = "implements IEntity<Long>";
			String pkType = def.cfg.strVal("pkType");
			if (def.getCfg("parentCls") != null) {
				parentCls = def.cfg.strVal("parentCls");
			} else if (def.isTree()) {
				parentCls = "implements IEntity<" + pkType + ">, ITreeNode<" + def.entityCls + ", " + pkType + ">";
			} else if ("String".equalsIgnoreCase(pkType)) {
				parentCls = "implements IEntity<String>";
			} else if ("Long".equalsIgnoreCase(pkType)) {
				parentCls = "implements IEntity<Long>";
			} else {
				LOG.error("没有主键类型！" + def.entityCls);
			}
			coder.appendln2(JCoder.publicClsDef(def.entityCls, parentCls));
			coder.beginIndent();
			coder.appendln2(JCoder.serialVersionUID(def.cfg.longVal("serialVersionUID")));

			//字段定义部分
			if (def.getDefs() != null) {
				for (EntityItemDef item : def.getDefs()) {
					StrObj itemCfg = item.cfg;
					String itemType = itemCfg.strVal("itemType");
					//跳过生成父类的字段
					if (item.getCfg("parentDef") != null || itemCfg.boolVal("noGen", false)) {
						continue;
					}
					String fieldType = item.valCls;
					if (fieldType.indexOf(".") > 0) {
						if (!fieldType.startsWith("java.util.")) {
							coder.addImportCls(fieldType);
						}
						fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
					}
					if ("LongId".equals(itemType)) {
						coder.appendln2("@Id");
						coder.appendln2("@Comment(\"%s\")", item.title);
						coder.appendln2(coder.fieldDef(fieldType, item.field, item.defaultVal));
					} else if ("StringId".equals(itemType)) {
						coder.appendln2("@Name(casesensitive = false)");
						coder.appendln2("@Comment(\"%s\")", item.title);
						if (item.getWidth() != null && (item.getWidth().intValue() == 32 || item.getWidth().intValue() == 64)) {
							coder.appendln2("@Prev(els = @EL(\"uuid(" + item.getWidth().intValue() + ")\"))");
						} else if (item.getWidth() != null && item.getWidth().intValue() == 16) {
							coder.appendln2("@Prev(els = @EL(\"uuid()\"))");
						}
						coder.appendln2("@ColDefine(type = ColType.VARCHAR , width = " + item.getWidth() + ")");
						coder.appendln2(coder.fieldDef(fieldType, item.field, item.defaultVal));
					} else if ("One".equals(itemType)) {
						coder.appendln2("@One(field = \"%s\")", item.getCfg("relationFromField"));
						coder.appendln2(coder.fieldDef(fieldType, item.field, item.defaultVal));
					} else if ("Many".equals(itemType)) {
						coder.appendln2("@Many(field = \"%s\")", item.getCfg("relationFrom"));
						coder.appendln2("@Comment(\"%s\")", item.title);
						coder.appendln2(coder.fieldDef("List<" + fieldType + ">", item.field, item.defaultVal));
						coder.appendln("");
						continue;
					} else if ("Many2Many".equals(itemType)) {
						coder.appendln2("@ManyMany(relation = \"%s\", from = \"%s\", to = \"%s\")", item.getCfg("relation"), item.getCfg("relationFrom"), item.getCfg("relationTo"));
						coder.appendln2(coder.fieldDef("List<" + fieldType + ">", item.field, item.defaultVal));
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

						if (itemCfg.strVal("dbField") != null && !item.field.equalsIgnoreCase(itemCfg.strVal("dbField"))) {
							coder.appendln2("@Column(\"%s\")", itemCfg.strVal("dbField"));
						} else {
							coder.appendln2("@Column()");
						}
						coder.appendln2("@Comment(\"%s\")", item.title);
						String[] allColType = new String[] { "CHAR", "BOOLEAN", "VARCHAR", "TEXT", "BINARY", "TIMESTAMP", "TIME", "DATE", "TIME", "FLOAT", "PSQL_JSON", "PSQL_ARRAY", "MYSQL_JSON", "AUTO" };
						if (ArraysUtil.contains(allColType, item.getDataType())) {
							coder.appendln2("@ColDefine(type = ColType.%s %s)", item.getDataType(), colDef);
						} else {
							if (StringUtil.equals("BIGINT", item.getDataType())) {
								coder.appendln2("@ColDefine(customType = \"%s\" %s)", "BIGINT", colDef);
							} else {
								coder.appendln2("@ColDefine(customType = \"%s\" %s)", item.getDataType(), colDef);
							}
						}
						coder.appendln2(coder.fieldDef(fieldType, item.field, item.defaultVal));
					}
					coder.appendln("");
				}
				if (def.isTree()) {
					coder.appendln("");
					coder.appendln2("private List<%s> items;", def.entityCls);
				}
			}

			//定制部分代码
			coder.insertMergedCodes("_CustomFields");

			if (!"id".equalsIgnoreCase(def.pkField())) {
				String ufield = ufield(def.pkField());
				coder.appendln2("@Override");
				coder.appendln2("public %s getId() {", pkType);
				coder.appendln2("\treturn get%s();", ufield);
				coder.appendln2("}");
				coder.appendln2("");
				coder.appendln2("@Override");
				coder.appendln2("public void setId(%s id) {", pkType);
				coder.appendln2("\tset%s(id);", ufield);
				coder.appendln2("}");
				coder.appendln2("");
			}

			if (def.isTree()) {
				String parentField = def.cfg.strVal("parentField");
				String ufield = ufield(parentField);
				if (!"ParentId".equals(ufield)) {
					coder.appendln2("@Override");
					coder.appendln2("public %s getParentId() {", pkType);
					coder.appendln2("\treturn get%s();", ufield);
					coder.appendln2("}");
					coder.appendln2("");
					coder.appendln2("@Override");
					coder.appendln2("public void setParentId(%s parentId) {", pkType);
					coder.appendln2("\tset%s(parentId);", ufield);
					coder.appendln2("}");
					coder.appendln2("");
				}

				coder.appendln2("@Override");
				coder.appendln2("public List<%s> getItems() {", def.entityCls);
				coder.appendln2("\treturn items;");
				coder.appendln2("}");
				coder.appendln2("");
				coder.appendln2("@Override");
				coder.appendln2("public void setItems(List<%s> items) {", def.entityCls);
				coder.appendln2("\tthis.items = items;");
				coder.appendln2("}");
				coder.appendln2("");
			}

			//setter & getter
			if (def.getDefs() != null) {
				coder.appendln2("//--------------------setter & getter-----------------------------------");
				for (EntityItemDef item : def.getDefs()) {
					//跳过生成父类的字段
					if (item.getCfg("parentDef") != null || "true".equals(item.getCfg("noGen"))) {
						continue;
					}
					String itemType = (String) item.getCfg("itemType");
					String fieldType = item.valCls;
					if (fieldType.indexOf(".") > 0) {
						fieldType = fieldType.substring(fieldType.lastIndexOf(".") + 1);
					}
					if ("Many".equals(itemType) || "Many2Many".equals(itemType)) {
						coder.appendFieldGSetter("List<" + fieldType + ">", item.field);
					} else {
						coder.appendFieldGSetter(fieldType, item.field);
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
			mapTplHelper.initModel(def);
			genDebugModelJson(EntityDef.genNameL(def.entityCls), def);

			genEntityCls();
			genDao();
			if (!def.isDetail()) {
				genService();
				genServiceTest();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
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

		//update realtions
		for (String cls : mapEntityDefs.keySet()) {
			EntityDef entityDef = mapEntityDefs.get(cls);
			if (entityDef.relations == null) {
				entityDef.relations = new ArrayList<StrObj>();
			}
			if (entityDef.relations.size() < 1) {
				for (EntityItemDef entityItemDef : entityDef.getDefs()) {
					if (entityItemDef.isOne2One() || entityItemDef.isOne2Many() || entityItemDef.isMany2Many()) {
						StrObj relation = new StrObj("relationField", entityItemDef.field, "relationType", entityItemDef.getRelation());
						EntityDef relationEntity = mapEntityDefs.get(entityItemDef.valCls);
						if (relationEntity == null) {
							LOG.error("不能找到" + entityItemDef.valCls + "的定义配置!");
							break;
						}
						relation.put("relationEntity", entityItemDef.valCls);
						relation.put("relationTable", entityItemDef.cfg.strVal("relation", ""));
						relation.put("relationFrom", entityItemDef.cfg.strVal("relationFrom", ""));
						relation.put("relationTo", entityItemDef.cfg.strVal("relationTo", ""));
						entityDef.relations.add(relation);
					}
				}
			}

			//relations
			if (entityDef.relations != null && !entityDef.relations.isEmpty()) {
				List<StrObj> relations = entityDef.relations;
				//移除重复关联
				Set<String> usedRelation = new HashSet<>();
				relations = relations.stream().filter(relation -> {
					EntityItemDef entityItemDef1 = (EntityItemDef) relation.get("entityItemDef");
					if (entityItemDef1 != null && entityItemDef1.field != null) {
						String relationNameU = entityItemDef1.field;
						if (usedRelation.contains(relationNameU)) { return false; }
						usedRelation.add(relationNameU);
					}
					return true;
				}).collect(Collectors.toList());
				entityDef.relations = relations;
			}
		}
	}
}
