package org.dataagg.codegen.util;

import javax.annotation.Nonnull;

public class JSCoder extends CodeGenHelper {
	public JSCoder(String jsFile) {
		super(jsFile);
		codeMerger = new JCodeMerger(jsFile);
	}

	@Nonnull
	public static String lineBreakComment(@Nonnull String comment) {
		StringBuffer sb = new StringBuffer();
		appendln(sb, "//--------------------------%s--------------------------------", comment);
		return sb.toString();
	}

}
