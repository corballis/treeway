package ie.corballis.treeway.generate.overrides;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringRuntimeInfo;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;

import java.util.List;

import static java.beans.Introspector.decapitalize;
import static org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil.simplePluralize;
import static org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil.toUpperCamelCase;

public class TreewayReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

    private ReverseEngineeringRuntimeInfo runtimeInfo;

    public TreewayReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
        super(delegate);
    }

    public void configure(ReverseEngineeringRuntimeInfo rti) {
        this.runtimeInfo = rti;
    }

    @Override
    public String foreignKeyToEntityName(String keyname,
                                         TableIdentifier fromTable,
                                         List fromColumnNames,
                                         TableIdentifier referencedTable,
                                         List referencedColumnNames,
                                         boolean uniqueReference) {

        // for now we assume there will only be simple foreign keys with only one column
        String name = ((Column) fromColumnNames.get(0)).getName();

        if (name.endsWith("_id")) {
            name = name.substring(0, name.length() - 3);
        }

        return columnToPropertyName(null, name);
    }

    @Override
    public String foreignKeyToCollectionName(String keyname,
                                             TableIdentifier fromTable,
                                             List fromColumns,
                                             TableIdentifier referencedTable,
                                             List referencedColumns,
                                             boolean uniqueReference) {
        if (uniqueReference) {
            return super.foreignKeyToCollectionName(keyname,
                                                    fromTable,
                                                    fromColumns,
                                                    referencedTable,
                                                    referencedColumns,
                                                    uniqueReference);
        } else {
            // for now we assume there will only be simple foreign keys with only one column
            String foreignKeyName = ((Column) fromColumns.get(0)).getName();
            if (foreignKeyName.endsWith("_id")) {
                foreignKeyName = foreignKeyName.substring(0, foreignKeyName.length() - 3);
            }

            String tableName = simplePluralize(decapitalize(toUpperCamelCase(fromTable.getName())));
            String columnName = toUpperCamelCase(columnToPropertyName(null, foreignKeyName));

            return tableName + "For" + columnName;
        }
    }

    @Override
    public boolean isForeignKeyCollectionInverse(String name,
                                                 TableIdentifier foreignKeyTableIdentifier,
                                                 List columns,
                                                 TableIdentifier foreignKeyReferencedTable,
                                                 List referencedColumns) {

        Table foreignKeyTable = runtimeInfo.getTable(foreignKeyTableIdentifier);
        if (foreignKeyTable == null) {
            return true; // we don't know better
        }

        if (isManyToManyTable(foreignKeyTable)) {
            // if the reference column is the first one then we are inverse.
            Column column = foreignKeyTable.getColumn(0);
            Column fkColumn = (Column) columns.get(0);
            return fkColumn.equals(column);
        }

        return true;
    }
}
