package ie.corballis.treeway;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractTreewayMojo extends AbstractMojo {

    @Parameter(property = "resourcePath", defaultValue = "${basedir}/src/main/resources/treeway")
    protected String resourcePath = "${basedir}/src/main/resources/treeway";

    @Parameter(property = "targetPath", defaultValue = "${basedir}/src/main/generated")
    protected String targetPath = "${basedir}/src/main/generated";

    @Parameter(property = "migrationVersion", required = true)
    protected String migrationVersion;

    @Parameter(property = "driver", required = true)
    protected String driver;

    @Parameter(property = "url", required = true)
    protected String url;

    @Parameter(property = "user", required = true)
    protected String user;

    @Parameter(property = "password", required = true)
    protected String password;

    @Parameter(property = "packageName", defaultValue = "com.corballis")
    protected String packageName = "com.corballis";

    @Parameter(property = "revengFile", defaultValue = "${basedir}/src/main/resources/reveng.xml")
    protected String revengFile = "${basedir}/src/main/resources/reveng.xml";

    @Parameter(property = "propertyFile", defaultValue = "${basedir}/src/main/resources/hibernate.properties")
    protected String propertyFile = "${basedir}/src/main/resources/hibernate.properties";

    @Parameter(property = "templateName", defaultValue = "templates/Pojo.ftl")
    protected String templateName = "templates/Pojo.ftl";

    @Parameter(property = "templatePaths")
    protected String[] templatePaths;

    @Parameter(property = "filePattern", defaultValue = "{package-name}/{class-name}.java")
    protected String filePattern = "{package-name}/{class-name}.java";

    public AbstractTreewayMojo() {
    }

    public AbstractTreewayMojo(AbstractTreewayMojo mojo) {
        this.resourcePath = mojo.resourcePath;
        this.targetPath = mojo.targetPath;
        this.migrationVersion = mojo.migrationVersion;
        this.driver = mojo.driver;
        this.url = mojo.url;
        this.user = mojo.user;
        this.password = mojo.password;
        this.packageName = mojo.packageName;
        this.revengFile = mojo.revengFile;
        this.propertyFile = mojo.propertyFile;
        this.templateName = mojo.templateName;
        this.templatePaths = mojo.templatePaths;
        this.filePattern = mojo.filePattern;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setMigrationVersion(String migrationVersion) {
        this.migrationVersion = migrationVersion;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setTemplatePaths(String[] templatePaths) {
        this.templatePaths = templatePaths;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }
}
