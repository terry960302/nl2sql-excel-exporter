package com.pandaterry.msa_contracts.constants;

public final class ApiPath {
    private ApiPath(){}

    public static final class Auth {
        public static final String SIGNUP = "/auth/signup";
        public static final String LOGIN  = "/auth/login";
        public static final String ME     = "/auth/me";
    }

    public static final class Schema {
        public static final String BASE        = "/schemas";
        public static final String ME          = BASE + "/me";
        public static final String USER_DETAIL = BASE + "/users/{id}";
    }

    public static final class Query {
        public static final String BASE     = "/queries";
        public static final String DOWNLOAD = BASE + "/{requestId}/download";
    }

    public static final class Quota {
        public static final String BASE     = "/quotas";
        public static final String DOWNLOAD = BASE + "/{requestId}/download";
    }
}
