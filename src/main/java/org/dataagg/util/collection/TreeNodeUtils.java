package org.dataagg.util.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public class TreeNodeUtils {
	private static final Logger LOG = LoggerFactory.getLogger(TreeNodeUtils.class);

	public static <F extends ITreeNode<F, I>, I> F buildTreeNode(List<F> entitys) {
		List<F> allNodes = layoutTreeNode(entitys);
		if (allNodes != null) { return allNodes.get(0); }
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <F extends ITreeNode<F, I>, I> List<F> layoutTreeNode(List<F> entitys) {
		List<ITreeNode<F, I>> allNodes = new ArrayList<>();
		allNodes.addAll(entitys);
		List<ITreeNode<F, I>> all = layoutTree(allNodes);
		List<F> all2 = new ArrayList<>();
		for (ITreeNode<F, I> a : all) {
			all2.add((F) a);
		}
		return all2;
	}

	/**
	 * 根据所有nodes节点构建一个Tree， 包含多个平级的父节点
	 *
	 * @param nodes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <F extends ITreeNode<F, I>, I> List<ITreeNode<F, I>> layoutTree(List<ITreeNode<F, I>> nodes) {
		Map<I, ITreeNode<F, I>> allNodes = new Hashtable<>();
		for (ITreeNode<F, I> node : nodes) {
			allNodes.put(node.getId(), node);
		}

		List<ITreeNode<F, I>> newTree = new ArrayList<>();
		for (ITreeNode<F, I> node : nodes) {
			if (node.getParentId() == null || allNodes.get(node.getParentId()) == null) {
				newTree.add(node);
			}
			ITreeNode<F, I> parent = null;
			if (node.getParentId() != null) {
				parent = allNodes.get(node.getParentId());
				if (parent != null) {
					parent.addChild((F) node);
				} else {
					LOG.debug("不能找到根节点" + node.getId() + "--" + node.getParentId());
				}
			}
		}
		return newTree;
	}
}
