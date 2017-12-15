package org.dataagg.codegen.base;

public abstract class ACodeGenBase<M extends ADefBase<?>> {
	protected M def;
	public boolean mergeCode = true;

	public abstract void genAllCode();

	public void genCodeByDef(M m) {
		def = m;
		genAllCode();
	}

	public void genAll(ADefBuilderBase<M, ?>[] allDefBuilders) {
		for (ADefBuilderBase<M, ?> defBuilder : allDefBuilders) {
			genCodeByDef(defBuilder.main);
		}
	}
}
