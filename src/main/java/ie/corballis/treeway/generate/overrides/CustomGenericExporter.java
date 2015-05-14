package ie.corballis.treeway.generate.overrides;

import ie.corballis.treeway.generate.TemplateUtil;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomGenericExporter extends GenericExporter {

    private Cfg2JavaTool cfg2JavaTool;

    public CustomGenericExporter() {
    }

    public CustomGenericExporter(Configuration cfg, File outputdir) {
        super(cfg, outputdir);
        cfg2JavaTool = new CustomCfg2JavaTool();
    }

    @Override
    protected void exportPOJO(Map additionalContext, POJOClass element) {
        updateProperties(element.getAllPropertiesIterator(), element);
        additionalContext.put("templateUtil", new TemplateUtil());
        super.exportPOJO(additionalContext, element);
    }

    private void updateProperties(Iterator propertiesIterator, POJOClass element) {
        while (propertiesIterator.hasNext()) {
            Property property = (Property) propertiesIterator.next();
            if ("id".equals(property.getName()) && !element.getExtends().isEmpty()) {
                Map metaAttributes = property.getMetaAttributes();
                if (metaAttributes == null) {
                    property.setMetaAttributes(new HashMap());
                }
                MetaAttribute metaAttribute = new MetaAttribute("not generate inheritor id");
                metaAttribute.addValue("false");
                property.getMetaAttributes().put("gen-property", metaAttribute);
            }
            boolean hasParent = TemplateUtil.hasParent(property.getName());
            property.setName(TemplateUtil.getDeclarationName(property.getName()));

            if (hasParent) {
                property.setName(TemplateUtil.getPropertyName(property, getCfg2HbmTool()));
            }
        }
    }

    @Override
    public Cfg2JavaTool getCfg2JavaTool() {
        return cfg2JavaTool;
    }
}
