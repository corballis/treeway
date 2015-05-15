${pojo.getPackageDeclaration()}
// Generated ${date} by Hibernate Tools ${version}

<#assign classbody>
<#include "PojoTypeDeclaration.ftl"/> {

<#if !pojo.isInterface()>
<#include "PojoFields.ftl"/>

<#include "PojoPropertyAccessors.ftl"/>

<#include "PojoToString.ftl"/>

<#else>
<#include "PojoInterfacePropertyAccessors.ftl"/>

</#if>
<#include "PojoExtraClassCode.ftl"/>

}
</#assign>

${pojo.generateImports()}
import com.google.common.base.Joiner;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.persistence.Transient;
import org.springframework.data.domain.Persistable;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.*;
import javax.validation.constraints.*;
import javax.persistence.*;
import org.hibernate.annotations.Type;
${classbody}

