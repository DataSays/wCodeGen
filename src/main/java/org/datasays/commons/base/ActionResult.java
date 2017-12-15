package org.datasays.commons.base;

import org.datasays.util.collection.StrObj;
import org.datasays.util.collection.WMap;

/**
 * Created by watano on 2016/12/22.
 */
public class ActionResult<T> implements IActionResult<T> {
	private int code;
	private String message;
	private T data;
	private StrObj extData;
	private WMap authorities;

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public T getData() {
		return data;
	}

	@Override
	public void setData(T data) {
		this.data = data;
	}

	@Override
	public void setAuthorities(WMap authorities) {
		this.authorities = authorities;
	}

	@Override
	public WMap getAuthorities() {
		return authorities;
	}

	@Override
	public StrObj getExtData() {
		return extData;
	}

	@Override
	public void setExtData(StrObj extData) {
		this.extData = extData;
	}

}
