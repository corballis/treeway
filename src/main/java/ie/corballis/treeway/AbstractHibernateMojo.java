package ie.corballis.treeway;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.ant.JDBCConfigurationTask;

import java.io.File;

public abstract class AbstractHibernateMojo extends AbstractTreewayMojo {

    public AbstractHibernateMojo() {
    }

    public AbstractHibernateMojo(AbstractTreewayMojo mojo) {
        super(mojo);
    }

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
