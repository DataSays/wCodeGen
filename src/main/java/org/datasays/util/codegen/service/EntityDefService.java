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

import org.datasays.util.codegen.dao.EntityDefDao;
import org.datasays.util.codegen.model.EEntityDef;

import org.datasays.util.codegen.dao.EntityItemDefDao;
import org.datasays.util.codegen.model.EEntityItemDef;

/**
 * Entity对象定义
 *
 * DataAgg
 *
 */
@Service
public class EntityDefService extends BaseLongIdEntityService<EntityDefDao, EEntityDef> {
	@Autowired
	public EntityDefService(EntityDefDao dao) {
		super(dao);
	}

	//定制的字段定义代码
	//##JavaCodeMerger.code:0
	private static final Logger LOG = LoggerFactory.getLogger(EntityDefService.class);
	@Autowired
	private CacheHelper cacheHelper;
	//##JavaCodeMerger.code

	//定制的方法代码

	//##JavaCodeMerger.code:1
	@Override
	public void fetchItems(EEntityDef entity, String profile) {
		if (ProfileFull.equalsIgnoreCase(profile)) {
			dao.defsHelper.fetchItems(entity);

		} else if (ProfileList.equalsIgnoreCase(profile)) {

		} else if (ProfileView.equalsIgnoreCase(profile)) {
			dao.defsHelper.fetchItems(entity);

		} else if (ProfileEdit.equalsIgnoreCase(profile)) {
			dao.defsHelper.fetchItems(entity);

		}
	}

	public EEntityDef saveAll(EEntityDef entity) throws Exception {
		List<EEntityItemDef> defs = entity.getDefs();
		EEntityDef newEntity = super.save(entity);
		dao.defsHelper.saveItems(newEntity, defs);
		return newEntity;
	}

	/**
	 * 删除关联对象
	 *
	 * @param entity
	 * @return  boolean true  成功    false  失败
	 */
	public boolean deleteItemByMasterId(Long id) throws Exception {
		return dao.defsHelper.delteItems(id) > 0;
	}

	//	@Cacheable(value = "EntityDefDao", key = "#pkg+'@'+#entityCls")
	public EEntityDef fetch(String pkg, String entityCls) {
		return (EEntityDef) cacheHelper.cache("EntityDefDao").key(() -> pkg + "@" + entityCls).fetch(() -> {
			return dao.fetch(pkg, entityCls);
		}).action();
	}

	//	@Cacheable(value = "EntityDefDao", key = "#pkg+'@'+#entityCls+'WithItems'")
	public EEntityDef fetchWithItems(String pkg, String entityCls) {
		return (EEntityDef) cacheHelper.cache("EntityDefDao").key(() -> pkg + "@" + entityCls + ":WithItems").fetch(() -> {
			return dao.fetchWithItems(pkg, entityCls);
		}).action();
	}

	//	@Cacheable(value = "EntityDefDao", key = "#pkg+'@'+#entityCls+'AllItems'")
	public List<EEntityItemDef> fetchAllItems(String pkg, String entityCls) {
		return cacheHelper.cacheList("EntityDefDao").key(() -> pkg + "@" + entityCls + ":AllItems").fetch(() -> {
			List<EEntityItemDef> allItems = new ArrayList<>();
			Map<String, List<EEntityItemDef>> cache = new Hashtable<>();
			allItems.addAll(fetchAllItems(cache, pkg, entityCls));
			return allItems;

		}).action();
	}

	private List<EEntityItemDef> fetchAllItems(Map<String, List<EEntityItemDef>> cache, String pkg, String entityCls) {
		if (cache.get(pkg + "." + entityCls) != null) { return cache.get(pkg + "." + entityCls); }
		List<EEntityItemDef> allItems = new ArrayList<>();
		EEntityDef entityDef = fetchWithItems(pkg, entityCls);
		if (entityDef != null) {
			for (EEntityItemDef entityItemDef : entityDef.getDefs()) {
				if (entityItemDef.isOne2One() || entityItemDef.isOne2Many() || entityItemDef.isMany2Many()) {
					String valCls = entityItemDef.getValCls();
					int index = valCls.lastIndexOf(".");
					if (index > 0) {
						String valPkg = valCls.substring(0, index);
						String valType = valCls.substring(index + 1);
						List<EEntityItemDef> allSubItems = fetchAllItems(cache, valPkg, valType);
						for (EEntityItemDef itemDef : allSubItems) {
							if (entityItemDef.isOne2One()) {
								itemDef.setField(entityItemDef.getField() + "." + itemDef.getField());
							} else {
								itemDef.setField(entityItemDef.getField() + "[]." + itemDef.getField());
							}
							allItems.add(itemDef);
						}
						//FIXME 再仔细考虑是否需要: 一对一时移除关联的字段,避免关联字段重复设置
						if (entityItemDef.isOne2One()) {
							allItems.removeIf((item) -> {
								return item.getField().equals(entityItemDef.getRelationFrom());
							});
						}
					} else {
						LOG.warn(valCls + " can't find!");
					}
				} else {
					allItems.add(entityItemDef);
				}
			}
		}
		return allItems;
	}
	//##JavaCodeMerger.code

}
