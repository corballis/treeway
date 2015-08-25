
<#--  /** default constructor */ -->
    public ${pojo.getDeclarationName()}() {
        <#if pojo.hasMetaAttribute("generate-uuid")>
            setId(UUID.randomUUID().toString());
            this.isNew = true;
        </#if>
    }

<#--  /** copy constructor */ -->
<#if pojo.hasMetaAttribute("generate-copy-constructor")>
    public ${pojo.getDeclarationName()}(${pojo.getDeclarationName()} that) {
        this();
        <#foreach field in pojo.getPropertiesForFullConstructor()>
            <#if !field.name.equals("id")>
                <#if c2h.isCollection(field)>
        this.${field.name}.addAll(that.${field.name});
                <#else>
        this.${field.name} = that.${field.name};
                </#if>
            </#if>
        </#foreach>
    }
</#if>
