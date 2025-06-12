package com.pandaterry.gateway.shared.supports;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

public class PublicPathChecker {
    private final List<String> publicPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public PublicPathChecker(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public boolean isPublic(String path) {
        return publicPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
