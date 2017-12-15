package org.datasays.commons.base;

import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;

public abstract class BaseLongIdEntityService<DAO extends ALongIdEntityDao<M>, M extends ILongIdEntity> {
	public static final String ProfileFull = "full";
	public static final String ProfileList = "list";
	public static final String ProfileView = "view";
	public static final String ProfileEdit = "edit";

	protected DAO dao;

	public abstract void fetchItems(M entity, String profile);

	public BaseLongIdEntityService(DAO dao) {
		super();
		this.dao = dao;
	}

	/**
	 * 逻辑保存实体，根据实体的id，判断新增或修改实体
	 *
	 * @param entity
	 * @return entity 保存之后的实体
	 */
	public M save(M entity) throws Exception {
		return dao.save(entity);
	}

	/**
	 * 插入或新增数据
	 *
	 * @param entity
	 * @return entity 保存之后的实体
	 */
	public M insert(M entity) throws Exception {
		return dao.insert(entity);
	}

	/**
	 * 修改数据
	 *
	 * @param entity
	 * @return entity 保存之后的实体
	 */
	public M update(M entity) throws Exception {
		return dao.update(entity);
	}

	/**
	 * 根据id删除entity及关联对象
	 *
	 * @param id
	 * @return 删除结果
	 */
	public boolean delete(Long id) throws Exception {
		return dao.delete(id);
	}

	/**
	 * 根据id获取对象及关联对象
	 *
	 * @param entity.id
	 * @return entity
	 */
	public M fetch(Long id) throws Exception {
		return fetch(id, null);
	}

	public M fetch(Long id, String profile) throws Exception {
		M e = dao.fetch(id);
		fetchItems(e, profile);
		return e;
	}

	/**
	 * 根据条件查询entity
	 *
	 * @param cnd
	 *            查询条件
	 * @return 查询结果
	 */
	public List<M> query(Condition cnd) throws Exception {
		return query(cnd, (String) null);
	}

	public List<M> query(Condition cnd, String profile) throws Exception {
		List<M> all = dao.query(cnd);
		fetchItems(all, profile);
		return all;
	}

	/**
	 * 根据条件分页查询entity
	 *
	 * @param cnd
	 *            查询条件
	 * @return 查询结果
	 */
	public QueryResult query(Condition cnd, Pager pager) throws Exception {
		return query(cnd, pager, null);
	}

	public QueryResult query(Condition cnd, Pager pager, String profile) throws Exception {
		List<M> all = dao.query(cnd, pager);
		fetchItems(all, profile);
		return new QueryResult(all, pager);
	}

	protected void fetchItems(List<M> all, String profile) {
		for (M e : all) {
			fetchItems(e, profile);
		}
	}
}
