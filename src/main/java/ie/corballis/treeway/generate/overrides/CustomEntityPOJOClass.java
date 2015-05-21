package ie.corballis.treeway.generate.overrides;

import com.google.common.base.Joiner;
import ie.corballis.treeway.generate.TemplateUtil;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.*;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;

import java.util.Iterator;

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

    @Override
    public String generateCollectionAnnotation(Property property, Configuration cfg) {
        StringBuilder annotations = new StringBuilder(super.generateCollectionAnnotation(property, cfg));
        Value value = property.getValue();
        if ( value != null && value instanceof Collection) {
            Collection collection = (Collection) value;
            if ( collection.isInverse() ) {
                PersistentClass pc = null;
                if ( collection.isOneToMany() ) {
                    pc = cfg.getClassMapping( ((OneToMany) collection.getElement()).getReferencedEntityName());
                } else {
                    pc = cfg.getClassMapping( ((ManyToOne) collection.getElement()).getReferencedEntityName());
                }
                Iterator properties = pc.getPropertyClosureIterator();
                while (properties.hasNext() ) {
                    Property manyProperty = (Property) properties.next();
                    MetaAttribute inverseAnnotation = manyProperty.getMetaAttribute("inverse-annotation");
                    if (inverseAnnotation != null) {
                        appendNewAnnotations(annotations, inverseAnnotation);
                    }
                }
            }
        }
        return annotations.toString();
    }

    private void appendNewAnnotations(StringBuilder annotations, MetaAttribute inverseAnnotation) {
        if (annotations.length() > 0) {
            annotations.append("\n");
        }
        annotations.append(Joiner.on("\n").join(inverseAnnotation.getValues()));
    }
}
