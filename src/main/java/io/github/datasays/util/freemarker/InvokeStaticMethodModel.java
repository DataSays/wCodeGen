package io.github.datasays.util.freemarker;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

public class InvokeStaticMethodModel implements TemplateMethodModelEx {
	private static final Logger LOG = LoggerFactory.getLogger(LoadJsonMethod.class);

	@Override
	@SuppressWarnings({"rawtypes"})
	public Object exec(List args) throws TemplateModelException {
		try {
			Class<?> cls = Class.forName((String) args.get(0));
			Method staticMethod = null;
			if (args == null) {

			} else if (args.size() == 2) {
				staticMethod = cls.getDeclaredMethod((String) args.get(1));
				return staticMethod.invoke(cls);
			} else if (args.size() >= 3) {
				switch (args.size()) {
					case 3:
						staticMethod = cls.getDeclaredMethod((String) args.get(1), args.get(2).getClass());
						return staticMethod.invoke(cls, args.get(2));
					case 4:
						staticMethod = cls.getDeclaredMethod((String) args.get(1), args.get(2).getClass(), args.get(3).getClass());
						return staticMethod.invoke(cls, args.get(2), args.get(3));
					case 5:
						staticMethod = cls.getDeclaredMethod((String) args.get(1), args.get(2).getClass(), args.get(3).getClass(), args.get(4).getClass());
						return staticMethod.invoke(cls, args.get(2), args.get(3), args.get(4));
				}
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

}
