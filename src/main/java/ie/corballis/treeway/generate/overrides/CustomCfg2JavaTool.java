package ie.corballis.treeway.generate.overrides;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

public class CustomCfg2JavaTool extends Cfg2JavaTool {
    @Override
    public POJOClass getPOJOClass(PersistentClass comp) {
        return new CustomEntityPOJOClass(comp, this);
    }
}
