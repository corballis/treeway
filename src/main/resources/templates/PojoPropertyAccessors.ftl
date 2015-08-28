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

    ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5, cfg)} ${pojo.getGetterSignature(property)}() {
        return this.${property.name};
    }
    
    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5, cfg)} ${property.name}) {
        <#if pojo.hasMetaAttribute("generate-uuid") && property.name.equals("id")>
        this.isNew = false;
        </#if>
        <#if c2h.isCollection(property)>
        this.${property.name}.isEmpty();
        if (${property.name} == null) {
           ${property.name} = ${pojo.getFieldInitialization(property, jdk5, cfg)};
        }
        this.${property.name}.clear();
        this.${property.name}.addAll(${property.name});
        <#else>
        this.${property.name} = ${property.name};
        </#if>
        <#if c2h.isCollection(property) && templateUtil.isOtherSideGeneratedForCollection(property, cfg)>
        for (${pojo.getGenericClassName(property)} obj : ${property.name}) {
        <#if c2h.isOneToMany(property)>
            obj.${templateUtil.getOneToManySetter(property)}(this);
        <#else>
            obj.${templateUtil.getGetterName(pojo, property)}().add(this);
        </#if>
        }
        <#elseif c2h.isManyToOne(property) && templateUtil.isOtherSideGenerated(property, cfg)>
        if (${property.name} != null && !${property.name}.${templateUtil.getGetterName(pojo, property)}().contains(this)) {
            ${property.name}.${templateUtil.getGetterName(pojo, property)}().add(this);
        }
        </#if>
    }

    <#if c2h.isCollection(property)>
    ${pojo.getPropertySetModifiers(property)} void addTo${property.name?cap_first}(${pojo.getGenericClassName(property)} ${templateUtil.collectionTableName(property)}) {
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
    @JsonDeserialize(using = SelectedOptionValuesDeserializer.class)
    @JsonSerialize(using = SelectedOptionValuesSerializer.class)
    @JsonView(AlwaysIncluded.class)
    public List<SelectedOption> getSelectedOptionValues() {
        return selectedOptionValues;
    }

    @Override
    public void setSelectedOptionValues(List<SelectedOption> selectedOptionValues) {
        addSelectedOptions(getId(), selectedOptionValues, this);
        this.selectedOptionValues = selectedOptionValues;
    }
</#if>

<#if pojo.hasMetaAttribute("generate-uuid")>
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
</#if>
