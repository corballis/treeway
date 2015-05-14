package ie.corballis.treeway.generate;

import ie.corballis.treeway.AbstractHibernateMojo;
import ie.corballis.treeway.AbstractTreewayMojo;
import ie.corballis.treeway.generate.overrides.CustomGenericExporter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.GenericExporter;

import java.io.File;
import java.util.Properties;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GeneratorMojo extends AbstractHibernateMojo {

    public GeneratorMojo() {
    }

    public GeneratorMojo(AbstractTreewayMojo mojo) {
        super(mojo);
    }

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

    public static void main(String[] args) throws MojoFailureException, MojoExecutionException {
        GeneratorMojo generatorMojo = new GeneratorMojo();
        generatorMojo.migrationVersion = "V1";
        generatorMojo.driver = "org.postgresql.Driver";
        generatorMojo.url = "jdbc:postgresql://localhost:5432/sms";
        generatorMojo.user = "sms";
        generatorMojo.password = "sms";
        generatorMojo.revengFile = "src/main/resources/reveng.xml";
        generatorMojo.propertyFile = "src/main/resources/hibernate.properties";
        generatorMojo.resourcePath = "src/main/resources/treeway";
        generatorMojo.targetPath = "src/main/generated";
        generatorMojo.execute();
    }
}
