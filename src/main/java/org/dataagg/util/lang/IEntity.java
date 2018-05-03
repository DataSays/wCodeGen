package org.dataagg.util.lang;

import java.io.Serializable;

public interface IEntity<T> extends Serializable {
	public default String getField(){
		return "id";
	}

	public default boolean isNullPk(){
		T pk = getId();
		return pk == null || (pk instanceof Number && ((Number) pk).intValue() <= 0) || (pk instanceof String && ((String)pk).trim().length()<=0);
	}

	public T getId();

	public void setId(T id);



}
