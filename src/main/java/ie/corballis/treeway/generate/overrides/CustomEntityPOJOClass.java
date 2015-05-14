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
        return javaTypeName.replaceAll("^(.*)Extends(.*)", "$1" + (javaTypeName.startsWith("Set<") ? ">" : ""));
    }

    @Override
    public String getFieldInitialization(Property p, boolean useGenerics) {
        String fieldInitialization = super.getFieldInitialization(p, useGenerics);
        if (fieldInitialization.matches("new HashSet<(.*)Extends.*>\\(0\\)")) {
            fieldInitialization =
                fieldInitialization.replaceAll("new HashSet<(.*)Extends.*>\\(0\\)", "new HashSet<$1>(0)");
        }
        return fieldInitialization;
    }

    @Override
    public boolean hasFieldInitializor(Property p, boolean useGenerics) {
        return super.getFieldInitialization(p, useGenerics) != null;
    }
}
