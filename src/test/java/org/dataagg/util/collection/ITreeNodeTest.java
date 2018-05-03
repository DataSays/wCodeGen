package org.dataagg.util.collection;

import static org.junit.Assert.*;

import java.util.List;

import org.dataagg.codegen.model.UIItemDef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ITreeNodeTest {
	private static final Logger LOG = LoggerFactory.getLogger(ITreeNodeTest.class);
	private TestTreeNode root;

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	private void insertNode(int parentId, int id) {
		TestTreeNode item = new TestTreeNode();
		item.setId(id);
		root.addChild(item);
		item.setParentId(parentId);
	}

	private void initTree() {
		root = new TestTreeNode();
		root.setId(1);
		insertNode(1, 2);
		insertNode(1, 3);
		insertNode(2, 4);
		insertNode(2, 5);
		insertNode(3, 6);
		insertNode(3, 7);
		insertNode(4, 8);
		root.buildTree();
	}

	@Test
	public void testDepthFirstTraversal() {
		initTree();
		final StringBuffer sb = new StringBuffer();
		root.depthFirstTraversal((node) -> {
			sb.append(node.getId() + ",");
		});
		LOG.info("深度优先遍历:" + sb.toString());
		assertEquals("1,2,4,8,5,3,6,7,", sb.toString());
	}

	@Test
	public void testBroadFirstTraversal() {
		initTree();
		final StringBuffer sb = new StringBuffer();
		root.broadFirstTraversal((node) -> {
			sb.append(node.getId() + ",");
		});
		LOG.info("广度优先遍历:" + sb.toString());
		assertEquals("1,2,3,4,5,6,7,8,", sb.toString());
	}

	class TestTreeNode implements ITreeNode<TestTreeNode, Integer> {
		private static final long serialVersionUID = -374189220052589243L;
		private Integer id;
		private Integer parentId = -1;
		private List<TestTreeNode> items;

		@Override
		public Integer getId() {
			return id;
		}

		@Override
		public void setId(Integer id) {
			this.id = id;
		}

		@Override
		public Integer getParentId() {
			return parentId;
		}

		@Override
		public void setParentId(Integer parentId) {
			this.parentId = parentId;
		}

		@Override
		public List<TestTreeNode> getItems() {
			return items;
		}

		@Override
		public void setItems(List<TestTreeNode> items) {
			this.items = items;
		}

		@Override
		public String toString() {
			return "[" + id + "," + parentId + "]@" + hashCode();
		}
	}

}
