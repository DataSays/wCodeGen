package io.github.datasays.util;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 */
public class YamlUtil {
	public static Map<?, ?> load(String file) throws IOException {
		return loadAndEval(file, null);
	}

	public static Map<String, Object> loadAndEval(String file, String propsField) throws IOException {
		Yaml yml = new Yaml();
		String codes = FileUtil.readString(file, "utf-8");
		if (propsField != null) {
			Map<String, Object> data = (Map<String, Object>) yml.loadAs(codes, Map.class);
			Object propsObj = data.get(propsField);
			if (propsObj != null && propsObj instanceof Map) {
				Map<?, ?> props = (Map<?, ?>) propsObj;
				for (Object k : props.keySet()) {
					codes = StringUtil.replace(codes, "${" + k.toString() + "}", props.get(k).toString());
				}
			}
		}
		return (Map<String, Object>) yml.loadAs(codes, Map.class);
	}
}
