<#assign data={} />
<#assign jdkVersion='1.8' />
${LoadYaml('data', './gradle.yml', 'props')}
<#macro commonCodes>
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
		configurations.compile.each {
			File file -> sw.write(file.toString() + '\n')
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

</#macro>

<#macro genCommon cfg>
group = '${cfg.group}'
version = '${cfg.version}'
description = """${cfg.description}"""
archivesBaseName = '${cfg.project}'
<#if (cfg.applyFrom)?? && cfg.applyFrom?trim != ''>apply from: '${cfg.applyFrom}'</#if>

plugins {
	<#list cfg.plugins as plugin>
	id '${plugin}'
	</#list>
}

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
	<@WriteFtl out="./${subProjectName}/build.gradle" comment='gen sub project:'+subProjectName>
	<@genCommon cfg=subProject />
	<@LeftTab left=-1>
	configurations {
		published
	}
	</@LeftTab>
	</@WriteFtl>
</#list>
</#macro>

<@WriteFtl out="./build.gradle" comment='gen main project'>
<@genCommon cfg=data />
<#if (data.subProjects)?? && data.subProjects?size gt 0>
subprojects {
	<@LeftTab left=1>
	<@commonCodes />
	</@LeftTab>
}
<@genSubProject />
<#else>
<@commonCodes />
configurations {
	published
}
</#if>
</@WriteFtl>