package org.dataagg.codegen.base;

import java.util.List;

import org.dataagg.codegen.util.CodeGenHelper;
import org.dataagg.util.collection.StrObj;
import org.dataagg.util.lang.IStringHelper;
import org.dataagg.util.props.PropDef;
import org.dataagg.util.props.PropSet;

import jodd.util.StringUtil;

public abstract class ADefBase<I extends PropDef> implements PropSet<I>, IStringHelper {
	private static final long serialVersionUID = -5924670759336190365L;
	public String name;//名称
	public String project;//所属项目
	public String pkg;//包名
	public String comments;//备注

	public StrObj cfg;//额外配置
	public List<I> defs;//Item定义

	public ADefBase(String name) {
		this.name = name;
	}

	public static String javaSrcPath(String project, String pkg) {
		String outDir = pkg;
		outDir = StringUtil.replace(outDir, ".", "/");
		return String.format("/%s/src/main/java/%s", project, outDir);
	}

	public static String testJavaSrcPath(String project, String pkg) {
		String outDir = pkg;
		outDir = StringUtil.replace(outDir, ".", "/");
		return String.format("/%s/src/test/java/%s", project, outDir);
	}

	public String javaFile(String pkg, String name) {
		return String.format("%s/%s.java", javaSrcPath(project, pkg), capFirst(name));
	}

	public String testJavaFile(String pkg, String name) {
		return String.format("%s/%s.java", testJavaSrcPath(project, pkg), capFirst(name));
	}

	public void common(String project, String pkg, String comments) {
		this.project = project;
		this.pkg = pkg;
		this.comments = comments;
	}

	public I lastItem() {
		return defs.get(defs.size() - 1);
	}

	public static String genName(String entityCls) {
		String cls = entityCls;
		if (cls.startsWith("Entity")) {

		} else if (cls.startsWith("E")) {
			cls = cls.substring(1);
		} else if (cls.startsWith("EUI")) {
			cls = "UI" + cls.substring(3);
		} else if (cls.startsWith("UI")) {
			cls = "UI" + cls.substring(2);
		}
		return cls;
	}

	public static String genNameL(String entityCls) {
		String cls = genName(entityCls);
		cls = StringUtil.uncapitalize(cls);
		if (cls.startsWith("uI")) {
			cls = "ui" + cls.substring(2);
		}
		return cls;
	}

	public static String genNameU(String entityCls) {
		String cls = genName(entityCls);
		cls = StringUtil.capitalize(cls);
		if (cls.startsWith("Ui")) {
			cls = "UI" + cls.substring(2);
		}
		return cls;
	}

	public StrObj addCfg(String name, Object value) {
		if (value == null) { return cfg; }
		if (cfg == null) {
			cfg = new StrObj(name, value);
			setCfg(cfg);
		} else {
			cfg.put(name, value);
		}
		return cfg;
	}

	public Object getCfg(String name) {
		return cfg == null ? null : cfg.get(name);
	}

	public String getRootPkg() {
		return pkg.substring(0, pkg.lastIndexOf("."));
	}

	@Override
	public List<I> getDefs() {
		return defs;
	}

	@Override
	public void setDefs(List<I> defs) {
		this.defs = defs;
	}

	public String getName() {
		return name;
	}

	public StrObj getCfg() {
		return cfg;
	}

	public void setCfg(StrObj cfg) {
		this.cfg = cfg;
	}
}
