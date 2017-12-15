package org.datasays.commons.base;

import java.util.List;

import org.datasays.util.collection.StrObj;
import org.datasays.util.collection.WMap;

/**
 * Created by watano on 2016/12/22.
 */
public class ActionResultList<T> implements IActionResult<List<T>> {
	private int code;
	private String message;
	private List<T> data;
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
	public List<T> getData() {
		return data;
	}

	@Override
	public void setData(List<T> data) {
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
