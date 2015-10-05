package ie.corballis.treeway.migrate;

import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class MigrationCollector {

    public static final String BASE_FOLDER = "base";

    private File directory;
    private String version;

    public MigrationCollector(String version, String resourcePath) {
        this.version = version;
        this.directory = new File(resourcePath);
    }

    public List<File> collect() throws MojoFailureException {
        List<File> files = newArrayList();
        Set<String> versionsSeenSoFar = newHashSet();

        if (directory.exists()) {
            traverseVersionFolder(version, files, versionsSeenSoFar, null);
            traverseVersionFolder(BASE_FOLDER, files, versionsSeenSoFar, null);
        }

        return newArrayList(files);
    }

    private void traverseVersionFolder(String version,
                                       List<File> files,
                                       Set<String> versionsSeenSoFar,
                                       String lastVersion) throws MojoFailureException {

        File versionFolder = findVersionFolder(version);
        processMigrationFiles(files, versionsSeenSoFar, versionFolder, lastVersion);
        continueWithParent(files, versionsSeenSoFar, versionFolder);
    }

    private File findVersionFolder(final String version) throws MojoFailureException {
        File[] versionDirectory = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith(version.toLowerCase() + "_") || name.equalsIgnoreCase(version);
            }
        });

        if (versionDirectory.length > 1) {
            throw new MojoFailureException("More than one folder was found for the specified version: " +
                                           Arrays.toString(versionDirectory));
        } else if (versionDirectory.length == 0) {
            throw new MojoFailureException("Could not find folder for version " + version);
        }

        return versionDirectory[0];
    }

    private void processMigrationFiles(List<File> files,
                                       Set<String> versionsSeenSoFar,
                                       File versionFolder,
                                       String lastVersion) {

        String versionPrefix = extractVersionPrefix(lastVersion);

        File[] migrationFiles = versionFolder.listFiles();
        if (migrationFiles != null) {
            for (File migrationFile : migrationFiles) {
                if (!migrationFile.isDirectory()) {
                    String[] filenameElements = migrationFile.getName().split("_");
                    String versionId = filenameElements[0];

                    boolean isBeforeLastVersion = lastVersion == null || !versionId.startsWith(versionPrefix) ||
                                                  versionId.compareToIgnoreCase(lastVersion) <= 0;

                    if (!versionsSeenSoFar.contains(versionId) && isBeforeLastVersion) {
                        versionsSeenSoFar.add(versionId);
                        files.add(migrationFile);
                    }
                }
            }
        }
    }

    private String extractVersionPrefix(String lastVersion) {
        String prefix = null;

        if (lastVersion != null) {
            int i = 0;
            while (!Character.isDigit(lastVersion.charAt(i))) {
                i++;
            }

            prefix = lastVersion.substring(0, i);
        }

        return prefix;
    }

    private void continueWithParent(List<File> files, Set<String> versionsSeenSoFar, File versionFolder) throws
                                                                                                         MojoFailureException {
        String[] versionFolderParts = versionFolder.getName().split("_");
        if (versionFolderParts.length > 1) {
            if (versionFolderParts.length != 3) {
                throw new MojoFailureException("Version folders need to have at least 3 parts: " +
                                               versionFolder.getName());
            }

            String parentFolder = versionFolderParts[1];
            String parentVersion = versionFolderParts[2];

            traverseVersionFolder(parentFolder, files, versionsSeenSoFar, parentVersion);
        }
    }

}
