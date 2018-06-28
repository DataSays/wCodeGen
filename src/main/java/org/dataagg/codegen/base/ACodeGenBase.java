package org.dataagg.codegen.base;

import java.util.function.Consumer;

import org.dataagg.codegen.util.CodeGenUtils;
import org.dataagg.util.lang.IStringHelper;
import org.dataagg.util.text.MapTplHelper;

public abstract class ACodeGenBase<M extends ADefBase<?>> implements IStringHelper {
	protected M def;
	public String baseDir = null;
	public boolean mergeCode = true;
	public String tmpDir = null;
	protected MapTplHelper mapTplHelper = new MapTplHelper();

	public ACodeGenBase(String baseDir, boolean mergeCode) {
		super();
		this.baseDir = baseDir;
		this.mergeCode = mergeCode;
	}

	public abstract void genAllCode();

	public void genCodeByDef(M m) {
		def = m;
		if (mapTplHelper == null) {
			mapTplHelper = new MapTplHelper();
		}
		genAllCode();
	}

	public void genAll(ADefBuilderBase<M, ?>[] allDefBuilders) {
		genAll(allDefBuilders, m -> {});
	}

	public void genAll(ADefBuilderBase<M, ?>[] allDefBuilders, Consumer<M> fun) {
		if (allDefBuilders != null) {
			for (ADefBuilderBase<M, ?> defBuilder : allDefBuilders) {
				fun.accept(defBuilder.main);
				genCodeByDef(defBuilder.main);
			}
		}
	}

	public void genDebugModelJson(String name, Object model) {
		CodeGenUtils.genJson(tmpDir + name + ".json", model);
	}
}
