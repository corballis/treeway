package ie.corballis.treeway.generate;

import ie.corballis.treeway.AbstractHibernateMojo;
import ie.corballis.treeway.CopyMigrationsMojo;
import ie.corballis.treeway.generate.overrides.CustomGenericExporter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.GenericExporter;

import java.io.File;
import java.util.Properties;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GeneratorMojo extends AbstractHibernateMojo {

    @Parameter(property = "targetPath", defaultValue = CopyMigrationsMojo.DEFAULT_TARGET_PATH)
    private String targetPath = CopyMigrationsMojo.DEFAULT_TARGET_PATH;

    @Parameter(property = "templateName", defaultValue = "templates/Pojo.ftl")
    private String templateName = "templates/Pojo.ftl";

    @Parameter(property = "templatePaths")
    private String[] templatePaths;

    @Parameter(property = "filePattern", defaultValue = "{package-name}/{class-name}.java")
    private String filePattern = "{package-name}/{class-name}.java";

    @Override
    protected void doExecute(Configuration configuration) {
        GenericExporter genericExporter = new CustomGenericExporter(configuration, new File(targetPath));
        genericExporter.setTemplateName(templateName);
        if (templatePaths != null) {
            genericExporter.setTemplatePath(templatePaths);
        }
        genericExporter.setFilePattern(filePattern);
        Properties properties = new Properties();
        properties.setProperty("ejb3", "true");
        properties.setProperty("jdk5", "true");
        genericExporter.setProperties(properties);
        genericExporter.start();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setRevengFile(String revengFile) {
        this.revengFile = revengFile;
    }

    public void setPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setTemplatePaths(String[] templatePath) {
        this.templatePaths = templatePath;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }
}
