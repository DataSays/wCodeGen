package org.dataagg.codegen.base;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dataagg.codegen.util.CodeGenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

public abstract class ACodeMerger {
	private static final Logger LOG = LoggerFactory.getLogger(ACodeMerger.class);
	protected String CodeTag = "//##CodeMerger.code";
	protected static final String N = "\r\n";
	protected Map<String, String> codes;
	protected boolean mergeCode = true;

	public ACodeMerger(String file) {
		init();
		if (new File(file).exists()) {
			parseFile(file);
		}
	}

	protected void init() {
		if (codes == null) {
			codes = new HashMap<>();
		} else {
			codes.clear();
		}
	}

	public void setMergeCode(boolean mergeCode) {
		this.mergeCode = mergeCode;
	}

	public void parseCodes(String... lines) {
		try {
			Iterator<String> allLines = Arrays.asList(lines).iterator();
			StringBuffer sbCode = null;
			boolean inCode = false;
			String codeKey = "";
			while (allLines.hasNext()) {
				String line = allLines.next();
				String tinyLine = line.trim();
				if (parseLineCode(allLines, line)) {

				} else if (tinyLine.startsWith(CodeTag)) {
					if (!inCode) {//CodeTag start
						sbCode = new StringBuffer();
						inCode = true;
						//如果以CodeMerger.code:(.+)结尾则说明指定了javaCodeIndex
						if (tinyLine.length() > CodeTag.length() + 1) {
							try {
								codeKey = tinyLine.substring(CodeTag.length() + 1).trim();
							} catch (Exception e) {}
						}
					} else {//CodeTag end
						if (sbCode == null) {
							sbCode = new StringBuffer();
						}
						inCode = false;

						appendCode(codeKey, sbCode.toString());
						sbCode = new StringBuffer();
					}
				} else if (inCode) {
					if (sbCode == null) {
						sbCode = new StringBuffer();
					}
					sbCode.append(line + N);
				}
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage(), e);
		}
	}

	public void parseFile(String file) {
		try {
			String[] lines = FileUtil.readLines(file, "utf-8");
			parseCodes(lines);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected abstract boolean parseLineCode(Iterator<String> allLines, String line);

	protected void appendCode(String codeKey, String code) {
		String tmpCode = codes.get(codeKey);
		//如果codeKey对应的部分已经有代码, 则合并代码
		if (tmpCode != null) {
			tmpCode = CodeGenHelper.rmDuplicateEmptyLine(tmpCode);
			tmpCode += N + code;
		} else {
			tmpCode = code;
		}
		tmpCode = CodeGenHelper.rmDuplicateEmptyLine(tmpCode);
		codes.put(codeKey, tmpCode);
	}

	public String getCodes(String codeKey, String defaultCodes) {
		StringBuffer sbCode = new StringBuffer();
		String code = defaultCodes;
		if (mergeCode) {
			code = codes.get(codeKey);
			if (StringUtil.isBlank(code)) {
				code = defaultCodes;
			}
		}
		code = StringUtil.cutSuffix(code, N);
		code = N + CodeTag + ":" + codeKey + N + code + N;
		code += CodeTag + N;
		sbCode.append(code);
		return sbCode.toString();
	}

	public Map<String, String> getAllCodes() {
		return codes;
	}
}
