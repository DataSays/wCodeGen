<#assign data=data!{} />
<#assign type=type!'' />
<#import 'inc/fn.ftl' as f>
<#assign jdkVersion=(data.jdkVersion)!'1.8' />
<#--------------------------------------------------------->
<#macro repositories>
	repositories {
		mavenLocal()
		maven {
			url "http://maven.aliyun.com/nexus/content/groups/public/"
		}
		jcenter()
		//mavenCentral()
	}
</#macro>

<#--------------------------------------------------------->
<#macro idea>
	idea{
		project {
			languageLevel = '${jdkVersion}'
			ipr {
				withXml {
					provider -> provider.node.component.find { it.@name == 'VcsDirectoryMappings' }.mapping.@vcs = 'Git'
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

<#--------------------------------------------------------->
<#macro javaPlugin project>
<#if project.plugins?seq_contains('java')>
<#if project.plugins?seq_contains('war')>
	war {
		<#if ((project.archiveName)!'') != ''>archiveName = '${project.archiveName}'</#if>
	}
<#else>
	jar {
		baseName = '${project.project}'
		version = '${project.version}'
		<#if ((project.archiveName)!'') != ''>archiveName = '${project.archiveName}'</#if>
	}
</#if>

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
		from {
			configurations.compile.collect {
				it.isDirectory() ? it : zipTree(it)
			}
		}
		with jar
	}
	</#if>
</#if>
</#macro>

<#--------------------------------------------------------->
<#macro genCommon cfg>
	plugins {
		<#list cfg.plugins as plugin>
			<#assign tmpIndex=plugin?index_of(':') />
			<#if tmpIndex gt 0>id '${plugin[0..tmpIndex-1]}' version '${plugin[(tmpIndex+1)..]}'
			<#else >id '${plugin}'
			</#if>
		</#list>
	}

	group = '${cfg.group}'
	version = '${cfg.version}'
	description = """${(cfg.description)!''}"""
	<#if (cfg.applyFrom)?? && cfg.applyFrom?trim != ''>apply from: '${cfg.applyFrom}'</#if>

	<#if (cfg.deps)?? && cfg.deps?size gt 0>
	dependencies {
		<#list cfg.deps as dep>
		${dep}
		</#list>
	}
	</#if>

	<#if (cfg.dependencyManagement)?? && cfg.dependencyManagement?size gt 0>
	dependencyManagement {
		<#list cfg.dependencyManagement as depManagement>
		${depManagement}
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
</#macro>

<#----------------------------------------------------------------------------------------------------->
<#-- 生成主项目-->
<#if type=='mainProject'>
	<#-- 生成默认目录-->
	<#if data.plugins?seq_contains('java')>
		<@f.writeFtl out="/src/main/java/" />
		<@f.writeFtl out="/src/main/resources/" />
		<@f.writeFtl out="/src/test/java/" />
		<@f.writeFtl out="/src/test/resources/" />
	</#if>
	<@f.writeFtl out="/build.gradle" comment='gen main project'>
		<@genCommon cfg=data />
		<@repositories />
		subprojects {
			<@repositories />
		}
		<@idea />
		${data.ExtCodes!''}
	</@f.writeFtl>
	<@f.writeFtl out="/settings.gradle" comment='gen settings.gradle'>
		<#list data.subProjects?keys as subProjectName>
		include ':${subProjectName}'
		</#list>
	</@f.writeFtl>
<#elseif type=='subProject'>
	<#-- 生成默认目录-->
	<#if data.plugins?seq_contains('java')>
		<@f.writeFtl out="/${subProjectName}/src/main/java/" />
		<@f.writeFtl out="/${subProjectName}/src/main/resources/" />
		<@f.writeFtl out="/${subProjectName}/src/test/java/" />
		<@f.writeFtl out="/${subProjectName}/src/test/resources/" />
	</#if>
	<#assign subProject=data.subProjects[subProjectName] />
	<#assign subProject=subProject+{'project': subProjectName, 'group':data.group} />
	<@f.writeFtl out="/${subProjectName}/build.gradle" comment='gen sub project:'+subProjectName>
		<@genCommon cfg=subProject />
		<@javaPlugin project=subProject />
		configurations {
			published
		}
		${subProject.ExtCodes!''}
	</@f.writeFtl>
<#else>
	<#-- 生成默认目录-->
	<#if data.plugins?seq_contains('java')>
		<@f.writeFtl out="/src/main/java/" />
		<@f.writeFtl out="/src/main/resources/" />
		<@f.writeFtl out="/src/test/java/" />
		<@f.writeFtl out="/src/test/resources/" />
	</#if>
	<@f.writeFtl out="/build.gradle" comment='gen single project'>
		<@genCommon cfg=data />
		<@repositories />
		<@idea />
		<@javaPlugin project=data />
		configurations {
			published
		}
		${data.ExtCodes!''}
	</@f.writeFtl>
</#if>
