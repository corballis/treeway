package ie.corballis.treeway;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.ant.JDBCConfigurationTask;

import java.io.File;

public abstract class AbstractHibernateMojo extends AbstractMojo {

    @Parameter(property = "packageName", defaultValue = "com.corballis")
    protected String packageName = "com.corballis";

    @Parameter(property = "revengFile", defaultValue = "src/main/resources/reveng.xml")
    protected String revengFile = "src/main/resources/reveng.xml";

    @Parameter(property = "revengFile", defaultValue = "src/main/resources/hibernate.properties")
    protected String propertyFile = "src/main/resources/hibernate.properties";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        JDBCConfigurationTask configurationTask = new JDBCConfigurationTask();
        configurationTask.setPackageName(packageName);
        Project project = new Project();
        configurationTask.setRevEngFile(new Path(project, revengFile));
        configurationTask.setPropertyFile(new File(propertyFile));
        doExecute(configurationTask.getConfiguration());
    }

    protected abstract void doExecute(Configuration configuration) throws MojoExecutionException;
}
