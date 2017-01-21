<#assign data={} />
<#assign jdkVersion='1.8' />
${LoadYaml('data', './gradle.yml', 'props')}
<@WriteFtl out="./build.gradle">
plugins {
	<#list data.plugins as plugin>
	id '${plugin}'
	</#list>
}

group = '${data.group}'
version = '${data.version}'
description = """${data.description}"""

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
		<@WriteFtl out="./${subProjectName}/build.gradle" left=2>
		plugins {
			<#list subProject.plugins as plugin>
			id '${plugin}'
			</#list>
		}

		group = '${data.group}'
		version = '${subProject.version}'
		description = """${subProject.description}"""
		archivesBaseName = '${subProjectName}'
		<#if (subProject.applyFrom)?? && subProject.applyFrom?trim != ''>apply from: '${subProject.applyFrom}'</#if>

		dependencies {
			<#list subProject.deps as dep>
			${dep}
			</#list>
		}

		configurations {
			published
		}

		<#list (subProject.GradleJavaTask!{})?keys as taskName>
		<#assign taskArgs=subProject.GradleJavaTask[taskName] />
		task ${taskName}(type: JavaExec, dependsOn: []) {
			workingDir = '${taskArgs[1]}'
			classpath = sourceSets.main.runtimeClasspath
			main = '${taskArgs[0]}'
			args = [${(taskArgs[2])!''}]
			systemProperties System.getProperties()
		}
		</#list>
		${subProject.ExtCodes!''}
		</@WriteFtl>
	</#list>
</#macro>