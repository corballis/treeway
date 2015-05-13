package ie.corballis.treeway;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.util.Location;

import java.io.File;
import java.util.Properties;

public abstract class AbstractFlywayMojo extends AbstractMojo {

    @Parameter(property = "driver", required = true)
    protected String driver;

    @Parameter(property = "url", required = true)
    protected String url;

    @Parameter(property = "user", required = true)
    protected String user;

    @Parameter(property = "password", required = true)
    protected String password;

    @Parameter(property = "targetPath", defaultValue = CopyMigrationsMojo.DEFAULT_TARGET_PATH)
    protected String targetPath;

    protected Flyway initFlyway() {
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
