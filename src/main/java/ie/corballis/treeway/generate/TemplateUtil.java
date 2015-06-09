package ie.corballis.treeway.generate;

import com.twitter.elephantbird.util.Strings;
import ie.corballis.treeway.generate.overrides.CustomEntityPOJOClass;
import org.apache.commons.lang.WordUtils;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.hibernate.mapping.*;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;

import java.util.ArrayList;
import java.util.Iterator;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.collect.Lists.newArrayList;
import static org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil.toUpperCamelCase;

public class TemplateUtil {

    public String simplePluralize(String singular) {
        return ReverseEngineeringStrategyUtil.simplePluralize(singular);
    }

    public String singularize(String plural) {
        return Strings.singularize(plural);
    }

    public String getGetterName(CustomEntityPOJOClass pojo, Property property) {
        return "get" + getAccessorName(pojo, property);
    }

    private String getAccessorName(CustomEntityPOJOClass pojo, Property property) {
        String getterName = simplePluralize(pojo.getDeclarationName());

        if (!isUniqueReference(property)) {
            getterName += "For" + toUpperCamelCase(property.getName());
        }

        return getterName;
    }

    private boolean isUniqueReference(Property property) {
        if (property.getValue() instanceof ToOne) {
            ToOne toOne = (ToOne) property.getValue();
            String referencedEntityName = toOne.getReferencedEntityName();
            Column propertyColumn = (Column) property.getColumnIterator().next();

            Iterator foreignKeyIterator = property.getValue().getTable().getForeignKeyIterator();
            while (foreignKeyIterator.hasNext()) {
                ForeignKey element = (ForeignKey) foreignKeyIterator.next();
                if (referencedEntityName.equals(element.getReferencedEntityName()) &&
                    !propertyColumn.equals(element.getColumn(0))) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getSetterName(CustomEntityPOJOClass pojo, Property property) {
        String setterName = "set";

        if (property.getValue() instanceof Set) {
            Column column = (Column) ((Set) property.getValue()).getKey().getColumnIterator().next();
            setterName += toUpperCamelCase(column.getName());
        } else {
            setterName += pojo.getDeclarationName();

            if (!isUniqueReference(property)) {
                setterName += "For" + toUpperCamelCase(property.getName());
            }
        }

        return setterName;
    }

    public String getOneToManySetter(Property property) {
        Column column = (Column) ((Set) property.getValue()).getKey().getColumnIterator().next();
        String name = column.getName();

        if (name.endsWith("_id")) {
            name = name.substring(0, name.length() - 3);
        }
        return "set" + toUpperCamelCase(name);
    }

    public String collectionTableName(Property property) {
        return LOWER_UNDERSCORE.to(LOWER_CAMEL, ((Set) property.getValue()).getCollectionTable().getName());
    }

    public static String[] splitByUpperCase(String propertyName) {
        return propertyName.split("(?=\\p{Upper})");
    }

    public static String getPropertyName(Property property, Cfg2HbmTool cfg2HbmTool) {
        return cfg2HbmTool.isCollection(property) ? ReverseEngineeringStrategyUtil.simplePluralize(property.getName()) : property
                                                                                                                             .getName();
    }

    @SuppressWarnings("unchecked")
    public boolean isOtherSideGenerated(Property property, Configuration configuration) {
        ArrayList<Column> propertyColumns = newArrayList(property.getColumnIterator());

        Iterator collectionMappings = configuration.getCollectionMappings();
        while (collectionMappings.hasNext()) {
            Set next = (Set) collectionMappings.next();
            ArrayList<Selectable> collectionColumns = newArrayList(next.getKey().getColumnIterator());
            // this will only work for simple foreign keys for now
            if (propertyColumns.equals(collectionColumns) && propertyColumns.get(0)
                                                                            .getValue()
                                                                            .getTable()
                                                                            .equals(((Column) collectionColumns.get(0)).getValue()
                                                                                                                       .getTable())) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean isOtherSideGeneratedForCollection(Property property, Configuration configuration) {
        Set propertySet = (Set) property.getValue();
        Value propertyElement = propertySet.getElement();

        if (propertyElement instanceof OneToMany) {
            return isOneToManyAndHasReferenceOnTheOtherSide(property);
        } else if (propertyElement instanceof ManyToOne) {
            Iterator collectionMappings = configuration.getCollectionMappings();

            while (collectionMappings.hasNext()) {
                Set currentCollectionSet = (Set) collectionMappings.next();
                Selectable firstPropertyKeyColumn = propertySet.getKey().getColumnIterator().next();
                Selectable firstPropertyElementColumn = propertyElement.getColumnIterator().next();
                Selectable firstCollectionElementColumn = currentCollectionSet.getElement().getColumnIterator().next();
                Selectable firstCollectionKeyColumn = currentCollectionSet.getKey().getColumnIterator().next();

                boolean referToTheSameTable =
                    currentCollectionSet.getCollectionTable().equals(propertySet.getCollectionTable());
                boolean isInverse = firstPropertyKeyColumn.equals(firstCollectionElementColumn) &&
                                    firstPropertyElementColumn.equals(firstCollectionKeyColumn);

                if (referToTheSameTable && isInverse) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean isOneToManyAndHasReferenceOnTheOtherSide(Property property) {
        Set propertySet = (Set) property.getValue();
        Value propertyElement = propertySet.getElement();

        if (propertyElement instanceof OneToMany) {
            Column foreignColumn = (Column) ((Set) property.getValue()).getKey().getColumnIterator().next();
            String name = foreignColumn.getName();
            if (name.endsWith("_id")) {
                name = name.substring(0, name.length() - 3);
                name = WordUtils.uncapitalize(toUpperCamelCase(name));

                PersistentClass associatedClass = ((OneToMany) propertyElement).getAssociatedClass();
                if (!persistentClassHasProperty(associatedClass, name)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private boolean persistentClassHasProperty(PersistentClass persistentClass, String propertyName) {
        try {
            persistentClass.getProperty(propertyName);
        } catch (MappingException e) {
            return false;
        }

        return true;
    }

}
