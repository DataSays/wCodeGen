package org.dataagg.util.freemarker;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import org.dataagg.util.text.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 * Load yaml file into freemarker variable.
 * args: varName, filePath, prop Field Name
 */
public class LoadYamlMethod implements TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(LoadYamlMethod.class);

	@Override
	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		if (args != null && args.size() >= 2) {
			try {
				String varName = args.get(0).toString();
				String filePath = args.get(1).toString();
				Environment env = Environment.getCurrentEnvironment();
				String propsField = null;
				if (args.size() >= 3) {
					propsField = args.get(2).toString();
				}
				LOG.info("LoadYaml(" + varName + ", " + filePath + ", " + (propsField != null ? propsField : "") + ")");
				Map<?, ?> data = YamlUtil.evalYml(filePath, propsField);
				FreemarkerHelper.setVar(varName, data, env);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return new SimpleScalar("");
	}
}
