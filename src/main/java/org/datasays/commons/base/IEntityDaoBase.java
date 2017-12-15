package org.datasays.commons.base;

import static org.datasays.util.lang.CndUtils.inIds;
import static org.datasays.util.lang.CndUtils.nameExp;
import static org.datasays.util.lang.CndUtils.where;

import java.sql.Timestamp;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;

/**
 * 基于nutzDao的实体类公用的Dao方法, 不绑定具体的实体类,完全实体无关的公用方法
 *
 * @author watano
 *
 */
public interface IEntityDaoBase {
	public Dao dao();

	/**
	 * 执行DELETE | UPDATE | INSERT，返回执行后所影响的记录数。否则返回 -1
	 * @param sql
	 * @param cnd
	 * @return
	 */
	public default int execUpSql(String sql, Condition cnd) {
		Sql sqlobj = execSql(sql, cnd);
		return sqlobj.getUpdateCount();
	}

	public default List<Long> queryIds(String table, Condition cnd) {
		String sql = "select distinct id from " + table + " $condition";
		return queryBySql(sql, cnd, Long.class);
	}

	public default int deleteByIds(Class<?> cls, long... ids) {
		if (ids != null && ids.length > 0) { return dao().clear(cls, Cnd.where(inIds("id", ids))); }
		return 0;
	}

	public default <E> E fetchByName(Class<E> cls, String name) {
		return dao().fetch(cls, where(nameExp(name)));
	}

	public default Sql execSql(String sqlText, Condition cnd) {
		Sql sql = Sqls.create(sqlText);

		if (cnd != null) {
			if (!sqlText.contains("$condition")) { throw new IllegalArgumentException("sql中需要包含$condition!"); }
			sql.setCondition(cnd);
		}
		return dao().execute(sql);
	}

	/**
	 *
	 * @param sqlText
	 * @param cnd
	 * @param cls
	 * @param flag 如果是获取一个实体则设置1,否则设置2
	 * @return
	 */
	public default <E> Sql execSql(String sqlText, Condition cnd, Class<E> cls, boolean multiVal) {
		Sql sql = null;
		String clsName = cls.getName();
		if (Integer.class.equals(cls) || "int".equals(clsName)) {
			if (multiVal) {
				sql = Sqls.create(sqlText);
				sql.setCallback(Sqls.callback.ints());
			} else {
				sql = Sqls.fetchInt(sqlText);
			}
		} else if (Long.class.equals(cls) || "long".equals(clsName)) {
			if (multiVal) {
				sql = Sqls.create(sqlText);
				sql.setCallback(Sqls.callback.longs());
			} else {
				sql = Sqls.fetchLong(sqlText);
			}
		} else if (Boolean.class.equals(cls) || "boolean".equals(clsName)) {
			if (multiVal) {
				sql = Sqls.create(sqlText);
				sql.setCallback(Sqls.callback.bools());
			} else {
				sql = Sqls.fetchRecord(sqlText);
			}
		} else if (Float.class.equals(cls) || "float".equals(clsName)) {
			if (multiVal) {
				throw new IllegalArgumentException();
			} else {
				sql = Sqls.fetchFloat(sqlText);
			}
		} else if (Double.class.equals(cls) || "double".equals(clsName)) {
			if (multiVal) {
				throw new IllegalArgumentException();
			} else {
				sql = Sqls.fetchDouble(sqlText);
			}
		} else if (String.class.equals(cls)) {
			if (multiVal) {
				sql = Sqls.create(sqlText);
				sql.setCallback(Sqls.callback.strList());
			} else {
				sql = Sqls.fetchString(sqlText);
			}
		} else if (Record.class.equals(cls)) {
			if (multiVal) {
				sql = Sqls.create(sqlText);
				sql.setCallback(Sqls.callback.records());
			} else {
				sql = Sqls.fetchRecord(sqlText);
			}
		} else if (Timestamp.class.equals(cls)) {
			if (multiVal) {
				throw new IllegalArgumentException();
			} else {
				sql = Sqls.fetchTimestamp(sqlText);
			}
		} else {
			sql = Sqls.create(sqlText);
			if (multiVal) {
				sql.setCallback(Sqls.callback.entities());
			} else {
				sql.setCallback(Sqls.callback.entity());
			}
			Entity<E> entity = dao().getEntity(cls);
			sql.setEntity(entity);
		}
		if (cnd != null) {
			if (!sqlText.contains("$condition")) { throw new IllegalArgumentException("sql中需要包含$condition!"); }
			sql.setCondition(cnd);
		}
		dao().execute(sql);
		return sql;
	}

	public default <E> E fetchBySql(String sqlText, Condition cnd, Class<E> cls) {
		Sql sqlobj = execSql(sqlText, cnd, cls, false);
		return sqlobj.getObject(cls);
	}

	public default <E> List<E> queryBySql(String sqlText, Condition cnd, Class<E> cls) {
		Sql sqlobj = execSql(sqlText, cnd, cls, true);
		return sqlobj.getList(cls);
	}

	public default Cnd getCri(Condition cnd) {
		if (cnd == null) {
			return Cnd.NEW();
		} else {
			return (Cnd) cnd;
		}
	}
}
