<#include "Ejb3PropertyGetAnnotation.ftl"/>
<#if pojo.getJavaTypeName(field, jdk5, cfg).equals("DateTime")>    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")</#if>
<#if pojo.hasMetaAttribute("generate-uuid") && field.name.equals("id")>    @Access(AccessType.PROPERTY)</#if>
