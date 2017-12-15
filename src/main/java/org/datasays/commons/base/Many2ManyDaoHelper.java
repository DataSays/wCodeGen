package org.datasays.commons.base;

import java.util.List;

import org.nutz.dao.Cnd;

public abstract class Many2ManyDaoHelper<F extends ILongIdEntity, T extends ILongIdEntity> {
	private ALongIdEntityDao<F> fromDao;
	private ALongIdEntityDao<T> toDao;
	private String field;
	private String fromField;
	private String toField;
	private String fetchSql;
	private String deleteSql;

	public Many2ManyDaoHelper(ALongIdEntityDao<F> fromDao, ALongIdEntityDao<T> toDao, String field, String fromTable, String toTable, String fromField, String toField, String table) {
		super();
		this.fromDao = fromDao;
		this.toDao = toDao;
		this.field = field;
		this.fromField = fromField;
		this.toField = toField;
		fetchSql = String.format("SELECT * FROM %s where id in (select distinct %s from %s $condition)", toTable, toField, table);
		deleteSql = String.format("delete from %s $condition", table);
	}

	public abstract void setSubItems(F entity, List<T> allItems);

	public Cnd fromCnd(Long id) {
		Cnd cnd = Cnd.where(fromField, "=", id);
		return cnd;
	}

	public Cnd toCnd(Long id) {
		Cnd cnd = Cnd.where(toField, "=", id);
		return cnd;
	}

	public void fetchItems(F entity) {
		Long masterId = fromDao.entityId(entity);
		List<T> allItems = toDao.queryBySql(fetchSql, fromCnd(masterId));
		setSubItems(entity, allItems);
	}

	public void fetchItems(List<F> all) {
		for (F e : all) {
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
	public void saveItems(F entity, List<T> items) {
		Long masterId = fromDao.entityId(entity);
		delteItems(masterId);
		fromDao._insertRelation(entity, field);
		setSubItems(entity, items);
	}

	public int delteItems(Long masterId) {
		return fromDao.execUpSql(deleteSql, fromCnd(masterId));
	}
}
