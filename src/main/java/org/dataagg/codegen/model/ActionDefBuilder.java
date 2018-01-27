package org.dataagg.codegen.model;

import static org.dataagg.codegen.base.ADefBase.*;

import java.util.ArrayList;
import java.util.List;

import org.dataagg.codegen.base.ADefBuilderBase;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.lang.ValuePlus;
import org.dataagg.util.props.PropDef;

public class ActionDefBuilder extends ADefBuilderBase<ActionDef, PropDef> {
	public static final String Method_GET = "GET";
	public static final String Method_POST = "POST";
	public static final String Method_PUT = "PUT";
	public static final String Method_OPTIONS = "OPTIONS";
	public static final String Method_PATCH = "PATCH";
	public static final String Method_HEAD = "HEAD";
	public static final String Method_DELETE = "DELETE";

	public ActionDefBuilder(ActionDef m) {
		super(m);
	}

	public static ActionDefBuilder newActionDef(String project, String name, String pkg, String controllerCls, String baseUrl, String comment) {
		ActionDef actionDef = new ActionDef(name);
		actionDef.common(project, pkg, comment);
		actionDef.setControllerCls(controllerCls);
		actionDef.setBaseUrl(baseUrl);
		ActionDefBuilder builder = new ActionDefBuilder(actionDef);
		return builder;
	}

	public static ActionDefBuilder newActionDef(EntityDef entityDef) {
		String entityCls = entityDef.getEntityCls();
		String entityNameL = genNameL(entityCls);
		ActionDef actionDef = new ActionDef(entityDef);
		ActionDefBuilder builder = new ActionDefBuilder(actionDef);

		builder.addObjMethod("list", "查询", "/list", null, Method_GET, Method_POST).addParamRequestBody("SearchQueryJS", "queryJs", false);
		builder.addObjMethod("save", "保存", "/save", entityCls, Method_POST).addParamRequestBody(entityCls, entityNameL, true);
		builder.addObjMethod("get", "获取", "/get/{id}", entityCls, Method_GET).addParamPathVariable("Long", "id", true);
		builder.addObjMethod("delete", "删除", "/delete/{id}", entityCls, Method_DELETE).addParamPathVariable("Long", "id", true);
		return builder;
	}

	public ActionDefBuilder extClsInfo(String extClsInfo) {
		main.addCfg("extClsInfo", extClsInfo);
		return this;
	}

	protected ActionDefBuilder addMethod(String name, String title, String url, String... methods) {
		PropDef item = main.newDef(name, title);
		item.addCfg("url", url);
		List<String> lstMethods = new ArrayList<>();
		for (String m : methods) {
			lstMethods.add(m);
		}
		item.addCfg("methods", lstMethods.toArray(new String[] {}));
		main.addPropDef(item);
		return this;
	}

	public ActionDefBuilder addObjMethod(String name, String title, String url, String returnCls, String... methods) {
		addMethod(name, title, url, methods);
		setReturnCls(returnCls, false);
		return this;
	}

	public ActionDefBuilder addListMethod(String name, String title, String url, String returnCls, String... methods) {
		addMethod(name, title, url, methods);
		setReturnCls(returnCls, true);
		return this;
	}

	protected ActionDefBuilder setReturnCls(String cls, boolean isArray) {
		PropDef item = lastItem();
		String returnObj = "ActionResultObj";
		if (!isArray) {
			if ("ActionResultObj".equals(cls)) {
				returnObj = "ActionResultObj";
			} else if ("void".equals(cls)) {
				returnObj = "void";
			} else if (cls != null) {
				returnObj = "ActionResult<" + cls + ">";
			} else {
				returnObj = "ActionResultObj";
			}
		} else {
			returnObj = "ActionResultList<" + cls + ">";
		}
		item.addCfg("returnObj", returnObj);
		return this;
	}

	/**
	 *	为method添加参数
	 * @param name 参数名称
	 * @param cls 参数类名
	 * @param type 0: 普通参数, 1: RequestBody 2: PathVariable
	 * @param required 是否必须
	 * @return
	 */
	protected ActionDefBuilder addParam(String name, String cls, int type, boolean required) {
		PropDef item = lastItem();
		StrObj param = new StrObj("name", name, "cls", cls, "type", type, "required", required);
		@SuppressWarnings("unchecked")
		List<StrObj> params = (List<StrObj>) item.getCfg("params");
		if (params == null) {
			params = ValuePlus.lst(param);
		} else {
			params.add(param);
		}
		item.addCfg("params", params);
		return this;
	}

	public ActionDefBuilder addParam(String cls, String name) {
		addParam(name, cls, 0, false);
		return this;
	}

	public ActionDefBuilder addParamRequestBody(String cls, String name, boolean required) {
		addParam(name, cls, 1, required);
		return this;
	}

	public ActionDefBuilder addParamPathVariable(String cls, String name, boolean required) {
		addParam(name, cls, 2, required);
		return this;
	}

	/**
	 * 定制method的java code
	 * @param code
	 * @return
	 */
	public ActionDefBuilder addMethodCode(String code) {
		main.addCfg("methodCode", code);
		return this;
	}
}
