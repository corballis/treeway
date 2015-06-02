<#if ejb3>
<#if pojo.hasIdentifierProperty()>
<#if field.equals(clazz.identifierProperty)>
 ${pojo.generateAnnIdGenerator()}
<#-- if this is the id field-->
<#-- explicitly set the column name for this field-->
</#if>
</#if>
<#if c2h.isManyToOne(field)>
${pojo.generateManyToOneAnnotation(field)}
${pojo.generateJoinColumnsAnnotation(field, cfg)}
<#elseif c2h.isCollection(field)>
${pojo.generateCollectionAnnotation(field, cfg)}
<#else>
${pojo.generateBasicAnnotation(field)}
${pojo.generateAnnColumnAnnotation(field)}
</#if>
</#if>