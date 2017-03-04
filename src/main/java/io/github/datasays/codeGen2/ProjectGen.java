package io.github.datasays.codeGen2;

import io.github.datasays.codeGen2.model.GradleProject;
import io.github.datasays.util.WMap;
import io.github.datasays.util.YamlUtil;
import io.github.datasays.util.YmlGenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by watano on 2017/2/6.
 */
public class ProjectGen extends AYmlCodeGen {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectGen.class);
	private YmlGenHelper gradleGen = new YmlGenHelper();
	private YmlGenHelper configGen = new YmlGenHelper();
	private YmlGenHelper dockerGen = new YmlGenHelper();

	public void init() {
		super.init();
		gradleGen.init();
		configGen.init();
		dockerGen.init();
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
				for (String subProjectName : subProjects.keySet()) {
					WMap subProject = subProjects.getAs(subProjectName, WMap.class);
					props.setv("_SubProject_", subProjectName);
					props.setv("_SubProjectPort_", subProject.getString("ports", ""));
					WMap allComponents = YamlUtil.evalYml(data.getString("Components", "./components.yml"), props);
					//merge Components into subProject.
					String[] thisComponents = subProject.getArray("components", String.class, new String[]{});
					WMap componetData = new WMap();
					for (String componentName : thisComponents) {
						WMap component = null;
						if (componentName.startsWith(":")) {
							component = subProjects.getAs(componentName.substring(1), WMap.class);
							component.put("deps", new String[]{"compile project(':"+componentName.substring(1)+"')"});
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
				}
				gradleGen.endMap();
			}
			gradleGen.writeFile("./gradle2.yml");
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
