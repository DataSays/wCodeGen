package org.dataagg.codegen.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CodeGenHelperTest {

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testCapFirst() {
		fail("Not yet implemented");
	}

	@Test
	public void testUncapFirst() {
		fail("Not yet implemented");
	}

	@Test
	public void testAppendStringBufferStringObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testAppendlnStringBufferStringObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoinPrefix() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoinSuffix() {
		fail("Not yet implemented");
	}

	@Test
	public void testRmDuplicateEmptyLine() {
		assertEquals("\n", CodeGenHelper.rmDuplicateEmptyLine("\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\n\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\n\n\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\n\n\n\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\n\n\n\n\n\n\n\n\n\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\n 	\n \n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\r\n\n\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\n\n\r\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\r\n\n\r\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\r\n\r\n\r\n"));
		assertEquals("\n\n", CodeGenHelper.rmDuplicateEmptyLine("\r\n \r\n\r\n"));
		assertEquals("x\n\n", CodeGenHelper.rmDuplicateEmptyLine("x\n\n"));
		assertEquals("x\n\n", CodeGenHelper.rmDuplicateEmptyLine("x\n\n\n"));
		assertEquals("a\n\nb\n\nc", CodeGenHelper.rmDuplicateEmptyLine("a\n\n\nb\n\n\nc"));
		assertEquals("x\n\n", CodeGenHelper.rmDuplicateEmptyLine("x\n\r\n\r\n"));
	}

}
