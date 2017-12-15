package org.datasays.util.collection;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.datasays.commons.base.ILongIdEntity;

public interface ITreeLongIdEntity<C extends ITreeLongIdEntity<C>> extends ILongIdEntity {
	public static final Logger LOG = LoggerFactory.getLogger(ITreeLongIdEntity.class);

	public Long getParentId();

	public void setParentId(Long parentId);

	public String getParentIds();

	public void setParentIds(String parentIds);

	public String getName();

	public void setName(String name);

	public String getCode();

	public void setCode(String code);

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

	@SuppressWarnings("unchecked")
	public default ITreeLongIdEntity<C> buildTree(List<ITreeLongIdEntity<C>> nodes) {
		Map<Long, ITreeLongIdEntity<C>> allNodes = new Hashtable<>();
		for (ITreeLongIdEntity<C> node : nodes) {
			allNodes.put(node.getId(), node);
		}

		ITreeLongIdEntity<C> root = null;
		for (ITreeLongIdEntity<C> node : nodes) {
			ITreeLongIdEntity<C> parent = null;
			if (node.getParentId() != null) {
				parent = allNodes.get(node.getParentId());
				if (parent != null) {
					parent.addChild((C) node);
				} else {
					LOG.debug("不能找到根节点" + node.getId() + "--" + node.getParentId());
				}
			} else {
				if (root != null) {
					LOG.debug("重复的根节点" + root.getId() + "--" + node.getParentId());
				}
				root = node;
			}
		}
		return root;
	}

	/**
	 * 深度优先遍历
	 */
	public default void depthFirstTraversa(Consumer<ITreeLongIdEntity<C>> fn) {
		fn.accept(this);
		if (this.getItems() != null) {
			for (C item : getItems()) {
				((ITreeLongIdEntity<C>) item).depthFirstTraversa(fn);
			}
		}
	}
}
