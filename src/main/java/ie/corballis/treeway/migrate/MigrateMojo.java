package ie.corballis.treeway.migrate;

import ie.corballis.treeway.CopyMigrationsMojo;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.util.Location;

import java.io.File;
import java.util.Properties;

@Mojo(name = "migrate", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class MigrateMojo extends AbstractMojo {

    @Parameter(property = "driver", required = true)
    private String driver;

    @Parameter(property = "url", required = true)
    private String url;

    @Parameter(property = "user", required = true)
    private String user;

    @Parameter(property = "password", required = true)
    private String password;

    @Parameter(property = "targetPath", defaultValue = CopyMigrationsMojo.DEFAULT_TARGET_PATH)
    private String targetPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Flyway flyway = initFlyway();
        flyway.clean();
        flyway.migrate();
    }

    private Flyway initFlyway() {
        Flyway flyway = new Flyway();
        Properties properties = new Properties();
        properties.setProperty("flyway.driver", driver);
        properties.setProperty("flyway.url", url);
        properties.setProperty("flyway.user", user);
        properties.setProperty("flyway.password", password);
        flyway.configure(properties);

        File targetDirectory = new File(targetPath);
        flyway.setLocations(Location.FILESYSTEM_PREFIX + targetDirectory.getAbsolutePath());

        return flyway;
    }
}
