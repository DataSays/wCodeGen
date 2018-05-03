package org.dataagg.codegen;

import java.io.IOException;

import org.dataagg.util.lang.IStringHelper;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import jodd.util.template.StringTemplateParser;

public class FtlConvert2JCoder implements IStringHelper {
	public void convert(String ftlFile) {
		try {
			String[] lines = FileUtil.readLines(ftlFile, "utf-8");
			String nameMergedCodes = null;
			for (String line : lines) {
				String line2 = line.trim();
				if (line2.startsWith("<#import") && line2.endsWith(">")) {
					continue;
				}
				if (line2.startsWith("<#") && line2.endsWith(">")) {
					if (line2.startsWith("<#if")) {
						line2 = StringUtil.replace(line, "<#if", "if(");
						line2 = cutRight(line2, ">");
						line2 += "){";
					} else if (line2.startsWith("<#--")) {
						line2 = StringUtil.replace(line, "<#--", "/** ");
						line2 = cutRight(line2, "-->");
						line2 += " **/";
					} else if (line2.startsWith("<#elseif")) {
						line2 = StringUtil.replace(line, "<#elseif", "}else if(");
						line2 = cutRight(line2, ">");
						line2 += "){";
					} else if (line2.startsWith("<#else")) {
						line2 = StringUtil.replace(line, "<#else", "}else {");
						line2 = cutRight(line2, ">");
						line2 += "";
					} else if (line2.startsWith("<#assign")) {
						line2 = StringUtil.replace(line, "<#assign", "String ");
						line2 = cutRight(line2, "/>");
						line2 = cutRight(line2, ">");
						line2 += ";";
					} else if (line2.startsWith("<#list")) {
						line2 = StringUtil.replace(line, "<#list", "for(");
						line2 = cutRight(line2, ">");
						line2 += "){";

						line2 = StringUtil.replace(line2, "uiDef.defs as item", "UIItemDef item:uiDef.defs");
					}
					line2 = StringUtil.replace(line2, "\"", "\\\"");
					line2 = StringUtil.replace(line2, "'", "\"");
					System.out.println(line2);
					continue;
				} else if (line2.indexOf("</#") >= 0) {
					line2 = StringUtil.replace(line2, "</#if>", "}");
					line2 = StringUtil.replace(line2, "</#list>", "}");
					line2 = StringUtil.replace(line2, "</#assign>", ";");
					System.out.println(line2);
					continue;
				} else if (line2.indexOf("${JavaImports!''}") >= 0) {
					System.out.println("coder.markImportCodeOffset();");
					continue;
				} else if (line2.indexOf("<@c.JavaCodes codes=JavaCodes key='_CustomFields' />") >= 0) {
					System.out.println("//定制部分代码");
					System.out.println("coder.insertMergedCodes(\"_CustomFields\");");
					continue;
				} else if (line2.indexOf("<@c.JavaCodes codes=JavaCodes key='_CustomMethods' />") >= 0) {
					System.out.println("//定制部分代码");
					System.out.println("coder.insertMergedCodes(\"_CustomMethods\");");
					continue;
				} else if (line2.indexOf("<@c.JavaCodes codes=JavaCodes key='_CustomFields'>") >= 0) {
					nameMergedCodes = "_CustomFields";
					System.out.println("//定制部分代码");
					System.out.println("coder.startMergedCodes(\"_CustomFields\");");
					continue;
				} else if (line2.indexOf("<@c.JavaCodes codes=JavaCodes key='_CustomMethods'>") >= 0) {
					nameMergedCodes = "_CustomMethods";
					System.out.println("//定制部分代码");
					System.out.println("coder.startMergedCodes(\"_CustomMethods\");");
					continue;
				} else if (line2.indexOf("</@c.JavaCodes>") >= 0) {
					System.out.println("coder.endMergedCodes(\"" + nameMergedCodes + "\");");
					continue;
				} else {
					line2 = line;
				}
				//				final StringBuffer sbParams = new StringBuffer();
				//				line2 = new StringTemplateParser().parse(line2, (in) -> {
				//					sbParams.append(in + ", ");
				//					return "%s";
				//				});
				//				if (sbParams.length() > 0) {
				//					line2 = String.format("coder.appendln2(\"%s\", %s);", line2, StringUtil.cutSuffix(sbParams.toString(), ", "));
				//				} else {
				line2 = StringUtil.replace(line2, "\"", "\\\"");
				line2 = String.format("appendCode(\"%s\");", line2);
				//				}
				System.out.println(line2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		FtlConvert2JCoder ftlConvert2JCoder = new FtlConvert2JCoder();
		ftlConvert2JCoder.convert("..\\codegen\\ui\\DataGridList.Vue.ftl");
	}
}
