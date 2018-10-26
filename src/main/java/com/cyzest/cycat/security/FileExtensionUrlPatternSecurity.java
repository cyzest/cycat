package com.cyzest.cycat.security;

public class FileExtensionUrlPatternSecurity implements UrlPatternSecurity {

    @Override
    public boolean validate(String url) {
        return !url.matches("(?i)\\.exe&");
    }
}
