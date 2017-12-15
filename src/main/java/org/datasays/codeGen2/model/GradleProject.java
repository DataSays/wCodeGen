package org.datasays.codeGen2.model;

import java.util.ArrayList;
import java.util.List;

import org.datasays.util.WMap;
import org.datasays.util.YmlGenHelper;

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

	public GradleProject() {
	}


	public GradleProject(WMap data) {
		this();
		fill(data);
	}

	public void fill(WMap data) {
		group = data.getString("group");
		project = data.getString("project");
		version = data.getString("version");
		archiveName = data.getString("archiveName");
		plugins = data.stringsNoDuplicate("plugins");
		deps = data.stringsNoDuplicate("deps");
		dependencyManagement = data.stringsNoDuplicate("dependencyManagement");
		description = data.getString("description");
		applyFrom = data.getString("applyFrom");
		fatJar = data.getString("fatJar");
		ExtCodes = data.getString("ExtCodes");
		if (data.has("GradleJavaTask")) {
			List<String[]> lstGradleJavaTasks = new ArrayList<>();
			String[][] gradleJavaTasks = data.getArray("GradleJavaTask", String[].class);
			for (String[] gradleJavaTask : gradleJavaTasks) {
				lstGradleJavaTasks.add(gradleJavaTask);
			}
			GradleJavaTask = lstGradleJavaTasks.toArray(new String[][]{});
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
			plugins = new String[]{};
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
