package org.datasays.util.codegen.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.datasays.util.codegen.CodeGenUtils;
import org.datasays.util.collection.StrObj;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

public class DocTpl {
	private static final Logger LOG = LoggerFactory.getLogger(DocTpl.class);

	private String docName;
	private String docId;
	private String tplCode;
	private Set<String> strVals = new HashSet<>();
	private Map<String, Set<String>> lstVals = new HashMap<>();

	public void parseTpl(String code) throws IOException {
		tplCode = code;
		//3. 解析模版字符串中的{{t[0-9]+_.*}}和{{.*}}分别填入strVals和lstVals
		Pattern p = Pattern.compile("\\{\\{[a-zA-Z0-9_]*\\}\\}");
		Matcher m = p.matcher(tplCode);
		int totalOffset = 0;
		while (m.find()) {
			int offset = 0;
			String var = m.group();
			offset = var.length();
			String newVar = "";
			var = StringUtil.cutPrefix(var, "{{");
			var = StringUtil.cutSuffix(var, "}}");
			var = var.trim();
			if (var.startsWith("t")) {
				Set<String> lval = lstVals.get(var);
				if (lval == null) {
					lval = new HashSet<>();
				}
				newVar = var + "_" + lval.size();
				lval.add(newVar);
				lstVals.put(var, lval);
			} else if (var.length() > 0) {
				newVar = var;
				strVals.add(newVar);
				continue;
			} else {
				newVar = "v" + strVals.size();
				strVals.add(newVar);
			}
			offset = newVar.length() + 4 - offset;
			String sCode = tplCode.substring(0, m.start() + totalOffset);
			String eCode = tplCode.substring(m.end() + totalOffset);
			tplCode = sCode + "{{" + newVar + "}}" + eCode;
			totalOffset += offset;
		}
		LOG.info(StringUtil.join(strVals, ";"));
		LOG.info(lstVals.toString());
		//		LOG.info(tplCode);
	}

	/**
	 * 解析模版文件,生成对应的编辑表单vue页面
	 * @param docTypeId
	 */
	public void genDocForm() {
		String htmlCodes = tplCode;
		for (String var : strVals) {
			htmlCodes = StringUtil.replace(htmlCodes, "{{" + var + "}}", "<el-input v-model=\"entity.vals." + var + "\"></el-input>");
		}

		StrObj params = new StrObj("docTpl", this, "htmlCodes", htmlCodes);
		CodeGenUtils.genFtlCode("..\\codegen\\document\\DocEditForm.ftl", params, "..\\..\\pscWeb\\src\\views\\psc\\document\\", docId + "Edit.vue");
	}

	/**
	 * 解析模版文件,生成对应的文书阅览freemarker模版
	 * @param docTypeId
	 */
	public void genDocView() {

	}

	public static void main(String[] args) {
		try {
			DocTpl docTpl = new DocTpl();
			docTpl.docName = "云南省农村土地承包经营权出租合同";
			docTpl.docId = "doc001";
			String tplFile = "..\\.doc\\template\\html\\" + docTpl.docName + ".html";
			String code = FileUtil.readString(tplFile, "utf-8");
			docTpl.parseTpl(code);
			FileUtil.writeString("..\\.doc\\template\\html\\" + docTpl.docId + ".htm", docTpl.tplCode);

			docTpl.genDocForm();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public String getTplCode() {
		return tplCode;
	}

	public void setTplCode(String tplCode) {
		this.tplCode = tplCode;
	}

	public Set<String> getStrVals() {
		return strVals;
	}

	public void setStrVals(Set<String> strVals) {
		this.strVals = strVals;
	}

	public Map<String, Set<String>> getLstVals() {
		return lstVals;
	}

	public void setLstVals(Map<String, Set<String>> lstVals) {
		this.lstVals = lstVals;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
}
