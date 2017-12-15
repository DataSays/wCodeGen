package org.datasays.util.codegen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.io.FileUtil;

public class JavaCodeMerger {
	private static final Logger LOG = LoggerFactory.getLogger(JavaCodeMerger.class);
	public static final String ImportTag = "//##JavaCodeMerger.import";
	public static final String JavaCodeTag = "//##JavaCodeMerger.code";
	private static final String N = "\r\n";
	private List<String> importCls;
	private List<String> javaCodes;

	public static JavaCodeMerger parseFile(String file) {
		JavaCodeMerger javaCodeMerger = new JavaCodeMerger();
		javaCodeMerger.parseJavaFile(file);
		return javaCodeMerger;
	}

	private void init() {
		if (importCls == null) {
			importCls = new ArrayList<>();
		} else {
			importCls.clear();
		}
		if (javaCodes == null) {
			javaCodes = new ArrayList<>();
		} else {
			javaCodes.clear();
		}
	}

	private void parseJavaFile(String file) {
		try {
			init();
			String[] lines = FileUtil.readLines(file, "utf-8");
			StringBuffer sbCode = new StringBuffer();
			boolean inCode = false;
			int javaCodeIndex = -1;
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				String tinyLine = line.trim();
				if (tinyLine.startsWith(ImportTag)) {
					String cls = tinyLine.substring(ImportTag.length());
					cls = cls.trim();
					if (cls.length() < 1) {
						//goto next line and parse import cls
						line = lines[++i];
						tinyLine = line.trim();
						if (tinyLine.startsWith("import ") && tinyLine.endsWith(";")) {
							cls = tinyLine.substring(7, tinyLine.length() - 1).trim();
						}
					}
					if (cls.length() > 0) {
						importCls.add(0, cls);
					}
				} else if (tinyLine.startsWith(JavaCodeTag)) {
					if (!inCode) {//JavaCodeTag start
						inCode = true;
						//如果以JavaCodeTag:([0-9]+)结尾则说明指定了javaCodeIndex
						if (tinyLine.length() > JavaCodeTag.length() + 1) {
							try {
								String indexText = tinyLine.substring(JavaCodeTag.length() + 1).trim();
								javaCodeIndex = Integer.parseInt(indexText);
							} catch (Exception e) {}
						}
					} else {//JavaCodeTag end
						inCode = false;
						if (javaCodeIndex > 0 && javaCodeIndex < javaCodes.size()) {
							String codes = javaCodes.remove(javaCodeIndex);
							codes += N + sbCode.toString();
							javaCodes.add(javaCodeIndex, codes);
						} else {
							javaCodes.add(sbCode.toString());
						}
						sbCode = new StringBuffer();
						javaCodeIndex = -1;
					}
				} else if (inCode) {
					sbCode.append(line + N);
				}
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage(), e);
		}
	}

	public String getImportCodes() {
		StringBuffer sbCode = new StringBuffer();
		if (importCls.size() > 0) {
			Collections.reverse(importCls);
			importCls.stream().distinct().forEach(importcls -> {
				sbCode.append(String.format("%s%nimport %s;%n", JavaCodeMerger.ImportTag, importcls, importcls));
			});
		}
		return sbCode.toString();
	}

	public String getJavaCodes(int index) {
		StringBuffer sbCode = new StringBuffer();
		if (javaCodes.size() > 0 && index < javaCodes.size()) {
			String javaCode = javaCodes.get(index);
			javaCode = N + JavaCodeMerger.JavaCodeTag + ":" + index + N + javaCode;
			javaCode += JavaCodeMerger.JavaCodeTag + N;
			sbCode.append(javaCode);
		}
		return sbCode.toString();
	}

	public String[] getAllJavaCodes(int size) {
		String[] codes = new String[size];
		for (int i = 0; i < size; i++) {
			codes[i] = getJavaCodes(i);
		}
		return codes;
	}
}
