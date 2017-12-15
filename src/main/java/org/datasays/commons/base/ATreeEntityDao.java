package org.datasays.commons.base;

import java.util.ArrayList;
import java.util.List;

import org.datasays.util.collection.ITreeLongIdEntity;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.service.EntityService;

import jodd.util.StringUtil;

/**
 * 树状类型实体的Dao抽象父类
 *
 * @author watano
 *
 * @param <E>
 */
public abstract class ATreeEntityDao<E extends ITreeLongIdEntity<E>> extends ALongIdEntityDao<E> {
	public abstract Long getParentId(E entity);

	public abstract void setParentId(E entity, Object id);

	public abstract String getParentIds(E entity);

	public abstract void setParentIds(E entity, Object pids);

	public List<E> getItems(E entity) {
		return entity.getItems();
	}

	public void setItems(E entity, List<E> list) {
		entity.setItems(list);
	}

	/**
	 * @see EntityService
	 */
	public ATreeEntityDao() {
		super();
	}

	/**
	 * @see EntityService
	 */
	public ATreeEntityDao(Dao dao) {
		super(dao);
	}

	/**
	 * @see EntityService
	 */
	public ATreeEntityDao(Dao dao, Class<E> entityType) {
		super(dao, entityType);
	}

	public Cnd parentIdsCnd(Long id) {
		Cnd cnd = Cnd.NEW();
		cnd.where().andLike("parentIds", "," + id + ",");
		return cnd;
	}

	public void buildParentIds(E entity) {
		if (getParentId(entity) != null && getParentId(entity) != 0L) {
			E parent = fetch(getParentId(entity));
			setParentIds(entity, getParentIds(parent) + parent.getId() + ",");
		} else {
			setParentIds(entity, ",0,");
		}
	}

	/**
	 * web端编辑的时候，前端级联组件需要点击选项才能加载数据
	 * 故在初始化级联组件数据的时候，把当前对象的父类所在层级初始化
	 * 包括父类的所有垂直上级、父类的同级以及父类的下级都加载出来
	 *
	 * @param id 当id！=null时，编辑当前对象
	 * 						Id=null,即新增下级对象
	 * @param parentId   代表当前对象父类id
	 * @param isExclude 是否排除自身，true 主要用于编辑当前对象的时候,需要排除自身及下级， false 外部业务功能使用entity时，不需要排除自身
	 * */
	public List<E> queryRelation(Long id, Long parentId, boolean isExclude) {
		List<E> list = new ArrayList<E>();
		List<E> entitys = null;
		Cnd sc = null;
		E entity = null;
		//新增的时候获取第一级
		if (id != null && parentId != null) {
			sc = Cnd.NEW();
			sc.where().andEquals("parentId", parentId);
			sc.where().andEquals("delFlag", "0");
			sc.asc("sort");
			entitys = super.query(sc);
			for (E e : entitys) {
				sc = Cnd.NEW();
				sc.where().andEquals("parentId", e.getId());
				sc.where().andEquals("delFlag", "0");
				sc.asc("sort");
				List<E> items = super.query(sc);
				if (items == null || items.isEmpty()) {
					setItems(e, null);
				} else {
					setItems(e, new ArrayList<E>());
				}
			}
			list.addAll(entitys);
		} else if (id != null && parentId == null) {
			entity = this.fetch(id); //id = '6' pId = '5' pids = ,0,3,5,
			//添加所有垂直上级
			String pids = entity.getParentIds();
			pids = StringUtil.replace(pids, "," + entity.getParentId() + ",", "");
			String[] pidsArray = StringUtil.split(pids, ",");
			for (String pId : pidsArray) {
				if (StringUtil.isNotBlank(pId)) {
					sc = Cnd.NEW();
					sc.where().andEquals("parentId", pId);
					sc.where().andEquals("delFlag", "0");
					sc.asc("sort");
					entitys = super.query(sc);
					for (E e : entitys) {
						sc = Cnd.NEW();
						sc.where().andEquals("parentId", e.getId());
						sc.where().andEquals("delFlag", "0");
						sc.where().andNotEquals("id", id);
						sc.asc("sort");
						List<E> items = super.query(sc);
						if (items == null || items.isEmpty()) {
							setItems(e, null);
						} else {
							setItems(e, new ArrayList<E>());
						}
					}
					list.addAll(entitys);
				}
			}
			//添加当前
			if (!isExclude) {
				list.add(entity);
				//添加下级
				sc = Cnd.NEW();
				sc.where().andEquals("parentId", id);
				sc.where().andEquals("delFlag", "0");
				sc.asc("sort");
				entitys = super.query(sc);
				for (E e : entitys) {
					sc = Cnd.NEW();
					sc.where().andEquals("parentId", e.getId());
					sc.where().andEquals("delFlag", "0");
					sc.asc("sort");
					List<E> items = super.query(sc);
					if (items == null || items.isEmpty()) {
						setItems(e, null);
					} else {
						setItems(e, new ArrayList<E>());
					}
				}
				list.addAll(entitys);
			}
			//添加同级
			sc = Cnd.NEW();
			sc.where().andEquals("parentId", entity.getParentId());
			sc.where().andEquals("delFlag", "0");
			sc.where().andNotEquals("id", id);
			sc.asc("sort");
			entitys = super.query(sc);
			for (E e : entitys) {
				sc = Cnd.NEW();
				sc.where().andEquals("parentId", e.getId());
				sc.where().andEquals("delFlag", "0");
				sc.where().andNotEquals("id", id);
				sc.asc("sort");
				List<E> items = super.query(sc);
				if (items == null || items.isEmpty()) {
					setItems(e, null);
				} else {
					setItems(e, new ArrayList<E>());
				}
			}
			list.addAll(entitys);

		} else if (id == null && parentId != null) {//新增下级对象
			entity = this.fetch(parentId);

			//添加所有垂直上级
			String pids = entity.getParentIds() + entity.getId() + ",";
			String[] pidsArray = StringUtil.split(pids, ",");
			for (String pId : pidsArray) {
				if (StringUtil.isNotBlank(pId)) {
					sc = Cnd.NEW();
					sc.where().andEquals("parentId", pId);
					sc.where().andEquals("delFlag", "0");
					sc.asc("sort");
					entitys = super.query(sc);
					for (E e : entitys) {
						sc = Cnd.NEW();
						sc.where().andEquals("parentId", e.getId());
						sc.where().andEquals("delFlag", "0");
						sc.where().andNotEquals("id", id);
						sc.asc("sort");
						List<E> items = super.query(sc);
						if (items == null || items.isEmpty()) {
							setItems(e, null);
						} else {
							setItems(e, new ArrayList<E>());
						}
					}
					list.addAll(entitys);
				}
			}
		}

		return list;
	}

	/**
	 * 根据ids 获取对应id和子节点的id，以数组的形式
	 * 将于用griddata带树形结构查询条件时使用
	 *
	 * @param idStr
	 * @return 返回idStr和子节点的id，以数组的形式
	 * @throws Exception
	 */
	public long[] buildQueryParams(String idStr) {
		Long id = Long.valueOf(idStr);
		List<E> childList = query(parentIdsCnd(id));//得到所有子类
		if (childList != null && !childList.isEmpty()) {
			long[] ids = new long[childList.size() + 1];
			ids[0] = id;
			int i = 1;
			for (E e : childList) {
				ids[i] = e.getId();
				i++;
			}
			return ids;
		} else {
			return new long[] { id };
		}
	}

	@Override
	public E save(E entity) {
		if (entityId(entity) != null && entityId(entity) > 0) {
			return update(entity);
		} else {
			return insert(entity);
		}
	}

	@Override
	public E insert(E entity) {
		this.buildParentIds(entity);
		return super.insert(entity);
	}

	@Override
	public E update(E entity) {
		E oldEntity = this.fetch(entityId(entity));
		if (oldEntity == null) { return null; }
		String oldParentIds = oldEntity.getParentIds();
		this.buildParentIds(entity);
		if (!StringUtil.equals(String.valueOf(getParentId(entity)), String.valueOf(getParentId(oldEntity)))) {
			List<E> items = query(parentIdsCnd(entityId(entity)));
			if (items != null && !items.isEmpty()) {
				for (E e2 : items) {
					setParentIds(e2, StringUtil.replace(getParentIds(e2), oldParentIds, getParentIds(entity)));
					super.update(e2, "parentIds");
				}
			}
		}
		return super.update(entity);
	}

	@Override
	public boolean delete(Long id) {
		E entity = this.fetch(id);
		if (entity == null) { return false; }
		return super.delete(id);
	}

	@Override
	public boolean deleteByName(String name) {
		E entity = super.fetchByName(name);
		if (entity == null) { return false; }
		return this.delete(entityId(entity));
	}

	/**
	 * 根据@Id所在的属性的值获取一个实体对象
	 *
	 * @param id
	 *            属性的值
	 * @return 实体对象,如不存在则返回null
	 */
	@Override
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
	@Override
	public E fetchByName(String name) {
		return fetchByName(getEntityClass(), name);
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
		return super.query(cnd, page);
	}
}
