package org.dataagg.codegen.util;

public class JSCoder extends CodeGenHelper {
	public JSCoder(String jsFile) {
		super(jsFile);
		codeMerger = new JCodeMerger(jsFile);
	}

	public static String lineBreakComment(String comment) {
		StringBuffer sb = new StringBuffer();
		appendln(sb, "//--------------------------%s--------------------------------", comment);
		return sb.toString();
	}

}
