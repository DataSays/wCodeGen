package org.datasays.commons.base;

import static org.datasays.util.lang.CndUtils.*;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.service.EntityService;
import org.nutz.service.IdEntityService;

/**
 * ILongIdEntity类型实体的Dao抽象父类
 *
 * @author watano
 *
 * @param <E>
 */
public abstract class ALongIdEntityDao<E extends ILongIdEntity> extends IdEntityService<E> implements IEntityDaoBase {
	/**
	 * @see EntityService
	 */
	public ALongIdEntityDao() {
		super();
	}

	/**
	 * @see EntityService
	 */
	public ALongIdEntityDao(Dao dao) {
		super(dao);
	}

	/**
	 * @see EntityService
	 */
	public ALongIdEntityDao(Dao dao, Class<E> entityType) {
		super(dao, entityType);
	}

	/**
	 * 根据entity获取entity的ID
	 *
	 * @param entity
	 *            实体
	 * @return Long entity.id
	 */
	public Long entityId(E entity) {
		return entity != null ? entity.getId() : null;
	}

	/**
	 * 逻辑保存实体，根据实体的id，判断新增或修改实体
	 *
	 * 主要使用是非一对多的情况
	 *
	 * @param entity
	 * @return entity 保存之后的实体
	 */
	public E save(E entity) {
		if (entityId(entity) != null && entityId(entity) > 0) {
			return update(entity);
		} else {
			return insert(entity);
		}
	}

	/**
	 * 插入或新增数据
	 *
	 * @param entity
	 * @return entity 保存之后的实体
	 */
	public E insert(E entity) {
		return _insert(entity);
	}

	/**
	 * 修改数据
	 *
	 * @param entity
	 * @return entity 保存之后的实体
	 */
	public E update(E entity) {
		return _update(entity) > 0 ? entity : null;
	}

	/**
	 * 修改数据,只更新指定字段，常用于逻辑删除delFlag或者更新状态
	 *
	 * @param entity
	 * @param field 指定字段
	 * @return entity 保存之后的实体
	 */
	public E update(E entity, String field) {
		return _update(entity, field) > 0 ? entity : null;
	}

	/**
	 * 根据id删除数据
	 *
	 * @param id
	 * @return boolean 删除实体的成功或失败情况
	 */
	public boolean delete(Long id) {
		return super.delete(id) > 0;
	}

	/**
	 * 根据对象 Name 删除一个对象。它只会删除这个对象，关联对象不会被删除。
	 * <p>
	 * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
	 * <p>
	 * 如果你设定了外键约束，没有正确的清除关联对象会导致这个操作失败
	 *
	 * @param classOfT
	 *            对象类型
	 * @param name
	 *            对象 Name
	 *
	 * @return 影响的行数
	 * @see org.nutz.dao.entity.annotation.Name
	 */
	public boolean deleteByName(String name) {
		try {
			return delete(fetchByName(name).getId());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 根据@Id所在的属性的值获取一个实体对象
	 *
	 * @param id
	 *            属性的值
	 * @return 实体对象,如不存在则返回null
	 */
	public E fetch(Long id) {
		return super.fetch(id);
	}

	/**
	 * 根据 WHERE 条件获取一个对象。如果有多个对象符合条件，将只获取 ResultSet 第一个记录
	 *
	 * @param classOfT
	 *            对象类型
	 * @param cnd
	 *            WHERE 条件
	 * @return 对象本身
	 *
	 * @see org.nutz.dao.Condition
	 * @see org.nutz.dao.entity.annotation.Name
	 */
	public E fetchByName(String name) {
		return fetch(where("name", name));
	}

	/**
	 * 根据条件获取唯一实体对象
	 *
	 * @param cnd
	 * @return 实体对象,如不存在则返回null
	 */
	@Override
	public E fetch(Condition cnd) {
		return super.fetch(cnd);
	}

	/**
	 * 查询一组对象。你可以为这次查询设定条件
	 *
	 * @param cnd
	 *            WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序<br>
	 *            只有在调用这个函数的时候， cnd.limit 才会生效
	 * @return 对象列表
	 */
	@Override
	public List<E> query(Condition cnd) {
		return super.query(cnd);
	}

	/**
	 * 根据条件分页查询
	 *
	 * @param cnd
	 *            查询条件
	 * @param pager
	 *            分页
	 * @return 查询结果
	 */
	@Override
	public List<E> query(Condition cnd, Pager page) {
		List<E> all = super.query(cnd, page);
		if (page.getRecordCount() <= 0) {
			page.setRecordCount(count(cnd));
		}
		return all;
	}

	public E fetchBySql(String sqlText, Condition cnd) {
		return fetchBySql(sqlText, cnd, getEntityClass());
	}

	public List<E> queryBySql(String sqlText, Condition cnd) {
		return queryBySql(sqlText, cnd, getEntityClass());
	}
}
