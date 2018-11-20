package com.cyzest.cycat.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlPatternSecurityTest {

    @Test
    public void directoryUrlPatternSecurityTest() throws Exception {

        String documentRoot = "src";

        UrlPatternSecurity urlPatternSecurity = new DirectoryUrlPatternSecurity(documentRoot);

        Assertions.assertTrue(urlPatternSecurity.validate("/main/resources/logback.xml"));
        Assertions.assertTrue(urlPatternSecurity.validate("/main/resources/"));
        Assertions.assertTrue(urlPatternSecurity.validate("/../src/main/resources/logback.xml"));

        Assertions.assertFalse(urlPatternSecurity.validate("/main/resources/logback"));
        Assertions.assertFalse(urlPatternSecurity.validate("/main/resources/logback.txt"));
        Assertions.assertFalse(urlPatternSecurity.validate("/../pom.xml"));
    }

    @Test
    public void fileExtensionUrlPatternSecurityTest() {

        UrlPatternSecurity urlPatternSecurity = new FileExtensionUrlPatternSecurity();

        Assertions.assertTrue(urlPatternSecurity.validate("/"));
        Assertions.assertTrue(urlPatternSecurity.validate("/test"));
        Assertions.assertTrue(urlPatternSecurity.validate("/test.txt"));

        Assertions.assertFalse(urlPatternSecurity.validate("/test.exe"));
        Assertions.assertFalse(urlPatternSecurity.validate("/test.EXE"));
    }

}
