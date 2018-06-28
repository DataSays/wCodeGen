package org.dataagg.codegen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dataagg.codegen.util.CodeGenHelper;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.io.FileUtil;

/**
 * version: string
 * archiveName: string
 * description: string
 * applyFrom: string
 * plugins: [string...]
 * deps: [string...]
 * dependencyManagement: [string...]
 * fatJar: string
 * GradleJavaTask: [string mainCls, String workingDir, String args]
 * ExtCodes: string
 *
 * Created by watano on 2017/2/24.
 */
public class GradleGen extends FtlCodeGen2 {
	private static final Logger LOG = LoggerFactory.getLogger(GradleGen.class);
	private CodeGenHelper codeGenHelper = null;

	@Override
	public void init() {
		super.init();
		//init codeGenHelper
		if (codeGenHelper == null) {
			codeGenHelper = new CodeGenHelper();
		}
		codeGenHelper.init();
	}

	@Override
	public void gen() {
		try {
			if (data.has("subProjects")) {
				//gen main project file
				model.put("type", "mainProject");
				StrObj subProjects = data.mapVal("subProjects");
				fmHelper.process(genType + ".ftl", model);
				formatGradle(workDir + "/build.gradle");

				//gen sub project files
				model.put("type", "subProject");
				for (String subProjectName : subProjects.keySet()) {
					model.put("subProjectName", subProjectName);
					fmHelper.process(genType + ".ftl", model);
					formatGradle(workDir + "/" + subProjectName + "/build.gradle");
				}

				//gen project.md
				codeGenHelper.init();
				codeGenHelper.appendln("# " + data.strVal("group") + ":" + data.strVal("project") + ":" + data.strVal("version"));
				codeGenHelper.appendln("" + data.strVal("description"));
				codeGenHelper.appendln("");
				String graph = "";//data.getString("project")+ "["+data.getString("description")+"]\n";
				for (String subProjectName : subProjects.keySet()) {
					StrObj subProject = subProjects.mapVal(subProjectName);
					graph += subProjectName + "[" + subProjectName + "]\n";
					codeGenHelper.appendln("## " + data.strVal("group") + ":" + subProjectName + ":" + subProject.strVal("version", data.strVal("version")));
					codeGenHelper.appendln("" + subProject.strVal("description"));
					codeGenHelper.appendln("");
					for (String dep : subProject.strArrayVal("deps")) {
						dep = dep.trim();
						if (dep.startsWith("compile project(")) {
							dep = dep.substring("compile project(".length() + 2, dep.length() - 2);
							graph += subProjectName + "-->" + dep + "[" + dep + "]\n";
						}
						codeGenHelper.appendln("+ " + dep);
					}
					codeGenHelper.appendln("");
				}
				codeGenHelper.appendln("# 项目依赖");
				codeGenHelper.appendln("```graphLR");
				codeGenHelper.appendln(graph + "```");
				graph = "%% " + data.strVal("project") + "\ngraph LR\n" + graph;
				//FileUtil.writeString(workDir + "/project.mmd", graph);
				codeGenHelper.writeFile(workDir + "/project.md");

				//gen settings.gradle
				codeGenHelper.init();
				if (profiles != null && profiles.length > 0) {
					StrObj depInfo = new StrObj();
					for (String subProjectName : subProjects.keySet()) {
						StrObj subProject = subProjects.mapVal(subProjectName);
						for (String dep : subProject.strArrayVal("deps")) {
							dep = dep.trim();
							if (dep.startsWith("compile project(")) {
								dep = dep.substring("compile project(".length() + 2, dep.length() - 2);
								graph += subProjectName + "-->" + dep + "[" + dep + "]\n";
								depInfo.put(subProjectName, dep);
							}
						}
					}
					Set<String> lstDeps = new HashSet<>();
					for (String project : profiles) {
						lstDeps = addAllDeps(lstDeps, depInfo, project);
					}
					for (String dep : lstDeps) {
						codeGenHelper.appendln("include ':" + dep + "'");
					}
					codeGenHelper.writeFile(workDir + "/settings.gradle");
				}
			} else {
				fmHelper.process(genType + ".ftl", model);
				formatGradle(workDir + "/build.gradle");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public Set<String> addAllDeps(Set<String> lstDeps, StrObj depInfo, String project) {
		lstDeps.add(project);
		List<String> deps = null;
		Object o = depInfo.get(project);
		if (o instanceof List) {
			deps = depInfo.listVal(project, String.class);
		} else if (o != null) {
			deps = new ArrayList<>();
			deps.add(o.toString());
		} else {
			deps = new ArrayList<>();
		}
		for (String depName : deps) {
			if (!lstDeps.contains(depName)) {
				addAllDeps(lstDeps, depInfo, depName);
			}
		}
		return lstDeps;
	}

	public static void formatGradle(String file) {
		CodeGenHelper codeGenHelper = new CodeGenHelper();
		codeGenHelper.init();
		try {
			int countBlankLine = 0;
			for (String line : FileUtil.readLines(file)) {
				line = line.trim();
				//only write one blank line
				if (line.length() < 1) {
					if (countBlankLine == 0) {
						codeGenHelper.appendln("");
					}
					countBlankLine++;
					continue;
				} else {
					countBlankLine = 0;
				}
				//check indent
				if (line.endsWith("{")) {
					codeGenHelper.appendln2(line);
					codeGenHelper.beginIndent();
				} else if (line.endsWith("}")) {
					codeGenHelper.endIndent();
					codeGenHelper.appendln2(line);
				} else {
					codeGenHelper.appendln2(line);
				}
			}
			codeGenHelper.writeFile(file);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		try {
			GradleGen codegen = new GradleGen();
			codegen.init();
			codegen.genAll(args);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
