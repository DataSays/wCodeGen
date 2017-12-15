package org.datasays.util.lang;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.Exps;
import org.nutz.dao.util.cri.SqlExpression;
import org.nutz.dao.util.cri.SqlExpressionGroup;

public class CndUtils {
	public static Cnd where(String field, Object value) {
		return Cnd.where(Exps.eq(field, value));
	}

	public static Cnd where(SqlExpression exp) {
		return Cnd.where(exp);
	}

	public static Cnd and(SqlExpression... exps) {
		Cnd cnd = Cnd.NEW();
		if (exps != null) {
			for (SqlExpression exp : exps) {
				if (exp != null) {
					cnd.and(exp);
				}
			}
		}
		return cnd;
	}

	public static Cnd or(SqlExpression... exps) {
		Cnd cnd = Cnd.NEW();
		if (exps != null) {
			for (SqlExpression exp : exps) {
				if (exp != null) {
					cnd.or(exp);
				}
			}
		}
		return cnd;
	}

	public static SqlExpressionGroup andGroup(SqlExpression... exps) {
		SqlExpressionGroup seg = null;
		if (exps != null) {
			for (SqlExpression exp : exps) {
				if (seg == null) {
					seg = Cnd.exps(exp);
				} else if (exp != null) {
					seg.and(exp);
				}
			}
		}
		if (seg == null) {
			seg = new SqlExpressionGroup();
		}
		return seg;
	}

	public static SqlExpressionGroup orGroup(SqlExpression... exps) {
		SqlExpressionGroup seg = null;
		if (exps != null) {
			for (SqlExpression exp : exps) {
				if (seg == null) {
					seg = Cnd.exps(exp);
				} else if (exp != null) {
					seg.or(exp);
				}
			}
		}
		if (seg == null) {
			seg = new SqlExpressionGroup();
		}
		return seg;
	}

	public static SqlExpressionGroup likes(String value, String... fields) {
		SqlExpressionGroup seg = null;
		if (fields != null && value != null) {
			for (String field : fields) {
				if (seg == null) {
					seg = Cnd.exps(Exps.like(field, value));
				} else if (field != null) {
					seg.or(Cnd.exps(Exps.like(field, value)));
				}
			}
		}
		if (seg == null) {
			seg = new SqlExpressionGroup();
		}
		return seg;
	}

	public static SqlExpression neq(String field, Object value) {
		return Exps.eq(field, value).setNot(true);
	}

	public static SqlExpression notLike(String field, String value) {
		return Exps.like(field, value).setNot(true);
	}

	public static SqlExpression isNotNull(String field) {
		return Exps.isNull(field).setNot(true);
	}

	public static SqlExpression inIds(String field, long... ids) {
		return Exps.inLong(field, ids);
	}

	public static SqlExpression notInIds(String field, long... ids) {
		return Exps.inLong(field, ids).setNot(true);
	}

	public static SqlExpression idExp(long id) {
		return Exps.eq("id", id);
	}

	public static SqlExpression nameExp(String name) {
		return Exps.eq("name", name);
	}

	public static SqlExpression delFlag() {
		return Exps.eq("delFlag", "0");
	}

	public static SqlExpression enableFlag() {
		return Exps.eq("enableFlag", true);
	}
}
