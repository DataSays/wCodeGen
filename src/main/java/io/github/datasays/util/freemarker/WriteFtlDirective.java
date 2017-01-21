package io.github.datasays.util.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 */
public class WriteFtlDirective implements TemplateDirectiveModel {
	private static final Logger LOG = LoggerFactory.getLogger(WriteFtlDirective.class);

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		try {
			//render body
			StringWriter sw = new StringWriter();
			body.render(sw);
			String code = sw.toString();

			//print comment
			String comment = params.get("comment").toString();
			if(StringUtil.isNotBlank(comment)){
				LOG.info(comment);
			}

			//gen the code files
			//LOG.debug(code);
			String out = params.get("out").toString();
			File outFile = new File(out);
			if(!outFile.getParentFile().exists()){
				FileUtil.mkdirs(outFile.getParentFile());
			}
			LOG.info(out+"-->"+outFile.getAbsolutePath());
			FileUtil.writeString(outFile, code, "utf-8");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
