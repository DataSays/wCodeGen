package org.dataagg.codegen;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.dataagg.util.FindFileUtil;
import org.dataagg.util.WJsonUtils;
import org.dataagg.util.freemarker.FreemarkerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import jodd.io.FileNameUtil;

/*
	gen code from freemarker ftl file and json data.
 */
public class FtlCodeGen {
	private static final String Ftl = ".ftl";
	private static final String Html = ".html";
	private static final String Js = ".js";
	private static final String Json = ".json";

	private static final Logger LOG = LoggerFactory.getLogger(FtlCodeGen.class);
	private File commonDir = null;
	private File sourceDir = null;
	private File targetDir = null;

	private FreemarkerHelper fmHelper = null;

	public void init(String common, String source, String target) {
		commonDir = new File(common);
		sourceDir = new File(source);
		targetDir = new File(target);
		fmHelper = new FreemarkerHelper();
		fmHelper.init();
		fmHelper.setTemplateLoadingDir(commonDir, sourceDir);
	}

	public void gen(String sourceFile) {
		try {
			LOG.info("gen " + sourceFile);
			Map<String, Object> model = new Hashtable<>();
			//get Common model
			Iterator<File> iterator = FindFileUtil.search(true, false, commonDir);
			while (iterator.hasNext()) {
				File f = iterator.next();
				if (f.getName().endsWith(Json)) {
					model.putAll(loadJson(f.getAbsolutePath()));
				}
			}

			String sfile = sourceDir.getAbsolutePath() + File.separator + sourceFile;
			String tfile = targetDir.getAbsolutePath() + File.separator + sourceFile;
			//get Ftl Json model
			model.putAll(loadJson(sfile + Json));

			//process html,js ftl
			if (new File(sfile + Html + Ftl).exists()) {
				fmHelper.process(sourceFile + Html + Ftl, model, tfile + Html);
			}
			if (new File(sfile + Js + Ftl).exists()) {
				fmHelper.process(sourceFile + Js + Ftl, model, tfile + Js);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	//重新生成所有文件
	public void updateAll() {
		LOG.info("update All " + "*" + Ftl + " on " + sourceDir.getAbsolutePath());
		Iterator<File> iterator = FindFileUtil.search(true, false, sourceDir);
		while (iterator.hasNext()) {
			File f = iterator.next();
			if (f.getName().endsWith(Ftl)) {
				gen(getRelativePath(f.getAbsolutePath()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> loadJson(String file) {
		LOG.info("loadJson:" + file);
		Map<String, Object> data = null;
		try {
			data = (Map<String, Object>) WJsonUtils.fromJson(new File(file)).map();
		} catch (Exception e) {
			if (!(e instanceof JsonSyntaxException && e.getMessage().contains("Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $"))) {
				LOG.error(e.getMessage(), e);
			}
		}
		if (data == null) {
			data = new Hashtable<>();
		}
		return data;
	}

	//获取文件的相对路径和主文件名
	private String getRelativePath(String absolutePath) {
		String fileName = absolutePath.trim();
		fileName = FileNameUtil.normalize(fileName);
		String source = FileNameUtil.normalize(sourceDir.getAbsolutePath().trim());
		fileName = fileName.substring(source.length() + 1);
		fileName = fileName.substring(0, fileName.length() - Ftl.length());
		if (fileName.endsWith(Html)) {
			fileName = fileName.substring(0, fileName.length() - Html.length());
		} else if (fileName.endsWith(Js)) {
			fileName = fileName.substring(0, fileName.length() - Js.length());
		}
		return fileName;
	}

	public void dispatch(String cmd) {
		if ("updateAll".equalsIgnoreCase(cmd) || "up".equalsIgnoreCase(cmd)) {
			updateAll();
		} else if ("exit".equalsIgnoreCase(cmd) || "ex".equalsIgnoreCase(cmd)) {
			System.exit(0);
		} else {
			gen(cmd);
		}
	}
}
