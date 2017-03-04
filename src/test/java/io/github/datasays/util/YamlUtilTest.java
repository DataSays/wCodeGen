package io.github.datasays.util;

import jodd.io.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by watano on 2017/3/4.
 */
public class YamlUtilTest {
	private static final Logger LOG = LoggerFactory.getLogger(YamlUtilTest.class);
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
}