package org.dataagg.codegen.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.dataagg.codegen.base.ACodeMerger;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

/**
 * Created by watano on 2017/2/7.
 */
public class CodeGenHelper {
	protected String codeFile;
	protected StringBuffer codeBuff = null;
	protected String encoding = "utf-8";
	protected int indent = 0;
	protected StringBuffer tmpCoderBuffer;
	protected ACodeMerger codeMerger;

	public CodeGenHelper() {
		init();
	}

	public CodeGenHelper(String codeFile) {
		this.codeFile = codeFile;
		init();
	}

	@Nonnull
	public static String capFirst(@Nonnull String field) {
		return field.substring(0, 1).toUpperCase() + field.substring(1);
	}

	@Nonnull
	public static String uncapFirst(@Nonnull String field) {
		return field.substring(0, 1).toLowerCase() + field.substring(1);
	}

	public static void append(@Nonnull StringBuffer sb, @Nonnull String line, @Nullable Object... args) {
		sb.append(String.format(line, args));
	}

	public static void appendln(@Nonnull StringBuffer sb, @Nonnull String line, @Nullable Object... args) {
		sb.append(String.format(line + "%n", args));
	}

	@Nonnull
	public static String joinPrefix(@Nonnull String prefix, @Nullable String... texts) {
		String outText = "";
		if (texts != null) {
			for (String inter : texts) {
				if (StringUtil.isNotBlank(inter)) {
					outText += prefix + inter.trim();
				}
			}
			outText = StringUtil.cutPrefix(outText, prefix);
		}
		return outText;
	}

	@Nonnull
	public static String joinSuffix(@Nonnull String suffix, @Nullable String... texts) {
		String outText = "";
		if (texts != null) {
			for (String inter : texts) {
				if (StringUtil.isNotBlank(inter)) {
					outText += inter.trim() + suffix;
				}
			}
			outText = StringUtil.cutSuffix(outText, suffix);
		}
		return outText;
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

	@Nonnull
	public CodeGenHelper init() {
		codeBuff = new StringBuffer();
		return this;
	}

	@Nonnull
	public CodeGenHelper encoding(@Nonnull String encoding) {
		this.encoding = encoding;
		return this;
	}

	@Nonnull
	public CodeGenHelper appendLines(@Nullable String... lines) {
		if (lines != null) {
			for (String line : lines) {
				appendln("%s", line);
			}
		}
		return this;
	}

	@Nonnull
	public CodeGenHelper append(@Nonnull String line, @Nullable Object... args) {
		append(codeBuff, line, args);
		return this;
	}

	@Nonnull
	public CodeGenHelper appendln(@Nonnull String line, @Nullable Object... args) {
		appendln(codeBuff, line, args);
		return this;
	}

	@Nonnull
	public CodeGenHelper appendln2(@Nonnull String line, @Nullable Object... args) {
		codeBuff.append(indent());
		appendln(codeBuff, line, args);
		return this;
	}

	@Nonnull
	public CodeGenHelper beginIndent() {
		indent++;
		return this;
	}

	@Nonnull
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
		return codeBuff.length() - 1;
	}

	public CodeGenHelper insert(int start, String text) {
		codeBuff.insert(start, text);
		return this;
	}

	public String getCode() {
		return codeBuff.toString();
	}

	public void writeFile(String outFilePath) throws IOException {
		File outFile = new File(outFilePath);
		if (!outFile.getParentFile().exists()) {
			FileUtil.mkdirs(outFile.getParentFile());
		}
		String code = codeBuff.toString();
		code = rmDuplicateEmptyLine(code);
		FileUtil.writeString(outFile, code, encoding);
	}

	/**
	 * 插入空的可合并的代码块
	 * @param key
	 */
	public void insertMergedCodes(String key) {
		appendln("%s", codeMerger.getCodes(key, ""));
	}

	/**
	 * 标记可合并的代码块开始
	 * @param key
	 */
	public void startMergedCodes(String key) {
		tmpCoderBuffer = codeBuff;
		codeBuff = new StringBuffer();
	}

	/**
	 * 标记可合并的代码块结束
	 * @param key
	 */
	public void endMergedCodes(String key) {
		String codes = codeMerger.getCodes(key, codeBuff.toString());
		codeBuff = tmpCoderBuffer;
		codeBuff.append(codes);
	}

	/**
	 * 把生成内容写入代码文件
	 * @throws IOException
	 */
	public void writeToFile() throws IOException {
		writeFile(codeFile);
	}
}
