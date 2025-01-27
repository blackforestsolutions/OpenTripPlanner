package org.opentripplanner.datastore.file;


import org.opentripplanner.datastore.CompositeDataSource;
import org.opentripplanner.datastore.DataSource;
import org.opentripplanner.datastore.FileType;
import org.opentripplanner.datastore.base.LocalDataSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.opentripplanner.datastore.FileType.CONFIG;
import static org.opentripplanner.datastore.FileType.DEM;
import static org.opentripplanner.datastore.FileType.GRAPH;
import static org.opentripplanner.datastore.FileType.GTFS;
import static org.opentripplanner.datastore.FileType.OSM;
import static org.opentripplanner.datastore.FileType.OTP_STATUS;
import static org.opentripplanner.datastore.FileType.REPORT;
import static org.opentripplanner.datastore.FileType.UNKNOWN;
import static org.opentripplanner.datastore.OtpDataStore.BUILD_REPORT_DIR;
import static org.opentripplanner.datastore.base.LocalDataSourceRepository.isCurrentDir;
import static org.opentripplanner.standalone.config.ConfigLoader.isConfigFile;


/**
 * This data store uses the local file system to access in-/out- data files.
 */
public class FileDataSourceRepository implements LocalDataSourceRepository {
    private static final Logger LOG = LoggerFactory.getLogger(FileDataSourceRepository.class);

    private final File baseDir;
    private final Pattern gtfsLocalFilePattern;
    private final Pattern osmLocalFilePattern;
    private final Pattern demLocalFilePattern;


    public FileDataSourceRepository(
        File baseDir,
        Pattern gtfsLocalFilePattern,
        Pattern osmLocalFilePattern,
        Pattern demLocalFilePattern
    ) {
        this.baseDir = baseDir;
        this.gtfsLocalFilePattern = gtfsLocalFilePattern;
        this.osmLocalFilePattern = osmLocalFilePattern;
        this.demLocalFilePattern = demLocalFilePattern;
    }

    /**
     * Use for unit testing
     */
    @NotNull
    public static CompositeDataSource compositeSource(File file, FileType type) {
        // The cast is safe
        return createCompositeSource(file, type);
    }

    @Override
    public String description() {
        return baseDir.getPath();
    }

    @Override
    public void open() { /* Nothing to do */ }

    @Override
    public DataSource findSource(URI uri, FileType type) {
        return new FileDataSource(new File(uri), type);
    }

    @Override
    public DataSource findSource(String filename, FileType type) {
        return new FileDataSource(new File(baseDir, filename), type);
    }

    @Override
    public CompositeDataSource findCompositeSource(URI uri, FileType type) {
        return createCompositeSource(new File(uri), type);
    }

    @Override
    public CompositeDataSource findCompositeSource(String localFilename, FileType type) {
        // If the local file name is '.' then use the 'baseDir', if not create a new file directory.
        File file = isCurrentDir(localFilename) ? baseDir : new File(baseDir, localFilename);
        return createCompositeSource(file, type);
    }

    @Override
    public List<DataSource> listExistingSources(FileType type) {
        // Return ALL resources of the given type, this is
        // auto-detecting matching files on the local file system
        List<DataSource> existingFiles = new ArrayList<>();
        File[] files = baseDir.listFiles();

        if (files == null) {
            LOG.error("'{}' is not a readable input directory.", baseDir);
            return existingFiles;
        }

        for (File file : files) {
            if(type == resolveFileType(file)) {
                if (isCompositeDataSource(file)) {
                    existingFiles.add(createCompositeSource(file, type));
                }
                else {
                    existingFiles.add(new FileDataSource(file, type));
                }
            }
        }
        return existingFiles;
    }

    @Override
    public String toString() {
        return "FileDataSourceRepository{" + "baseDir=" + baseDir + '}';
    }

    /* private methods */

    private boolean isCompositeDataSource(File file) {
       return file.isDirectory() || file.getName().endsWith(".zip");
    }

    private static CompositeDataSource createCompositeSource(File file, FileType type) {
        if (file.exists() && file.isDirectory()) {
            return new DirectoryDataSource(file, type);
        }
        if (file.getName().endsWith(".zip")) {
            return new ZipFileDataSource(file, type);
        }
        // If writing to a none-existing directory
        if (!file.exists() && type.isOutputDataSource()) {
            return new DirectoryDataSource(file, type);
        }
        throw new IllegalArgumentException("The " + file + " is not recognized as a zip-file or "
                + "directory. Unable to create composite data source for file type " + type + ".");
    }



    private FileType resolveFileType(File file) {
        String name = file.getName();
        if (isTransitFile(file, gtfsLocalFilePattern)) { return GTFS; }
        if (osmLocalFilePattern.matcher(name).find()) { return OSM; }
        // Digital elevation model (elevation raster)
        if (demLocalFilePattern.matcher(name).find()) { return DEM; }
        if (name.matches("(streetG|g)raph.obj")) { return GRAPH; }
        if (name.matches("otp-status.(inProgress|ok|failed)")) { return OTP_STATUS; }
        if (name.equals(BUILD_REPORT_DIR)) { return REPORT; }
        if (isConfigFile(name)) { return CONFIG;}
        return UNKNOWN;
    }

    private static boolean isTransitFile(File file, Pattern pattern) {
        return pattern.matcher(file.getName()).find()
                && (file.isDirectory() || file.getName().endsWith(".zip"));
    }
}
