package ie.corballis.treeway;

import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class MigrationCollector {

    private File directory;
    private String version;
    private Set<String> usedVersions = newHashSet();

    public MigrationCollector(String version, String resourcePath) {
        this.version = version;
        this.directory = new File(resourcePath);
    }

    public List<File> collect() throws MojoFailureException {
        List<File> files = newArrayList();

        if (directory.exists()) {
            addFile(files, version);
        }

        return newArrayList(files);
    }

    private void addFile(List<File> files, final String version) throws MojoFailureException {

        List<File> filesWithVersion = getFilesWithVersion(version, directory);

        if (filesWithVersion.size() > 1) {
            throw new MojoFailureException("Migration is ambiguous with version: " + version);
        } else if (filesWithVersion.size() < 1) {
            throw new MojoFailureException("There are no migrations with version: " + version);
        }

        File file = filesWithVersion.get(0);

        files.add(file);

        String[] filenameElements = file.getName().split("_");

        if (!isTimestamp(filenameElements[1])) {
            if (!usedVersions.contains(filenameElements[1])) {
                usedVersions.add(filenameElements[1]);
                addFile(files, filenameElements[1]);
            } else {
                throw new MojoFailureException("Version: " + filenameElements[1] + " generates a circular reference");
            }
        }

    }

    private List<File> getFilesWithVersion(String version, File directory) throws MojoFailureException {
        File[] files = directory.listFiles();
        List<File> filesWithVersion = newArrayList();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    filesWithVersion.addAll(getFilesWithVersion(version, file));
                } else if (file.getName().startsWith(version + "_")) {
                    filesWithVersion.add(file);
                }
            }
        }

        return filesWithVersion;
    }

    private boolean isTimestamp(String filenameElement) {
        return filenameElement.matches("^[0-9][0-9][0-9][0-9]\\.[0-1][0-9]\\.[0-3][0-9]\\.[0-2][0-9]\\.[0-5][0-9]$");
    }

}
