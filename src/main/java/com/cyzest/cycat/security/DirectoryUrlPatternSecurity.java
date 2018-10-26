package com.cyzest.cycat.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DirectoryUrlPatternSecurity implements UrlPatternSecurity {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryUrlPatternSecurity.class);

    private final String rootDirectory;

    public DirectoryUrlPatternSecurity(String rootDirectory) throws IOException {
        if (!rootDirectory.startsWith("/")) {
            this.rootDirectory = new File(rootDirectory).getCanonicalPath();
        } else {
            this.rootDirectory = rootDirectory;
        }
    }

    @Override
    public boolean validate(String url) {

        boolean isValid = false;

        try {
            File file = new File(rootDirectory, url);
            if (file.canRead() && file.getCanonicalPath().startsWith(rootDirectory)) {
                isValid = true;
            }
        } catch (IOException ex) {
            logger.warn("validate file io exception", ex);
        }

        return isValid;
    }

}
