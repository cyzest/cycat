package com.cyzest.cycat.security;

import java.util.ArrayList;
import java.util.List;

public class UrlPatternSecurityChecker {

    private List<UrlPatternSecurity> urlPatternSecurityList = new ArrayList<>();

    public void addUrlPatternSecurity(UrlPatternSecurity urlPatternSecurity) {
        this.urlPatternSecurityList.add(urlPatternSecurity);
    }

    public void setUrlPatternSecurityList(List<UrlPatternSecurity> urlPatternSecurityList) {

        if (urlPatternSecurityList == null) {
            throw new NullPointerException();
        }

        this.urlPatternSecurityList = urlPatternSecurityList;
    }

    public boolean validate(String url) {

        if (url == null || url.isEmpty()) {
            throw new NullPointerException("url is not empty");
        }

        boolean isValid = true;

        if (!urlPatternSecurityList.isEmpty()) {
            for (UrlPatternSecurity urlPatternSecurity : urlPatternSecurityList) {
                if (!urlPatternSecurity.validate(url)) {
                    isValid = false;
                    break;
                }
            }
        }

        return isValid;
    }

}
