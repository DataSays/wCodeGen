package io.github.datasays.util;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.datasays.util.JsonObjGetter;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 */
public class YamlUtil {
	public static JsonObjGetter load(String file) throws IOException {
		return loadAndEval(file, null);
	}

	public static JsonObjGetter loadAndEval(String file, String propsField) throws IOException {
		Yaml yml = new Yaml();
		String codes = FileUtil.readString(file, "utf-8");
		if (propsField != null) {
			JsonObjGetter data = new JsonObjGetter(yml.loadAs(codes, Map.class));
			JsonObjGetter propsObj = data.obj(propsField);
			if (propsObj != null && propsObj instanceof Map) {
				for (Object k : propsObj.map().keySet()) {
					codes = StringUtil.replace(codes, "${" + k.toString() + "}", propsObj.str(k));
				}
			}
		}
		return new JsonObjGetter(yml.loadAs(codes, Map.class));
	}
}
