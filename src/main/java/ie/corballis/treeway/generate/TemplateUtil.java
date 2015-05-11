package ie.corballis.treeway.generate;

import com.twitter.elephantbird.util.Strings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategyUtil;
import org.hibernate.tool.hbm2x.pojo.POJOClass;

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
