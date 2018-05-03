package org.dataagg.codegen;

import static org.dataagg.codegen.util.ElementUI.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dataagg.codegen.base.ACodeGenBase;
import org.dataagg.codegen.model.UIDef;
import org.dataagg.codegen.model.UIItemDef;
import org.dataagg.codegen.model.ValidateDef;
import org.dataagg.codegen.util.ElementUI;
import org.dataagg.codegen.util.JSCoder;
import org.dataagg.util.collection.StrObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UICodeGen extends ACodeGenBase<UIDef> {
	private static final Logger LOG = LoggerFactory.getLogger(UICodeGen.class);
	public String webDir = null;
	private JSCoder coder = null;

	public UICodeGen(String baseDir, boolean mergeCode) {
		super(baseDir, mergeCode);
	}

	/**
	 * 追加代码,并处理表达式
	 *
	 * @param line
	 */
	protected void appendCode(String line) {
		coder.appendln2(mapTplHelper.parse(line));
	}

	/**
	 * 追加代码,但是不处理表达式
	 *
	 * @param line
	 */
	protected void appendCode0(String line) {
		coder.appendln2(line);
	}

	/**
	 * 根据UIDef生成对应Entity的DataGrid vue文件
	 *
	 * @param uiDef ui组件参数
	 */
	public void genVueDataGrid(UIDef uiDef) {
		try {
			String vueFile = webDir + "src\\" + uiDef.outDir + uiDef.action + "List.vue";
			LOG.info(vueFile);

			coder = new JSCoder(vueFile, mergeCode);
			//StrObj mainCfg = (uiDef.cfg != null) ? uiDef.cfg : new StrObj();

			boolean needDict = true;
			appendCode("<template>");
			appendCode("	<page-grid ref=\"${action}PageGrid\" v-on:doQuery=\"search\">");
			appendCode("		<el-form ref=\"${action}QueryForm\" :model=\"query\" label-width=\"120px\" slot=\"queryForm\">");
			appendCode("		<el-row>");
			uiDef.switchGroup(0);
			for (UIItemDef uiItemDef : uiDef.groupDef.getItems()) {
				StrObj childCfg = uiItemDef.cfg != null ? uiItemDef.cfg : new StrObj();
				if (childCfg.get("v-model") == null) {
					uiItemDef.addCfg("v-model", "query." + uiItemDef.field);
				}
				appendCode("<el-col " + ElementUI.colProfile(childCfg.strVal("_colProfile", "3")) + ">");
				appendCode("    <el-form-item label=\"" + uiItemDef.title + "\" prop=\"" + uiItemDef.field + "\">");
				appendCode(ElementUI.genVueCode(uiItemDef, coder));
				appendCode("    </el-form-item>");
				appendCode("</el-col>");
			}
			appendCode("			<el-col " + ElementUI.colProfile("3") + ">");
			appendCode("				<el-form-item>");
			appendCode("					<el-button type=\"primary\" icon=\"el-icon-search\" @click=\"search\">查询</el-button>");
			appendCode("					<el-button type=\"primary\" icon=\"el-icon-plus\" @click=\"doAdd\" v-show=\"hasAuthority('${action}_add')\">新增</el-button>");
			appendCode("				</el-form-item>");
			appendCode("			</el-col>");
			appendCode("		</el-row>");
			appendCode("		</el-form>");
			appendCode("		<el-table :data=\"allData\" stripe border highlight-current-row resizable slot=\"resultGrid\">");
			appendCode("			<el-table-column type=\"selection\"></el-table-column>");

			uiDef.switchGroup(1);
			for (UIItemDef item : uiDef.groupDef.getItems()) {
				appendCode("			<el-table-column prop=\"" + item.field + "\" label=\"" + item.title + "\">");
				if ((TYPE_ELText + "").equals(item.type)) {
					appendCode("<template slot-scope=\"scope\">");
					appendCode(ElementUI.genVueCode(item, coder));
					appendCode("</template>");
				}
				appendCode("			</el-table-column>");
			}
			appendCode("			<el-table-column label=\"操作\" align=\"center\" width=\"130\">");
			appendCode("				<template slot-scope=\"scope\">");
			appendCode("					<el-button-group>");
			appendCode0("						<el-button size=\"small\" type=\"success\" icon=\"el-icon-edit\" @click=\"doEdit(scope.$index, scope.row)\" v-show=\"hasAuthority('" + uiDef.action + "_edit')\"></el-button>");
			appendCode0("						<el-button size=\"small\" type=\"danger\" icon=\"el-icon-delete\" @click=\"doDelete(scope.$index, scope.row)\" v-show=\"hasAuthority('" + uiDef.action + "_delete')\"></el-button>");
			appendCode("					</el-button-group>");
			appendCode("				</template>");
			appendCode("			</el-table-column>");
			appendCode("		</el-table>");
			appendCode("	</page-grid>");
			appendCode("</template>");
			appendCode("<script>");
			appendCode("var _ = require('lodash');");
			appendCode("import common from '../../../assets/common.js';");
			appendCode("import PageGrid from '../../../components/PageGrid.vue';");
			appendCode("import ${action}Action from '../../../actions/${action}Actions.js';");
			coder.insertMergedCodes("importJs");
			if (needDict) {
				appendCode("import dictAction from '../../../actions/dictActions.js';");
			}
			appendCode("");
			appendCode("export default {");
			appendCode("	components: {");
			appendCode("		'page-grid': PageGrid");
			appendCode("	},");
			appendCode("	data() {");
			appendCode("		return {");
			appendCode("			query: common.getState('${action}ListQuery', {");
			appendCode("				name: ''");
			appendCode("			}),");
			appendCode("			allData: [],");
			if (needDict) {
				appendCode("			allDict: [],");
			}
			appendCode("			allExtData: [],");
			appendCode("			authorities: [],");
			appendCode0(coder.codeBlock.codes("dataInit", ""));
			coder.insertMergedCodes("customData");
			appendCode("		};");
			appendCode("	},");
			appendCode("	filters: {");
			coder.insertMergedCodes("filters");
			appendCode("	},");
			appendCode("	created: function () {},");
			appendCode("	mounted: function () {");
			appendCode("		var self = this;");
			appendCode("		common.init(this);");
			appendCode("		${action}Action.init(this);");
			coder.insertMergedCodes("mountedMethods");
			if (needDict) {
				appendCode("		dictAction.init(this);");
				appendCode("		dictAction.doAllDict(response => {");
				appendCode("        	self.allDict = response.data.data;");
				appendCode("      	});");
			}
			coder.insertMergedCodes("loadQueryFormData");
			appendCode("		this.search();");
			appendCode("	},");
			appendCode("	methods: {");
			appendCode("		search() {");
			appendCode("			var self = this;");
			appendCode("			common.upState('${action}ListQuery', self.query);");
			appendCode("			${action}Action.doFetchList(");
			appendCode0("					self.$refs." + uiDef.action + "PageGrid.page,");
			appendCode("					self.query,");
			appendCode("				response => {");
			appendCode0("					self.$refs." + uiDef.action + "PageGrid.updatePage(response.data.data.page);");
			appendCode("					self.allData = response.data.data.data;");
			appendCode("					self.allExtData = response.data.data.extData;");
			appendCode("					self.authorities = response.data.authorities;");
			appendCode("				}");
			appendCode("			);");
			appendCode("		},");
			appendCode("		hasAuthority(authority) {");
			appendCode("			var self = this;");
			appendCode("			if (_.has(self.authorities, authority)) {");
			appendCode("				//console.log(authority, _.get(self.authorities, authority));");
			appendCode("				return _.get(self.authorities, authority);");
			appendCode("			}");
			appendCode("			return false;");
			appendCode("		},");
			appendCode("		showExtData(scope, key) {");
			appendCode("			var self = this;");
			appendCode("			try {");
			appendCode0("				return _.get(self.allExtData[scope.$index], key);");
			appendCode("			} catch (error) {");
			appendCode("				common.errorMsg(error);");
			appendCode("				return '';");
			appendCode("			}");
			appendCode("		},");
			appendCode("		doAdd() {");
			appendCode0("			this.$router.push('/" + uiDef.action + "/add');");
			appendCode("		},");
			appendCode("		doEdit(index, el) {");
			appendCode0("			this.$router.push('/" + uiDef.action + "/edit/' + el.id);");
			appendCode("		},");
			appendCode("		doDelete(index, el) {");
			appendCode("			var self = this;");
			appendCode("			common.confirmMsg('警告','确认删除这条记录?',() => {");
			appendCode("				${action}Action.doDelete(el.id, response => {");
			appendCode("					self.search();");
			appendCode("					common.okMsg('删除成功!');");
			appendCode("				});");
			appendCode("			});");
			appendCode("		},");
			appendCode0(coder.codeBlock.codes("jsMethods", ""));
			coder.insertMergedCodes("_CustomMethods");
			appendCode("	}");
			appendCode("};");
			appendCode("</script>");

			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 根据UIDef生成对应Entity的DataEditForm vue文件
	 *
	 * @param uiDef ui组件参数
	 */
	public void genVueDataEditForm(UIDef uiDef) {
		try {
			String vueFile = webDir + "src\\" + uiDef.outDir + uiDef.action + "Form.vue";
			LOG.info(vueFile);

			coder = new JSCoder(vueFile, mergeCode);

			StrObj mainCfg = (uiDef.cfg != null) ? uiDef.cfg : new StrObj();
			boolean needDict = true;
			appendCode("<template>");
			appendCode("	<div>");
			appendCode("	<el-form ref=\"${action}Form\" :model=\"entity\" :rules=\"rules\" label-width=\"160px\">");
			appendCode("		<el-row>");
			List<ValidateDef> allValidateDefs = new ArrayList<>();
			for (UIItemDef uiItemDef : uiDef.defs) {
				uiItemDef.depthFirstTraversal(node -> {
					allValidateDefs.addAll(((UIItemDef) node).validates);
				});
				if ((TYPE_FieldSet + "").equals(uiItemDef.type)) {
					if (uiItemDef.title != null && uiItemDef.title.trim().length() > 0) {
						appendCode("<el-col " + ElementUI.colProfile("1") + ">");
						appendCode("    <h2>" + uiItemDef.title.trim() + "</h2>");
						appendCode("</el-col>");
					}
				}
				if (uiItemDef.getItems() != null) {
					for (UIItemDef child : uiItemDef.getItems()) {
						StrObj childCfg = child.cfg != null ? child.cfg : new StrObj();
						if (childCfg.get("v-model") == null && !"0".equals(child.type + "")) {
							child.addCfg("v-model", "entity." + child.field);
						}
						appendCode("<el-col " + ElementUI.colProfile(childCfg.strVal("_colProfile", "3")) + ">");
						appendCode("    <el-form-item label=\"" + child.title + "\" prop=\"" + child.field + "\">");
						appendCode(ElementUI.genVueCode(child, coder));
						appendCode("    </el-form-item>");
						appendCode("</el-col>");
					}
				}
			}
			appendCode("		</el-row>");
			appendCode("		<el-row>");
			appendCode("			<el-col " + ElementUI.colProfile("1") + ">");
			appendCode("				<el-form-item>");
			appendCode("					<el-button type=\"primary\" @click=\"doSave\">{{(entity.id>0) ? '编辑' : '新增'}}</el-button>");
			appendCode("					<el-button :plain=\"true\" type=\"warning\" @click=\"doBack\">返回</el-button>");
			appendCode("				</el-form-item>");
			appendCode("			</el-col>");
			appendCode("		</el-row>");
			appendCode("	</el-form></div>");
			appendCode("</template>");
			appendCode("<script>");
			appendCode("var _ = require('lodash');");
			appendCode("import common from '../../../assets/common.js';");
			appendCode("import ${action}Action from '../../../actions/${action}Actions.js';");
			coder.insertMergedCodes("importJs");
			if (needDict) {
				appendCode("import dictAction from '../../../actions/dictActions.js';");
			}
			appendCode("");
			appendCode("export default {");
			appendCode("	data() {");
			appendCode("		return {");
			if (needDict) {
				appendCode("			allDict:[],");
			}
			appendCode0(coder.codeBlock.codes("dataInit", ""));
			coder.insertMergedCodes("customData");
			appendCode("			entity: {");
			for (UIItemDef item : uiDef.defs) {
				if (item.type != "1" && item.jsDefaultVal() != null) {
					appendCode("			" + item.field + ": " + item.jsDefaultVal() + ",");
				}
			}
			appendCode("			id:0");
			appendCode("			},");
			appendCode("			rules: " + ValidateDef.genRules(allValidateDefs));
			appendCode("");
			appendCode("		};");
			appendCode("	},");
			appendCode("	mounted: function () {");
			appendCode("		common.init(this);");
			appendCode("		${action}Action.init(this);");
			coder.insertMergedCodes("mountedMethods");
			if (needDict) {
				appendCode("		dictAction.init(this);");
			}
			appendCode("		this.initData();");
			appendCode("	},");
			appendCode("	computed: {");
			appendCode("		//");
			coder.insertMergedCodes("computedMethods");
			appendCode("	},");
			appendCode("	methods: {");
			appendCode("		initData() {");
			appendCode("			var self = this;");
			if (needDict) {
				appendCode("			dictAction.doAllDict(response => {");
				appendCode("				self.allDict = response.data.data;");
				appendCode("				self.initEntity();");
				appendCode("			});");
			} else {
				appendCode("			self.initEntity();");
			}
			appendCode("		},");
			appendCode("		initEntity() {");
			appendCode("			var self = this;");
			appendCode0("			if (_.has(self.$route.params, 'id')) {");
			appendCode0("				self.entity.id = self.$route.params.id;");
			appendCode("				${action}Action.doFetch(self.entity.id, response => {");
			appendCode("					self.updateEntity(response.data.data);");
			appendCode("				});");
			appendCode("			}");
			coder.insertMergedCodes("initEntity");
			appendCode("		},");
			appendCode("		updateEntity(newEntity) {");
			appendCode("			this.entity = newEntity;");
			appendCode("			//TODO 获取其他数据");
			appendCode("		},");
			appendCode("		doSave() {");
			appendCode("			//保存实体数据");
			appendCode("			var self = this;");
			appendCode0("			this.$refs." + uiDef.action + "Form.validate(valid => {");
			appendCode("				if (valid) {");
			appendCode("					//TODO 保存前的准备工作");
			appendCode("					var newEntity = _.cloneDeep(self.entity);");

			appendCode("					//提交实体数据到save action");
			appendCode("					${action}Action.doSave(newEntity, response => {");
			appendCode0("						self.$router.push('" + mainCfg.strVal("backAction", "/" + uiDef.action + "/list") + "');");
			appendCode("						common.okMsg(response.data.message);");
			appendCode("					});");
			appendCode("				} else {");
			appendCode("					return;");
			appendCode("				}");
			appendCode("			});");
			appendCode("		},");
			appendCode("		doBack() {");
			appendCode("			//返回上一页");
			appendCode0("			this.$router.go(-1);");
			appendCode("		},");
			appendCode0(coder.codeBlock.codes("jsMethods", ""));
			coder.insertMergedCodes("_CustomMethods");
			appendCode("	}");
			appendCode("};");
			appendCode("</script>");

			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 根据UIDef生成对应Entity的DataTreeForm vue文件
	 *
	 * @param uiDef ui组件参数
	 */
	public void genVueDataTreeForm(UIDef uiDef) {
		try {
			String vueFile = webDir + "src\\" + uiDef.outDir + uiDef.action + "Tree.vue";
			LOG.info(vueFile);

			coder = new JSCoder(vueFile, mergeCode);

			//StrObj mainCfg = (uiDef.cfg != null) ? uiDef.cfg : new StrObj();
			boolean needDict = true;
			StrObj entityDefCfg = uiDef.entityDef.cfg != null ? uiDef.entityDef.cfg : new StrObj();
			String idField = entityDefCfg.strVal("idField");
			String parentField = entityDefCfg.strVal("parentField");
			parentField = polishField(parentField);
			String parentVal = ("String".equals(entityDefCfg.strVal("pkType", "Long"))) ? "''" : "0";
			UIItemDef parentItem = uiDef.fetchItem(parentField);
			StrObj parentItemCfg = parentItem.cfg != null ? parentItem.cfg : new StrObj();
			String allParent = "all_" + parentItemCfg.strVal("_id", "parent");
			String options = parentItemCfg.strVal(":options");
			appendCode("<template>");
			appendCode("<div>");
			appendCode("<el-row style=\"margin-bottom: 15px;\">");
			appendCode("<span class=\"wrapper\">");
			appendCode("<el-button type=\"primary\" icon=\"el-icon-plus\" @click=\"doAdd\" v-show=\"hasAuthority('${action}_add')\">新增</el-button>");
			appendCode("<el-button type=\"primary\" icon=\"el-icon-plus\" @click=\"doAddChild\" v-show=\"hasAuthority('${action}_add')\">新增下级节点</el-button>");
			appendCode("</span>");
			appendCode("</el-row>");
			appendCode("<el-row>");
			appendCode("<el-col :xs=\"24\" :sm=\"24\" :md=\"12\" :lg=\"12\">");
			appendCode("<el-tree :props=\"" + parentItemCfg.strVal(":props") + "\" node-key=\"id\" accordion :load=\"loadData\" lazy :highlight-current=\"true\" class=\"action-tree\" @node-click=\"loadEditForm\">");
			appendCode("</el-tree>");
			appendCode("</el-col>");
			appendCode("<el-col :xs=\"24\" :sm=\"24\" :md=\"12\" :lg=\"12\">");

			appendCode("	<el-form ref=\"editForm\" :model=\"editForm.entity\" :rules=\"editForm.rules\" label-width=\"120px\" style=\"border:1px solid #d1dbe5;\">");
			appendCode("		<el-row>");
			List<ValidateDef> allValidateDefs = new ArrayList<>();
			for (UIItemDef uiItemDef : uiDef.defs) {
				uiItemDef.depthFirstTraversal(node -> {
					allValidateDefs.addAll(((UIItemDef) node).validates);
				});
				if ((TYPE_FieldSet + "").equals(uiItemDef.type)) {
					if (uiItemDef.title != null && uiItemDef.title.trim().length() > 0) {
						appendCode("<el-col " + ElementUI.colProfile("1") + ">");
						appendCode("    <h2>" + uiItemDef.title.trim() + "</h2>");
						appendCode("</el-col>");
					}
				}
				if (uiItemDef.getItems() != null) {
					for (UIItemDef child : uiItemDef.getItems()) {
						StrObj childCfg = child.cfg != null ? child.cfg : new StrObj();
						if ((TYPE_Cascader + "").equals(child.type)) {
							child.addCfg("v-model", allParent);
							child.addCfg("change-on-select", true);
							child.addCfg("clearable", true);
							coder.codeBlock.startBlock("dataInit");
							coder.appendln2(allParent + ": [],");
							coder.codeBlock.endBlock();
						} else if (childCfg.get("v-model") == null) {
							child.addCfg("v-model", "editForm.entity." + child.field);
						}
						appendCode("<el-col " + ElementUI.colProfile(childCfg.strVal("_colProfile", "1")) + ">");
						appendCode("    <el-form-item label=\"" + child.title + "\" prop=\"" + child.field + "\">");
						appendCode(ElementUI.genVueCode(child, coder));
						appendCode("    </el-form-item>");
						appendCode("</el-col>");
					}
				}
			}
			appendCode("		</el-row>");
			appendCode("		<el-row>");
			appendCode("			<el-col " + ElementUI.colProfile("1") + ">");
			appendCode("				<el-form-item>");
			appendCode("					<el-button type=\"primary\" @click=\"doSave\">{{(editForm.entity.id>0) ? '编辑' : '新增'}}</el-button>");
			appendCode("					<el-button type=\"danger\" @click=\"doDelete\">删除</el-button>");
			appendCode("				</el-form-item>");
			appendCode("			</el-col>");
			appendCode("		</el-row>");
			appendCode("	</el-form></el-col></el-row></div>");
			appendCode("</template>");
			appendCode("<script>");
			appendCode("var _ = require('lodash');");
			appendCode("import common from '../../../assets/common.js';");
			appendCode("import baseAction from '../../../assets/baseActions.js';");
			appendCode("import ${action}Action from '../../../actions/${action}Actions.js';");
			coder.insertMergedCodes("importJs");
			if (needDict) {
				appendCode("import dictAction from '../../../actions/dictActions.js';");
			}
			appendCode("");
			appendCode("export default {");
			appendCode("	data() {");
			appendCode("		return {");
			if (needDict) {
				appendCode("			allDict:[],");
			}
			appendCode("			authorities:[],");
			appendCode0(coder.codeBlock.codes("dataInit", ""));
			coder.insertMergedCodes("customData");
			appendCode("editForm: {" + "			entity: {");
			for (UIItemDef item : uiDef.defs) {
				if (item.type != "1" && item.jsDefaultVal() != null) {
					appendCode("			" + item.field + ": " + item.jsDefaultVal() + ",");
				}
			}
			appendCode("			" + idField + ":0");
			appendCode("			},");
			appendCode("			rules: " + ValidateDef.genRules(allValidateDefs));
			appendCode("},");
			appendCode("		};");
			appendCode("	},");
			appendCode("	mounted: function () {");
			appendCode("		common.init(this);");
			appendCode("       baseAction.init(this);");
			appendCode("		${action}Action.init(this);");
			coder.insertMergedCodes("mountedMethods");
			if (needDict) {
				appendCode("		dictAction.init(this);");
			}
			appendCode("		this.initData();");
			appendCode("	},");
			appendCode("	computed: {");
			appendCode("		//");
			coder.insertMergedCodes("computedMethods");
			appendCode("	},");
			appendCode("	methods: {");
			appendCode("		initData() {");
			appendCode("			var self = this;");
			if (needDict) {
				appendCode("			dictAction.doAllDict(response => {");
				appendCode("				self.allDict = response.data.data;");
				appendCode("			});");
			} else {

			}
			appendCode("		},");
			appendCode("		loadData(node, resolve) {");
			appendCode("      var self = this;");
			appendCode("      var param = {};");
			appendCode("      if (node.level === 0) {");
			appendCode("        param = {};");
			appendCode("      } else {");
			appendCode("        param = { " + parentField + ": node.data." + idField + " };");
			appendCode("      }");
			appendCode("      ${action}Action.doFetchList({}, param, response => {");
			appendCode("        if (response.data.data.data != null) {");
			appendCode("          resolve(response.data.data.data);");
			appendCode("          self.authorities = response.data.authorities;");
			appendCode("        }");
			appendCode("      });");
			appendCode("    },");
			appendCode("    loadEditForm(data, node, component) {");
			appendCode("      var self = this;");
			appendCode("      if (data." + parentField + " === " + parentVal + ") {");
			appendCode("        self.editForm.entity = data;");
			appendCode("        self." + options + " = [];");
			appendCode("        self." + allParent + " = [];");
			appendCode("      } else {");
			appendCode("        self.editForm.entity = data;");
			appendCode("        self." + options + " = [];");
			appendCode("        ${action}Action.doFetchRelation(data." + idField + ", 5, '', response => {");
			appendCode("          if (response.data.data.data != null) {");
			appendCode("            self." + options + " = response.data.data.data;");
			appendCode("            var " + allParent + " = [];");
			appendCode("            if (typeof data." + parentField + " !== 'undefined' && data." + parentField + " !== " + parentVal + ") {");
			appendCode("              " + allParent + " = [data." + parentField + "];");
			appendCode("            }");
			appendCode("            self." + allParent + " = " + allParent + ";");
			appendCode("          }");
			appendCode("        });");
			appendCode("      }");
			appendCode("    },hasAuthority(authority) {");
			appendCode("      var self = this;");
			appendCode("      if (_.has(self.authorities, authority)) {");
			appendCode("        //console.log(authority, _.get(self.authorities, authority));");
			appendCode("        return _.get(self.authorities, authority);");
			appendCode("      }");
			appendCode("      return false;");
			appendCode("    },");
			appendCode("    //点击新增按钮");
			appendCode("    doAdd() {");
			appendCode("      var self = this;");
			appendCode("      var newData = {");
			appendCode("        name: '',");
			appendCode("        code: '',");
			appendCode("        sort: 10,");
			appendCode("        " + parentField + ": " + parentVal + ",");
			appendCode("        " + idField + ": 0,");
			appendCode("      };");
			appendCode("      self.loadEditForm(newData);");
			appendCode("    },");
			appendCode("    doAddChild() {");
			appendCode("      var self = this;");
			appendCode("      var newData = _.cloneDeep(self.editForm.entity);");
			appendCode("      newData." + idField + " = -1;");
			appendCode("      newData.name = '';");
			appendCode("      newData.code = '';");
			appendCode("      newData.sort = newData.sort + 10;");
			appendCode("      self.loadEditForm(newData);");
			appendCode("    },");
			appendCode("		doSave() {");
			appendCode("			//保存实体数据");
			appendCode("			var self = this;");
			appendCode0("			self.$refs.editForm.validate(valid => {");
			appendCode("				if (valid) {");
			appendCode("					self.editForm.entity." + parentField + " = baseAction.getLastPid(self." + allParent + ");");
			appendCode("                 //TODO 保存前的准备工作");
			appendCode("					var newNode = _.cloneDeep(self.editForm.entity);");

			appendCode("					//提交实体数据到save action");
			appendCode("					${action}Action.doSave(newNode, response => {");
			appendCode("						//刷新页面");
			appendCode0("						self.$router.go(0);");
			appendCode("						common.okMsg(response.data.message);");
			appendCode("					});");
			appendCode("				} else {");
			appendCode("					return;");
			appendCode("				}");
			appendCode("			});");
			appendCode("		},");
			appendCode("		");
			appendCode("    doDelete() {");
			appendCode("      var self = this;");
			appendCode("      common.confirmMsg('警告', '确认删除这条记录?', () => {");
			appendCode("        ${action}Action.doDelete(self.editForm.entity." + idField + ", response => {\n");
			appendCode("          //刷新页面");
			appendCode0("           self.$router.go(0);\n");
			appendCode("          common.okMsg('删除成功!');");
			appendCode("        });");
			appendCode("      });");
			appendCode("    },");
			appendCode0(coder.codeBlock.codes("jsMethods", ""));
			coder.insertMergedCodes("_CustomMethods");
			appendCode("	}");
			appendCode("};");
			appendCode("</script>");

			coder.writeToFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void genAllCode() {
		mapTplHelper.initModel(def);
		genDebugModelJson(lfield(def.name) + "UI", def);

		if (def.type == UIDef.TYPE_Form) {
			genVueDataEditForm(def);
		} else if (def.type == UIDef.TYPE_DataGrid) {
			genVueDataGrid(def);
		} else if (def.type == UIDef.TYPE_TreeForm) {
			genVueDataTreeForm(def);
		}
	}
}
