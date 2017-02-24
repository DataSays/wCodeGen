package io.github.datasays.util;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.nutz.lang.util.NutMap;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 */
public class YamlUtil {

	@SuppressWarnings("unchecked")
	public static NutMap load(String codes) throws IOException {
		Yaml yml = new Yaml();
		Map<String, Object> data = (Map<String, Object>) yml.loadAs(codes, Map.class);
		return NutMap.WRAP(data);
	}

	public static NutMap evalYml(String file, String propsField) throws IOException {
		String codes = FileUtil.readString(file, "utf-8");
		if (propsField != null) {
			NutMap data = load(codes);
			NutMap propsObj = data.getAs(propsField, NutMap.class);
			if (propsObj != null) {
				for (String k : propsObj.keySet()) {
					codes = StringUtil.replace(codes, "${" + k + "}", propsObj.getString(k));
				}
			}
		}
		return load(codes);
	}
}
