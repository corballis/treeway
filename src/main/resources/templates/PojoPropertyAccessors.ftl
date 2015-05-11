<#-- // Property accessors -->
<#assign pluralizedDeclarationName = templateUtil.simplePluralize(pojo.getDeclarationName())/>
<#foreach property in pojo.getAllPropertiesIterator()>
<#assign singularizedPropertyName = templateUtil.singularize(property.name)/>
<#foreach column in property.getColumnIterator()>
<#if column.comment??>
    ${column.comment}
</#if>
</#foreach>
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
        checkNotNull(${property.name}, "${property.name?capitalize} must be present");
        </#if>
        this.${property.name} = ${property.name};
        <#if c2h.isCollection(property)>
        for (${pojo.getJavaTypeName(property, jdk5).replaceAll("^Set<(.*)>", "$1")} obj : ${property.name}) {
        <#if c2h.isOneToMany(property)>
            obj.set${pojo.getDeclarationName()}(this);
        <#else>
            obj.get${pluralizedDeclarationName}().add(this);
        </#if>
        }
        <#elseif c2h.isManyToOne(property)>
        ${property.name}.get${pluralizedDeclarationName}().add(this);
        </#if>
    }

    <#if c2h.isCollection(property)>
    ${pojo.getPropertySetModifiers(property)} void add${singularizedPropertyName?capitalize}(${pojo.getJavaTypeName(property, jdk5).replaceAll("^Set<(.*)>", "$1")} ${singularizedPropertyName}) {
        this.get${pojo.getPropertyName(property)}().add(${singularizedPropertyName});
        <#if c2h.isOneToMany(property)>
        ${singularizedPropertyName}.set${pojo.getDeclarationName()}(this);
        <#else>
        ${singularizedPropertyName}.get${pluralizedDeclarationName}().add(this);
        </#if>
    }
    </#if>
</#if>
</#foreach>
