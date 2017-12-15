package org.datasays.util.codegen.vo;

import java.util.List;

public class EntityDefVo {
	
	private String entityPkg;//用于生成前端页面时，dialog选择的包名接收对象
	
	public String getEntityPkg() {
		return entityPkg;
	}

	public void setEntityPkg(String entityPkg) {
		this.entityPkg = entityPkg;
	}

	private List<String> entityPkgList;//用于生成entity信息时，dialog选择的包名接收对象

	public List<String> getEntityPkgList() {
		return entityPkgList;
	}

	public void setEntityPkgList(List<String> entityPkgList) {
		this.entityPkgList = entityPkgList;
	}
 
}
