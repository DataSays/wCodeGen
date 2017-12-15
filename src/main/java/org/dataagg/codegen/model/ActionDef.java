package org.dataagg.codegen.model;

import java.util.List;

import org.dataagg.codegen.base.ADefBase;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.props.PropDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.util.StringUtil;

public class ActionDef extends ADefBase<PropDef> {
	private static final Logger LOG = LoggerFactory.getLogger(ActionDef.class);
	private static final long serialVersionUID = -6781265090351752245L;
	public EntityDef entityDef;//关联实体对象定义
	private String baseUrl;
	private String controllerCls;//controller类额外配置

	public ActionDef(String name) {
		super(name);
	}

	public ActionDef(EntityDef entityDef) {
		super(genNameU(entityDef.getEntityCls()) + "Controller");
		this.entityDef = entityDef;
		String entityNameL = genNameL(entityDef.getEntityCls());
		controllerCls = name;
		baseUrl = "/" + entityNameL;
		common(entityDef.getProject(), entityDef.getRootPkg() + ".controller", entityDef.getComments());
	}

	@Override
	public PropDef newDef(String key, String title) {
		return new PropDef(key, title);
	}

	@Override
	public StrObj buildModel() {
		StrObj model = super.buildModel();
		String entityCls = entityDef.getEntityCls();
		model.put("rootPkg", getRootPkg());
		model.put("name", name);
		model.put("project", project);
		model.put("pkg", pkg);
		model.put("baseUrl", baseUrl);
		model.put("controllerCls", controllerCls);
		model.put("comments", comments);
		model.put("entityNameU", genNameU(entityCls));
		model.put("entityNameL", genNameL(entityCls));
		model.put("nameU", genNameU(name));
		model.put("nameL", genNameL(name));
		model.put("entityDef", entityDef);
		model.put("defs", getDefs());

		for (PropDef pd : getDefs()) {
			String methodsCode = "";
			String[] methods = (String[]) pd.getCfg("methods");
			if (methods != null && methods.length > 0) {
				for (String m : methods) {
					methodsCode += "RequestMethod." + m + ",";
				}
				methodsCode = StringUtil.cutSuffix(methodsCode, ",");
			}
			pd.addCfg("methodsCode", methodsCode);

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
			pd.addCfg("paramsCode", paramsCode);
		}
		return model;
	}

	public String getControllerCls() {
		return controllerCls;
	}

	public void setControllerCls(String controllerCls) {
		this.controllerCls = controllerCls;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
