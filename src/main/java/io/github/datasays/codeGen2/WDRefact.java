package io.github.datasays.codeGen2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

/**
 * Created by watano on 2017/2/21.
 */
public class WDRefact {
	public static void main(String[] args) {
		String home = "e:\\wddev\\lovingtrip\\";
		String home2 = "e:\\wddev\\lovingtrip2\\";
		File fHome = new File(home);
		File[] fProjects = fHome.listFiles();
		if (fProjects == null) { return; }
		for (File fProject : fProjects) {
			if (fProject.isDirectory() && fProject.getName().startsWith("lovingtrip_") && !fProject.getName().equalsIgnoreCase("lovingtrip__doc")) {
				String project = fProject.getName().substring("lovingtrip_".length());
				File[] fSubProjects = fProject.listFiles();
				for (File fSubProject : fSubProjects) {
					if (fSubProject.isDirectory() && fSubProject.getName().startsWith("lovingtrip-")) {
						String subProject = fSubProject.getName().substring("lovingtrip-".length());
						subProject = StringUtil.replace(subProject, StringUtil.replace(project, "_", "-"), "");
						subProject = StringUtil.replace(subProject, "--", "-");
						subProject = StringUtil.cutPrefix(subProject, "-");
						subProject = StringUtil.cutSuffix(subProject, "-");
						String newProject = project + "_" + subProject;
						if ("module_core".equalsIgnoreCase(project)) {
							subProject = StringUtil.cutPrefix(subProject, "module-");
							newProject = project + "_" + subProject;
						} else if ("web_base".equalsIgnoreCase(project)) {
							newProject = project;
						}
						newProject = StringUtil.replace(newProject, "-", "_");
						//check branches and tags
						for (String other : new String[] { "branches", "tags" }) {
							File fOther = new File(fSubProject.getAbsolutePath() + "/" + other);
							if (fOther.list() != null && fOther.list().length > 0) {
								System.err.println(newProject + "/" + other + " is not emtpy!");
							}
						}
						//copy trunk files to home2
						File fSource = new File(fSubProject.getAbsolutePath() + "/trunk");
						String description = fSubProject.getName();
						try {
							for (File f : fSource.listFiles()) {
								if (f.isDirectory() && !"|.settings|build|target|".contains("|" + f.getName() + "|")) {
									FileUtil.copyDir(f, new File(home2 + "/" + newProject + "/" + f.getName()));
								}
								if (f.isFile() && !"|.project|.classpath|pom.xml|".contains("|" + f.getName() + "|")) {
									FileUtil.copy(f, new File(home2 + "/" + newProject + "/" + f.getName()));
								}
								if (f.isFile() && "pom.xml".equalsIgnoreCase(f.getName())) {
									try {
										DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
										DocumentBuilder builder = dbf.newDocumentBuilder();
										Document doc = builder.parse(new FileInputStream(f));
										XPathFactory factory = XPathFactory.newInstance();
										XPath xpath = factory.newXPath();
										XPathExpression expr = xpath.compile("//project/description/text()");
										Object result = expr.evaluate(doc, XPathConstants.STRING);
										description = result.toString();
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						String parentProject = "core_commons";
						if (newProject.startsWith("data_")) {
							parentProject = "data_base";
						}
						if (newProject.startsWith("dataport_")) {
							parentProject = "dataport_base";
						}
						if (newProject.startsWith("insurance_")) {
							parentProject = "insurance_module_common";
						}
						if (newProject.startsWith("module_core")) {
							parentProject = "module_core_base";
						}
						System.out.println("\t" + newProject + ":\n" + "\t\tversion: 1.0-SNAPSHOT\n" + "\t\tarchiveName: " + fSubProject.getName() + ".jar\n" + "\t\tdescription: " + description + "\n" + "\t\tplugins: [java, eclipse, idea, maven]\n" + "\t\tdeps:\n" + "\t\t\t- compile project(':" + parentProject + "')\n");

					}
				}
			}
		}
	}
}
