package org.example.app.helpers;


import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FilesHelper {
    private Set<String> existedImages;
    private Logger logger = Logger.getLogger(FilesHelper.class);


    public FilesHelper() {
        existedImages = getImagesPaths();
    }

    public Set<String> getImagesPaths() {
        if (existedImages == null) {
            existedImages = new HashSet<>();
            String rootPath = System.getProperty("catalina.home");
            Path filesPath = Paths.get(rootPath + File.separator + "external_upload");
            try {
                existedImages = Files.list(filesPath).map(Path::toString).collect(Collectors.toSet());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return existedImages;
    }

    public void addNewImagePath(String fullPath) {
        existedImages.add(fullPath);
    }
}
