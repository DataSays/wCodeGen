package org.datasays.util.codegen;

import java.io.File;
import java.io.IOException;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

/**
 * Created by watano on 2017/2/7.
 */
public class CodeGenHelper {
	private StringBuffer codeBuff = null;
	private String encoding = "utf-8";
	protected int indent = 0;

	public CodeGenHelper() {
		init();
	}

	public CodeGenHelper init() {
		codeBuff = new StringBuffer();
		return this;
	}

	public CodeGenHelper encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public CodeGenHelper append(String line, Object... args) {
		codeBuff.append(String.format(line, args));
		return this;
	}

	public CodeGenHelper appendln(String line, Object... args) {
		codeBuff.append(String.format(line + "%n", args));
		return this;
	}

	public CodeGenHelper appendln2(String line, Object... args) {
		codeBuff.append(indent());
		codeBuff.append(String.format(line + "%n", args));
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
		FileUtil.writeString(outFile, codeBuff.toString(), encoding);
	}
}
