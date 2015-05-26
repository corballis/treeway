package ie.corballis.treeway;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.ant.JDBCConfigurationTask;

import java.io.File;
import java.util.Arrays;

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
        String revengFiles = getRevengFiles();
        configurationTask.setRevEngFile(new Path(project, revengFiles));
        configurationTask.setReverseStrategy("ie.corballis.treeway.generate.overrides.TreewayReverseEngineeringStrategy");
        configurationTask.setPropertyFile(new File(propertyFile));
        doExecute(configurationTask.getConfiguration());
    }

    private String getRevengFiles() {
        FileSetManager fileSetManager = new FileSetManager();
        String[] includedFiles = fileSetManager.getIncludedFiles(revengFile);
        return Joiner.on(";").join(Lists.transform(Arrays.asList(includedFiles), new Function<String, String>() {
            @Override
            public String apply(String revengXml) {
                return revengFile.getDirectory() + revengXml;
            }
        }));
    }

    protected abstract void doExecute(Configuration configuration) throws MojoExecutionException;
}
