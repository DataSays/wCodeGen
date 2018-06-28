package org.dataagg.util.lang;

import java.util.List;
import java.util.Map;

import org.dataagg.util.collection.StrObj;

import jodd.typeconverter.Converter;
import jodd.typeconverter.TypeConverterManager;

public interface ITypeHelper {
	public default Boolean boolVal(Object key) {
		try {
			return converter().toBoolean(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Boolean boolVal(Object key, Boolean defaultVal) {
		try {
			return converter().toBoolean(get(key), defaultVal);
		} catch (Exception e) {
			return null;
		}
	}

	public default Byte byteVal(Object key) {
		try {
			return converter().toByte(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Byte byteVal(Object key, Byte defaultVal) {
		try {
			return converter().toByte(get(key), defaultVal);
		} catch (Exception e) {
			return null;
		}
	}

	public default Character charVal(Object key) {
		try {
			return converter().toCharacter(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Character charVal(Object key, Character defaultVal) {
		try {
			return converter().toCharacter(get(key), defaultVal);
		} catch (Exception e) {
			return null;
		}
	}

	public default Converter converter() {
		return Converter.get();
	}

	public default TypeConverterManager converter2() {
		return new TypeConverterManager(converter());
	}

	public default Double doubleVal(Object key) {
		try {
			return converter().toDouble(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Double doubleVal(Object key, Double defaultVal) {
		try {
			return converter().toDouble(get(key), defaultVal);
		} catch (Exception e) {
			return null;
		}
	}

	public default Float floatVal(Object key) {
		try {
			return converter().toFloat(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Float floatVal(Object key, Float defaultVal) {
		try {
			return converter().toFloat(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public Object get(Object key);

	public default Integer intVal(Object key) {
		try {
			return converter().toInteger(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Integer intVal(Object key, Integer defaultVal) {
		try {
			return converter().toInteger(get(key), defaultVal);
		} catch (Exception e) {
			return null;
		}
	}

	public default <T> List<T> listVal(String key, Class<T> componentType) {
		try {
			return converter2().convertToCollection(get(key), List.class, componentType);
		} catch (Exception e) {
			return null;
		}
	}

	public default Long longVal(Object key) {
		try {
			return converter().toLong(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Long longVal(Object key, Long defaultVal) {
		try {
			return converter().toLong(get(key), defaultVal);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public default StrObj mapVal(String key) {
		try {
			Object val = get(key);
			if (val != null && val instanceof Map) {
				StrObj map = new StrObj();
				map.putAll((Map<String, Object>) val);
				return map;
			} else if (val != null && val instanceof StrObj) { return (StrObj) val; }
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public default String strVal(Object key) {
		try {
			return converter().toString(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default String strVal(Object key, String defaultVal) {
		try {
			return converter().toString(get(key), defaultVal);
		} catch (Exception e) {
			return null;
		}
	}

	public default String[] strArrayVal(Object key) {
		try {
			return converter().toStringArray(get(key));
		} catch (Exception e) {
			return null;
		}
	}

	public default Object val(Object key, Class<?> componentType) {
		try {
			return converter2().convertType(get(key), componentType);
		} catch (Exception e) {
			return null;
		}
	}
}
