package io.github.datasays.codeGen2;

import io.github.datasays.util.YamlUtil;
import io.github.datasays.util.YmlGenHelper;
import jodd.util.StringUtil;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by watano on 2017/2/6.
 */
public class ProjectGen {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectGen.class);
	private String WorkDir = ".";
	private NutMap data = null;
	private Map<String, String[]> versions = null;
	private YmlGenHelper helper = null;

	public void init() {
		data = null;
		if (helper == null) {
			helper = new YmlGenHelper();
		} else {
			helper.init();
		}
	}

	public void loadYmlData(String dataFile) {
		try {
			data = YamlUtil.evalYml(dataFile, "props");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public String[] libInfo(String id) {
		if (versions == null) {
			versions = new HashMap<>();
			try {
				NutMap data = YamlUtil.evalYml("./versions.yml", "props");
				if (data != null) {
					for (String libId : data.keySet()) {
						versions.put(libId, StringUtil.split(data.getString(libId), ":"));
					}
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		String[] info = versions.get(id);
		if (info == null) {
			LOG.error("不能找到Lib:" + id + "的版本信息!");
			return new String[]{"", id, ""};
		}
		return info;
	}

	public void appendComponentDep(String component) {
		component = component.trim();
		String dep = "compile '";
		if (component.startsWith("test:")) {
			dep = "testCompile '";
			component = component.substring(5);
		} else if (component.startsWith("runtime:")) {
			dep = "runtime '";
			component = component.substring(8);
		} else if (component.startsWith("testRuntime:")) {
			dep = "testRuntime '";
			component = component.substring(12);
		}
		String[] libInfo = libInfo(component);
		if (libInfo.length > 0) {
			dep += libInfo[0];
			if (libInfo.length > 1) {
				dep += ":" + libInfo[1];
				if (libInfo.length > 2) {
					dep += ":" + libInfo[2];
				}
			}
		}
		dep += "'";
		helper.addLst(dep);
	}

	public void appendPlugins(String... plugins) {
		List<String> pluginLst = new ArrayList<>();
		for (String plugin : plugins) {
			pluginLst.add(getPlugin(plugin));
		}
		helper.inlineList("plugins", pluginLst.toArray(new Object[]{}));
	}

	public String getPlugin(String id) {
		if ("java".equals(id) || "eclipse".equals(id) || "idea".equals(id) || "maven".equals(id)) {
			return id;
		} else {
			return "'" + libInfo(id)[0] + ":" + libInfo(id)[2] + "'";
		}
	}

	public void genGradle() {
		try {
			WorkDir = data.getString("WorkDir", ".");
			helper.set("WorkDir", WorkDir);
			helper.set("GenType", "gradle");
			helper.comment(null);

			helper.inlineMap("props");
			NutMap props = data.getAs("props", NutMap.class);
			helper.set("group", data.getString("group"));
			helper.set("project", data.getString("project"));
			helper.set("version", data.getString("version"));
			//plugins
			appendPlugins("eclipse", "idea", "plugin-versions");
			helper.set("description", data.getString("description"));
			//outObj.setNotNull("applyFrom", "/bintray.gradle");
			if (data.has("subProjects")) {
				List<String> components = data.getList("components", String.class);
				helper.beginLst("deps");
				for (String component : components) {
					appendComponentDep(component);
				}
				helper.endLst();
			} else {
				NutMap subProjects = data.getAs("subProjects", NutMap.class);
				helper.beginMap("subProjects");
				for (Object name : subProjects.keySet()) {
					String subProjectName = name.toString();
					helper.beginMap(subProjectName);
					NutMap subProject = subProjects.getAs(subProjectName, NutMap.class);
					helper.set("version", data.getString("version"));
					helper.set("archiveName", "app.jar");
					helper.set("description", subProject.getString("description"));
					//plugin
					appendPlugins("java", "eclipse", "idea", "plugin-boot");
					//components
					List<String> components = subProject.getList("components", String.class);
					helper.beginLst("deps");
					for (String component : components) {
						appendComponentDep(component);
					}
					helper.endLst();
					//set dependencyManagement
					helper.beginLst("dependencyManagement");
					helper.addLst("imports { mavenBom 'org.springframework.cloud:spring-cloud-dependencies:" + props.getString("spring-cloud") + "' }");
					helper.endLst();

					helper.endMap();
				}
				helper.endMap();
			}

			//write File
			helper.writeFile("./gradle.yml");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void genAllCodes(String dataFile) {
		init();
		loadYmlData(dataFile);
		genGradle();
	}

	public static void main(String[] args) {
		try {
			if (args != null && args.length > 0) {
				ProjectGen codegen = new ProjectGen();
				for (String arg : args) {
					codegen.genAllCodes(arg.trim());
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
