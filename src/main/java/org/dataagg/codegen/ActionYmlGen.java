package org.dataagg.codegen;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dataagg.codegen.model.ModelFieldDef;
import org.dataagg.codegen.util.JCoder;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.text.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.util.StringUtil;

public class ActionYmlGen {
	private static final Logger LOG = LoggerFactory.getLogger(ActionYmlGen.class);
	private StrObj cfg;

	public void init() {
		cfg = YamlUtil.readFile("./actions.yml");
	}

	public void genAll() {
		StrObj common = cfg.mapVal("common");
		StrObj apis = cfg.mapVal("apis");
		if (apis != null) {
			for (String apiName : apis.keySet()) {
				StrObj api = apis.mapVal(apiName);
				StrObj apiDefs = YamlUtil.readFile(api.strVal("apiDefs"));
				apiDefs.put("common", common);
				apiDefs.put("api", api);
				switch (api.strVal("type").toLowerCase()) {
				case "jsonrpc1":
					//genJsonRpc1(apiDefs);
					break;
				case "restapi1":
					genRestApi1(apiDefs);
					break;

				}
			}
		}
	}

	public void genJsonRpc1(StrObj apiDefs) {
		boolean mergeCode = false;
		String target = apiDefs.mapVal("common").strVal("target") + "/src/main/java/";
		String apiPackage = apiDefs.mapVal("api").strVal("apiPackage");
		String modelPackage = apiDefs.mapVal("api").strVal("modelPackage", apiPackage + ".model");
		//gen models
		_genModels(apiDefs, mergeCode);

		//init Service Class
	}

	public void genRestApi1(StrObj apiDefs) {
		boolean mergeCode = false;
		String target = apiDefs.mapVal("common").strVal("target") + "/src/main/java/";
		String apiPackage = apiDefs.mapVal("api").strVal("apiPackage");
		String modelPackage = apiDefs.mapVal("api").strVal("modelPackage", apiPackage + ".model");
		//gen models
		_genModels(apiDefs, mergeCode);

		//init Service Class
		try {
			JCoder jCoder = new JCoder(target + StringUtil.replace(apiPackage, ".", "/") + "/" + apiDefs.strVal("name") + "Service.java", mergeCode);
			genJavaCommonComments(jCoder, apiDefs);
			jCoder.appendln2("package %s;", apiPackage);
			jCoder.appendLines("");

			//import
			jCoder.appendln2("import %s.*;", modelPackage);
			jCoder.appendLines("import java.util.*;");
			jCoder.appendLines("import org.dataagg.util.http.*;");
			jCoder.appendLines("import org.dataagg.util.json.*;");
			jCoder.appendLines("import com.fasterxml.jackson.core.type.*;");
			jCoder.appendLines("import okhttp3.*;");
			jCoder.appendLines("");
			jCoder.markImportCodeOffset();
			jCoder.appendLines("");

			String serviceName = apiDefs.strVal("name") + "Service";
			jCoder.appendln2(JCoder.publicClsDef(serviceName, null));
			jCoder.beginIndent();

			jCoder.appendln2("public String baseUrl;");
			jCoder.appendln2("private final HttpHelper httpHelper;");

			jCoder.appendln2("public %s(String baseUrl, String accessKey) {", serviceName);
			jCoder.appendln2("	httpHelper = new HttpHelper(new SignatureAuthorization(accessKey));");
			jCoder.appendln2("	this.baseUrl = baseUrl;");
			jCoder.appendln2("}");
			jCoder.appendLines("");

			jCoder.appendln2("public HttpHelper getHttpHelper() {");
			jCoder.appendln2("	return httpHelper;");
			jCoder.appendln2("}");
			jCoder.appendLines("");

			//gen actions
			StrObj actions = apiDefs.mapVal("actions");
			if (actions != null) {
				for (String actionName : actions.keySet()) {
					try {
						StrObj action = actions.mapVal(actionName);
						jCoder.appendln2("/**");
						jCoder.appendln2("* %s", action.strVal("description", ""));
						jCoder.appendln2("* @category %s", StringUtil.join(action.strArrayVal("tag"), ","));

						String actionParams = "";
						String actionParams2 = "";
						String actionUrl = action.strVal("path");
						String paramCodes = "";
						if (action.has("params")) {
							List<ModelFieldDef> allParams = new ArrayList<>();
							for (Object p : action.listVal("params", Object.class)) {
								ModelFieldDef paramDef = ModelFieldDef.param((List<?>) p);
								allParams.add(paramDef);
							}
							Collator myCollator = Collator.getInstance();
							allParams.sort((o1, o2) -> myCollator.compare(o1.name, o2.name));
							for (ModelFieldDef paramDef : allParams) {
								if ("body".equalsIgnoreCase(paramDef.scope)) {
									paramCodes += String.format("	request.jsonRequestBody(%s);\n", paramDef.name);
								} else if ("url".equalsIgnoreCase(paramDef.scope)) {
									actionUrl = StringUtil.replace(actionUrl, "{" + paramDef.name + "}", "\"+" + paramDef.name + "+\"");
									//paramCodes += String.format("	request.addPathParam(\"%s\", %s);\n", paramDef.name, paramDef.name);
								} else {
									paramCodes += String.format("	request.addParam(\"%s\", %s);\n", paramDef.name, paramDef.name);
								}
								actionParams += paramDef.cls + " " + paramDef.name + ",";
								actionParams2 += paramDef.name + ",";
								jCoder.appendln2("* @param %s %s %s %s %s", paramDef.name, paramDef.cls, paramDef.required, paramDef.scope, paramDef.comment);
							}
						}
						actionParams = StringUtil.cutSuffix(actionParams, ",");
						actionParams2 = StringUtil.cutSuffix(actionParams2, ",");
						String[] returnObj = action.strArrayVal("returnObj");
						if (returnObj.length < 2) {
							returnObj = new String[] { returnObj[0], "" };
						}
						jCoder.appendln2("* @return %s %s", returnObj[0], returnObj[1]);
						jCoder.appendln2("**/");
						jCoder.appendln2("public %s %s(%s) throws Exception {", returnObj[0], actionName, actionParams);
						jCoder.appendln2("	Call call = _%s(%s);", actionName, actionParams2);
						jCoder.appendln2("	return httpHelper.send(call, new TypeReference<%s>() {});", returnObj[0]);
						jCoder.appendln2("}");

						jCoder.appendln2("");
						jCoder.appendln2("protected Call _%s(%s) {", actionName, actionParams);
						if ("GET".equalsIgnoreCase(action.strVal("method"))) {
							jCoder.appendln2("	HttpRequestBuilder request = HttpRequestBuilder.get(baseUrl + \"%s\");", actionUrl);
						} else if ("head".equalsIgnoreCase(action.strVal("method"))) {
							jCoder.appendln2("	HttpRequestBuilder request = HttpRequestBuilder.head(baseUrl + \"%s\");", actionUrl);
						} else if ("delete".equalsIgnoreCase(action.strVal("method"))) {
							jCoder.appendln2("	HttpRequestBuilder request = HttpRequestBuilder.delete(baseUrl + \"%s\");", actionUrl);
						} else if ("POST".equalsIgnoreCase(action.strVal("method"))) {
							jCoder.appendln2("	HttpRequestBuilder request = HttpRequestBuilder.post(baseUrl + \"%s\");", actionUrl);
						} else if ("put".equalsIgnoreCase(action.strVal("method"))) {
							jCoder.appendln2("	HttpRequestBuilder request = HttpRequestBuilder.put(baseUrl + \"%s\");", actionUrl);
						} else if ("patch".equalsIgnoreCase(action.strVal("method"))) {
							jCoder.appendln2("	HttpRequestBuilder request = HttpRequestBuilder.patch(baseUrl + \"%s\");", actionUrl);
						}
						jCoder.appendln2(paramCodes);
						jCoder.appendln2("	return httpHelper.doAction(request);");
						jCoder.appendln2("}");
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}

			jCoder.insertImportCodes();
			jCoder.endIndent();
			jCoder.appendLines("}");
			jCoder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void _genModels(StrObj apiDefs, boolean mergeCode) {
		String target = apiDefs.mapVal("common").strVal("target") + "/src/main/java/";
		String apiPackage = apiDefs.mapVal("api").strVal("apiPackage");
		String modelPackage = apiDefs.mapVal("api").strVal("modelPackage", apiPackage + ".model");
		StrObj models = apiDefs.mapVal("models");
		if (models != null) {
			for (String modelName : models.keySet()) {
				try {
					StrObj model = models.mapVal(modelName);
					String type = model.strVal("type");
					if ("Object".equalsIgnoreCase(type)) {
						JCoder jCoder = new JCoder(target + StringUtil.replace(modelPackage, ".", "/") + "/" + modelName + ".java", mergeCode);
						genJavaCommonComments(jCoder, apiDefs);
						jCoder.appendln2("package %s;", modelPackage);
						jCoder.appendLines("");

						//import
						jCoder.appendLines("import java.math.*;");
						jCoder.appendLines("import java.util.*;");
						jCoder.markImportCodeOffset();
						jCoder.appendLines("");

						//class def
						String cls = jCoder.ufield(modelName);
						if (model.has("genericType")) {
							String[] genericTypes = model.strArrayVal("genericType");
							if (genericTypes.length > 0) {
								cls += "<" + StringUtil.join(genericTypes, ",") + ">";
							}
						}

						Map<String, ModelFieldDef> fieldDefs = new HashMap<>();
						jCoder.appendln2(JCoder.publicClsDef(cls, model.strVal("parentCls", ""), model.strArrayVal("interfaces")));
						jCoder.beginIndent();
						//properties
						if (model.has("properties")) {
							StrObj properties = model.mapVal("properties");
							for (String propName : properties.keySet()) {
								ModelFieldDef fDef = ModelFieldDef.modleFiled(propName, properties.strArrayVal(propName));
								fieldDefs.put(propName, fDef);
								jCoder.appendFieldDef(fDef);
							}
							String block = "_Custom";
							jCoder.startMergedCodes(block);
							jCoder.endMergedCodes(block);
							for (String propName : properties.keySet()) {
								ModelFieldDef fDef = fieldDefs.get(propName);
								jCoder.appendFieldGSetter(fDef.cls, fDef.name);
							}
						}

						jCoder.insertImportCodes();
						jCoder.endIndent();
						jCoder.appendLines("}");
						jCoder.writeToFile();
					} else {
						LOG.warn("can't suuport type[" + type + "] on " + modelName);
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	private void genJavaCommonComments(JCoder jCoder, StrObj apiDefs) {
		jCoder.appendln("/**");
		jCoder.appendln("* <h1><a href=\"%s\">%s</a></h1>", apiDefs.strVal("url", ""), apiDefs.strVal("name", ""));
		jCoder.appendln("* %s", apiDefs.strVal("description", ""));
		jCoder.appendln("* license: %s", apiDefs.mapVal("common").strVal("licenseName", ""));
		jCoder.appendln("*");
		StrObj developer = apiDefs.mapVal("common").mapVal("developer");
		if (developer.has("name")) {
			if (developer.has("email")) {
				jCoder.appendln("* @author  %s<%s>", developer.strVal("name", ""), developer.strVal("email"), "");
			} else {
				jCoder.appendln("* @author  %s", developer.strVal("name", ""));
			}
		}
		if (developer.has("organization")) {
			if (developer.has("organizationUrl")) {
				jCoder.appendln("* organization:  %s<%s>", developer.strVal("organization", ""), developer.strVal("organizationUrl"), "");
			} else {
				jCoder.appendln("* organization:  %s", developer.strVal("organization", ""));
			}
		}
		jCoder.appendln("* @version %s", apiDefs.strVal("version", ""));
		jCoder.appendln("*/");
	}

	public void genSwagger(StrObj swagger) {
		//		mapTplHelper.initModel(swagger);
		//		StrObj definitions = swagger.mapVal("definitions");
		//		String target = swagger.mapVal("common").strVal("target") + "/src/main/java/";
		//		String apiPackage = swagger.mapVal("api").strVal("apiPackage");
		//		String modelPackage = swagger.mapVal("api").strVal("modelPackage", apiPackage + ".model");
		//		if (definitions != null) {
		//			for (String modelName : definitions.keySet()) {
		//				StrObj definition = new StrObj(definitions.getAs(modelName, Map.class));
		//				String javaFile = target + StringUtil.replace(modelPackage, ".", "/") + "/" + modelName + ".java";
		//				JCoder coder = new JCoder(javaFile, true);
		//			}
		//		}
		//		List<String> tags = swagger.listVal("tags", String.class);
		//		Map<String, JCoder> actionCoders = new HashMap<String, JCoder>();
		//		if (tags != null) {
		//			for (String tag : tags) {
		//				//				String javaFile = target + StringUtil.replace(apiPackage, ".", "/") + "/" + action.strVal("") + ".java";
		//				//				JCoder actionCoder = new JCoder(javaFile, true);
		//
		//			}
		//		}
		//		String basePath = swagger.strVal("basePath");
		//		StrObj paths = swagger.mapVal("paths");
		//		if (paths != null) {
		//			for (String path : paths.keySet()) {
		//				StrObj action = new StrObj(paths.getAs(path, Map.class)).mapVal("");
		//
		//			}
		//		}
		//		StrObj responses = swagger.mapVal("responses");
		//		StrObj securityDefinitions = swagger.mapVal("securityDefinitions");
	}

	public static void main(String[] args) {
		ActionYmlGen codeGen = new ActionYmlGen();
		codeGen.init();
		codeGen.genAll();
	}
}
