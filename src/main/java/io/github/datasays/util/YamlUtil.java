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

	@SuppressWarnings("unchecked")
	public static WMap load(String codes) {
		Yaml yml = new Yaml();
		Map<String, Object> data = (Map<String, Object>) yml.loadAs(codes, Map.class);
		return new WMap(data);
	}

	public static WMap eval(String codes, WMap propsObj) {
		if (propsObj != null) {
			for (String k : propsObj.keySet()) {
				codes = StringUtil.replace(codes, "${" + k + "}", propsObj.getString(k));
			}
		}
		return load(codes);
	}

	public static WMap evalSelf(String codes, String propsField) {
		if (propsField != null) {
			WMap data = load(codes);
			WMap propsObj = data.getAs(propsField, WMap.class);
			if (propsObj != null) {
				return eval(codes, propsObj);
			} else {
				return data;
			}
		}
		return load(codes);
	}

	public static WMap evalYml(String file, String propsField) throws IOException {
		String codes = FileUtil.readString(file, "utf-8");
		return evalSelf(codes, propsField);
	}

	public static WMap evalYml(String file, WMap propsObj) throws IOException {
		String codes = FileUtil.readString(file, "utf-8");
		return eval(codes, propsObj);
	}
}
