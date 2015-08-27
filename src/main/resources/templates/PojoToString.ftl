
    <#assign primitive_types = ['boolean', 'short', 'int', 'long', 'float', 'double', 'Boolean', 'Short', 'Integer', 'Long', 'Float', 'Double', 'String', 'DateTime']>
    /**
     * toString
     * @return String
     */
     public String toString() {
	     StringBuilder sb = new StringBuilder("${pojo.getDeclarationName()}{");
	     sb.append("id='").append(getId()).append('\'');
        <#foreach field in pojo.getAllPropertiesIterator()>
        <#if !field.name.equals("id")>
        <#if c2h.isCollection(field)>
        sb.append(", ${field.name}='");
        <#assign type=pojo.getGenericClassName(field)>
        <#if primitive_types?seq_contains(type)>
        sb.append(${field.name} == null ? null : Joiner.on(", ")
                                                       .skipNulls()
                                                       .join(${field.name}))
                                                       .append('\'');
        <#else>
        sb.append(${field.name} == null ? null : Joiner.on(", ")
                                                       .skipNulls()
                                                       .join(CollectionUtils.collect(${field.name},
                                                                                    new Transformer<${type}, String>() {
                                                                                        @Override
                                                                                        public String transform(${type} obj) {
                                                                                            return String.valueOf(obj.getId());
                                                                                        }
                                                                                    }))).append('\'');
        </#if>
        <#else>
        <#if primitive_types?seq_contains(pojo.getJavaTypeName(field, jdk5, cfg)) || pojo.isEnum(field)>
        sb.append(", ${field.name}='").append(${field.name}).append('\'');
        <#else>
        sb.append(", ${field.name}='").append(${field.name} == null ? null : ${field.name}.getId()).append('\'');
        </#if>
        </#if>
        </#if>
        </#foreach>
        sb.append('}');
        return sb.toString();
     }