<#-- // Property accessors -->
<#assign pluralizedDeclarationName = templateUtil.simplePluralize(pojo.getDeclarationName())/>
<#foreach property in pojo.getAllPropertiesIterator()>
<#assign singularizedPropertyName = templateUtil.singularize(property.name)/>
<#if pojo.getMetaAttribAsBool(property, "gen-property", true)>
 <#if pojo.hasFieldJavaDoc(property)>    
    /**       
     * ${pojo.getFieldJavaDoc(property, 4)}
     */
</#if>

    ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5)} ${pojo.getGetterSignature(property)}() {
        return this.${property.name};
    }
    
    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5)} ${property.name}) {
        <#if c2h.isCollection(property)>
        this.${property.name}.isEmpty();
        if (${property.name} == null) {
           ${property.name} = new HashSet<${pojo.getJavaTypeName(property, jdk5).replaceAll("^Set<(.*)>", "$1")}>();
        }
        this.${property.name}.clear();
        this.${property.name}.addAll(${property.name});
        <#else>
        this.${property.name} = ${property.name};
        </#if>
        <#if c2h.isCollection(property) && templateUtil.isOtherSideGeneratedForCollection(property, cfg)>
        for (${pojo.getJavaTypeName(property, jdk5).replaceAll("^Set<(.*)>", "$1")} obj : ${property.name}) {
        <#if c2h.isOneToMany(property)>
            obj.${templateUtil.getOneToManySetter(property)}(this);
        <#else>
            obj.${templateUtil.getGetterName(pojo, property)}().add(this);
        </#if>
        }
        <#elseif c2h.isManyToOne(property) && templateUtil.isOtherSideGenerated(property, cfg)>
        if (${property.name} != null) {
            ${property.name}.${templateUtil.getGetterName(pojo, property)}().add(this);
        }
        </#if>
    }

    <#if c2h.isCollection(property)>
    ${pojo.getPropertySetModifiers(property)} void addTo${property.name?cap_first}(${pojo.getJavaTypeName(property, jdk5).replaceAll("^Set<(.*)>", "$1")} ${templateUtil.collectionTableName(property)}) {
        if (${templateUtil.collectionTableName(property)} != null) {
            this.get${pojo.getPropertyName(property)}().add(${templateUtil.collectionTableName(property)});
            <#if templateUtil.isOneToManyAndHasReferenceOnTheOtherSide(property)>
            ${templateUtil.collectionTableName(property)}.${templateUtil.getOneToManySetter(property)}(this);
            <#elseif templateUtil.isOtherSideGenerated(property, cfg)>
            ${templateUtil.collectionTableName(property)}.${templateUtil.getGetterName(pojo, property)}().add(this);
            </#if>
        }
    }
    </#if>
</#if>
</#foreach>
<#if pojo.hasMetaAttribute("has-selected-option")>
    @Override
    @JsonProperty("selectedOptionValues")
    public List<SelectedOption> getSelectedOptionValues() {
        return selectedOptionValues;
    }

    @Override
    public void setSelectedOptionValues(List<SelectedOption> selectedOptionValues) {
        this.selectedOptionValues = selectedOptionValues;
    }
</#if>
