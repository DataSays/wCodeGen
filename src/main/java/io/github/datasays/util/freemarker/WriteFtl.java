package io.github.datasays.util.freemarker;

import freemarker.core.Environment;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 */
public class WriteFtl implements TemplateDirectiveModel {
	private static final Logger LOG = LoggerFactory.getLogger(WriteFtl.class);

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		try {
			String out = params.get("out").toString();
			int left = 0;
			if (params.get("left") != null && params.get("left") instanceof SimpleNumber) {
				left = ((SimpleNumber) params.get("left")).getAsNumber().intValue();
			}
			LOG.info(out);
			StringWriter sw = new StringWriter();
			body.render(sw);
			String code = sw.toString();
			if (left > 0) {
				String leftCode = StringUtil.repeat("\t", left);
				code = code.replaceAll("^" + leftCode, "");
				code = code.replaceAll("\n" + leftCode, "\n");
			}
			//LOG.debug(code);
			FileUtil.writeString(out, code, "utf-8");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
