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
    public boolean hasFieldInitializor(Property p, boolean useGenerics) {
        return super.getFieldInitialization(p, useGenerics) != null;
    }

    @Override
    public String getGetterSignature(Property p) {
        String signature = super.getGetterSignature(p);
        String[] signatureElements = TemplateUtil.splitByUpperCase(signature);

        if (signatureElements[1].equals("Has")) {
            return signature.replaceAll("^isHas(.*)", "has$1");
        }

        return signature;
    }

    @Override
    public String getImplements() {
        String implementsString = super.getImplements();
        if (implementsString != null && !implementsString.equals("")) {
            return implementsString + ", Persistable<Long>";
        }
        return "Persistable<Long>";
    }
}
