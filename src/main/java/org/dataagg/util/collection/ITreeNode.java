package org.dataagg.util.collection;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Consumer;

import org.dataagg.util.lang.IEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ITreeNode<C extends ITreeNode<C, I>, I> extends IEntity<I> {
	public static final Logger LOG = LoggerFactory.getLogger(ITreeNode.class);
	/**
	 * 树形结构表，跟节点为0L
	 * */
	public static final Long Tree_Root_Id = 0L;

	@Override
	public I getId();

	@Override
	public void setId(I id);

	public I getParentId();

	public void setParentId(I parentId);

	public List<C> getItems();

	public void setItems(List<C> items);

	public default void addChild(C node) {
		List<C> items = getItems();
		if (items == null) {
			items = new ArrayList<>();
		}
		node.setParentId(getId());
		items.add(node);
		setItems(items);
	}

	public default void addAllChildren(C[] nodes) {
		if (nodes != null) {
			for (C node : nodes) {
				addChild(node);
			}
		}
	}

	public default ITreeNode<C, I> up(I id, I parentId) {
		setId(id);
		setParentId(parentId);
		return this;
	}

	@SuppressWarnings("unchecked")
	public default void buildTree() {
		List<ITreeNode<C, I>> nodes = (List<ITreeNode<C, I>>) getItems();
		setItems(null);
		Map<I, ITreeNode<C, I>> allNodes = new Hashtable<>();
		for (ITreeNode<C, I> node : nodes) {
			allNodes.put(node.getId(), node);
		}

		for (ITreeNode<C, I> node : nodes) {
			ITreeNode<C, I> parent = null;
			if (node.getParentId() != null) {
				if (node.getParentId().equals(this.getId())) {
					parent = this;
				} else {
					parent = allNodes.get(node.getParentId());
				}
				if (parent != null) {
					parent.addChild((C) node);
				} else {
					LOG.debug("不能找到根节点" + node.getId() + "--" + node.getParentId());
				}
			} else {
				LOG.debug("父节点为null：" + node.getId());
			}
		}
	}

	/**
	 * 深度优先遍历
	 */
	public default void depthFirstTraversal(Consumer<ITreeNode<C, I>> fn) {
		fn.accept(this);
		if (this.getItems() != null) {
			for (C item : getItems()) {
				((ITreeNode<C, I>) item).depthFirstTraversal(fn);
			}
		}
	}

	/**
	 * 广度优先遍历
	 */
	public default void broadFirstTraversal(Consumer<ITreeNode<C, I>> fn) {
		Queue<ITreeNode<C, I>> nodeStack = new LinkedList<>();
		ITreeNode<C, I> node;
		nodeStack.add(this);
		while (!nodeStack.isEmpty()) {
			node = nodeStack.poll();
			fn.accept(node);

			if (node.getItems() != null) {
				for (C item : node.getItems()) {
					nodeStack.add(item);
				}
			}
		}
	}

	public default String allNodeIds() {
		final StringBuffer sb = new StringBuffer();
		broadFirstTraversal((node) -> {
			sb.append(node.getId() + ",");
		});
		LOG.info("广度优先遍历:" + sb.toString());
		return sb.toString();
	}
}
