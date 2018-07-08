package org.dataagg.codegen;

import static jodd.util.StringUtil.capitalize;

import java.io.IOException;

import org.dataagg.codegen.model.GradleProject;
import org.dataagg.codegen.util.AYmlCodeGen;
import org.dataagg.codegen.util.YmlGenHelper;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.text.MapTplHelper;
import org.dataagg.util.text.YamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.io.FileUtil;
import jodd.util.ArraysUtil;
import jodd.util.StringUtil;

/**
 * Created by watano on 2017/2/6.
 */
public class ProjectGen extends AYmlCodeGen {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectGen.class);
	private YmlGenHelper gradleGen = new YmlGenHelper();

	@Override
	public void init() {
		super.init();
		gradleGen.init();
	}

	@Override
	public void gen() {
		try {
			//gen gradle.build for main project
			gradleGen.set("WorkDir", workDir);
			gradleGen.set("GenType", "gradle");
			gradleGen.inlineMap("props");
			gradleGen.comment(null);
			String appCode = props.strVal("appCode", "").trim();
			GradleProject mainProject = new GradleProject(data);
			mainProject.write(gradleGen);

			if (data.has("subProjects")) {
				gradleGen.beginMap("subProjects");
				StrObj subProjects = data.mapVal("subProjects");
				//gen docker-compose.yml
				StrObj dockerServices = new StrObj();
				for (String subProjectName : subProjects.keySet()) {
					StrObj subProject = subProjects.mapVal(subProjectName);
					props.put("_SubProject_", subProjectName);
					props.put("_SubProjectPort_", subProject.strVal("ports", ""));
					StrObj allComponents = YamlUtil.evalYml(data.strVal("Components", "./components.yml"), props);
					//merge Components into subProject.
					String[] thisComponents = subProject.strArrayVal("components");
					StrObj componetData = new StrObj();
					for (String componentName : thisComponents) {
						StrObj component;
						if (componentName.startsWith(":")) {
							component = subProjects.mapVal(componentName.substring(1));
							component.put("deps", new String[] { "compile project(':" + componentName.substring(1) + "')" });
						} else {
							component = allComponents.mapVal(componentName);
						}
						if (component == null) {
							LOG.error("can't find the component:" + componentName);
							continue;
						}
						componetData.mergeAll(component);
					}
					//merge Components into subProject.
					componetData.mergeAll(subProject);
					GradleProject subProjectData = new GradleProject(componetData);

					//gen gradle.build for sub project
					subProjectData.project = null;
					if (subProjectData.version == null) {
						subProjectData.version = data.strVal("version");
					}
					if (subProjectData.archiveName == null) {
						subProjectData.archiveName = subProjectName + ".jar";
					}
					if (subProjectData.description == null) {
						subProjectData.description = subProjectName;
					}
					gradleGen.beginMap(subProjectName);
					subProjectData.write(gradleGen);
					gradleGen.endMap();

					if (componetData.containsKey("config$")) {
						componetData.remove("config");
					}
					if (componetData.containsKey("testConfig$")) {
						componetData.remove("testConfig");
					}
					if (componetData.containsKey("docker$")) {
						componetData.remove("docker");
					}

					if (componetData.has("config") && componetData.mapVal("config").size() > 0) {
						YamlUtil.write(componetData.mapVal("config"), workDir + "/" + subProjectName + "/src/main/resources/application.yml", 2);
					}

					if (componetData.has("testConfig") && componetData.mapVal("testConfig").size() > 0) {
						YamlUtil.write(componetData.mapVal("testConfig"), workDir + "/" + subProjectName + "/src/test/resources/bootstrap.yml", 2);
					}

					if (componetData.has("docker") && componetData.mapVal("docker").size() > 0) {
						//gen docker-compose.yml
						dockerServices.put(subProjectName, componetData.mapVal("docker"));
					}

					//gen config for sub projects
					String srcPath = workDir + "/" + subProjectName + "/src";
					if (ArraysUtil.contains(thisComponents, "springBoot")) {
						YamlUtil.write(componetData.mapVal("config"), srcPath + "/main/resources/application.yml", 2);
						YamlUtil.write(componetData.mapVal("testConfig"), srcPath + "/test/resources/bootstrap.yml", 2);

						//gen docker-compose.yml
						//						dockerServices.put(subProjectName, componetData.map("docker"));

						//						WMap dockerCompose = new WMap();
						//						dockerCompose.put("version", "2");
						//						dockerCompose.put("services", dockerServices);
						//						YamlUtil.write(dockerCompose, "./docker-compose.yml");
					}

					//主项目生成对应的
					if (appCode.equals(subProjectName)) {
						String appPkg = props.strVal("appPkg", "").trim();
						appPkg = StringUtil.replace(appPkg, ".", "/");
						MapTplHelper mapTplHelper = new MapTplHelper();
						mapTplHelper.initModel(model);
						//{appCode}App.java
						writeByFile(mapTplHelper, "./tpls/App.java", srcPath + "/main/java/" + appPkg + "/" + capitalize(subProjectName) + "App.java");

						//{appCode}AppTests.java
						writeByFile(mapTplHelper, "./tpls/AppTests.java", srcPath + "/test/java/" + appPkg + "/" + capitalize(subProjectName) + "AppTests.java");
					}
				}
				gradleGen.endMap();
			}
			gradleGen.writeFile("./gradle.yml");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static void writeByFile(MapTplHelper mapTplHelper, String ftlFile, String outFile) throws IOException {
		//if (!new File(outFile).exists()) {
		String ftl = FileUtil.readString(ftlFile, "utf-8");
		String codes = mapTplHelper.parse(ftl);
		FileUtil.writeString(outFile, codes);
		//}
	}

	public static void main(String[] args) {
		try {
			ProjectGen codegen = new ProjectGen();
			codegen.init();
			codegen.genAll(args);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
