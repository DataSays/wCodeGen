package org.datasays.util.freemarker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.core.Environment;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class ValueMethodModel implements TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(FreemarkerHelper.class);

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		try {
			if (arguments != null && arguments.size() > 0) {
				Environment env = Environment.getCurrentEnvironment();
				Object value = env.getVariable((String) arguments.get(0));
				if (value != null) { return value; }
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
}
