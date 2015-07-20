<#-- // Fields -->
<#if pojo.hasMetaAttribute("has-selected-option")>
    @Transient
    <#if pojo.hasMetaAttribute("selected-options-filter")>
    ${pojo.getMetaAsString("selected-options-filter")}
    </#if>
    private Multimap<String, SelectedOptionValue> selectedOptionValues = ArrayListMultimap.create();
</#if>
<#foreach field in pojo.getAllPropertiesIterator()><#if pojo.getMetaAttribAsBool(field, "gen-property", true)> <#if pojo.hasMetaAttribute(field, "field-description")>    /**
     ${pojo.getFieldJavaDoc(field, 0)}
     */
 </#if>
     <#if field.getMetaAttribute("annotation")??>
     <#foreach value in field.getMetaAttribute("annotation").getValues()>
         ${value}
     </#foreach>
     </#if>
     <#include "GetPropertyAnnotation.ftl"/>
     ${pojo.getFieldModifiers(field)} ${pojo.getJavaTypeName(field, jdk5)} ${field.name}<#if pojo.hasFieldInitializor(field, jdk5)> = ${pojo.getFieldInitialization(field, jdk5)}</#if>;
</#if>
</#foreach>
