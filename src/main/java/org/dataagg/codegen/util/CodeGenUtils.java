package org.dataagg.codegen.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

import org.dataagg.util.FindFileUtil;
import org.dataagg.util.WJsonUtils;
import org.dataagg.util.collection.StrMap;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.freemarker.FreemarkerHelper;
import org.dataagg.util.lang.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.bean.BeanUtilBean;
import jodd.io.FileUtil;
import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.util.StringTemplateParser;
import jodd.util.StringTemplateParser.MacroResolver;
import jodd.util.StringUtil;

public class CodeGenUtils {
	private static final Logger LOG = LoggerFactory.getLogger(CodeGenUtils.class);

	public static void genCode(String target, String code) {
		try {
			target = StringUtil.replace(target, "/", File.separator);
			target = StringUtil.replace(target, "\\", File.separator);
			target = StringUtil.cutSuffix(target, File.separator);

			LOG.info(target);
			File fTarget = new File(target);
			if (!fTarget.exists() && fTarget.getParentFile() != null) {
				FileUtil.mkdirs(fTarget.getParentFile());
				fTarget.createNewFile();
			}
			FileUtil.writeString(new File(target), code, TextUtils.Encoding);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void genCode(String target, String template, Object data) {
		try {
			String tpl = FileUtil.readString(template, TextUtils.Encoding);
			genCode(target, tpl(tpl, data));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void genCode(String target, String template, StrMap data) {
		try {
			String tpl = FileUtil.readString(template, TextUtils.Encoding);
			genCode(target, tpl(tpl, data));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static String genArticleContent(String template, StrObj data) {
		FreemarkerHelper helper = new FreemarkerHelper();
		helper.init();
		helper.setTplLoader("classpath:/template");
		return helper.process(template, data);
	}

	public static void genFtlCode(String template, StrObj data, String... pathFragments) {
		String targetFile = buildPath(pathFragments);
		FreemarkerHelper helper = new FreemarkerHelper();
		helper.init();
		helper.setTplLoader("../codegen");
		helper.process(template, data, targetFile);
	}

	public static void genFtlCode4Obj(String template, Object data, String... pathFragments) {
		String targetFile = buildPath(pathFragments);
		FreemarkerHelper helper = new FreemarkerHelper();
		helper.init();
		helper.setTplLoader("../codegen");
		helper.processObj(template, data, targetFile);
	}

	public static void genJson(String target, Object data) {
		String json = WJsonUtils.toJson(data, true);
		genCode(target, json);
	}

	private static String tplParse(String template, MacroResolver macroResolver) {
		StringTemplateParser stp = new StringTemplateParser();
		stp.setReplaceMissingKey(false);
		return stp.parse(template, macroName -> {
			String value = macroResolver.resolve(macroName);
			if (value == null) {
				LOG.warn("Can't find the '" + macroName + "'!");
			}
			return value;
		});
	}

	public static String tpl(String template, final StrMap params) {
		return tplParse(template, macroName -> params.get(macroName));
	}

	public static String tpl(String template, final Object params) {
		return tplParse(template, macroName -> new BeanUtilBean().getProperty(params, macroName).toString());
	}

	/**
	 *
	 * @param template
	 * @param params
	 * @param obj
	 * @return
	 */
	public static String tpl(String template, final StrMap params, final Object obj) {
		String result = tplParse(template, macroName -> {
			String value = params.get(macroName);
			if (value == null) {
				value = new BeanUtilBean().getProperty(obj, macroName).toString();
			}
			return value;
		});
		return result;
	}

	public static void findAllClass(String srcPath, String pkg, Consumer<Class<?>> fn) {
		try {
			String dir = buildPath(srcPath, StringUtil.replace(pkg, ".", File.separator));
			if (LOG.isDebugEnabled()) {
				LOG.debug("findAllClass in srcPath:" + new File(dir).getAbsolutePath());
			}
			Iterator<File> iterator = FindFileUtil.search(true, false, dir);
			String rootPath = new File(srcPath).getCanonicalPath();
			while (iterator.hasNext()) {
				try {
					File f = iterator.next();
					if (f.isFile()) {
						String clsName = f.getCanonicalPath();
						if (f.getName().endsWith(".java")) {
							clsName = clsName.substring(rootPath.length() + 1, clsName.length() - 5);
							clsName = clsName.replace(File.separatorChar, '.');
						} else {
							continue;
						}
						Class<?> clsModel = Class.forName(clsName);
						fn.accept(clsModel);
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <A extends Annotation> void findClassByAnnotation(String srcPath, String pkg, Class<A> annotationClass, AnnotationClassParser<A> parser) {
		findAllClass(srcPath, pkg, clsModel -> {
			A a = clsModel.getAnnotation(annotationClass);
			if (a != null) {
				try {
					parser.parse((Class<A>) clsModel, a);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		});
	}

	public static String rmPostfix(String text, String postfix) {
		String out = text;
		if (out.endsWith(postfix)) {
			out = out.substring(0, out.length() - postfix.length());
		}
		return out;
	}

	public static String rmPrefix(String text, String prefix) {
		String out = text;
		if (out.startsWith(prefix)) {
			out = out.substring(prefix.length());
		}
		return out;
	}

	public static <T> T[] sort(T[] array) {
		Arrays.sort(array, (o1, o2) -> o1.toString().compareTo(o2.toString()));
		return array;
	}

	public static String[] getMethodArgNames(Class<?> clazz, AccessibleObject methd) {
		try {
			MethodParameter[] mp = Paramo.resolveParameters(methd);
			String[] paramNames = new String[mp.length];
			for (int i = 0; i < mp.length; i++) {
				paramNames[i] = mp[i].getName();
			}
			//			ClassPool pool = ClassPool.getDefault();
			//			CtClass cc = pool.get(clazz.getName());
			//			CtMethod cm = cc.getDeclaredMethod(methd);
			//
			//			// 使用javaassist的反射方法获取方法的参数名
			//			MethodInfo methodInfo = cm.getMethodInfo();
			//			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			//			LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
			//			if (attr == null) {
			//				// exception
			//			}
			//			String[] paramNames = new String[cm.getParameterTypes().length];
			//			int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
			//			for (int i = 0; i < paramNames.length; i++) {
			//				paramNames[i] = attr.variableName(i + pos);
			//			}
			return paramNames;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public static String simpleClsText(String pkg, String fullCls) {
		String type = fullCls;
		int index = fullCls.lastIndexOf('.');
		if (index > 0) {
			if (pkg != null && pkg.equals(fullCls.substring(0, index))) {
				type = fullCls.substring(index + 1);
			} else if ("java.lang".equals(fullCls.substring(0, index))) {
				type = fullCls.substring(index + 1);
			}
		}
		return type;
	}

	public static String simpleClsText(String pkg, Class<?> cls) {
		if (cls.isArray()) {
			return simpleClsText(pkg, cls.getComponentType().getName()) + "[]";
		} else {
			return simpleClsText(pkg, cls.getName());
		}
	}

	public static String buildPath(String... pathFragments) {
		String path = "";
		for (String pathFragment : pathFragments) {
			pathFragment = pathFragment.trim();
			pathFragment = StringUtil.replace(pathFragment, "/", File.separator);
			pathFragment = StringUtil.replace(pathFragment, "\\", File.separator);
			if (!pathFragment.endsWith(File.separator)) {
				pathFragment += File.separator;
			}
			path += pathFragment;
		}
		path = StringUtil.cutSuffix(path, File.separator);
		return path;
	}
}
