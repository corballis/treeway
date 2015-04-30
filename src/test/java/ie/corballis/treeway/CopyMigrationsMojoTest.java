package ie.corballis.treeway;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.fest.assertions.api.Fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

public class CopyMigrationsMojoTest {

    public static final String RESOURCE_PATH = "src/test/resources/treeway";
    public static final String TARGET_PATH = "src/test/resources/result";
    private CopyMigrationsMojo copyMigrationsMojo;

    @Before
    public void setUp() {
        copyMigrationsMojo = new CopyMigrationsMojo();
        copyMigrationsMojo.setResourcePath(RESOURCE_PATH);
        copyMigrationsMojo.setTargetPath(TARGET_PATH);
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(TARGET_PATH));
    }

    @Test
    public void verifyNonExistingMigrationVersion() throws MojoExecutionException {
        try {
            copyMigrationsMojo.setMigrationVersion("NE1");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("There are no migrations with version: NE1");
        }
    }

    @Test
    public void verifyAmbiguousVersion() throws MojoExecutionException {
        try {
            copyMigrationsMojo.setMigrationVersion("A1");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Migration is ambiguous with version: A1");
        }

        try {
            copyMigrationsMojo.setMigrationVersion("A2");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Migration is ambiguous with version: A1");
        }

        try {
            copyMigrationsMojo.setMigrationVersion("A3");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Migration is ambiguous with version: A1");
        }
    }

    @Test
    public void verifyCircularReferences() throws MojoExecutionException {
        try {
            copyMigrationsMojo.setMigrationVersion("C1");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Version: C1 refers circularly");
        }

        try {
            copyMigrationsMojo.setMigrationVersion("C2");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Version: C1 refers circularly");
        }

        try {
            copyMigrationsMojo.setMigrationVersion("C3");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Version: C4 refers circularly");
        }

        try {
            copyMigrationsMojo.setMigrationVersion("C4");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Version: C3 refers circularly");
        }
    }

    @Test
    public void verifyCopyMigrations() throws MojoFailureException, MojoExecutionException {
        copyMigrationsMojo.setMigrationVersion("B1");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql");

        copyMigrationsMojo.setMigrationVersion("CO11");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql", "V2015.04.30.08.42__migration_1.sql");

        copyMigrationsMojo.setMigrationVersion("CO12");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.30.08.42__migration_1.sql",
                         "V2015.04.30.08.43__migration_2.sql");

        copyMigrationsMojo.setMigrationVersion("SC11");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.30.08.42__migration_1.sql",
                         "V2015.04.30.08.43__migration_2.sql",
                         "V2015.04.30.08.44__migration_3.sql");

        copyMigrationsMojo.setMigrationVersion("SC21");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.30.08.42__migration_1.sql",
                         "V2015.04.30.08.48__migration_4.sql");
    }

    private void verifyMigrations(String... expectedMigrations) {
        File targetDirectory = new File(TARGET_PATH);

        File[] actualMigrations = targetDirectory.listFiles();

        assertThat(actualMigrations.length).isEqualTo(expectedMigrations.length);

        for (int i = 0; i < actualMigrations.length; i++) {
            File actualMigration = actualMigrations[i];
            assertThat(actualMigration.getName()).isEqualTo(expectedMigrations[i]);
        }
    }
}
