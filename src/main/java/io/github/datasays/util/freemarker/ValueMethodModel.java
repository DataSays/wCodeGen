package io.github.datasays.util.freemarker;

import java.util.List;

import org.apache.log4j.Logger;

import freemarker.core.Environment;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class ValueMethodModel implements TemplateMethodModelEx {
	private static final Logger LOG = Logger.getLogger(ValueMethodModel.class);

	@Override
	@SuppressWarnings({"rawtypes"})
	public Object exec(List arguments) throws TemplateModelException {
		try {
			if(arguments != null && arguments.size() > 0) {
				Environment env = Environment.getCurrentEnvironment();
				Object value = env.getVariable((String)arguments.get(0));
				if(value != null) {
					return value;
				}
			}
		}catch(Throwable t) {
			LOG.error("", t);
		}
		return null;
	}
}
