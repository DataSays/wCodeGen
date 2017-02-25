package io.github.datasays.codeGen2;

import io.github.datasays.util.CodeGenHelper;
import jodd.io.FileUtil;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by watano on 2017/2/24.
 */
public class GradleGen extends FtlCodeGen2 {
	private static final Logger LOG = LoggerFactory.getLogger(GradleGen.class);
	private CodeGenHelper codeGenHelper = null;

	public void init() {
		super.init();
		//init codeGenHelper
		if (codeGenHelper == null) {
			codeGenHelper = new CodeGenHelper();
		}
		codeGenHelper.init();
	}

	public void gen() {
		try {
			if (data.has("subProjects")) {
				//gen main project file
				model.setv("type", "mainProject");
				NutMap subProjects = data.getAs("subProjects", NutMap.class);
				fmHelper.process(genType + ".ftl", model);
				formatGradle(workDir + "/build.gradle");

				//gen sub project files
				model.setv("type", "subProject");
				for (String subProjectName : subProjects.keySet()) {
					model.setv("subProjectName", subProjectName);
					fmHelper.process(genType + ".ftl", model);
					formatGradle(workDir + "/" + subProjectName + "/build.gradle");
				}

				//gen project.md
				codeGenHelper.init();
				codeGenHelper.appendln("# "+data.getString("group")+":"+data.getString("project")+":"+data.getString("version"));
				codeGenHelper.appendln(""+data.getString("description"));
				codeGenHelper.appendln("");
				String graph = "";//data.getString("project")+ "["+data.getString("description")+"]\n";
				for (String subProjectName : subProjects.keySet()) {
					NutMap subProject = subProjects.getAs(subProjectName, NutMap.class);
					graph += subProjectName+ "["+subProjectName+"]\n";
					codeGenHelper.appendln("## "+data.getString("group")+":"+subProjectName+":"+subProject.getString("version", data.getString("version")));
					codeGenHelper.appendln(""+subProject.getString("description"));
					codeGenHelper.appendln("");
					for(String dep: subProject.getList("deps", String.class)){
						dep = dep.trim();
						if(dep.startsWith("compile project(")){
							dep = dep.substring("compile project(".length()+2, dep.length()-2);
							graph += subProjectName+"-->"+dep+ "["+dep+"]\n";
						}
						codeGenHelper.appendln("+ "+dep);
					}
					codeGenHelper.appendln("");
				}
//				codeGenHelper.appendln("# 项目依赖");
//				codeGenHelper.appendln("```graphLR");
//				codeGenHelper.appendln(graph+"```");
				graph = "%% "+data.getString("project")+"\ngraph LR\n"+graph;
				//FileUtil.writeString(workDir + "/project.mmd", graph);
				codeGenHelper.writeFile(workDir + "/project.md");
			} else {
				fmHelper.process(genType + ".ftl", model);
				formatGradle(workDir + "/build.gradle");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
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
				}else{
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
