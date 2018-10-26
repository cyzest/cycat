package com.cyzest.cycat.security;

import org.junit.Assert;
import org.junit.Test;

public class UrlPatternSecurityTest {

    @Test
    public void directoryUrlPatternSecurityTest() throws Exception {

        String documentRoot = "src";

        DirectoryUrlPatternSecurity directoryUrlPatternSecurity = new DirectoryUrlPatternSecurity(documentRoot);

        Assert.assertTrue(directoryUrlPatternSecurity.validate("/main/resources/logback.xml"));
        Assert.assertTrue(directoryUrlPatternSecurity.validate("/main/resources/"));
        Assert.assertTrue(directoryUrlPatternSecurity.validate("/../src/main/resources/logback.xml"));

        Assert.assertFalse(directoryUrlPatternSecurity.validate("/main/resources/config"));
        Assert.assertFalse(directoryUrlPatternSecurity.validate("/main/resources/config.txt"));
        Assert.assertFalse(directoryUrlPatternSecurity.validate("/../pom.xml"));
    }

}
