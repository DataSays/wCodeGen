package io.github.datasays.util.freemarker;

import freemarker.core.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import jodd.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 * Load yaml file into freemarker variable.
 * args: varName, filePath
 */
public class LoadYaml implements TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(LoadYaml.class);

	@Override
	public Object exec(List args) throws TemplateModelException {
		if(args != null && args.size() >= 2){
			try {
				Environment env = Environment.getCurrentEnvironment();
				Yaml yml = new Yaml();
				Object data = yml.load(FileUtil.readString(args.get(1).toString(), "utf-8"));
				env.setVariable(args.get(0).toString(), new SimpleHash((Map<?, ?>) data, env.getObjectWrapper()));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return new SimpleScalar("");
	}
}
