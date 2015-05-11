package ie.corballis.treeway.generate.overrides;

import com.google.common.base.Joiner;
import ie.corballis.treeway.generate.TemplateUtil;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.pojo.ImportContext;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import java.util.Iterator;

public class CustomCfg2JavaTool extends Cfg2JavaTool {
    @Override
    public POJOClass getPOJOClass(PersistentClass comp) {
        return new CustomEntityPOJOClass(comp, this);
    }

    @Override
    public String asParameterList(Iterator fields, boolean useGenerics, ImportContext ic) {
        String parameters = super.asParameterList(fields, useGenerics, ic);
        String[] parameterArray = parameters.split(",");
        for (int i = 0; i < parameterArray.length; i++) {
            String[] parameter = parameterArray[i].trim().split(" ");
            parameterArray[i] = TemplateUtil.getDeclarationName(parameter[0]) + " " + parameter[1];
        }
        return Joiner.on(", ").join(parameterArray);
    }
}
