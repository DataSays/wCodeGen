package org.dataagg.util.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.dataagg.util.collection.WMap;
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
		WMap data = YamlUtil.load(codes);
		assertNotNull(data);
		assertNotNull(data.get("springBoot"));
		WMap map = data.map("springBoot");
		assertNotNull(map);
		assertEquals(4, map.strings("plugins").length);
		assertEquals("app.jar", map.getString("archiveName"));
		assertEquals(1, map.strings("deps").length);
		assertEquals(3, map.map("config").keySet().size());
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
		WMap data = YamlUtil.eval(codes, null);
		WMap eurekaServer = data.map("eurekaServer");
		assertEquals(1, eurekaServer.map("config").keySet().size());
		WMap client = eurekaServer.map("config").map("eureka").map("client");
		assertNotNull(client);
		assertEquals(3, client.keySet().size());
		assertNotNull(client.get("server"));
		assertNull(client.get("serviceUrl"));

		WMap eurekaClient = data.map("eurekaClient");
		assertEquals(1, eurekaClient.map("config").keySet().size());
		client = eurekaClient.map("config").map("eureka").map("client");
		assertNotNull(client);
		assertEquals(1, client.keySet().size());
		assertNull(client.get("server"));
		assertNotNull(client.get("serviceUrl"));

		eurekaServer.addAll(eurekaClient);
		assertEquals(1, eurekaServer.map("config").keySet().size());
		client = eurekaServer.map("config").map("eureka").map("client");
		assertNotNull(client);
		assertEquals(4, client.keySet().size());
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
				WMap props = new WMap();
				props.put("ACCOUNT_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("CONFIG_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("MONGODB_PASSWORD", "YFzCAfocMInyJ5YaO805");

				WMap projectData = new WMap();
				WMap tmpData = null;
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
		String rootDir = "e:\\temp\\PiggyMetrics\\";
		String outDir = "e:\\work\\DataAgg\\DAFramework\\codegen\\PiggyMetrics\\";
		String[] allProjects = { "account-service", "notification-service", "registry", "auth-service", "statistics-service", "gateway" };
		String configDir = rootDir + "config\\src\\main\\resources\\shared\\";
		for (String project : allProjects) {
			try {
				WMap props = new WMap();
				props.put("ACCOUNT_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("CONFIG_SERVICE_PASSWORD", "YFzCAfocMInyJ5YaO805");
				props.put("MONGODB_PASSWORD", "YFzCAfocMInyJ5YaO805");

				WMap projectData = YamlUtil.evalYml(configDir + "application.yml", props);
				WMap tmpData = YamlUtil.evalYml(configDir + project + ".yml", props);
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
