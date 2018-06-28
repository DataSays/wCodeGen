package org.dataagg.codegen.model;

import java.util.ArrayList;
import java.util.List;

import org.dataagg.codegen.util.YmlGenHelper;
import org.dataagg.util.collection.StrObj;

/**
 * Created by watano on 2017/3/4.
 */
public class GradleProject {
	public String group;
	public String project;
	public String version;
	public String archiveName;
	public String description;
	public String applyFrom;
	public String[] plugins;
	public String[] deps;
	public String[] dependencyManagement;
	public String fatJar;
	public String[][] GradleJavaTask;//: [string mainCls, String workingDir, String args]
	public String ExtCodes;

	public GradleProject() {}

	public GradleProject(StrObj data) {
		this();
		fill(data);
	}

	@SuppressWarnings("unchecked")
	public void fill(StrObj data) {
		group = data.strVal("group");
		project = data.strVal("project");
		version = data.strVal("version");
		archiveName = data.strVal("archiveName");
		plugins = data.strArrayVal("plugins");
		deps = data.strArrayVal("deps");
		dependencyManagement = data.strArrayVal("dependencyManagement");
		description = data.strVal("description");
		applyFrom = data.strVal("applyFrom");
		fatJar = data.strVal("fatJar");
		ExtCodes = data.strVal("ExtCodes");
		if (data.has("GradleJavaTask")) {
			List<List<String>> lstGradleJavaTasks = new ArrayList<>();
			List<?> gradleJavaTasks = data.listVal("GradleJavaTask", List.class);
			for (Object gradleJavaTask : gradleJavaTasks) {
				lstGradleJavaTasks.add((List<String>) gradleJavaTask);
			}
			GradleJavaTask = lstGradleJavaTasks.toArray(new String[][] {});
		}

	}

	public void write(YmlGenHelper gradleGen) {
		gradleGen.setNotNull("group", group);
		gradleGen.setNotNull("project", project);
		gradleGen.set("version", version);
		gradleGen.set("description", description);
		gradleGen.setNotNull("applyFrom", applyFrom);
		gradleGen.setNotNull("archiveName", archiveName);
		//plugin
		if (plugins == null) {
			plugins = new String[] {};
		}
		gradleGen.inlineList("plugins", (Object[]) plugins);
		//deps
		if (deps != null && deps.length > 0) {
			gradleGen.addSetData2("deps", (Object[]) deps);
		}
		//dependencyManagement
		if (dependencyManagement != null && dependencyManagement.length > 0) {
			gradleGen.addSetData2("dependencyManagement", (Object[]) dependencyManagement);
		}
		gradleGen.setNotNull("fatJar", fatJar);
		if (GradleJavaTask != null && GradleJavaTask.length > 0) {
			gradleGen.beginLst("GradleJavaTask");
			for (String[] taskInfo : GradleJavaTask) {
				gradleGen.addLst(gradleGen.getInlineLst((Object[]) taskInfo));
			}
			gradleGen.endLst();
		}
		gradleGen.setNotNull("ExtCodes", ExtCodes);
	}
}
