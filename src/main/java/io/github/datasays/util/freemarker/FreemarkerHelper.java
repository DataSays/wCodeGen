package io.github.datasays.util.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import jodd.io.FileUtil;
import jodd.io.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class FreemarkerHelper {
	private static final String Encoding = "utf-8";
	private static final String ClassPath = "classpath:";
	private static final Logger LOG = LoggerFactory.getLogger(FreemarkerHelper.class);
	private Configuration cfg = null;

	public void init() {
		cfg = new Configuration(Configuration.getVersion());
		cfg.setSharedVariable("invokestatic", new InvokeStaticMethodModel());
		cfg.setSharedVariable("value", new ValueMethodModel());
		cfg.setSharedVariable("WriteFtl", new WriteFtlDirective());
		cfg.setSharedVariable("LoadYaml", new LoadYamlMethod());
		cfg.setSharedVariable("LeftTab", new LeftTabDirective());
		cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
	}

	public void setTemplateLoadingDir(File... dirs) {
		if (dirs != null && dirs.length > 0) {
			try {
				List<TemplateLoader> tls = new ArrayList<TemplateLoader>();
				for (File f : dirs) {
					tls.add(new FileTemplateLoader(f));
				}
				MultiTemplateLoader mtl = new MultiTemplateLoader(tls.toArray(new TemplateLoader[]{}));
				cfg.setTemplateLoader(mtl);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	public static void setVar(String varName, Object data, Environment env){
		if(data == null){
			return;
		}
		if(env == null){
			env = Environment.getCurrentEnvironment();
		}
		if(data instanceof Map) {
			env.setVariable(varName, new SimpleHash((Map) data, env.getObjectWrapper()));
		}else if(data instanceof List){
			env.setVariable(varName, new SimpleSequence((List)data, env.getObjectWrapper()));
		}else if(data instanceof String){
			env.setVariable(varName, new SimpleScalar((String)data));
		}else if(data instanceof Number){
			env.setVariable(varName, new SimpleNumber((Number)data));
		}else if(data instanceof java.sql.Date){
			env.setVariable(varName, new SimpleDate((java.sql.Date)data));
		}else if(data instanceof java.sql.Time){
			env.setVariable(varName, new SimpleDate((java.sql.Time)data));
		}else if(data instanceof java.sql.Timestamp){
			env.setVariable(varName, new SimpleDate((java.sql.Timestamp)data));
		}else if(data instanceof Date){
			env.setVariable(varName, new SimpleDate((Date)data, 3));
		}
	}

	public void setTplLoader(String... loaders) {
		if (loaders != null) {
			List<TemplateLoader> tplLoaders = new ArrayList<>();
			for (String loader : loaders) {
				try {
					if (loader.startsWith(ClassPath)) {
						String basePkg = loader.substring(ClassPath.length());
						tplLoaders.add(new ClassTemplateLoader(this.getClass(), basePkg));
					} else {
						tplLoaders.add(new FileTemplateLoader(new File(loader)));
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			MultiTemplateLoader mtl = new MultiTemplateLoader(tplLoaders.toArray(new TemplateLoader[]{}));
			cfg.setTemplateLoader(mtl);
		}
	}

	public Configuration getCfg() {
		return cfg;
	}

	public void process(String ftlName, Map<String, Object> model, Writer out) {
		Template temp;
		try {
			temp = cfg.getTemplate(ftlName);
			if (model == null) {
				model = new Hashtable<String, Object>();
			}
			temp.process(model, out);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			StreamUtil.close(out);
		}
	}

	public void process(String ftlName, Map<String, Object> model, String targetFile) {
		try {
			LOG.debug(ftlName + "->" + targetFile);
			Writer out = new StringWriter();
			process(ftlName, model, out);
			File target = new File(targetFile);
			if (!target.exists()) {
				FileUtil.mkdirs(target.getParentFile());
				target.createNewFile();
			}
			FileUtil.writeString(targetFile, out.toString(), Encoding);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

	}
}
