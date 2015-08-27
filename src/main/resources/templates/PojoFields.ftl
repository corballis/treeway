<#-- // Fields -->
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
     ${pojo.getFieldModifiers(field)} ${pojo.getJavaTypeName(field, jdk5, cfg)} ${field.name}<#if pojo.hasFieldInitializor(field, jdk5)> = ${pojo.getFieldInitialization(field, jdk5, cfg)}</#if>;
</#if>
</#foreach>
<#if pojo.hasMetaAttribute("has-selected-option")>
    @Transient
    <#if pojo.hasMetaAttribute("selected-options-annotation")>
    ${pojo.getMetaAsString("selected-options-annotation")}
    </#if>
    private List<SelectedOption> selectedOptionValues = newArrayList();
</#if>
<#if pojo.hasMetaAttribute("generate-uuid")>
    @Transient
    private boolean isNew;
</#if>
