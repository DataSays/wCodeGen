<#function largeNum num>
	<#if num == 0>
		<#return '0'/>
	<#elseif num lt 1000 >
		<#return (num)?string(',###')/>
	<#elseif num gte 10000 && num lt 1000000>
		<#return (num/10000)?string(',###.##')+"万"/>
	<#elseif num gte 1000000 && num lt 100000000>
		<#return (num/1000000)?string(',###.##')+"百万"/>
	<#elseif num gte 100000000 && num lt 10000000000>
		<#return (num/100000000)?string(',###.##')+"亿"/>
	<#elseif num gte 10000000000 && num lt 1000000000000>
		<#return (num/10000000000)?string(',###.##')+"百亿"/>
	<#elseif num gte 1000000000000>
		<#return (num/1000000000000)?string(',###.##')+"万亿"/>
	<#else>
		<#return num />
	</#if>
</#function>

<#function largeMoney num>
	<#if num lt 1000 >
		<#return (num)?string(',###.##')/>
	<#else>
		<#return largeNum(num) />
	</#if>
</#function>

<#function out text default=''>
	<#return ((text!'') != '')?string(text?string, default) />
</#function>

<#macro writeFtl out comment=''>
${log(comment)}
<@WriteFtl out=out><#nested ></@WriteFtl>
</#macro>

<#function log msg=''>
<#if msg!=''>
	<#return Log(msg) />
<#else >
    <#return '' />
</#if>
</#function>

<#function loadYaml varName filePath props='props'>
	<#return LoadYaml(varName, filePath, props) />
</#function>

<#function loadJson varName filePath>
	<#return LoadJson(varName, filePath) />
</#function>