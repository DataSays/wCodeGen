package org.dataagg.util.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.dataagg.util.lang.ValuePlus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WPageIteratorTest {
	private static final Logger LOG = LoggerFactory.getLogger(WPageIteratorTest.class);

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testWPage() {
		WPage page = new WPage();
		//pages
		int total = 0;
		page.setTotal(total);
		assertEquals(0, page.getPages());
		total = 1;
		page.setTotal(total);
		assertEquals(1, page.getPages());
		total = 20;
		page.setTotal(total);
		assertEquals(1, page.getPages());
		total = 21;
		page.setTotal(total);
		assertEquals(2, page.getPages());
		//100
		total = 100;
		page.setTotal(total);
		assertEquals(5, page.getPages());

		page.setFrom(0);
		assertEquals(1, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(19);
		assertEquals(1, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(20);
		assertEquals(2, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(99);
		assertEquals(5, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(100);
		assertEquals(5, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		//101
		total = 101;
		page.setTotal(total);
		assertEquals(6, page.getPages());

		page.setFrom(0);
		assertEquals(1, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(19);
		assertEquals(1, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(20);
		assertEquals(2, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(99);
		assertEquals(5, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(100);
		assertEquals(6, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());

		page.setFrom(101);
		assertEquals(6, page.getPageNo());
		assertEquals(total, page.getTotal().intValue());
	}

	@Test
	public void testDoSearch() {
		WPage page = new WPage();
		int total = 10;
		page.setSize(3);

		WPageIterator<Integer> wpIterator = new WPageIterator<Integer>(page) {
			@Override
			public void doSearch() {
				update(ValuePlus.lst(1, 2, 3), total);
			}
		};
		assertTrue(wpIterator.hasNext());//0
		assertEquals(0, page.getFrom());
		assertEquals(1, page.getPageNo());
		assertEquals(1, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//1
		assertEquals(1, page.getFrom());
		assertEquals(1, page.getPageNo());
		assertEquals(2, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//2
		assertEquals(2, page.getFrom());
		assertEquals(1, page.getPageNo());
		assertEquals(3, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//3
		assertEquals(3, page.getFrom());
		assertEquals(2, page.getPageNo());
		assertEquals(1, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//4
		assertEquals(4, page.getFrom());
		assertEquals(2, page.getPageNo());
		assertEquals(2, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//5
		assertEquals(5, page.getFrom());
		assertEquals(2, page.getPageNo());
		assertEquals(3, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//6
		assertEquals(6, page.getFrom());
		assertEquals(3, page.getPageNo());
		assertEquals(1, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//7
		assertEquals(7, page.getFrom());
		assertEquals(3, page.getPageNo());
		assertEquals(2, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//8
		assertEquals(8, page.getFrom());
		assertEquals(3, page.getPageNo());
		assertEquals(3, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertTrue(wpIterator.hasNext());//9
		assertEquals(9, page.getFrom());
		assertEquals(4, page.getPageNo());
		assertEquals(1, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertFalse(wpIterator.hasNext());//10
		assertEquals(10, page.getFrom());
		assertEquals(4, page.getPageNo());
		assertEquals(2, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		assertFalse(wpIterator.hasNext());//11
		assertEquals(11, page.getFrom());
		assertEquals(4, page.getPageNo());
		assertEquals(3, wpIterator.next().intValue());
		assertEquals(total, page.getTotal().intValue());

		try {
			page.setFrom(0);
			page.setSize(20);
			int total2 = 10;
			wpIterator = new WPageIterator<Integer>(page) {
				@Override
				public void doSearch() {
					update(ValuePlus.lst(1, 2, 3), total2);
				}
			};

			int index = 0;
			while (wpIterator.hasNext()) {
				assertEquals(index++, page.getFrom());
				Integer item = wpIterator.next();
				LOG.info("---------------------------------------------" + item + "->" + wpIterator.getPage());
				assertNotNull(item);
				if (index % 3 == 1) {
					assertEquals(1, item.intValue());
				} else if (index % 3 == 2) {
					assertEquals(2, item.intValue());
				} else if (index % 3 == 0) {
					assertEquals(3, item.intValue());
				} else {
					fail("OMG!");
				}
				assertEquals(index, page.getFrom());
				assertEquals(total2, page.getTotal().intValue());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	private void assertSearch(int total, int pageSize) {
		Integer[] data = new Integer[total];
		for (int i = 0; i < total; i++) {
			data[i] = i;
		}
		WPage page = new WPage();
		page.setSize(pageSize);
		WPageIterator<Integer> wpIterator = new WPageIterator<Integer>(page) {
			@Override
			public void doSearch() {
				update(Arrays.asList(Arrays.copyOfRange(data, page.getFrom(), page.getFrom() + page.getSize())), total);
			}
		};
		int i = 0;
		while (wpIterator.hasNext()) {
			Assert.assertEquals(i, page.getFrom());
			if (i == 0) {
				Assert.assertEquals(1, page.getPageNo());
			}
			if (i == 19) {
				Assert.assertEquals(1, page.getPageNo());
			}
			if (i == 20) {
				Assert.assertEquals(2, page.getPageNo());
			}
			if (i == 21) {
				Assert.assertEquals(2, page.getPageNo());
			}
			if (i == 399) {
				Assert.assertEquals(20, page.getPageNo());
			}

			if (i == total) {
				fail("超出");
			}
			Integer item = wpIterator.next();
			Assert.assertEquals(i, item.intValue());
			i++;
		}
	}

	@Test
	public void testDoSearch2() {
		assertSearch(400, 20);
	}
}
