package ie.corballis.treeway.clean;

import ie.corballis.treeway.AbstractFlywayMojo;
import ie.corballis.treeway.AbstractTreewayMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.flywaydb.core.Flyway;

@Mojo(name = "clean-db", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class DbCleanerMojo extends AbstractFlywayMojo {

    public DbCleanerMojo() {
    }

    public DbCleanerMojo(AbstractTreewayMojo mojo) {
        super(mojo);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Flyway flyway = initFlyway();
        flyway.clean();
    }
}
