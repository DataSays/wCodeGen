<#assign data=data!{} />
<#assign Ext=Ext!{} />
<#import 'inc/fn.ftl' as f>
<#if ((data.ExtJsonData)!'') != ''>
	${f.loadJson('Ext', data.ExtJsonData)}
</#if>
${f.loadYaml('versions', './versions.yml', '')}
<#assign versions=versions!{} />
<#function libInfo id>
	<#if (versions[id]!'') != ''>
	<#return versions[id]?split(':') >
	</#if>
    <#return ['','','']>
</#function>

<#-- 生成gradle.yml文件-->
<@f.writeFtl out="/gradle.yml" comment='gen main project'>
WorkDir: ..
GenType: gradle
props:
group: ${data.group}
project: ${data.project}
version: ${data.version}
plugins: [eclipse,idea,"com.github.ben-manes.versions' version '${libInfo('ben-manes')[2]}"]
description: ${data.description}
subProjects:
<#list data.subProjects?keys as subProjectName>
	<#assign subProject=data.subProjects[subProjectName] />
	<#assign subProject=subProject+{'project': subProjectName, 'group':data.group} />
  ${subProjectName}:
    version: ${data.version}
    description: ${subProject.description}
    plugins: [java,eclipse,idea,maven,"org.springframework.boot' version '${libInfo('plugin-boot')[2]}"]
    deps:
<@f.leftTab left=3><#compress >
	<#list subProject.components as comp>
	<#if comp == 'starter-test'>
	    - testCompile '${(versions[comp])!comp}'
	<#elseif comp == 'extDeps'>
		<#assign extDeps=data.props.extDeps />
		<#list extDeps as dep>
		- testCompile '${(versions[dep])!dep}'
		</#list>
	<#elseif comp == 'extTestDeps'>
	<#assign extTestDeps=data.props.extTestDeps />
	<#list extTestDeps as dep>
		- testCompile '${(versions[dep])!dep}'
	</#list>
	<#else>
	    - compile '${(versions[comp])!comp}'
	</#if>
	</#list>
</#compress>
</@f.leftTab>

</#list>
</@f.writeFtl>
<#-- 生成docker-compose.yml文件-->