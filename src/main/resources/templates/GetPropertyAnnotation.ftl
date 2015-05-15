<#include "Ejb3PropertyGetAnnotation.ftl"/>
<#if pojo.getJavaTypeName(property, jdk5).equals("DateTime")>    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")</#if>
