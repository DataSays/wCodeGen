package org.dataagg.util.text;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import jodd.io.FileUtil;
import jodd.util.StringUtil;

/**
 * Created by watano on 2017/1/21.
 */
public class YamlUtil {
	private static final Logger LOG = LoggerFactory.getLogger(YamlUtil.class);

	public static StrObj load(String codes) {
		Yaml yml = new Yaml();
		StrObj data = yml.loadAs(codes, StrObj.class);
		return data;
	}

	public static StrObj eval(String codes, StrObj propsObj) {
		if (propsObj != null) {
			for (String k : propsObj.keySet()) {
				codes = StringUtil.replace(codes, "${" + k + "}", propsObj.strVal(k));
			}
		}
		return load(codes);
	}

	public static StrObj evalSelf(String codes, String propsField) {
		if (propsField != null) {
			StrObj data = load(codes);
			StrObj propsObj = data.mapVal(propsField);
			if (propsObj != null) {
				return eval(codes, propsObj);
			} else {
				return data;
			}
		}
		return load(codes);
	}

	public static StrObj evalYml(String file, String propsField) throws IOException {
		String codes = FileUtil.readString(file, "utf-8");
		return evalSelf(codes, propsField);
	}

	public static StrObj evalYml(String file, StrObj propsObj) throws IOException {
		String codes = FileUtil.readString(file, "utf-8");
		return eval(codes, propsObj);
	}

	public static StrObj readFile(String file) {
		try {
			String codes = FileUtil.readString(file, "utf-8");
			return load(codes);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public static void write(StrObj data, String file) {
		write(data, file, 0);
	}

	public static void write(StrObj data, String file, int flowStyle) {
		DumperOptions dp = new DumperOptions();
		//dp.setDefaultScalarStyle(ScalarStyle.SINGLE_QUOTED);
		//		dp.setPrettyFlow(true);
		//dp.setCanonical(true);
		dp.setSplitLines(false);
		dp.setWidth(200);
		Yaml yml = new Yaml(dp);
		if (data == null) { return; }
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
