<#include "Ejb3PropertyGetAnnotation.ftl"/>
<#if pojo.getJavaTypeName(field, jdk5).equals("DateTime")>    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")</#if>
