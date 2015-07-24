${pojo.getPackageDeclaration()}
// Generated ${date} by Hibernate Tools ${version}

<#assign classbody>
<#include "PojoTypeDeclaration.ftl"/> {

<#include "PojoConstructors.ftl"/>
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
import org.springframework.data.annotation.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.*;
import javax.validation.constraints.*;
import javax.persistence.*;
import java.util.*;
import org.hibernate.annotations.Type;
import com.corballis.data.json.deserializer.*;
import com.corballis.data.json.serializer.*;
import java.util.Collection;
import java.util.Map;
import java.util.List;
<#if pojo.hasMetaAttribute("has-selected-option")>
import static com.google.common.collect.Lists.newArrayList;
import com.corballis.sms.core.option.*;
import static com.corballis.sms.core.option.EntityIdToSelectedOptionValuesMapper.addSelectedOptions;
</#if>
${classbody}

