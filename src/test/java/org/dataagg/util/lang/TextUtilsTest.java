package org.dataagg.util.lang;

import static org.junit.Assert.*;

import org.dataagg.util.lang.TextUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextUtilsTest {
	private static final Logger LOG = LoggerFactory.getLogger(TextUtilsTest.class);

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testPoolishToString() {
		Object obj = new Object[] { new Object[] { 1L } };
		assertEquals("[[1]]", TextUtils.poolishToString(obj));
		obj = new Object[] { new Object[] { new Long(1) } };
		LOG.info(TextUtils.poolishToString(obj));
		assertEquals("[[1]]", TextUtils.poolishToString(obj));
		obj = new long[] { 1L };
		LOG.info(TextUtils.poolishToString(obj));
		assertEquals("[[1]]", TextUtils.poolishToString(obj));
	}

}
