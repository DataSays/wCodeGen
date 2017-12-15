package org.datasays.util.codegen;

import java.lang.annotation.Annotation;

public interface AnnotationClassParser<A extends Annotation> {
	public void parse(Class<A> clsModel, A a) throws Exception;
}
