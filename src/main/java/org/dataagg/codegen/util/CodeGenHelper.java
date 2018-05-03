package org.dataagg.codegen.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dataagg.codegen.base.ACodeMerger;
import org.dataagg.util.lang.IStringHelper;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

/**
 * Created by watano on 2017/2/7.
 */
public class CodeGenHelper implements IStringHelper {
	protected String codeFile;
	protected String encoding = "utf-8";
	protected int indent = 0;
	protected ACodeMerger codeMerger;
	public CodeBlock codeBlock = new CodeBlock();

	public CodeGenHelper() {
		init();
	}

	public CodeGenHelper(String codeFile) {
		this.codeFile = codeFile;
		init();
	}

	public static void append(StringBuffer sb, String line, Object... args) {
		sb.append(String.format(line, args));
	}

	public static void appendln(StringBuffer sb, String line, Object... args) {
		sb.append(String.format(line + "%n", args));
	}

	/**
	 * 移除重复的多个空行
	 * @param lines
	 * @return
	 */
	public static String rmDuplicateEmptyLine(String lines) {
		String tmpLines = lines;
		Pattern p = Pattern.compile("([\\s\\r]*\\n){3,}");
		Matcher m = p.matcher(tmpLines);
		while (m.find()) {
			tmpLines = m.replaceAll("\n\n");
			m = p.matcher(tmpLines);
		}
		return tmpLines;
	}

	public CodeGenHelper init() {
		codeBlock.codeKey(null);
		return this;
	}

	public CodeGenHelper encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public CodeGenHelper appendLines(String... lines) {
		if (lines != null) {
			for (String line : lines) {
				appendln("%s", line);
			}
		}
		return this;
	}

	public CodeGenHelper append(String line, Object... args) {
		codeBlock.appendFormatCode(line, args);
		return this;
	}

	public CodeGenHelper appendln(String line, Object... args) {
		codeBlock.appendFormatCode(line+"%n", args);
		return this;
	}

	public CodeGenHelper appendln2(String line, Object... args) {
		appendln(indent()+line, args);
		return this;
	}

	public CodeGenHelper beginIndent() {
		indent++;
		return this;
	}

	public CodeGenHelper endIndent() {
		indent--;
		if (indent < 0) {
			indent = 0;
		}
		return this;
	}

	protected String indent() {
		if (indent <= 0) { return ""; }
		return StringUtil.repeat(indentStr(), indent);
	}

	public String indentStr() {
		return "\t";
	}

	public int offset() {
		return codeBlock.offset();
	}

	public CodeGenHelper insert(int start, String text) {
		codeBlock.insertPlainCode(start, text);
		return this;
	}

	public String getCode() {
		return codeBlock.codes(null, null);
	}

	public void writeFile(String outFilePath) throws IOException {
		File outFile = new File(outFilePath);
		if (!outFile.getParentFile().exists()) {
			FileUtil.mkdirs(outFile.getParentFile());
		}
		String code = getCode();
		code = rmDuplicateEmptyLine(code);
		FileUtil.writeString(outFile, code, encoding);
	}

	/**
	 * 插入空的可合并的代码块
	 * @param key
	 */
	public void insertMergedCodes(String key) {
		appendln(codeMerger.getCodes(key, codeBlock.codes(key, "")));
	}

	/**
	 * 标记可合并的代码块开始
	 * @param key
	 */
	public void startMergedCodes(String key) {
		codeBlock.codeKey(key);
	}

	/**
	 * 标记可合并的代码块结束
	 * @param key
	 */
	public void endMergedCodes(String key) {
		String codes = codeMerger.getCodes(key, codeBlock.codes(key, null));
		codeBlock.codeKey(null);
		codeBlock.appendPlainCode(codes);
	}

	/**
	 * 把生成内容写入代码文件
	 * @throws IOException
	 */
	public void writeToFile() throws IOException {
		writeFile(codeFile);
	}
}
