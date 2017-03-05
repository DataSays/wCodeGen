package io.github.datasays.util;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by watano on 2017/1/21.
 */
public class YamlUtil {
	private static final Logger LOG = LoggerFactory.getLogger(YamlUtil.class);

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

	public static void write(WMap data, String file) {
		write(data, file, 0);
	}

	public static void write(WMap data, String file, int flowStyle) {
		Yaml yml = new Yaml();
		if (data == null) {
			return;
		}
		try {
			File outFile = new File(file);
			if (!outFile.exists()) {
				FileUtil.mkdirs(outFile.getParent());
				outFile.createNewFile();
			}
			DumperOptions.FlowStyle flowStyle1;
			if (flowStyle == 1) {
				flowStyle1 = DumperOptions.FlowStyle.FLOW;
			} else if (flowStyle == 2) {
				flowStyle1 = DumperOptions.FlowStyle.BLOCK;
			} else {
				flowStyle1 = DumperOptions.FlowStyle.AUTO;
			}
			String codes = yml.dumpAs(data, Tag.MAP, flowStyle1);
			FileUtil.writeString(outFile, codes, "utf-8");
		} catch (IOException e) {
			LOG.error("write yml file error!" + file);
			LOG.error(yml.dump(data));
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
}
