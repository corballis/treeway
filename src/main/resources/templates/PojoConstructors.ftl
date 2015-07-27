
<#--  /** default constructor */ -->
    public ${pojo.getDeclarationName()}() {
        <#if pojo.hasMetaAttribute("generate-uuid")>
            setId(UUID.randomUUID().toString());
        </#if>
    }
