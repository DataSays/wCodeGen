package org.dataagg.util.text;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dataagg.util.text.VCard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

public class VCardTest {
	private static final Logger LOG = LoggerFactory.getLogger(VCardTest.class);

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void test() {
		try {
			String[] lines = FileUtil.readLines("f:\\Downloads\\contacts.txt");
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
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
