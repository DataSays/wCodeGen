package org.dataagg.util.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.dataagg.util.collection.StrObj;
import org.dataagg.util.text.YamlUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jodd.io.FileUtil;

/**
 * Created by watano on 2017/3/4.
 */
public class YamlUtilTest {
	private String codes = null;

	@Before
	public void setUp() throws Exception {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("components.yml");
		codes = FileUtil.readUTFString(inputStream);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void load() throws Exception {
		StrObj data = YamlUtil.load(codes);
		assertNotNull(data);
		assertNotNull(data.get("springBoot"));
		StrObj map = data.mapVal("springBoot");
		assertNotNull(map);
		assertEquals(64, map.strVal("plugins").length());
		assertEquals("app.jar", map.strVal("archiveName"));
		assertEquals(1, map.listVal("deps", StrObj.class).size());
		assertEquals(3, map.mapVal("config").keySet().size());
	}

	@Test
	public void evalYml() throws Exception {

	}

	@Test
	public void evalYml1() throws Exception {

	}

	@Test
	public void convert() throws Exception {

	}

	@Test
	public void convert1() throws Exception {

	}

	@Test
	public void getStrings() throws Exception {

	}

	@Test
	public void getSets() throws Exception {

	}

	@Test
	public void addAll() throws Exception {
		StrObj data = YamlUtil.eval(codes, null);
		StrObj eurekaServer = data.mapVal("eurekaServer");
		assertEquals(1, eurekaServer.mapVal("config").keySet().size());
		StrObj client = eurekaServer.mapVal("config").mapVal("eureka").mapVal("client");
		assertNotNull(client);
		assertEquals(3, client.keySet().size());
		assertNotNull(client.get("server"));
		assertNull(client.get("serviceUrl"));

		StrObj eurekaClient = data.mapVal("eurekaClient");
		assertEquals(1, eurekaClient.mapVal("config").keySet().size());
		client = eurekaClient.mapVal("config").mapVal("eureka").mapVal("client");
		assertNotNull(client);
		assertEquals(1, client.keySet().size());
		assertNull(client.get("server"));
		assertNotNull(client.get("serviceUrl"));

		eurekaServer.addAll(eurekaClient);
		assertEquals(1, eurekaServer.mapVal("config").keySet().size());
		client = eurekaServer.mapVal("config").mapVal("eureka").mapVal("client");
		assertNotNull(client);
		assertEquals(3, client.keySet().size());
		assertNotNull(client.get("server"));
		assertNotNull(client.get("serviceUrl"));
	}

	@Test
	public void parseDaFrameworkYml() {
		String rootDir = "e:\\work\\DataAgg\\DAFramework\\";
		String outDir = "e:\\work\\DataAgg\\DAFramework\\codegen\\DAFramework\\";
		String[] allProjects = { "account", "api-gateway", "security", "service-center" };
		for (String project : allProjects) {
			try {
				StrObj props = new StrObj();
				props.put("ACCOUNT_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("CONFIG_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("MONGODB_PASSWORD", "YFzCAfocMInyJ5YaO805");

				StrObj projectData = new StrObj();
				StrObj tmpData = null;
				String cfgFile = null;
				cfgFile = rootDir + project + "\\src\\main\\resources\\bootstrap.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				cfgFile = rootDir + project + "\\src\\main\\resources\\application.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				YamlUtil.write(projectData, outDir + project + ".yml", 2);
				//test
				cfgFile = rootDir + project + "\\src\\test\\resources\\bootstrap.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				cfgFile = rootDir + project + "\\src\\test\\resources\\application.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				YamlUtil.write(projectData, outDir + project + "-test.yml", 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void parsePiggyMetricsYml() {
		String rootDir = "e:\\work\\DataAgg\\PiggyMetrics\\";
		String outDir = "e:\\work\\DataAgg\\DAFramework\\codegen\\PiggyMetrics\\";
		String[] allProjects = { "account-service", "notification-service", "registry", "auth-service", "statistics-service", "gateway" };
		String configDir = rootDir + "config\\src\\main\\resources\\shared\\";
		for (String project : allProjects) {
			try {
				StrObj props = new StrObj();
				props.put("ACCOUNT_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("CONFIG_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("MONGODB_PASSWORD", "YFzCAfocMInyJ5YaO805");

				StrObj projectData = YamlUtil.evalYml(configDir + "application.yml", props);
				StrObj tmpData = YamlUtil.evalYml(configDir + project + ".yml", props);
				projectData.addAll(tmpData);
				String cfgFile = null;
				cfgFile = rootDir + project + "\\src\\main\\resources\\bootstrap.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				cfgFile = rootDir + project + "\\src\\main\\resources\\application.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				YamlUtil.write(projectData, outDir + project + ".yml", 2);
				//test
				cfgFile = rootDir + project + "\\src\\test\\resources\\bootstrap.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				cfgFile = rootDir + project + "\\src\\test\\resources\\application.yml";
				if (new File(cfgFile).exists()) {
					tmpData = YamlUtil.evalYml(cfgFile, props);
					projectData.addAll(tmpData);
				}
				YamlUtil.write(projectData, outDir + project + "-test.yml", 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
