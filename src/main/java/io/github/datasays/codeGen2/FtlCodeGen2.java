package io.github.datasays.codeGen2;

import io.github.datasays.util.freemarker.FreemarkerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.HashMap;

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

	public void gen(String ftl){
		try {
			StringWriter sw = new StringWriter();
			fmHelper.process(ftl+".ftl", new HashMap<>(), sw);
			LOG.info(sw.toString());
		}catch (Exception e){
			LOG.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		if(args!=null){
			FtlCodeGen2 codegen = new FtlCodeGen2();
			codegen.init();
			for(String arg: args){
				codegen.gen(arg);
			}
		}
	}
}
