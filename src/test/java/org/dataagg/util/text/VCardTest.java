package org.dataagg.util.text;

import java.io.File;
import java.util.List;

import org.dataagg.util.text.VCard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.lang.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.util.StringUtil;

public class VCardTest {
	private static final Logger LOG = LoggerFactory.getLogger(VCardTest.class);

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void test() {
		List<String> lines = Files.readLines(new File("f:\\Downloads\\contacts.txt"));
		for (String line : lines) {
			String[] texts = StringUtil.split(line, "\t");
			if (texts != null && texts.length > 6) {
				VCard vcard = new VCard(texts[2], texts[6]);
				vcard.org = texts[1];
				vcard.title = texts[5];
				try {
					System.out.print(vcard.toText());
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}
}
