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
            getLog().debug("Treeway target path: " + targetPath);
            getLog().debug("Treeway migration version: " + migrationVersion);

            cleanupTargetFolder();

            MigrationCollector migrationCollector = new MigrationCollector(migrationVersion, resourcePath);

            List<File> migrations = migrationCollector.collect();

            copyMigrations(migrations);
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void cleanupTargetFolder() throws IOException {
        FileUtils.deleteDirectory(new File(targetPath));
        getLog().info("Target folder: " + targetPath + " has been deleted");
    }

    private void copyMigrations(List<File> migrations) throws IOException {
        for (File migration : migrations) {
            FileUtils.copyFile(migration, new File(targetPath + "/" + replaceMigrationVersion(migration)));
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

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setMigrationVersion(String migrationVersion) {
        this.migrationVersion = migrationVersion;
    }
}
