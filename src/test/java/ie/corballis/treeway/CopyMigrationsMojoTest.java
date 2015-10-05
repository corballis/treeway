package ie.corballis.treeway;

import ie.corballis.treeway.migrate.CopyMigrationsMojo;
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
        copyMigrationsMojo.setMigrationTargetPath(TARGET_PATH);
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(TARGET_PATH));
    }

    @Test
    public void verifyNullMigrationVersion() throws MojoExecutionException {
        try {
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Migration version has not specified");
        }
    }

    @Test
    public void verifyNonExistingMigrationVersion() throws MojoExecutionException {
        try {
            copyMigrationsMojo.setMigrationVersion("NE1");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("Could not find folder for version NE1");
        }
    }

    @Test
    public void verifyDuplicateVersionFolder() throws MojoExecutionException {
        try {
            copyMigrationsMojo.setMigrationVersion("D1");
            copyMigrationsMojo.execute();
            Fail.failBecauseExceptionWasNotThrown(MojoFailureException.class);
        } catch (MojoFailureException e) {
            assertThat(e).hasMessage("More than one folder was found for the specified version: [src\\test\\resources\\treeway\\D1, src\\test\\resources\\treeway\\D1_company1_A1]");
        }
    }

    @Test
    public void verifyBaseOnly() throws MojoFailureException, MojoExecutionException {
        copyMigrationsMojo.setMigrationVersion("base");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.31.08.40__second_base.sql",
                         "V2015.05.18.08.40__third_base.sql");
    }

    @Test
    public void verifySimpleVersionWithoutParent() throws MojoFailureException, MojoExecutionException {
        copyMigrationsMojo.setMigrationVersion("company1");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.30.11.40__c_first.sql",
                         "V2015.04.31.08.40__second_base.sql",
                         "V2015.05.18.08.40__third_base.sql",
                         "V2015.06.01.08.40__c_second.sql");
    }

    @Test
    public void verifyOverwriting() throws MojoFailureException, MojoExecutionException {
        copyMigrationsMojo.setMigrationVersion("company3");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.30.11.40__c_first.sql",
                         "V2015.04.31.08.40__overwritten_b2.sql",
                         "V2015.05.18.08.40__third_base.sql",
                         "V2015.06.01.08.40__c_second.sql");
    }

    @Test
    public void verifyInheritance() throws MojoFailureException, MojoExecutionException {
        copyMigrationsMojo.setMigrationVersion("comp2");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.30.11.40__c_first.sql",
                         "V2015.04.31.08.40__second_base.sql",
                         "V2015.05.18.08.40__third_base.sql",
                         "V2015.06.01.08.40__d_first.sql");
    }

    @Test
    public void verifyOverwriting2() throws MojoFailureException, MojoExecutionException {
        copyMigrationsMojo.setMigrationVersion("company5");
        copyMigrationsMojo.execute();

        verifyMigrations("V2015.04.30.08.40__base_migration.sql",
                         "V2015.04.30.11.40__c_first.sql",
                         "V2015.04.31.08.40__overwritten_b2.sql",
                         "V2015.05.18.08.40__third_base.sql",
                         "V2015.06.01.08.40__d_first.sql");
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
