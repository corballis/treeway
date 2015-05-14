package ie.corballis.treeway.migrate;

import ie.corballis.treeway.AbstractFlywayMojo;
import ie.corballis.treeway.AbstractTreewayMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.flywaydb.core.Flyway;

@Mojo(name = "migrate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class MigrateMojo extends AbstractFlywayMojo {

    public MigrateMojo() {
    }

    public MigrateMojo(AbstractTreewayMojo mojo) {
        super(mojo);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Flyway flyway = initFlyway();
        flyway.migrate();
    }

}
