package org.datasays.codeGen2;

import org.datasays.util.freemarker.FreemarkerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by watano on 2017/1/21.
 * gen code from freemarker tpl and yaml data
 */
public class FtlCodeGen2 extends AYmlCodeGen{
	private static final Logger LOG = LoggerFactory.getLogger(FtlCodeGen2.class);

	protected FreemarkerHelper fmHelper = null;

	public void init() {
		super.init();
		fmHelper = new FreemarkerHelper();
		fmHelper.init();
		fmHelper.setTplLoader(".", "classpath:/codegen/");
	}

	@Override
	public void gen() {
		try {
			fmHelper.process(genType + ".ftl", model);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		try {
			FtlCodeGen2 codegen = new FtlCodeGen2();
			codegen.init();
			codegen.genAll(args);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
