<#assign data={} />
<#assign jdkVersion='1.8' />
<#function evl text>
	<#assign props=data.props!{}/>
	<#assign result=text/>
	<#list props?keys as key>
		<#assign result=result?replace('$'+'{'+key+'}', props[key]) />
	</#list>
	<#return result />
</#function>
${LoadYaml('data', './gradle.yml')}
<@WriteFtl out="./build.gradle">
plugins {
	<#list data.plugins as plugin>
	id '${evl(plugin)}'
	</#list>
}

group = '${evl(data.group)}'
version = '${evl(data.version)}'
description = """${evl(data.description)}"""

subprojects {
	sourceCompatibility = ${jdkVersion}
	targetCompatibility = ${jdkVersion}

	tasks.withType(JavaCompile) {
		sourceCompatibility = ${jdkVersion}
		targetCompatibility = ${jdkVersion}
		options.encoding = "UTF-8"
	}

	repositories {
		mavenLocal()
		maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
		//mavenCentral()
		jcenter()
	}

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

	task listJars {
		doLast {
			def sw = new StringWriter()
			configurations.compile.each { File file ->
				sw.write(file.toString() + '\n')
			}
			def allJarsFile = new File("./AllJars.txt")
			allJarsFile.write(sw.toString())
		}
	}

	task fatJar(type: Jar) {
		baseName = project.name + '-all'
		from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
		with jar
	}
}
<@genSubProject />
</@WriteFtl>

<#macro genSubProject>
	<#list data.subProjects?keys as subProjectName>
		<#assign subProject=data.subProjects[subProjectName] />
		<@WriteFtl out="./${evl(subProjectName)}/build.gradle" left=2>
		plugins {
			<#list subProject.plugins as plugin>
			id '${evl(plugin)}'
			</#list>
		}

		group = '${evl(data.group)}'
		version = '${evl(subProject.version)}'
		description = """${evl(subProject.description)}"""
		archivesBaseName = '${evl(subProjectName)}'
		<#if (subProject.applyFrom!'') != ''>apply from: '${evl(subProject.applyFrom)}'</#if>

		dependencies {
			<#list subProject.deps as dep>
			${evl(dep)}
			</#list>
		}

		configurations {
			published
		}

		<#list (subProject.GradleJavaTask!{})?keys as taskName>
		<#assign taskArgs=subProject.GradleJavaTask[taskName] />
		task ${evl(taskName)}(type: JavaExec, dependsOn: []) {
			workingDir = '${evl(taskArgs[1])}'
			classpath = sourceSets.main.runtimeClasspath
			main = '${evl(taskArgs[0])}'
			args = [${evl((taskArgs[2])!'')}]
			systemProperties System.getProperties()
		}
		</#list>
		${evl(subProject.ExtCodes!'')}
		</@WriteFtl>
	</#list>
</#macro>