package org.datasays.util.codegen.service;

import static org.datasays.util.lang.CndUtils.*;
import org.slf4j.*;
import java.util.*;
import jodd.util.*;
import org.datasays.util.*;
import org.datasays.util.collection.*;
import org.datasays.util.lang.*;
import org.datasays.commons.base.*;

import org.nutz.dao.*;
import org.nutz.dao.util.cri.*;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.datasays.util.codegen.dao.UIDataGridDefDao;
import org.datasays.util.codegen.model.EUIDataGridDef;

import org.datasays.util.codegen.dao.UIItemDefDao;
import org.datasays.util.codegen.model.EUIItemDef;



/**
 * DataGrid定义
 *
 * DataAgg
 *
 */
@Service
public class UIDataGridDefService extends BaseLongIdEntityService<UIDataGridDefDao, EUIDataGridDef> {
	@Autowired
	public UIDataGridDefService(UIDataGridDefDao dao) {
		super(dao);
	}

	//定制的字段定义代码

//##JavaCodeMerger.code:0
//##JavaCodeMerger.code


	//定制的方法代码

//##JavaCodeMerger.code:1
	@Override
	public void fetchItems(EUIDataGridDef entity, String profile) {
		if (ProfileFull.equalsIgnoreCase(profile)) {
			dao.defsHelper.fetchItems(entity);

		} else if (ProfileList.equalsIgnoreCase(profile)) {

		} else if (ProfileView.equalsIgnoreCase(profile)) {
			dao.defsHelper.fetchItems(entity);

		} else if (ProfileEdit.equalsIgnoreCase(profile)) {
			dao.defsHelper.fetchItems(entity);

		}
		entity.sortDefs();
	}

	public EUIDataGridDef saveAll(EUIDataGridDef entity) throws Exception {
		List<EUIItemDef> defs = entity.getDefs();
		entity.sortDefs();
		entity.upDefSorts();
		entity.initDataTable();
		EUIDataGridDef newEntity = super.save(entity);
		dao.defsHelper.saveItems(newEntity, defs);
		return newEntity;
	}
//##JavaCodeMerger.code

}