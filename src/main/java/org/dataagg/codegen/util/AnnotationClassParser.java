package org.dataagg.codegen.util;

import java.lang.annotation.Annotation;

public interface AnnotationClassParser<A extends Annotation> {
	public void parse(Class<A> clsModel, A a) throws Exception;
}
