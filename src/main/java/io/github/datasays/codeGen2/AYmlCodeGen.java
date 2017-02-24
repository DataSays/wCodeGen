package io.github.datasays.codeGen2;

import io.github.datasays.util.YamlUtil;
import org.nutz.lang.util.NutMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by watano on 2017/2/24.
 */
public abstract class AYmlCodeGen {
	private static final Logger LOG = LoggerFactory.getLogger(AYmlCodeGen.class);
	protected NutMap model = null;
	protected NutMap data = null;
	protected NutMap props = null;
	//gen code root dir
	protected String workDir = null;
	//gen type
	protected String genType = null;

	public void init() {
		model = new NutMap();
	}

	public void load(String dataFile) {
		try {
			//load data yml files, the "props" is the local vars;
			data = YamlUtil.evalYml(dataFile, "props");
			model.setv("data", data);
			props = data.getAs("props", NutMap.class);
			//gen code root dir
			workDir = data.getString("WorkDir", ".");
			model.put("WorkDir", workDir);
			//gen type
			genType = data.getString("GenType", "gradle");
			model.put("GenType", genType);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void gen(String dataFile) {
		load(dataFile);
		gen();
	}

	public void genAll(String... dataFiles) {
		if (dataFiles != null) {
			for (String dataFile : dataFiles) {
				gen(dataFile);
			}
		}
	}

	public abstract void gen();
}
