package ie.corballis.treeway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.util.Location;

import java.io.File;
import java.util.Properties;

public abstract class AbstractFlywayMojo extends AbstractTreewayMojo {

    public AbstractFlywayMojo() {
    }

    public AbstractFlywayMojo(AbstractTreewayMojo mojo) {
        super(mojo);
    }

    protected Flyway initFlyway() {
        Flyway flyway = new Flyway();
        Properties properties = new Properties();
        properties.setProperty("flyway.driver", driver);
        properties.setProperty("flyway.url", url);
        properties.setProperty("flyway.user", user);
        properties.setProperty("flyway.password", password);
        flyway.configure(properties);

        File targetDirectory = new File(migrationTargetPath);
        flyway.setLocations(Location.FILESYSTEM_PREFIX + targetDirectory.getAbsolutePath());

        return flyway;
    }
}
