package org.dataagg.codegen.util;

public class JSCoder extends CodeGenHelper {
	public JSCoder(String jsFile, boolean mergeCode) {
		super(jsFile);
		codeMerger = new JCodeMerger(jsFile);
		codeMerger.setMergeCode(mergeCode);
	}

	public static String lineBreakComment(String comment) {
		StringBuffer sb = new StringBuffer();
		appendln(sb, "//--------------------------%s--------------------------------", comment);
		return sb.toString();
	}

	public void jsMethod(String name, String params, String codes){
		appendln2(name + "("+params+") {");
		appendln2(codeMerger.getCodes(name, codes));
		appendln2("},");
	}
}
