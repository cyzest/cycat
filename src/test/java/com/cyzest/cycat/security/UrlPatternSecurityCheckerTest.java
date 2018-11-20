package com.cyzest.cycat.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class UrlPatternSecurityCheckerTest {

    private UrlPatternSecurityChecker urlPatternSecurityChecker;

    @BeforeEach
    public void init() throws IOException {
        this.urlPatternSecurityChecker = new UrlPatternSecurityChecker();
        this.urlPatternSecurityChecker.addUrlPatternSecurity(new DirectoryUrlPatternSecurity("src"));
        this.urlPatternSecurityChecker.addUrlPatternSecurity(new FileExtensionUrlPatternSecurity());
    }

    @Test
    public void validateTest() {
        Assertions.assertTrue(urlPatternSecurityChecker.validate("/../src/main/resources/logback.xml"));
        Assertions.assertFalse(urlPatternSecurityChecker.validate("/../pom.xml"));
    }

}
