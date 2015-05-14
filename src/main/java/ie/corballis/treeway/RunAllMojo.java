package ie.corballis.treeway;

import ie.corballis.treeway.clean.DbCleanerMojo;
import ie.corballis.treeway.clean.ForeignKeyCleanerMojo;
import ie.corballis.treeway.generate.GeneratorMojo;
import ie.corballis.treeway.migrate.CopyMigrationsMojo;
import ie.corballis.treeway.migrate.MigrateMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "all", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class RunAllMojo extends AbstractTreewayMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        new DbCleanerMojo(this).execute();
        new CopyMigrationsMojo(this).execute();
        new MigrateMojo(this).execute();
        new GeneratorMojo(this).execute();
        new ForeignKeyCleanerMojo(this).execute();
    }
}
