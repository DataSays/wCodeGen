package org.dataagg.codegen.base;

import java.util.function.Consumer;

import org.dataagg.codegen.util.CodeGenUtils;
import org.dataagg.util.collection.StrObj;

public abstract class ACodeGenBase<M extends ADefBase<?>> {
	protected M def;
	public boolean mergeCode = true;
	protected String tmpDir = "../codeGen/tmp/";

	public abstract void genAllCode();

	public void genCodeByDef(M m) {
		def = m;
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

	public void genDebugModelJson(String name, StrObj model) {
		CodeGenUtils.genJson(tmpDir + name + ".json", model);
	}
}
