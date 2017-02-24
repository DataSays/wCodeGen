package io.github.datasays.util;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;

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

	public CodeGenHelper append(String line) {
		codeBuff.append(line);
		return this;
	}

	public CodeGenHelper appendln(String line) {
		codeBuff.append(line);
		codeBuff.append("\r\n");
		return this;
	}

	public CodeGenHelper appendln2(String line) {
		codeBuff.append(indent());
		codeBuff.append(line);
		codeBuff.append("\r\n");
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
		return StringUtil.repeat("\t", indent);
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
