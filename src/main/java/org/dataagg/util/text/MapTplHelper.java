package org.dataagg.util.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.bean.BeanUtil;
import jodd.util.template.StringTemplateParser;

public class MapTplHelper {
	private static final Logger LOG = LoggerFactory.getLogger(MapTplHelper.class);
	private StringTemplateParser templateParser = new StringTemplateParser();
	private Object model;
	private BeanUtil beanUtil;

	public void initModel(Object model) {
		this.model = model;
		beanUtil = BeanUtil.declaredForcedSilent;
	}

	public String parse(String stringTpl) {
		return templateParser.parse(stringTpl, key -> {
			Object val = val(key);
			if (val == null) {
				LOG.warn("未在model中找到" + key + "对应的value！");
			}
			return (String) val;
		});
	}

	public Object val(String el) {
		try {
			return beanUtil.getProperty(model, el);
		} catch (Exception e) {
			return null;
		}
	}

	public void put(String el, Object value) {
		beanUtil.setProperty(model, el, value);
	}
}
