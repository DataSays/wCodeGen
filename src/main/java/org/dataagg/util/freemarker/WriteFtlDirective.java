package org.dataagg.util.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import jodd.io.FileUtil;
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
			String out = params.get("out").toString();
			out = out.trim();
			String workDir = ".";
			Object workDirTmp = FreemarkerHelper.getVar("WorkDir", env);
			if(workDirTmp != null){
				workDir = workDirTmp.toString();
			}
			File outFile = new File(workDir+out);
			if(out.endsWith("/") || out.endsWith("\\")){
				if(!outFile.exists()){
					FileUtil.mkdirs(outFile);
					LOG.info("mkdirs-->" + outFile.getAbsolutePath());
				}
			}else {
				//render body
				StringWriter sw = new StringWriter();
				if(body!= null){
					body.render(sw);
				}
				String code = sw.toString();

				if (!outFile.getParentFile().exists()) {
					FileUtil.mkdirs(outFile.getParentFile());
				}

				//gen the code files
				//LOG.debug(code);
				LOG.info(out + "-->" + outFile.getAbsolutePath());
				FileUtil.writeString(outFile, code, "utf-8");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
