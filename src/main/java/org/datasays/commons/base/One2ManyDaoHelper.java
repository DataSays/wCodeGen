package org.datasays.commons.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.Exps;

public abstract class One2ManyDaoHelper<M extends ILongIdEntity, D extends ILongIdEntity> {
	private ALongIdEntityDao<M> masterDao;
	private ALongIdEntityDao<D> detailDao;
	private String masterField;

	public One2ManyDaoHelper(ALongIdEntityDao<M> masterDao, ALongIdEntityDao<D> detailDao, String masterField) {
		super();
		this.masterDao = masterDao;
		this.detailDao = detailDao;
		this.masterField = masterField;
	}

	/**
	 * 更新Master相关信息
	 * @param item
	 * @param id
	 */
	public abstract void upMasterInfo(D item, Long id);

	public abstract void setSubItems(M entity, List<D> allItems);

	/**
	 * 设置特殊的master和item关联字段，需要在具体的masterDao中重写
	 *
	 * @param name
	 * @return Cnd
	 */
	public Cnd masterCnd(Long id) {
		return Cnd.where(Exps.eq(masterField, id));
	}

	public void fetchItems(M entity) {
		Long masterId = masterDao.entityId(entity);
		List<D> allItems = detailDao.query(masterCnd(masterId));
		setSubItems(entity, allItems);
	}

	public void fetchItems(List<M> all) {
		for (M e : all) {
			fetchItems(e);
		}
	}

	/**
	 * 根据entity对象，关联对象的新增 删除 更新操作
	 *
	 * @param entity
	 * @param items
	 *            关联对象
	 * @param itemDao
	 *            关联对象的DAO
	 * @return entity 更新之后的entity对象
	 */
	public void saveItems(M entity, List<D> items) {
		Long masterId = masterDao.entityId(entity);
		for (D itemDef : items) {
			upMasterInfo(itemDef, masterId);
		}
		Cnd queryItems = masterCnd(masterId);
		Set<Long> itemIds = new HashSet<>();
		List<D> oldItems = detailDao.query(queryItems);
		for (D item : oldItems) {
			itemIds.add(detailDao.entityId(item));
		}
		for (D item : items) {
			Long itemId = detailDao.entityId(item);
			if (itemId == null || itemId <= 0) {
				detailDao.insert(item);
			} else {
				if (itemIds.contains(itemId)) {
					detailDao.update(item);
					itemIds.remove(itemId);
				} else {
					detailDao.insert(item);
				}
			}
		}
		if (!itemIds.isEmpty()) {
			long[] ids = new long[itemIds.size()];
			int x = 0;
			for (Long id : itemIds) {
				ids[x++] = id;
			}
			detailDao.deleteByIds(detailDao.getEntityClass(), ids);
		}
		setSubItems(entity, items);
	}

	public int delteItems(Long masterId) {
		Cnd queryItems = masterCnd(masterId);
		return detailDao.clear(queryItems);
	}
}
