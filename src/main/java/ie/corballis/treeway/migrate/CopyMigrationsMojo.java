package ie.corballis.treeway.migrate;

import ie.corballis.treeway.AbstractTreewayMojo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Mojo(name = "copy-migrations", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class CopyMigrationsMojo extends AbstractTreewayMojo {

    public CopyMigrationsMojo() {
    }

    public CopyMigrationsMojo(AbstractTreewayMojo mojo) {
        super(mojo);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (migrationVersion == null) {
            throw new MojoFailureException("Migration version has not specified");
        }

        try {
            getLog().debug("Treeway resource path: " + resourcePath);
            getLog().debug("Treeway migration target path: " + migrationTargetPath);
            getLog().debug("Treeway migration version: " + migrationVersion);

            cleanupMigrationTargetFolder();

            MigrationCollector migrationCollector = new MigrationCollector(migrationVersion, resourcePath);

            List<File> migrations = migrationCollector.collect();

            copyMigrations(migrations);
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void cleanupMigrationTargetFolder() throws IOException {
        FileUtils.deleteDirectory(new File(migrationTargetPath));
        getLog().info("Migration target folder: " + migrationTargetPath + " has been deleted");
    }

    private void copyMigrations(List<File> migrations) throws IOException {
        for (File migration : migrations) {
            FileUtils.copyFile(migration, new File(migrationTargetPath + "/" + replaceMigrationVersion(migration)));
            getLog().info("Migration has been copied: " + migration.getName());
        }
    }

    private String replaceMigrationVersion(File migration) {
        return migration.getName()
                        .replaceAll("^.*([0-9][0-9][0-9][0-9]\\.[0-1][0-9]\\.[0-3][0-9]\\.[0-2][0-9]\\.[0-5][0-9].*)",
                                    "V$1");
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public void setMigrationTargetPath(String targetPath) {
        this.migrationTargetPath = targetPath;
    }

    public void setMigrationVersion(String migrationVersion) {
        this.migrationVersion = migrationVersion;
    }
}
