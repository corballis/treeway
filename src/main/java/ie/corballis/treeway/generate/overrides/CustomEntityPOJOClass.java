package ie.corballis.treeway.generate.overrides;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import ie.corballis.treeway.generate.TemplateUtil;
import org.hibernate.FetchMode;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.*;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.EntityPOJOClass;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class CustomEntityPOJOClass extends EntityPOJOClass {

    public CustomEntityPOJOClass(PersistentClass clazz, Cfg2JavaTool cfg) {
        super(clazz, cfg);
    }

    @Override
    public boolean hasFieldInitializor(Property p, boolean useGenerics) {
        return super.getFieldInitialization(p, useGenerics) != null;
    }

    public boolean isEnum(Property p) throws ClassNotFoundException {
        MetaAttribute annotation = p.getMetaAttribute("annotation");
        if (annotation != null) {
            if (annotation.isMultiValued()) {
                for (Object value : annotation.getValues()) {
                    if (value instanceof String) {
                        return ((String) value).contains("Enumerated");
                    }
                }
            } else {
                String value = annotation.getValue();
                return value.contains("Enumerated");
            }
        }
        return false;
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
        String superImplements = super.getImplements();
        StringBuilder implementsString = new StringBuilder(superImplements);
        if (!superImplements.contains("Persistable")) {
            appendToImplements(implementsString, "Persistable<String>");
        }
        if (hasMetaAttribute("has-selected-option")) {
            appendToImplements(implementsString, "HasSelectedOption");
        }
        return implementsString.toString();
    }

    private void appendToImplements(StringBuilder implementsString, String implementedClass) {
        if (implementsString.length() != 0) {
            implementsString.append(", ").append(implementedClass);
        } else {
            implementsString.append(implementedClass);
        }
    }

    @Override
    public String generateCollectionAnnotation(Property property, Configuration cfg) {
        final StringBuilder annotations = new StringBuilder(super.generateCollectionAnnotation(property, cfg));
        Value value = property.getValue();
        if (value != null && value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.isOneToMany()) {
                addOrphanRemovalClause(property, annotations);

                callFunctionWithInverseProperty(cfg, collection, new Function<Property, Object>() {
                    @Override
                    public Object apply(Property manyProperty) {
                        MetaAttribute inverseAnnotation = manyProperty.getMetaAttribute("inverse-annotation");
                        if (inverseAnnotation != null) {
                            appendNewAnnotations(annotations, inverseAnnotation);
                        }
                        return null;
                    }
                });
            } else {
                // ManyToMany
                Column column = (Column) collection.getKey().getColumnIterator().next();
                Table joinTable = column.getValue().getTable();
                TableIdentifier tableIdentifier =
                    new TableIdentifier(joinTable.getCatalog(), joinTable.getSchema(), joinTable.getName());
                Map metaAttributes = ((JDBCMetaDataConfiguration) cfg).getReverseEngineeringStrategy()
                                                                      .columnToMetaAttributes(tableIdentifier,
                                                                                              column.getName());
                if (metaAttributes != null) {
                    MetaAttribute metaAnnotations = (MetaAttribute) metaAttributes.get("annotation");
                    if (metaAnnotations != null) {
                        appendNewAnnotations(annotations, metaAnnotations);
                    }
                }
            }
        }
        return annotations.toString();
    }

    private <T> T callFunctionWithInverseProperty(Configuration cfg,
                                                  Collection collection,
                                                  Function<Property, T> function) {
        T retVal = null;
        Iterator<Selectable> joinColumnsIt = collection.getKey().getColumnIterator();
        Set<Selectable> joinColumns = new HashSet<Selectable>();
        while (joinColumnsIt.hasNext()) {
            joinColumns.add(joinColumnsIt.next());
        }
        PersistentClass pc = cfg.getClassMapping(((OneToMany) collection.getElement()).getReferencedEntityName());
        Iterator properties = pc.getPropertyClosureIterator();

        boolean isOtherSide = false;
        while (!isOtherSide && properties.hasNext()) {
            Property manyProperty = (Property) properties.next();
            Value manyValue = manyProperty.getValue();
            if (manyValue != null && manyValue instanceof ManyToOne) {
                if (joinColumns.size() == manyValue.getColumnSpan()) {
                    isOtherSide = true;
                    Iterator it = manyValue.getColumnIterator();
                    while (it.hasNext()) {
                        Object column = it.next();
                        if (!joinColumns.contains(column)) {
                            isOtherSide = false;
                            break;
                        }
                    }
                    if (isOtherSide) {
                        retVal = function.apply(manyProperty);
                    }
                }

            }
        }

        return retVal;
    }

    private void addOrphanRemovalClause(Property property, StringBuilder annotations) {
        java.util.List<String> cascadeElements = newArrayList(property.getCascade().split(", "));
        if (cascadeElements.contains("orphan-removal")) {
            annotations.insert(11, "orphanRemoval=true, ");
        }
    }

    private void appendNewAnnotations(StringBuilder annotations, MetaAttribute inverseAnnotation) {
        if (annotations.length() > 0) {
            annotations.append("\n");
        }
        annotations.append(Joiner.on("\n").join(inverseAnnotation.getValues()));
    }

    @Override
    public String getFetchType(Property property) {
        Value value = property.getValue();
        String fetchType = importType("javax.persistence.FetchType");
        boolean lazy = false;
        if (value instanceof ToOne || value instanceof Collection) {
            lazy = value.getFetchMode().equals(FetchMode.SELECT);
        } else {
            //we're not collection neither *toone so we are looking for property fetching
            lazy = property.isLazy();
        }
        if (lazy) {
            return fetchType + "." + "LAZY";
        } else {
            return fetchType + "." + "EAGER";
        }
    }

    public String getJavaTypeName(Property property, boolean useGenerics, Configuration configuration) {
        String javaTypeName = super.getJavaTypeName(property, useGenerics);
        return getValueOfInverseMeta(property, configuration, javaTypeName, "inverse-type", "Set");
    }

    public String getFieldInitialization(Property property, boolean useGenerics, Configuration configuration) {
        String fieldInitialization = super.getFieldInitialization(property, useGenerics);
        return getValueOfInverseMeta(property,
                                     configuration,
                                     fieldInitialization,
                                     "inverse-default-value-type",
                                     "HashSet");
    }

    private String getValueOfInverseMeta(Property property,
                                         Configuration configuration,
                                         String defaultValue,
                                         final String attributeName,
                                         String oldValue) {
        Value value = property.getValue();
        if (value != null && value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.isOneToMany()) {
                String overrideTypeName =
                    callFunctionWithInverseProperty(configuration, collection, new Function<Property, String>() {
                        @Override
                        public String apply(Property manyProperty) {
                            MetaAttribute metaAttribute = manyProperty.getMetaAttribute(attributeName);
                            if (metaAttribute != null) {
                                return metaAttribute.getValue();
                            }

                            return null;
                        }
                    });

                if (overrideTypeName != null) {
                    return defaultValue.replace(oldValue, overrideTypeName);
                }
            }
        }
        return defaultValue;
    }

    public String getGenericClassName(Property property) {
        Value value = property.getValue();

        if (value != null && value instanceof Collection) {
            Collection collection = (Collection) value;
            return ((OneToMany) collection.getElement()).getAssociatedClass().getJpaEntityName();
        }

        return null;
    }
}
