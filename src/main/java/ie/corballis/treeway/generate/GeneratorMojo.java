package ie.corballis.treeway.generate;

import ie.corballis.treeway.CopyMigrationsMojo;
import ie.corballis.treeway.generate.overrides.CustomGenericExporter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.hibernate.tool.ant.JDBCConfigurationTask;
import org.hibernate.tool.hbm2x.GenericExporter;

import java.io.File;
import java.util.Properties;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GeneratorMojo extends AbstractMojo {

    @Parameter(property = "packageName", defaultValue = "com.corballis")
    private String packageName = "com.corballis";

    @Parameter(property = "revengFile", defaultValue = "src/main/resources/reveng.xml")
    private String revengFile = "src/main/resources/reveng.xml";

    @Parameter(property = "revengFile", defaultValue = "src/main/resources/hibernate.properties")
    private String propertyFile = "src/main/resources/hibernate.properties";

    @Parameter(property = "targetPath", defaultValue = CopyMigrationsMojo.DEFAULT_TARGET_PATH)
    private String targetPath = CopyMigrationsMojo.DEFAULT_TARGET_PATH;

    @Parameter(property = "templateName", defaultValue = "templates/Pojo.ftl")
    private String templateName = "templates/Pojo.ftl";

    @Parameter(property = "templatePaths")
    private String[] templatePaths;

    @Parameter(property = "filePattern", defaultValue = "{package-name}/{class-name}.java")
    private String filePattern = "{package-name}/{class-name}.java";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        JDBCConfigurationTask configurationTask = new JDBCConfigurationTask();
        configurationTask.setPackageName(packageName);
        Project project = new Project();
        configurationTask.setRevEngFile(new Path(project, revengFile));
        configurationTask.setPropertyFile(new File(propertyFile));

        GenericExporter genericExporter = new CustomGenericExporter(configurationTask.getConfiguration(), new File(targetPath));
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
