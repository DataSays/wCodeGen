package org.dataagg.codegen.util;

import jodd.util.StringUtil;
import org.dataagg.codegen.model.UIDefBuilder;
import org.dataagg.codegen.model.UIItemDef;
import org.dataagg.util.WJsonUtils;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.dataagg.codegen.model.UIDef.CFG_InnerHtml;
import static org.dataagg.codegen.model.UIDef.CFG_InnerHtml2;

public class ElementUI {
	private static final Logger LOG = LoggerFactory.getLogger(ElementUI.class);

	public static final int TYPE_Custom = 0;//自定义的组件
	public static final int TYPE_Hidden = 10;//隐藏字段
	public static final int TYPE_Text = 20;//普通文本
	public static final int TYPE_TextArea = 30;//多行文本
	public static final int TYPE_RiceText = 40;//富文本
	public static final int TYPE_Password = 50;//密码类型
	public static final int TYPE_InputNumber = 60;//计数器
	public static final int TYPE_Slider = 70;//滑块
	public static final int TYPE_Switch = 80;//开关
	public static final int TYPE_Rate = 90;//评分
	public static final int TYPE_Color = 100;//颜色
	public static final int TYPE_ColorPicker = 110;//颜色选择器
	public static final int TYPE_CheckBox = 120;//多选框
	public static final int TYPE_CheckBoxGroup = 125;//多选框组
	public static final int TYPE_Radio = 130;//单选
	public static final int TYPE_RadioGroup = 135;//单选组
	public static final int TYPE_Select = 140;//选择器
	public static final int TYPE_Option = 145;//选择器选项
	public static final int TYPE_Cascader = 150;//级联选择器
	public static final int TYPE_Date = 160;//日期
	public static final int TYPE_Time = 170;//时间
	public static final int TYPE_DateTime = 180;//日期时间
	public static final int TYPE_DateRange = 190;//日期范围选择
	public static final int TYPE_TimeRange = 200;//时间范围选择
	public static final int TYPE_DateTimeRange = 210;//日期+时间范围选择
	public static final int TYPE_Upload = 220;//文件上传
	public static final int TYPE_Icon = 230;//图标
	public static final int TYPE_Transfer = 240;//颜色选择器
	public static final int TYPE_Table = 250;//表格显示
	public static final int TYPE_TableColumn = 260;//表格显示
	public static final int TYPE_Button = 270;//按钮
	public static final int TYPE_Form = 280;//表单
	public static final int TYPE_FormItem = 290;//表单项
	public static final int TYPE_Tag = 300;//标签
	public static final int TYPE_Progress = 310;//进度条
	public static final int TYPE_Tree = 320;//树
	public static final int TYPE_Pagination = 330;//分页
	public static final int TYPE_Badge = 340;//标记
	public static final int TYPE_Tabs = 350;//标签页
	public static final int TYPE_TabPane = 360;//标签面板
	public static final int TYPE_Breadcrumb = 370;//面包屑
	public static final int TYPE_Dropdown = 380;//下拉菜单
	public static final int TYPE_DropdownMenu = 390;//下拉菜单
	public static final int TYPE_DropdownItem = 400;//下拉菜单项
	public static final int TYPE_Steps = 410;//步骤条
	public static final int TYPE_Step = 420;//步骤项
	public static final int TYPE_Card = 430;//卡片
	public static final int TYPE_Carousel = 440;//轮播图
	public static final int TYPE_CarouselItem = 450;//轮播图项
	public static final int TYPE_Collapse = 460;//折叠面板
	public static final int TYPE_CollapseItem = 470;//折叠面板项
	public static final int TYPE_Row = 480;//行容器
	public static final int TYPE_Col = 490;//列容器

	public static final int TYPE_FieldSet = 600;//表单元素分组
	public static final int TYPE_ELText = 610;//基于表达式的文本显示

	/**
	 * Eelement UI中单值的bool型属性key列表, 这些属性只有在为true时才显示
	 */
	private static final String SingleAttributes = "|filterable|remote|multiple|reserve-keyword|autosize|stripe|border|highlight-current-row|resizable|change-on-select|clearable|inline|";

	/**
	 * 新建一个UI组件，并配置基本信息
	 *
	 * @param type  组件类型
	 * @param cfgKV UI组件配置的的key&value键值对,详细组件配置，请参考文档 http://element-cn.eleme.io/#/zh-CN/component/installation 和UIDef#CFG_*注释
	 * @return
	 */
	public static UIItemDef create(int type, String... cfgKV) {
		UIItemDef uiItemDef = new UIItemDef();
		return UIDefBuilder.cfg(uiItemDef, type, cfgKV);
	}

	public static UIItemDef group(int type, UIItemDef... children) {
		UIItemDef uiItemDef = new UIItemDef();
		uiItemDef = UIDefBuilder.cfg(uiItemDef, type);
		uiItemDef.addAllChildren(children);
		return uiItemDef;
	}

	public static UIItemDef custom(String tag, String... cfgKV) {
		UIItemDef uiItemDef = new UIItemDef();
		uiItemDef.addCfg("_tagName", tag);
		return UIDefBuilder.cfg(uiItemDef, TYPE_Custom, cfgKV);
	}

	/**
	 * 创建一个button按钮
	 *
	 * @param id
	 * @param text
	 * @param cfgKV
	 * @return
	 */
	public static UIItemDef btn(String id, String text, String... cfgKV) {
		UIItemDef uiItemDef = new UIItemDef(id, text);
		return UIDefBuilder.cfg(uiItemDef, TYPE_Button, cfgKV);
	}

	public static UIItemDef elText(String elText, String... cfgKV) {
		UIItemDef uiItemDef = new UIItemDef();
		uiItemDef.subType = "1";
		uiItemDef.title = elText;
		return UIDefBuilder.cfg(uiItemDef, TYPE_ELText, cfgKV);
	}

	public static UIItemDef textArea(String... cfgKV) {
		UIItemDef uiItemDef = new UIItemDef();
		return UIDefBuilder.cfg(uiItemDef, TYPE_TextArea, cfgKV);
	}

	/**
	 * 使用elCode的表达式直接显示
	 *
	 * @param elCode
	 * @return
	 */
	public static UIItemDef elCode(String elCode) {
		UIItemDef uiItemDef = new UIItemDef();
		uiItemDef.subType = "2";
		return UIDefBuilder.cfg(uiItemDef, TYPE_ELText, CFG_InnerHtml, elCode);
	}

	/**
	 * 使用showExtData中的对应字段数据显示
	 *
	 * @param key
	 * @return
	 */
	public static UIItemDef elShowExtData(String key) {
		return elCode("{{showExtData(scope, \"" + key + "\")}}");
	}

	/**
	 * 自定义一个Filter显示结果
	 *
	 * @param el     表达式
	 * @param filter
	 * @param params
	 * @param codes
	 * @return
	 */
	public static UIItemDef elLocalFilter(String el, String filter, String params, String codes) {
		UIItemDef uiItemDef = elCode("{{" + el + " | " + filter + (params != null ? "(" + params + ")" : "") + "}}");
		uiItemDef.subType = "3";
		uiItemDef.addCfg("_filter", filter);
		uiItemDef.addCfg("_filterParams", params);
		uiItemDef.addCfg("_filterCode", codes);
		return uiItemDef;
	}

	public static UIItemDef dateRange() {
		UIItemDef uiItemDef = new UIItemDef();
		return UIDefBuilder.cfg(uiItemDef, TYPE_DateRange, "type", "daterange", "range-separator", "至", "start-placeholder", "开始日期", "end-placeholder", "结束日期");
	}

	public static UIItemDef fieldset(String field, String title) {
		UIItemDef uiItemDef = new UIItemDef(field, title);
		return UIDefBuilder.cfg(uiItemDef, TYPE_FieldSet);
	}

	public static UIItemDef table(String field, String title) {
		UIItemDef uiItemDef = new UIItemDef(field, title);
		return UIDefBuilder.cfg(uiItemDef, TYPE_Table);
	}

	public static UIItemDef tableColNo(String field, String title) {
		UIItemDef uiItemDef = new UIItemDef(field, title);
		uiItemDef.subType = "1";
		return UIDefBuilder.cfg(uiItemDef, TYPE_TableColumn);
	}

	public static UIItemDef tableColProp(String prop, String label, String width) {
		UIItemDef uiItemDef = new UIItemDef(prop, label);
		if (width != null) {
			uiItemDef.addCfg("width", width);
		}
		uiItemDef.addCfg("prop", prop);
		uiItemDef.addCfg("label", label);
		uiItemDef.subType = "2";
		return UIDefBuilder.cfg(uiItemDef, TYPE_TableColumn);
	}

	public static UIItemDef tableColInline(String field, UIItemDef... children) {
		UIItemDef uiItemDef = new UIItemDef(field, field);
		uiItemDef.addCfg("type", "expand");
		uiItemDef.addAllChildren(children);
		uiItemDef.subType = "3";
		return UIDefBuilder.cfg(uiItemDef, TYPE_TableColumn);
	}

	public static UIItemDef formItem(String el, String label) {
		UIItemDef uiItemDef = new UIItemDef("", label);
		uiItemDef.addCfg("label", label);
		uiItemDef.addChild(elText(el));
		return UIDefBuilder.cfg(uiItemDef, TYPE_FormItem);
	}

	public static UIItemDef radioGroup(String dictName) {
		UIItemDef uiItemDef = new UIItemDef();
		uiItemDef.type = TYPE_RadioGroup + "";
		uiItemDef.addChild(create(TYPE_Radio, "v-for", "item in allDict." + dictName, ":label", "item.value", ":key", "item.id", CFG_InnerHtml, "{{item.label}}"));
		return uiItemDef;
	}

	/**
	 * 使用字典数据显示的下拉菜单
	 * @param dictName
	 * @return
	 */
	public static UIItemDef select(String dictName) {
		UIItemDef uiItemDef = new UIItemDef();
		uiItemDef.type = TYPE_Select + "";
		uiItemDef.addChild(create(TYPE_Option, "v-for", "item in allDict." + dictName, ":label", "item.label", ":key", "item.id", ":value", "item.value"));
		return uiItemDef;
	}

	/**
	 * 使用action数据显示的下拉菜单
	 * @param id
	 * @param action
	 * @param props
	 * @return
	 */
	public static UIItemDef select(String id, String action, StrObj props) {
		UIItemDef uiItemDef = create(TYPE_Select, "filterable", "true", "remote", "true", ":remote-method", "load" + id, "@change", "change" + id, "_id", id);
		uiItemDef.subType = "2";
		uiItemDef.addChild(create(TYPE_Option, "v-for", "item in " + id + "Options", ":label", "item." + props.strVal("label", "label"), ":key", "item." + props.strVal("key", "id"), ":value", "item." + props.strVal("value", "value")));
		return uiItemDef;
	}

	/**
	 * 级联选择器
	 * @param id
	 * @param props
	 * @return
	 */
	public static UIItemDef cascader(String id, StrObj props) {
		UIItemDef uiItemDef = new UIItemDef();
		return UIDefBuilder.cfg(uiItemDef, TYPE_Cascader, ":options", id + "Options", "@change", "change" + id, ":props", id + "Props", "_id", id, "_propsVal", WJsonUtils.toJson(props));
	}

	//根据num(一行显示数量)设置响应式布局的cls属性
	public static String colProfile(String profile) {
		if (profile == "2") {
			return ":xs=\"24\" :sm=\"12\" :md=\"12\" :lg=\"12\"";
		} else if (profile == "3") {
			return ":xs=\"24\" :sm=\"12\" :md=\"8\" :lg=\"8\"";
		} else if (profile == "4") {
			return ":xs=\"24\" :sm=\"24\" :md=\"6\" :lg=\"6\"";
		} else {
			return ":xs=\"24\" :sm=\"24\" :md=\"24\" :lg=\"24\"";
		}
	}

	/**
	 * 获得组件的vuejs的html code, 不包含子元素
	 *
	 * @param uiItemDef
	 * @return
	 */
	public static String genVueCode(UIItemDef uiItemDef, JSCoder coder) {
		StrObj itemCfg = uiItemDef.cfg != null ? uiItemDef.cfg : new StrObj();
		CodeBlock codeBlock = coder.codeBlock;
		if ((TYPE_ELText + "").equals(uiItemDef.type) && "1".equals(uiItemDef.subType)) {
			return "{{" + uiItemDef.field + "}}";
		} else if ((TYPE_ELText + "").equals(uiItemDef.type) && "2".equals(uiItemDef.subType)) {
			return itemCfg.strVal(CFG_InnerHtml);
		} else if ((TYPE_ELText + "").equals(uiItemDef.type) && "3".equals(uiItemDef.subType)) {
			String filterParams = itemCfg.strVal("_filterParams");
			if (filterParams == null) {
				filterParams = "";
			} else {
				filterParams = ", " + filterParams;
			}
			codeBlock.startBlock("filters");
			codeBlock.appendPlainCode(itemCfg.strVal("_filter") + ": function(value" + filterParams + ") {\n");
			codeBlock.appendPlainCode(itemCfg.strVal("_filterCode"));
			codeBlock.appendPlainCode("\n},");
			codeBlock.endBlock();
			return itemCfg.strVal(CFG_InnerHtml);
		}
		StringBuffer sbCodes = new StringBuffer();
		String tag = "";
		switch (Integer.parseInt(uiItemDef.type)) {
		case TYPE_Custom:
			tag = itemCfg.strVal("_tagName", "div");
			break;
		case TYPE_Radio:
			tag = "el-radio";
			break;
		case TYPE_RadioGroup:
			tag = "el-radio-group";
			break;
		case TYPE_CheckBox:
			tag = "el-checkbox";
			break;
		case TYPE_CheckBoxGroup:
			tag = "el-checkbox-group";
			break;
		case TYPE_Text:
			tag = "el-input";
			uiItemDef.addCfg("type", "text");
			break;
		case TYPE_Password:
			tag = "el-input";
			uiItemDef.addCfg("type", "password");
			break;
		case TYPE_TextArea:
			tag = "el-input";
			uiItemDef.addCfg("type", "textarea");
			if (uiItemDef.getCfg(":row") == null) {
				uiItemDef.addCfg(":row", "5");
			}
			break;
		case TYPE_InputNumber:
			tag = "el-input-number";
			break;
		case TYPE_Select:
			tag = "el-select";
			if ("2".equals(uiItemDef.subType)) {
				//远程获取Options数据
				String selectId = itemCfg.strVal("_id", "select");
				codeBlock.startBlock("dataInit");
				coder.appendln2(selectId + "Options: [],");
				codeBlock.endBlock();

				codeBlock.startBlock("jsMethods");
				coder.jsMethod(itemCfg.strVal(":remote-method", "load" + selectId), "queryText", "");
				coder.jsMethod(itemCfg.strVal("@change", "change" + selectId), "value", "");
				codeBlock.endBlock();
			}
			break;
		case TYPE_Option:
			tag = "el-option";
			break;
		case TYPE_Cascader:
			tag = "el-cascader";
			String cascaderId = itemCfg.strVal("_id", "cascader");
			codeBlock.startBlock("dataInit");
			coder.appendln2(cascaderId + "Props: " + itemCfg.strVal("_propsVal", "[]") + ",");
			coder.appendln2(cascaderId + "Options: [],");
			codeBlock.endBlock();

			codeBlock.startBlock("jsMethods");
			coder.jsMethod(itemCfg.strVal("@change", "change" + cascaderId), "value", "");
			codeBlock.endBlock();
			break;
		case TYPE_Switch:
			tag = "el-switch";
			break;
		case TYPE_Slider:
			tag = "el-slider";
			break;
		case TYPE_Time:
			tag = "el-time-select";
			break;
		case TYPE_TimeRange:
			tag = "el-time-select";
			uiItemDef.addCfg("is-range", "");
			break;
		case TYPE_Date:
			tag = "el-date-picker";
			break;
		case TYPE_DateRange:
			tag = "el-date-picker";
			uiItemDef.addCfg("type", "daterange");
			break;
		case TYPE_DateTime:
			tag = "el-date-picker";
			uiItemDef.addCfg("type", "datetime");
			break;
		case TYPE_DateTimeRange:
			tag = "el-date-picker";
			uiItemDef.addCfg("type", "datetimerange");
			break;
		case TYPE_Upload:
			tag = "el-upload";
			codeBlock.startBlock("dataInit");
			coder.appendln2(itemCfg.strVal(":file-list", "filesList") + ": [],");
			codeBlock.endBlock();
			codeBlock.startBlock("jsMethods");
			coder.jsMethod(itemCfg.strVal(":on-success", "filesSuccess"), "response, file, fileList", "var self = this;\n" + "if (typeof self.entity.attachment !== 'string' || self.entity.attachment === '') {\n" + "   self.entity.attachment = response.data.data.grouping;\n" + "}\n" + "self.attachmentList = fileList;\n" + "common.okMsg('文件上传成功');");
			coder.jsMethod(itemCfg.strVal(":on-error", "filesError"), "response, file, fileList", "var self = this;\n" + "var msg = '';\n" + "if (file.size > 104857600) {\n" + "   msg = '，文件大于100M，不允许上传';\n" + "}\n" + "common.errorMsg('文件上传失败' + msg);");
			coder.jsMethod(itemCfg.strVal(":on-progress", "filesProgress"), "response, file, fileList", "");
			coder.jsMethod(itemCfg.strVal(":on-preview", "filesPreview"), "file", "window.open(file.url);");
			coder.jsMethod(itemCfg.strVal(":on-remove", "filesRemove"), "response, file, fileList", "if (file.response !== undefined) {\n" + "   fileAction.doDelete(file.response.data.data.id, response => {});\n" + "} else {\n" + "   fileAction.doDelete(file.id, response => {});\n" + "}");
			coder.jsMethod(itemCfg.strVal(":before-upload", "filesBUpload"), "file", "var self = this;\n" + "if (file.size > 104857600) {\n" + "   common.errorMsg('文件大于100M，不允许上传');\n" + "   return false;\n" + "} else {\n" + "   return true;\n" + "}");
			codeBlock.endBlock();
			break;
		case TYPE_Rate:
			tag = "el-rate";
			break;
		case TYPE_ColorPicker:
			tag = "el-color-picker";
			break;
		case TYPE_Transfer:
			tag = "el-transfer";
			break;
		case TYPE_Form:
			tag = "el-form";
			break;
		case TYPE_FormItem:
			tag = "el-form-item";
			break;
		case TYPE_Button:
			tag = "el-button";
			break;
		case TYPE_Table:
			tag = "el-table";
			break;
		case TYPE_TableColumn:
			tag = "el-table-column";
			break;
		case TYPE_Tag:
			tag = "el-tag";
			break;
		case TYPE_Progress:
			tag = "el-progress";
			break;
		case TYPE_Tree:
			tag = "el-tree";
			break;
		case TYPE_Pagination:
			tag = "el-pagination";
			break;
		case TYPE_Badge:
			tag = "el-badge";
			break;
		case TYPE_Tabs:
			tag = "el-tabs";
			break;
		case TYPE_TabPane:
			tag = "el-tab-pane";
			break;
		case TYPE_Breadcrumb:
			tag = "el-breadcrumb ";
			break;
		case TYPE_Dropdown:
			tag = "el-dropdown";
			break;
		case TYPE_DropdownMenu:
			tag = "el-dropdown-menu";
			break;
		case TYPE_DropdownItem:
			tag = "el-dropdown-item";
			break;
		case TYPE_Steps:
			tag = "el-steps";
			break;
		case TYPE_Step:
			tag = "el-step";
			break;
		case TYPE_Card:
			tag = "el-card";
			break;
		case TYPE_Carousel:
			tag = "el-carousel";
			break;
		case TYPE_CarouselItem:
			tag = "el-carousel-item";
			break;
		case TYPE_Collapse:
			tag = "el-collapse";
			break;
		case TYPE_CollapseItem:
			tag = "el-collapse-item";
			break;
		case TYPE_FieldSet:
			tag = null;
			break;
		case TYPE_Row:
			tag = "el-row";
			break;
		case TYPE_Col:
			tag = "el-col";
			break;
		case TYPE_Icon:
			tag = "i";
			break;
		default:
			LOG.warn("未知的组件类型:" + uiItemDef.type);
			break;
		}
		if (tag != null) {
			sbCodes.append("<" + tag);
			for (String key : itemCfg.keySet()) {
				if (key.startsWith("_")) {
					//UIDef定义的内部属性

				} else {
					//ElementUI定义的组件属性
					if (key.startsWith("is_")) {
						sbCodes.append("" + key);
					} else if (SingleAttributes.indexOf("|" + key + "|") >= 0) {
						boolean val = itemCfg.boolVal(key, false);
						if (val) {
							sbCodes.append(" " + key);
						}
					} else {
						sbCodes.append(" " + key + "=\"" + itemCfg.strVal(key, "") + "\"");
					}
				}
			}
			sbCodes.append(">");
		}
		String _innerHtml = itemCfg.strVal(CFG_InnerHtml, "");
		if (StringUtil.isNotBlank(_innerHtml)) {
			sbCodes.append(_innerHtml);
		}
		if (uiItemDef.getItems() != null) {
			for (UIItemDef child : uiItemDef.getItems()) {
				sbCodes.append(genVueCode(child, coder));
			}
		}
		String _innerHtml2 = itemCfg.strVal(CFG_InnerHtml2, "");
		if (StringUtil.isNotBlank(_innerHtml2)) {
			sbCodes.append(_innerHtml2);
		}
		if (tag != null) {
			sbCodes.append("</" + tag + ">");
		}
		return sbCodes.toString();
	}

}
