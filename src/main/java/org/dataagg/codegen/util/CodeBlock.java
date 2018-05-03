package org.dataagg.codegen.util;

import java.util.HashMap;
import java.util.Map;

public class CodeBlock {
	protected Map<String, StringBuffer> codeBlocks = new HashMap<>();
	protected StringBuffer currCodeBuffer;

	public CodeBlock codeKey(String codeKey) {
		if (codeKey == null) {
			codeKey = "_main_";
		}
		currCodeBuffer = codeBlocks.get(codeKey);
		if (currCodeBuffer == null) {
			currCodeBuffer = new StringBuffer();
			codeBlocks.put(codeKey, currCodeBuffer);
		}
		return this;
	}

	public CodeBlock appendPlainCode(String codes) {
		currCodeBuffer.append(codes);
		return this;
	}

	public CodeBlock appendFormatCode(String codes, Object... args) {
		return appendPlainCode(String.format(codes, args));
	}

	public int offset() {
		return currCodeBuffer.length() - 1;
	}

	public CodeBlock insertPlainCode(int start, String text) {
		currCodeBuffer.insert(start, text);
		return this;
	}

	public CodeBlock insertFormatCode(int start, String codes, Object... args) {
		currCodeBuffer.insert(start, String.format(codes, args));
		return this;
	}

	public String codes(String codeKey, String defaultVal) {
		if (codeKey == null) {
			codeKey = "_main_";
		}
		StringBuffer sb = codeBlocks.get(codeKey);
		return sb != null ? sb.toString() : defaultVal;
	}

	public void put(String codeKey, String codes) {
		if (codeKey == null) {
			codeKey = "_main_";
		}
		StringBuffer sb = new StringBuffer(codes);
		codeBlocks.put(codeKey, sb);
	}

	public void startBlock(String codeKey) {
		codeKey(codeKey);
	}

	public void endBlock() {
		codeKey(null);
	}

	public int blockSize(String codeKey) {
		if (codeBlocks.get(codeKey) != null) { return codeBlocks.get(codeKey).length(); }
		return -1;
	}
}
