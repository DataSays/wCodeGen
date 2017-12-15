package org.dataagg.util.freemarker;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by watano on 2017/2/5.
 * Log msg
 * args: msg
 */
public class LogMethod implements TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(LogMethod.class);

	@Override
	public Object exec(List args) throws TemplateModelException {
		if (args != null && args.size() > 0) {
			LOG.info(args.get(0).toString());
		}
		return new SimpleScalar("");
	}
}
