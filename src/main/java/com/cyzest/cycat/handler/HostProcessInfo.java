package com.cyzest.cycat.handler;

import com.cyzest.cycat.config.HostInfo;
import com.cyzest.cycat.security.UrlPatternSecurityChecker;

public class HostProcessInfo extends HostInfo {

    private UrlPatternSecurityChecker urlPatternSecurityChecker;

    public HostProcessInfo(HostInfo hostInfo) {

        if (hostInfo == null) {
            throw new IllegalArgumentException("hostInfo is not be null");
        }

        super.setHost(hostInfo.getHost());
        super.setRoot(hostInfo.getRoot());
        super.setIndex(hostInfo.getIndex());
        super.setErrorPage(hostInfo.getErrorPage());
        super.setServletMapping(hostInfo.getServletMapping());
    }

    public UrlPatternSecurityChecker getUrlPatternSecurityChecker() {
        return urlPatternSecurityChecker;
    }

    public void setUrlPatternSecurityChecker(UrlPatternSecurityChecker urlPatternSecurityChecker) {
        this.urlPatternSecurityChecker = urlPatternSecurityChecker;
    }
}
