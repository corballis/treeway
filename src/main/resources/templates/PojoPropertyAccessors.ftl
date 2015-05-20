<#-- // Property accessors -->
<#assign pluralizedDeclarationName = templateUtil.simplePluralize(pojo.getDeclarationName())/>
<#foreach property in pojo.getAllPropertiesIterator()>
<#assign singularizedPropertyName = templateUtil.singularize(property.name)/>
<#if property.getMetaAttribute("annotation")??>
<#foreach value in property.getMetaAttribute("annotation").getValues()>
    ${value}
</#foreach>
</#if>
<#if pojo.getMetaAttribAsBool(property, "gen-property", true)>
 <#if pojo.hasFieldJavaDoc(property)>    
    /**       
     * ${pojo.getFieldJavaDoc(property, 4)}
     */
</#if>
    <#include "GetPropertyAnnotation.ftl"/>
    ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5)} ${pojo.getGetterSignature(property)}() {
        return this.${property.name};
    }
    
    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5)} ${property.name}) {
        <#if c2h.isCollection(property)>
        checkNotNull(${property.name}, "${property.name} must be present");
        </#if>
        this.${property.name} = ${property.name};
        <#if c2h.isCollection(property) && templateUtil.isOtherSideGenerated(property, cfg)>
        for (${pojo.getJavaTypeName(property, jdk5).replaceAll("^Set<(.*)>", "$1")} obj : ${property.name}) {
        <#if c2h.isOneToMany(property)>
            obj.${templateUtil.getSetterName(pojo.getDeclarationName(), singularizedPropertyName)}(this);
        <#else>
            obj.${templateUtil.getGetterName(pojo.getDeclarationName(), property.name)}().add(this);
        </#if>
        }
        <#elseif c2h.isManyToOne(property) && templateUtil.isOtherSideGenerated(property, cfg)>
        if (${property.name} != null) {
            ${property.name}.${templateUtil.getGetterName(pojo.getDeclarationName(), property.name)}().add(this);
        }
        </#if>
    }

    <#if c2h.isCollection(property)>
    ${pojo.getPropertySetModifiers(property)} void addTo${singularizedPropertyName?cap_first}(${pojo.getJavaTypeName(property, jdk5).replaceAll("^Set<(.*)>", "$1")} ${singularizedPropertyName}) {
        this.get${pojo.getPropertyName(property)}().add(${singularizedPropertyName});
        <#if c2h.isOneToMany(property)>
        ${singularizedPropertyName}.${templateUtil.getSetterName(pojo.getDeclarationName(), singularizedPropertyName)}(this);
        <#elseif templateUtil.isOtherSideGenerated(property, cfg)>
        ${singularizedPropertyName}.${templateUtil.getGetterName(pojo.getDeclarationName(), property.name)}().add(this);
        </#if>
    }
    </#if>
</#if>
</#foreach>
