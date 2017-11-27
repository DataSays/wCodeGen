package io.github.datasays.codeGen2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.datasays.codeGen2.model.GradleProject;
import io.github.datasays.util.WMap;
import io.github.datasays.util.YamlUtil;
import io.github.datasays.util.YmlGenHelper;
import jodd.util.ArraysUtil;

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
			GradleProject mainProject = new GradleProject(data);
			mainProject.write(gradleGen);

			if (data.has("subProjects")) {
				gradleGen.beginMap("subProjects");
				WMap subProjects = data.getAs("subProjects", WMap.class);
				//gen docker-compose.yml
				WMap dockerServices = new WMap();
				for (String subProjectName : subProjects.keySet()) {
					WMap subProject = subProjects.getAs(subProjectName, WMap.class);
					props.setv("_SubProject_", subProjectName);
					props.setv("_SubProjectPort_", subProject.getString("ports", ""));
					WMap allComponents = YamlUtil.evalYml(data.getString("Components", "./components.yml"), props);
					//merge Components into subProject.
					String[] thisComponents = subProject.getArray("components", String.class, new String[] {});
					WMap componetData = new WMap();
					for (String componentName : thisComponents) {
						WMap component;
						if (componentName.startsWith(":")) {
							component = subProjects.getAs(componentName.substring(1), WMap.class);
							component.put("deps", new String[] { "compile project(':" + componentName.substring(1) + "')" });
						} else {
							component = allComponents.getAs(componentName, WMap.class);
						}
						if (component == null) {
							LOG.error("can't find the component:" + componentName);
							continue;
						}
						componetData.addAll(component);
					}
					//merge Components into subProject.
					componetData.addAll(subProject);
					GradleProject subProjectData = new GradleProject(componetData);

					//gen gradle.build for sub project
					subProjectData.project = null;
					if (subProjectData.version == null) {
						subProjectData.version = data.getString("version");
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

					if (componetData.map("config") != null && componetData.map("config").size() > 0) {
						YamlUtil.write(componetData.map("config"), workDir + "/" + subProjectName + "/src/main/resources/application.yml", 2);
					}

					if (componetData.map("testConfig") != null && componetData.map("testConfig").size() > 0) {
						YamlUtil.write(componetData.map("testConfig"), workDir + "/" + subProjectName + "/src/test/resources/bootstrap.yml", 2);
					}

					if (componetData.map("docker") != null && componetData.map("docker").size() > 0) {
						//gen docker-compose.yml
						dockerServices.put(subProjectName, componetData.map("docker"));
					}

					//gen config for sub projects
					if (ArraysUtil.contains(thisComponents, "springBoot")) {
						YamlUtil.write(componetData.map("config"), workDir + "/" + subProjectName + "/src/main/resources/application.yml", 2);
						YamlUtil.write(componetData.map("testConfig"), workDir + "/" + subProjectName + "/src/test/resources/bootstrap.yml", 2);

						//gen docker-compose.yml
						dockerServices.put(subProjectName, componetData.map("docker"));

				WMap dockerCompose = new WMap();
				dockerCompose.put("version", "2");
				dockerCompose.put("services", dockerServices);
				YamlUtil.write(dockerCompose, "./docker-compose.yml");
					}
				}
				gradleGen.endMap();
			}
			gradleGen.writeFile("./gradle.yml");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
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
