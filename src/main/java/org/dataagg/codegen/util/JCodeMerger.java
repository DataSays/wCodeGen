package org.dataagg.codegen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dataagg.codegen.base.ACodeMerger;

public class JCodeMerger extends ACodeMerger {
	public static final String ImportTag = "//##CodeMerger.import";
	private List<String> importCls;

	public JCodeMerger(String file) {
		super(file);
	}

	@Override
	protected void init() {
		super.init();
		if (importCls == null) {
			importCls = new ArrayList<>();
		} else {
			importCls.clear();
		}
	}

	public String getImportCodes() {
		StringBuffer sbCode = new StringBuffer();
		if (importCls.size() > 0) {
			Collections.reverse(importCls);
			importCls.stream().distinct().forEach(importcls -> {
				sbCode.append(String.format("%s%nimport %s;%n", JCodeMerger.ImportTag, importcls, importcls));
			});
		}
		return sbCode.toString();
	}

	@Override
	protected boolean parseLineCode(Iterator<String> allLines, String line) {
		String tinyLine = line.trim();
		if (tinyLine.startsWith(ImportTag)) {
			String cls = tinyLine.substring(ImportTag.length());
			cls = cls.trim();
			if (cls.length() < 1) {
				//goto next line and parse import cls
				line = allLines.next();
				tinyLine = line.trim();
				if (tinyLine.startsWith("import ") && tinyLine.endsWith(";")) {
					cls = tinyLine.substring(7, tinyLine.length() - 1).trim();
				}
			}
			if (cls.length() > 0) {
				importCls.add(0, cls);
			}
			return true;
		}
		return false;
	}
}
