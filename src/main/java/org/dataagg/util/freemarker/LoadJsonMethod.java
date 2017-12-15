package org.dataagg.util.freemarker;

import java.util.List;

import org.dataagg.util.WJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * Created by watano on 2017/2/5.
 * Load json file into freemarker variable.
 * args: varName, filePath
 */
public class LoadJsonMethod implements TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(LoadJsonMethod.class);

	@Override
	public Object exec(List args) throws TemplateModelException {
		if (args != null && args.size() >= 1) {
			try {
				String varName = args.get(0).toString();
				String filePath = args.get(1).toString();
				LOG.info("LoadJson(" + varName + ", " + filePath + ")");
				Environment env = Environment.getCurrentEnvironment();
				Object data = WJsonUtils.fromJson(filePath, Object.class);
				FreemarkerHelper.setVar(varName, data, env);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return new SimpleScalar("");
	}
}
