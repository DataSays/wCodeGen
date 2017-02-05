<#assign jdkVersion='1.8' />
<#macro repositories>
repositories {
	mavenLocal()
	maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
	jcenter()
	//mavenCentral()
}
</#macro>
<#macro idea>
idea{
	project {
		languageLevel = '${jdkVersion}'
		ipr {
			withXml { provider ->
				provider.node.component.find { it.@name == 'VcsDirectoryMappings' }.mapping.@vcs = 'Git'
			}
		}
	}
	module {
		inheritOutputDirs = false
		outputDir = file("$buildDir/classes/main/")
		testOutputDir = file("$buildDir/classes/test/")
	}
}
</#macro>

<#macro javaPlugin project>
<#if project.plugins?seq_contains('java')>
jar {
	baseName = '${project.project}'
	version = '${project.version}'
}

sourceCompatibility = ${jdkVersion}
targetCompatibility = ${jdkVersion}

tasks.withType(JavaCompile) {
	sourceCompatibility = ${jdkVersion}
	targetCompatibility = ${jdkVersion}
	options.encoding = "UTF-8"
}
<#if (project.fatJar!'') != ''>
task fatJar(type: Jar) {
	manifest {
		attributes 'Implementation-Title': '${project.description}',
		'Implementation-Version': version,
		'Main-Class': '${project.fatJar}'
	}

	baseName = project.name + '-all'
	from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
	with jar
}
</#if>
</#if>
</#macro>

<#macro genCommon cfg>
plugins {
	<#list cfg.plugins as plugin>
	id '${plugin}'
	</#list>
}

group = '${cfg.group}'
version = '${cfg.version}'
description = """${cfg.description}"""
<#if (cfg.applyFrom)?? && cfg.applyFrom?trim != ''>apply from: '${cfg.applyFrom}'</#if>

<#if (cfg.deps)?? && cfg.deps?size gt 0>
dependencies {
	<#list cfg.deps as dep>
	${dep}
	</#list>
}
</#if>

<#list (cfg.GradleJavaTask!{})?keys as taskName>
<#assign taskArgs=cfg.GradleJavaTask[taskName] />
task ${taskName}(type: JavaExec, dependsOn: []) {
	workingDir = '${taskArgs[1]}'
	classpath = sourceSets.main.runtimeClasspath
	main = '${taskArgs[0]}'
	args = [${(taskArgs[2])!''}]
	systemProperties System.getProperties()
}
</#list>
${cfg.ExtCodes!''}
</#macro>

<#macro genSubProject>
<#list data.subProjects?keys as subProjectName>
	<#assign subProject=data.subProjects[subProjectName] />
	<#assign subProject=subProject+{'project': subProjectName, 'group':data.group} />
	<@WriteFtl out="/${subProjectName}/build.gradle" comment='gen sub project:'+subProjectName>
		<@genCommon cfg=subProject />
		<@javaPlugin project=subProject />
		configurations {
			published
		}
	</@WriteFtl>
</#list>
</#macro>

<#-- 生成默认目录-->
<#if data.plugins?seq_contains('java')>
	<@WriteFtl out="/src/main/java/" />
	<@WriteFtl out="/src/main/resources/" />
	<@WriteFtl out="/src/test/java/" />
	<@WriteFtl out="/src/test/resources/" />
</#if>
<#-- 生成主项目-->
<@WriteFtl out="/build.gradle" comment='gen main project'>
	<@genCommon cfg=data />
	<#-- 有子项目-->
	<#if (data.subProjects)?? && data.subProjects?size gt 0>
		subprojects {
			<@repositories />
		}
		<@idea />
		<@genSubProject />
		<@WriteFtl out="/settings.gradle" comment='gen settings.gradle'>
			include <#list data.subProjects?keys as subProjectName>':${subProjectName}'${(subProjectName?has_next)?string(', ', '')}</#list>
		</@WriteFtl>
	<#else><#-- 没有子项目 -->
		<@repositories />
		<@idea />
		<@javaPlugin project=data />
		configurations {
			published
		}
	</#if>
</@WriteFtl>