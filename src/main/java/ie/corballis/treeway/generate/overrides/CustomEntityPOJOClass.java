package ie.corballis.treeway.generate.overrides;

import ie.corballis.treeway.generate.TemplateUtil;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;

public class CustomEntityPOJOClass extends EntityPOJOClass {

    public CustomEntityPOJOClass(PersistentClass clazz, Cfg2JavaTool cfg) {
        super(clazz, cfg);
    }

    @Override
    public String getDeclarationName() {
        String declarationName = super.getDeclarationName();
        return TemplateUtil.getDeclarationName(declarationName);
    }

    @Override
    public String getExtends() {
        return TemplateUtil.getExtends(super.getDeclarationName());
    }

    @Override
    public String getJavaTypeName(Property p, boolean useGenerics) {
        String javaTypeName = super.getJavaTypeName(p, useGenerics);
        return javaTypeName.replaceAll("^(.*)Extends(.*)", "$1");
    }
}
