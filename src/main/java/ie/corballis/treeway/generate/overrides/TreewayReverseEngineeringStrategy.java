package ie.corballis.treeway.generate.overrides;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

import java.util.List;

public class TreewayReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

    public TreewayReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
        super(delegate);
    }

    @Override
    public String foreignKeyToEntityName(String keyname,
                                         TableIdentifier fromTable,
                                         List fromColumnNames,
                                         TableIdentifier referencedTable,
                                         List referencedColumnNames,
                                         boolean uniqueReference) {
        String name = super.foreignKeyToEntityName(keyname,
                                                   fromTable,
                                                   fromColumnNames,
                                                   referencedTable,
                                                   referencedColumnNames,
                                                   uniqueReference);

        if (!uniqueReference && name.endsWith("Id")) {
            name = name.substring(0, name.length() - 2);
        }

        return name;
    }
}
