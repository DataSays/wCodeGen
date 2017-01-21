package io.github.datasays.util.freemarker;

import freemarker.core.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import io.github.datasays.util.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 * Load yaml file into freemarker variable.
 * args: varName, filePath, prop Field Name
 */
public class LoadYaml implements TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(LoadYaml.class);

	@Override
	public Object exec(List args) throws TemplateModelException {
		if(args != null && args.size() >= 2){
			try {
				Environment env = Environment.getCurrentEnvironment();
				String propsField = null;
				if(args.size() >= 3){
					propsField = args.get(2).toString();
				}
				Map<?,?> data = YamlUtil.loadAndEval(args.get(1).toString(), propsField);
				env.setVariable(args.get(0).toString(), new SimpleHash(data, env.getObjectWrapper()));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return new SimpleScalar("");
	}
}
