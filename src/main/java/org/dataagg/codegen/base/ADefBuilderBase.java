package org.dataagg.codegen.base;

import org.dataagg.util.props.PropDef;

public abstract class ADefBuilderBase<M extends ADefBase<D>, D extends PropDef> {
	public M main;

	public ADefBuilderBase(M m) {
		main = m;
	}

	public String getName() {
		return main.name;
	}

	protected D lastItem() {
		return main.lastItem();
	}
}
