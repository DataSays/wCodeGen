# 概述
DataAgg的代码生成器主要用于:

1. **减少**日常开发中的一些**重复工作**, 尽可能**减少开发人员的基础代码开发工作量**;

2. 同时也使用统一的"**框架/骨架**"限制程序员天马行空的编码方式, **规范项目代码风格**和**统一项目的UI样式**.

3. 通过定义信息**自动生成相关的类图和对象关系图**,尽可能保证代码与文档内容同步;

4. 在**项目启动阶段**,使用ProjectGen配置生成项目的**工程目录**及**配置文件**

5. 在**产品详细设计阶段**，由功能设计人员使用简单Java编码的方式配置所有**业务实体定义**、**后台服务Action定义**和**UI页面定义**，再生成相关的代码及文档，快速搭建产品原型及骨架代码；

6. 在**产品开发阶段**,不断调整(实体/服务Action/UI页面定义/)配置并调用相应的代码生成器更新代码, 而**不用担心已开发代码被覆盖**

总之, DataAgg的代码生成器具备以下特点:

+ **完整的解决方案:** 包含项目配置,配套文档,商业模型(实体类)的CURD服务端和界面的骨架代码, 单元测试
+ **高度可定制:** 尽可能地解耦和模块依赖,可根据实际需要定制任意子模块
+ **完全"热拔插":**不引入额外的类库依赖, 随时可以移除或引入代码生成器
+ **可重复生成而不覆盖代码:**, 大多数代码生成器都是只能使用一次,但是我们的保证在常用情况下重复生成
+ **学习成本低:** 纯java代码+概要文档说明, 使用时随时查看API注释,简单易用

# 原则
* 只完整生成全部骨架代码, 复杂的业务逻辑及代码定制自行**二次开发**;二次开发时请遵循**JCodeMerger**规则使用配套注释包裹定制代码块,这样即**可重复生成代码而不覆盖定制代码**;
* 代码生成**暂时不支持备份**功能,请自行使用git/svn等版本管理工具; 
* 保持一定程度的定制化能力, 保证最大限度地复用; 但不会过度使用设计模式,增加代码复杂度;
* KISS原则
* 尽可能地解耦和,不要过于依赖某个框架或者某个类库

# 功能模块

1. **ProjectGen:** 根据项目的功能模块定义,生成项目的完整**工程目录结构**,并统一配置文件(**应用参数配置**、**Gradle编译配置**和**Docker配置**). 特别适用于微服务架构 
2. **EntityCodeGen:** 根据EntityDefBuilder编写所有实体定义AllEntityDefs,生成**实体类**及其配套的**DAO**、**Service**、**数据库表结构**、类图、对象图和ER图;
3. **ActionCodeGen:** 根据ActionDefBuilder编写所有后台服务定义AllActionDefs,生成**Controller类**、**ActionJs**(前端代码调用服务端的js封装)、**feign调用Service接口**(微服务间相互调用时使用)及**Swagger接口规范文件**(生成API文档及各种语言版本的SDK)
4. **UICodeGen:** 根据*DefBuilder编写所有管理界面定义AllUIDefs,生成**前端页面代码**、**静态mock测试Json数据**及相关的**权限配置**和**菜单配置**
5. **JCodeMerger:** 通用的基于行扫描的代码合并机制, 简单的规则保证可重复生成的代码
6. **JCoder,JSCoder和HtmlCoder:** 基于CodeGenHelper的生成java, js, html类代码的帮助类, 生成代码包含基本的代码样式. 因为Freemarker编写的代码可读性非常糟糕,而且非常多的小括号,所以改为使用java代码的方式编写生成模版,这样不但学习成本低,而且直接使用代码注释及*DefBuilder中封装常见业务场景提高组件复用 

# 代码生成模版
* 生成模版这东东很难保证可读性,所以新版移除Freemarker模版方式,直接**使用*Coder的Java代码方式手工编写**, 增强代码复用和降低学习成本

* 根据需要自行重构` *CodeGen`中的`gen*`方法

* 编写生成代码时可以使用JCoder,JSCoder和HtmlCoder的父类方法

​
## 完整的java文件生成骨架
~~~java
try {
	String javaFile = baseDir + def.javaFile(def.getRootPkg() + ".dao", def.getSimpleName() + "Dao");
	LOG.info(javaFile);

	JCoder coder = new JCoder(javaFile, mergeCode);
	coder.appendln2("package %s.dao;", def.getRootPkg());
	/** ..............import................. **/
    
    coder.appendln2("");
	coder.markImportCodeOffset();

	coder.appendln2("");
	coder.append(JCoder.longComment(def.comments, null));
	coder.appendln2("@Component");
	coder.appendln2("public class %sDao extends %s<%s> {", model.strVal("nameU"), ("String".equals(def.getCfg("pkType")) ? "AStringPKEntityDao" : "ALongPKEntityDao"), def.entityCls);
	coder.beginIndent();
	
	/** ..............fields................. **/
	
	//定制部分代码
	coder.insertMergedCodes("_CustomFields");
	/** ..............methods................. **/
    
	//定制部分代码
	coder.insertMergedCodes("_CustomMethods");
	coder.endIndent();
	coder.appendln("}");

	//插入ImportCodes
	coder.insertImportCodes();
	coder.writeToFile();
} catch (IOException e) {
	LOG.error(e.getMessage(), e);
}
~~~


# 代码合并工具JCodeMerger
JCodeMerger合并代码时, 首先扫描源代码文件中`//##CodeMerger.`开头的注释, 并把相关代码放入缓存, 重新生成代码后再把缓存代码插入相应的位置, 所以**JCodeMerger限制较多**,使用时请注意!

+ **新增import类:** 定制java代码中需要新增import类时, 请使用`//##CodeMerger.import`注释放在对应import语句的上一行. 例如:
```java
//##CodeMerger.import
import jodd.util.StringUtil;
```

+ **定制代码块:** 在生成代码中需要插入定制化代码时,请使用`//##CodeMerger.code:?`和`//##CodeMerger.code`包裹需要定制的代码块. 其中?标识定制代码块的索引key, 插入代码时请与模版的索引位置相同! 例如:
```java
//##CodeMerger.code:_CustomFields
@Autowired
public RoleService roleService;

@Autowired
public UserService userService;
//##CodeMerger.code
```
+ **生成模版中生成定制代码块:** 在代码生成模版中标记定制的代码块位置时使用如下方式:
  +	标记插入import语句的位置
  ```java
  jCoder.markImportCodeOffset();
  ```
  +	插入所有的import类
  ```java
  jCoder.insertImportCodes(imports);
  ```
  + 插入空的可合并代码块
  ```java
  jCoder.insertMergedCodes("_CustomFields");
  ```
  + 标记可合并的代码块开始和结束
  ```java
  jCoder.startMergedCodes("_CustomMethods");
  	...生成的默认代码...
  jCoder.endMergedCodes("_CustomMethods");
  ```
# 代码生成帮助类XCoder

在代码生成时,需要大量的字符串拼接工作,而且有许多操作都是重复的、通用的，因此构建了一组工具类完成此类任务，提高代码复用和可读性。

+ 包含一些常用的字符串处理静态方法. 例如: capFirst, uncapFirst, joinPrefix, joinSuffix等
+ 大部分方法都内部使用String.format, 使用参数占位符%?可以减少字符串拼接. 使用String.format的原因是因为1. 这个足够简单常见,几乎没有额外的学习成本; 2. 使用${xx}规则的这样还需要构建一个额外的model,复杂度增加不少,所以暂时没采取此方案.
+ 支持代码缩进处理

  + 单行代码缩进建议直接在字符串中使用tab处理,这样简单易懂,而且没有额外的开销

  + 多行批量缩进可以使用beginIndent()和endIndent()组合方法,内置一个缩进计数器

  + 单行代码在一些特殊情况下也可使用indent()获取当前上下文的缩进字符
+ 代码偏移量: 在代码编写过程中经常需要回头插入一些内容到之前的代码中,这时就需要使用代码偏移量
  + offset() 获取当前代码的偏移量, 之后使用insert()方法插入代码
  + markImportCodeOffset()是JCoder使用的方法,由于标记在当前位置插入JCodeMerger提供的import语句位置
+ JCoder: Java代码的生成帮助类
  + 内置JCodeMerger, 使用一组配套方法完成代码的合并工作
  + 提供一些java常用的方法: clsDef,publicClsDef,fieldDef,appendFieldGSetter,serialVersionUID,lineBreakComment,longComment等
+ JSCoder: js的代码的生成帮助类
+ HtmlCoder: Html的代码的生成帮助类

# 项目配置生成ProjectGen

对于一个规范的中型/大型项目应该:

+ 使用不同的子项目把各个模块区分开
+ 每个子项目会依赖不同的类库
+ 每个子项目都会存在一些公共和独有的配置
+ 在编译发布时需要针对每个模块进行一些定制化的处理
+ 如果使用Docker, 每个项目还要需要编写对应的Dockefile和docker-compose.yml

以上这些内容在每一个项目都需要重复编写;模块的应用配置也散落在各个配置文件中,难以统一管理.因此,我们构建了一个ProjectGen工具把所有的配置都统一到2个yml配置中,这样大幅减少了重复工作量,而且也统一管理了所有配置. 此工具只负责生成对应的配置文件,不会与现有代码进行耦合,完全"热拔插".
+ 所有公用重复的信息都可以放入components.yml, 定义为一个组件给其他项目复用, 一些通用性质的配置可以打包放入其中, 例如:类库依赖,公用配置,docker配置
+ 在projects.yml中定义主项目及子项目的配置, 可生成build.gradle, SpringBoot的application.yml和bootstrap.yml, Docker的Dockefile和docker-compose.yml
+ 在projects.yml中可以定义一些全局的变量,这些变量会在生成前就逐一做为字符串替换,这个优先级最高
+ 每个项目都可以使用components定义依赖的组件,使用deps定义附件的类库依赖, 使用plugins定义使用的gradle插件.java的项目的编译构建使用Gradle,目前暂时不支持web项目和maven项目.
+ 应用配置可以使用config和testConfig, 目前支持的基于Spring boot项目的yml配置
+ 整个生成的机制都是按照特定的优先级逐级覆盖替换,暂时不支持删除操作. 优先级列表为: `字符变量>项目配置>组件配置`.

# 生成实体相关代码

## EntityDefBuilder实体类定义构建工具类
使用POJO加注解方式存在以下问题:
* 实体类散落在各个工程及包下,并且存在大量重复的getter/setter方法,导致代码可阅读性非常糟糕;
* 因为使用的是NutzDao的注解,增加了学习成本,而且他原始目的是构建数据库实体存储的,所以非常不直观,需要了解大量数据库字段定义信息.
* 封装通用用途的字段时只能使用复制粘贴大法统一命名规范,这样非常容易出错,而且将来批量修改麻烦.
* 因为java的classloader机制和注解解析问题,导致生成的其他实体配套代码(Dao,Service, Contrroller等)时需要二次生成. 开发效率低下.

基于以上原因, 新增了一个EntityDefBuilder工具类帮助生成实体类及相关定义信息.他主要包含:
* 使用AllEntityDefs类定义所有实体信息; 每个static字段代表一个实体类定义的EntityDefBuilder; 并且包含每个project下的所有EntityDefBuilder数组; 这样可以从全局视角查看所有实体间的关系, 相比实体类注解定义更加直观!
* 为保证兼容性, 可以使用*EntityCodeGen.genEntityDefBuilder*方法扫描所有实体类注解生成AllEntityDefs文件. 注意: *genEntityDefBuilder*将在后续版本逐步废弃,一般情况下不建议使用.
* EntityDefBuilder封装了大量常见用法,统一了开发命名规范.
* EntityDefBuilder不需要使用spring的IOC, 所以可以直接在任何java代码中直接使用,增加了启动速度. 基于EntityDefBuilder的EntityCodeGen相关代码生成运行时间都在1s以内.
* 为了规范代码, 建议生成的`AllEntityDefs.java`文件放到`/{项目代码}/src/test/java/com/dataagg/`目录下. 并使用相同目录下的`com.dataagg.EntityCodeMaintain.main(String[])`方法生成所有实体相关代码.
* commons工程中包含了许多公用的实体及相关定义, 目前建议在新项目使用时全部文件都复制过来再精简内容. 所以AllEntityDefs.commons中也包含了所有commons下的实体信息,将来commons工程更改复用方式时可以移除这部分的实体定义.

## EntityDef 实体类信息定义

* ~~使用POJO加注解的方式描述实体的各个字段定义和实体数据间的关系，告别文档与代码脱节问题~~ 已使用EntityDefBuilder替代此方式,不建议再手动编写实体注解,会被EntityCodeGen.genEntityCls生成覆盖代码.
* ~~根据实体定义信息选择性地生成同一功能范围内的实体类图(使用PlantUML/StarUML类图)，从功能的角度查看对象图及其细节~~ EntityDefBuilder已经包含此功能.
* 使用nutzDAO自动维护表结构，减少DDL编写
* 在实体定义中描述显示格式，数据效验信息，为UI组件显示时减少重复定义工作
* ~~可编程的方式编写数据定义规范检查，统一命名规范，给出警告信息~~ 常见用法都已经被封装在EntityDefBuilder中,所以不再需要规范检查工作.
    ## TODO
    * ~~解析实体类的字段及其注解, 生成对应的EEntityDef和EEntityItemDef信息,并保存到数据库,方便查询~~

    * ~~可视化实体定义的编辑,查询界面;~~

    * 解析数据库表字段,生成对应的EEntityDef和EEntityItemDef信息,并保存到数据库;

    * ~~根据EEntityDef和EEntityItemDef信息生成对应的实体类;~~ 已废弃,直接使用EntityDefBuilder生成实体类

    * 设计一种统一的字段数据效验规则, 自动生成前后端相关的效验代码;

    * 根据定义信息实现一个queryBuilder生成工具

# 生成后台服务请求代码 

## ActionDef

## ActionDefBuilder

# 生成原型界面代码
复用实体定义信息(只是复用,**并不依赖实体**,可以支持普通POJO)，设计并生成相关的界面，减少重复工作，让程序员专注于业务逻辑。并且让功能详细设计人员可以一次就生成出复用的原型界面代码。实现以下目标:
* 使用界面元素定义的方式,从原型的角度定义: 界面元素布局,使用什么样的UI组件,以及关联的业务对象字段;
* 封装通用组件调用方式, 让设计人员必须要考虑技术细节, 专注于业务逻辑展现和业务对象数据读取及保存;
    ## TODO
    * ~~UIFormDef表单页面定义: 实现基础功能, 支持主流表单UI组件,生成对应表单的vue文件~~
    * ~~使用table-expand显示DefGroup,但是现在只支持一层分组,分组嵌套暂时不准备支持,过于复杂.~~
    * ~~复用EntityDef的字段定义信息创建UIDef~~;新建一个空白的UIDef用于支持普通pojo对象;
    * 根据菜单配置json生成相关代码: router.js, 关联静态json测试数据, 数据表sys_menus;
    * UIDataTableDef 简单数据表格页面定义: 实现基础功能, 支持数据表格的数据加载及展示;
    * UIDataGridDef 查询数据Grid页面定义: 实现基础功能, 支持常规查询表格数据的界面生成vue文件;
    * 为CURD操作生成对应的xxxActions.js文件骨架;
    * 可视化界面定义的编辑,查询界面;
    * 实现UIDataGridDef和UIDataTableDef的数据复用;
    * 实现UIChartDef数据图表页面定义的基础功能;

    ## 备注
    * 生成的vue,js,css文件需要在VsCode中手动格式化,否则EsLint可能会出现错误提示;

## UIFormDef--表单页面定义
复用实体对象定义信息，设计对应实体的编辑和查看表单界面. 
- 支持UI组件列表
    * ~~TYPE_Hidden = 1;//隐藏字段~~
    * ~~TYPE_Text = 10;//普通文本~~
    * ~~TYPE_TextArea = 15;//多行文本~~
    * ~~TYPE_RiceText = 16;//富文本~~
    * ~~TYPE_Password = 17;//密码类型~~
    * ~~TYPE_InputNumber = 20;//计数器~~
    * ~~TYPE_Slider = 26;//滑块~~
    * ~~TYPE_Switch = 27;//开关~~
    * TYPE_Rate = 28;//评分
    * TYPE_Color = 29;//颜色
    * TYPE_CheckBox = 30;//多选框
    * ~~TYPE_Radio = 35;//单选~~
    * ~~TYPE_Select = 40;//选择器~~
    * TYPE_Cascader = 45;//级联选择器
    * TYPE_Date = 50;//日期
    * TYPE_Time = 55;//时间
    * TYPE_DateTime = 56;//日期时间
    * TYPE_Upload = 60;//文件上传

- 使用table-expand显示DefGroup
    * ~~基本Table及展开表单显示~~
    * 新增和删除支持
    * 自定义显示表格内容
- 使用数值字典数据显示字典数据的label
    * ~~在代码生成时获取字典数据,直接生成对应的转换代码[TYPE_Radio]~~
    * 在运行时获取字典的json数据,动态转换;
- 根据定义项的format格式化显示到界面的数据
    * 后台数据格式化输出到请求的json
    * 前台数据格式化显示
- 根据定义项的validation生成表单项的前端效验
    * 前台FormItem的效验class
    * 后台保存数据时的数据效验

## UIDataGridDef--查询数据Grid页面定义
设计并生成一个带有过滤条件的数据表格界面。包含查询条件表单，数据显示表格，汇总数据，翻页组件及关联操作（多选框，操作按钮组，批量操作功能按钮）等常见信息。

## UIDataTableDef--简单数据表格页面定义
纯数据显示的表格应用，可翻页，可显示汇总信息，可分组，可支持树形结构数据表格

## UIChartDef--数据图表页面定义
图表数据显示组件，封装使用echart。
