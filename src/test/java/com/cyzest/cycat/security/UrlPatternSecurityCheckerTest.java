package com.cyzest.cycat.security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UrlPatternSecurityCheckerTest {

    private UrlPatternSecurityChecker urlPatternSecurityChecker;

    @Before
    public void init() throws IOException {
        this.urlPatternSecurityChecker = new UrlPatternSecurityChecker();
        this.urlPatternSecurityChecker.addUrlPatternSecurity(new DirectoryUrlPatternSecurity("src"));
        this.urlPatternSecurityChecker.addUrlPatternSecurity(new FileExtensionUrlPatternSecurity());
    }

    @Test
    public void validateTest() {
        Assert.assertTrue(urlPatternSecurityChecker.validate("/../src/main/resources/logback.xml"));
        Assert.assertFalse(urlPatternSecurityChecker.validate("/../pom.xml"));
    }

}
