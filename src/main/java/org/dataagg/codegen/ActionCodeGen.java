package org.dataagg.codegen;

import static org.dataagg.codegen.base.ADefBase.genNameL;
import static org.dataagg.codegen.base.ADefBase.genNameU;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.dataagg.codegen.base.ACodeGenBase;
import org.dataagg.codegen.model.ActionDef;
import org.dataagg.codegen.model.ActionDefBuilder;
import org.dataagg.codegen.model.EntityDefBuilder;
import org.dataagg.codegen.util.JCoder;
import org.dataagg.codegen.util.JSCoder;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.props.PropDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.util.StringUtil;

public class ActionCodeGen extends ACodeGenBase<ActionDef> {
	private static final Logger LOG = LoggerFactory.getLogger(ActionCodeGen.class);

	public String webProjectHome = "";

	@Override
	public void genAllCode() {
		genController();
		genActionJs();
	}

	protected void genController() {
		try {
			String javaFile = def.javaFile(def.getRootPkg() + ".controller", def.getControllerCls());
			LOG.info(javaFile);

			String entityPkg = def.entityDef.getPkg();
			String entityCls = def.entityDef.getEntityCls();
			String entityNameU = genNameU(entityCls);
			String entityNameL = genNameL(entityCls);

			JCoder coder = new JCoder(javaFile, mergeCode);
			coder.appendln2("package %s;", def.getPkg());

			coder.appendln2("");
			coder.appendln2("import static com.dataagg.util.CndUtils.*;");
			coder.appendln2("");

			coder.appendln2("import org.dataagg.util.collection.*;");

			coder.appendln2("import java.util.*;");
			coder.appendln2("import jodd.util.*;");
			coder.appendln2("import org.nutz.dao.Cnd;");
			coder.appendln2("import org.nutz.dao.QueryResult;");
			coder.appendln2("import org.nutz.dao.util.cri.Exps;");
			coder.appendln2("import org.slf4j.Logger;");
			coder.appendln2("import org.slf4j.LoggerFactory;");
			coder.appendln2("import org.springframework.beans.factory.annotation.Autowired;");
			coder.appendln2("import org.springframework.web.bind.annotation.*;");
			coder.appendln2("");

			coder.appendln2("import com.dataagg.commons.base.*;");
			coder.appendln2("import %s.%s;", entityPkg, def.entityDef.getEntityCls());
			coder.appendln2("import %s.service.%sService;", def.getRootPkg(), entityNameU);
			coder.appendln2("import com.dataagg.util.SearchQueryJS;");

			coder.markImportCodeOffset();

			coder.appendln2("");
			coder.append(JCoder.longComment(def.getComments(), null));
			coder.appendln2("@RestController");
			coder.appendln2("@RequestMapping(\"%s\")", def.getBaseUrl());
			coder.appendln2("public class %s extends BaseController {", genNameU(def.getName()));
			coder.beginIndent();
			coder.appendln2("private Logger LOG = LoggerFactory.getLogger(%s.class);", genNameU(def.getName()));
			coder.appendln2("");
			coder.appendln2("@Autowired");
			coder.appendln2("public %sService %sService;", entityNameU, entityNameL);

			coder.insertMergedCodes("_CustomFields");
			coder.insertMergedCodes("_CustomMethods");

			coder.append(JCoder.lineBreakComment("genCode"));
			if (def.getDefs() != null) {
				for (PropDef pd : def.getDefs()) {
					String actionName = pd.getField();
					//http methods
					String methodsCode = "";
					String[] methods = (String[]) pd.getCfg("methods");
					if (methods != null && methods.length > 0) {
						for (String m : methods) {
							methodsCode += "RequestMethod." + m + ",";
						}
						methodsCode = StringUtil.cutSuffix(methodsCode, ",");
					}

					@SuppressWarnings("unchecked")
					List<StrObj> params = (List<StrObj>) pd.getCfg("params");
					String paramsCode = "";
					if (params != null && params.size() > 0) {
						for (StrObj p : params) {
							if (p.intVal("type") == 0) {
								paramsCode += String.format("%s %s,", p.strVal("cls"), p.strVal("name"));
							} else if (p.intVal("type") == 1) {
								paramsCode += String.format("@RequestBody%s %s %s,", (p.boolVal("required") ? "" : "(required=false)"), p.strVal("cls"), p.strVal("name"));
							} else if (p.intVal("type") == 2) {
								paramsCode += String.format("@PathVariable%s %s %s,", (p.boolVal("required") ? "" : "(required=false)"), p.strVal("cls"), p.strVal("name"));
							} else {
								LOG.warn("未知的参数类型:[" + p.intVal("type") + "]");
							}
						}
						paramsCode = StringUtil.cutSuffix(paramsCode, ",");
					}

					coder.appendln2("@RequestMapping(value = \"%s\", method = { %s })", pd.getCfg("url"), methodsCode);
					coder.appendln2("public %s %s(%s) {", pd.getCfg("returnObj"), actionName, paramsCode);
					coder.beginIndent();
					if (!"void".equals(pd.getCfg("returnObj"))) {
						coder.appendln2("%s result = new %s();", pd.getCfg("returnObj"), pd.getCfg("returnObj"));
					}
					coder.appendln2("try {");
					coder.beginIndent();

					//action方法体
					coder.startMergedCodes(actionName);
					if ("list".equals(actionName)) {
						coder.appendln2("// 过滤");
						coder.appendln2("Cnd cnd = Cnd.where(delFlag());");
						coder.appendln2("WMap query = ControllerHelper.getQuery(queryJs);");
						coder.appendln2("if (query.str2(\"name\") != null) {");
						coder.appendln2("	cnd.and(Exps.like(\"name\", query.str2(\"name\")));");
						coder.appendln2("}");
						coder.appendln2("cnd.desc(\"id\");");
						coder.appendln2("QueryResult queryResult = %sService.query(cnd, ControllerHelper.toPager(queryJs), %sService.ProfileList);", entityNameL, entityNameU);
						coder.appendln2("WMap resultData = ControllerHelper.buildResult(queryJs, queryResult, (%s %s) -> {", entityCls, entityNameL);
						coder.beginIndent();
						coder.appendln2("StrObj obj = new StrObj();");
						coder.appendln2("try {");

						coder.appendln2("} catch (Exception e) {");
						coder.appendln2("	LOG.error(\"查询失败：\" + e.getMessage());");
						coder.appendln2("}");
						coder.appendln2("return obj;");
						coder.endIndent();
						coder.appendln2("});");
						coder.appendln2("upAuthorities(result, \"%s_add\", \"%s_get\", \"%s_edit\", \"%s_delete\");", entityNameL, entityNameL, entityNameL, entityNameL);
						coder.appendln2("result.ok(resultData, \"查询成功！\");");
					} else if ("save".equals(actionName)) {
						coder.appendln2("%s = %sService.saveAll(%s);", entityNameL, entityNameL, entityNameL);
						coder.appendln2("if (%s != null) {", entityNameL);
						coder.appendln2("	result.okMsg(\"保存成功！\");");
						coder.appendln2("} else {");
						coder.appendln2("	result.errorMsg(\"保存失败！\");");
						coder.appendln2("}");
					} else if ("get".equals(actionName)) {
						coder.appendln2("%s %s = %sService.fetch(id, %sService.ProfileEdit);", entityCls, entityNameL, entityNameL, entityNameU);
						coder.appendln2("if (%s != null) {", entityNameL);
						coder.appendln2("	result.ok(%s);", entityNameL);
						coder.appendln2("	result.okMsg(\"获取成功！\");");
						coder.appendln2("} else {");
						coder.appendln2("	result.errorMsg(\"获取失败！\");");
						coder.appendln2("}");
					} else if ("delete".equals(actionName)) {
						coder.appendln2("if (%sService.delete(id)) {", entityNameL);
						coder.appendln2("	result.okMsg(\"删除成功！\");");
						coder.appendln2("} else {");
						coder.appendln2("	result.errorMsg(\"删除失败！\");");
						coder.appendln2("}");
					}
					coder.endMergedCodes(actionName);

					coder.endIndent();
					coder.appendln2("} catch (Exception e) {");
					coder.beginIndent();
					coder.appendln2("LOG.error(\"%s失败：\" + e.getMessage());", pd.getTitle());
					if (!"void".equals(pd.getCfg("returnObj"))) {
						coder.appendln2("result.error(e);");
					}
					coder.endIndent();
					coder.appendln2("}");
					if (!"void".equals(pd.getCfg("returnObj"))) {
						coder.appendln2("return result;");
					}
					coder.endIndent();
					coder.appendln2("}");
				}
			}
			coder.endIndent();
			coder.appendln2("}");

			//插入ImportCodes
			coder.insertImportCodes();
			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected void genActionJs() {
		try {
			String jsFile = webProjectHome + "src\\actions\\" + def.entityDef.getEntityNameL() + "Actions.js";
			LOG.info(jsFile);

			JSCoder coder = new JSCoder(jsFile);
			coder.appendln2("import common from '../assets/common.js';");
			coder.appendln2("");
			coder.appendln2("export default {");
			coder.beginIndent();
			coder.insertMergedCodes("_CustomMethods");
			coder.append(JCoder.lineBreakComment("genCode"));
			if (def.getDefs() != null) {
				for (PropDef pd : def.getDefs()) {
					String actionName = pd.getField();
					//action方法体
					coder.startMergedCodes(actionName);
					if ("list".equals(actionName)) {
						coder.appendln2("doFetchList: function (self, page, query, callBack) {");
						coder.appendln2("common.FetchRemote = true;");
						coder.appendln2("common.postAction(self, '%s%s', {", def.getBaseUrl(), pd.getCfg("url"));
						coder.appendln2("		page: page,");
						coder.appendln2("		query: query");
						coder.appendln2("	},");
						coder.appendln2("	callBack, (error) => {");
						coder.appendln2("		common.errorMsg(self, error);");
						coder.appendln2("	});");
						coder.appendln2("},");
					} else if ("save".equals(actionName)) {
						coder.appendln2("doSave: function (self, data, callBack) {");
						coder.appendln2("common.FetchRemote = true;");
						coder.appendln2("common.postAction(self, '%s%s', data,", def.getBaseUrl(), pd.getCfg("url"));
						coder.appendln2("	callBack, (error) => {");
						coder.appendln2("		common.errorMsg(self, error.message);");
						coder.appendln2("	});");
						coder.appendln2("},");
					} else if ("get".equals(actionName)) {
						coder.appendln2("doFetch: function (self, id, callBack) {");
						coder.appendln2("common.FetchRemote = true;");
						coder.appendln2("common.getAction(self, '%s%s' + id,", def.getBaseUrl(), pd.getCfg("url"));
						coder.appendln2("	callBack, (error) => {");
						coder.appendln2("		console.log(error);");
						coder.appendln2("		common.errorMsg(self, error);");
						coder.appendln2("	});");
						coder.appendln2("},");

					} else if ("delete".equals(actionName)) {
						coder.appendln2("doDelete: function (self, id, callBack) {");
						coder.appendln2("common.FetchRemote = true;");
						coder.appendln2("common.delAction(self, '%s%s' + id,", def.getBaseUrl(), pd.getCfg("url"));
						coder.appendln2("	callBack, (error) => {");
						coder.appendln2("		common.errorMsg(self, error);");
						coder.appendln2("	});");
						coder.appendln2("},");
					} else {
						//FIXME
						coder.appendln2("%s: function (self, callBack) {", actionName);
						coder.appendln2("	common.FetchRemote = true;");
						coder.appendln2("	common.getAction(self, '%s%s',", def.getBaseUrl(), pd.getCfg("url"));
						coder.appendln2("		callBack, (error) => {");
						coder.appendln2("			common.errorMsg(self, error);");
						coder.appendln2("		});");
						coder.appendln2("}");
					}
					coder.endMergedCodes(actionName);
				}
			}
			coder.endIndent();
			coder.appendln2("};");
			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void genAll(EntityDefBuilder[] allEntityDefBuilder, Consumer<ActionDefBuilder> fun) {
		if (allEntityDefBuilder != null) {
			for (EntityDefBuilder entityDefBuilder : allEntityDefBuilder) {
				ActionDefBuilder actionDefBuilder = ActionDefBuilder.newActionDef(entityDefBuilder.main);
				fun.accept(actionDefBuilder);
				genCodeByDef(actionDefBuilder.main);
			}
		}
	}
}
