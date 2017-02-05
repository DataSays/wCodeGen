package io.github.datasays.codeGen2;

import io.github.datasays.util.YamlUtil;
import io.github.datasays.util.freemarker.FreemarkerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 * gen code from freemarker tpl and yaml data
 */
public class FtlCodeGen2 {
	private static final Logger LOG = LoggerFactory.getLogger(FtlCodeGen2.class);

	private FreemarkerHelper fmHelper = null;

	public void init() {
		fmHelper = new FreemarkerHelper();
		fmHelper.init();
		fmHelper.setTplLoader("classpath:/codegen/");
	}

	public void gen(String ftl) {
		try {
			StringWriter sw = new StringWriter();
			fmHelper.process(ftl + ".ftl", new HashMap<>(), sw);
			LOG.info(sw.toString());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		try {
			if (args != null && args.length > 0) {
				FtlCodeGen2 codegen = new FtlCodeGen2();
				codegen.init();
				for (String arg : args) {
					String dataFile = arg.trim();
					//load data yml files, the "props" is the local vars;
					Map<?, ?> data = YamlUtil.loadAndEval(dataFile, "props");
					FreemarkerHelper.setVar("data", data, null);

					//gen code root dir
					String workDir = ".";
					if (data.get("WorkDir") != null) {
						workDir = data.get("WorkDir").toString();
					}
					FreemarkerHelper.setVar("WorkDir", workDir, null);

					//code gen tpl
					String genType = "gradle";
					if (data.get("GenType") != null) {
						genType = data.get("GenType").toString();
					}
					codegen.gen(genType);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
