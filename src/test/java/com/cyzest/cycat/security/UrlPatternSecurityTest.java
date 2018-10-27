package com.cyzest.cycat.security;

import org.junit.Assert;
import org.junit.Test;

public class UrlPatternSecurityTest {

    @Test
    public void directoryUrlPatternSecurityTest() throws Exception {

        String documentRoot = "src";

        UrlPatternSecurity urlPatternSecurity = new DirectoryUrlPatternSecurity(documentRoot);

        Assert.assertTrue(urlPatternSecurity.validate("/main/resources/logback.xml"));
        Assert.assertTrue(urlPatternSecurity.validate("/main/resources/"));
        Assert.assertTrue(urlPatternSecurity.validate("/../src/main/resources/logback.xml"));

        Assert.assertFalse(urlPatternSecurity.validate("/main/resources/logback"));
        Assert.assertFalse(urlPatternSecurity.validate("/main/resources/logback.txt"));
        Assert.assertFalse(urlPatternSecurity.validate("/../pom.xml"));
    }

    @Test
    public void fileExtensionUrlPatternSecurityTest() throws Exception {

        UrlPatternSecurity urlPatternSecurity = new FileExtensionUrlPatternSecurity();

        Assert.assertTrue(urlPatternSecurity.validate("/"));
        Assert.assertTrue(urlPatternSecurity.validate("/test"));
        Assert.assertTrue(urlPatternSecurity.validate("/test.txt"));

        Assert.assertFalse(urlPatternSecurity.validate("/test.exe"));
        Assert.assertFalse(urlPatternSecurity.validate("/test.EXE"));
    }

}
