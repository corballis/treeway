<#if pojo.hasMetaAttribute("class-code")>  // The following is extra code specified in the hbm.xml files
${pojo.getExtraClassCode()}
  // end of extra code specified in the hbm.xml files
</#if>
<#if !pojo.hasMetaAttribute("extends")>

    @Transient
    public boolean isNew() {
        <#if pojo.hasMetaAttribute("generate-uuid")>
        return this.isNew;
        <#else>
        return null == getId();
        </#if>
    }

    @Override
	public boolean equals(Object obj) {

		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		${pojo.getDeclarationName()} that = (${pojo.getDeclarationName()}) obj;

		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {

		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}
</#if>
