package ie.corballis.treeway.generate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.twitter.elephantbird.util.Strings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TemplateUtil {

    public String simplePluralize(String singular) {
        return ReverseEngineeringStrategyUtil.simplePluralize(singular);
    }

    public String singularize(String plural) {
        return Strings.singularize(plural);
    }

    public boolean idRequired(POJOClass pojoClass) {
        return pojoClass.getExtends().isEmpty();
    }

    public String getGetterName(String declarationName, String propertyName) {
        return "get" + simplePluralize(declarationName) + cutPropertyNameLeading(propertyName, "By", "For");

    }

    public String getSetterName(String declarationName, String singularizedPropertyName) {
        return "set" + declarationName + cutPropertyNameLeading(singularizedPropertyName, "For", "By");
    }

    private String cutPropertyNameLeading(String propertyName, String separator, String replaceSeparator) {
        Iterator<String> propertyNameElements = Lists.newArrayList(propertyName.split("(?=\\p{Upper})")).iterator();

        while(propertyNameElements.hasNext()) {
            String element = propertyNameElements.next();
            propertyNameElements.remove();
            if (element.equals(separator)) {
                break;
            }
        }

        ArrayList<String> elementList = Lists.newArrayList(propertyNameElements);

        if (!elementList.isEmpty()) {
            return replaceSeparator + Joiner.on("").join(elementList);
        }

        return "";
    }

    public static String getDeclarationName(String name) {
        return name.replaceAll("^(.*)Extends(.*)", "$1");
    }

    public static String getExtends(String name) {
        if (hasParent(name)) {
            return name.replaceAll("^(.*)Extends(.*)", "$2");
        }

        return "";
    }

    public static boolean hasParent(String name) {
        return name.contains("Extends");
    }

}
