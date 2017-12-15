package org.datasays.commons.base;

import org.datasays.util.collection.StrObj;
import org.datasays.util.collection.WMap;

/**
 * Created by watano on 2016/12/22.
 */
public interface IActionResult<T> {
	public int getCode();

	public String getMessage();

	public void setCode(int code);

	public void setMessage(String msg);

	public void setData(T data);

	public T getData();

	public void setExtData(StrObj  obj);

	public StrObj getExtData();

	public void setAuthorities(WMap authorities);

	public WMap getAuthorities();

	public default void extData(StrObj obj) {
		this.setExtData(obj);
	}
	
	public default void msg(int code, String msg) {
		this.setCode(code);
		this.setMessage(msg);
	}

	public default void errorMsg(String msg) {
		this.msg(-1, msg);
	}

	public default void okMsg(String msg) {
		this.msg(1, msg);
	}

	public default void ok(T data) {
		this.ok(data, "");
	}

	public default void ok(T data, String msg) {
		this.setData(data);
		okMsg(msg);
	}

	public default void error(Throwable t) {
		this.msg(-100, t.getMessage());
	}
}
