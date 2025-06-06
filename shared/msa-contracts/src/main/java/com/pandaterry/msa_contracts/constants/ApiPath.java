package com.pandaterry.msa_contracts.constants;

public final class ApiPath {
    private ApiPath() {
    }

    public static final class Auth {
        public static final String BASE = "/auth";

        // 회원가입
        public static final String SIGNUP_SUFFIX = "/signup";
        public static final String SIGNUP = BASE + SIGNUP_SUFFIX;

        // 로그인
        public static final String LOGIN_SUFFIX = "/login";
        public static final String LOGIN = BASE + LOGIN_SUFFIX;

        // 내 정보 조회
        public static final String ME_SUFFIX = "/me";
        public static final String ME = BASE + ME_SUFFIX;

        // 토큰 갱신
        public static final String REFRESH_TOKEN_SUFFIX = "/token/refresh";
        public static final String REFRESH_TOKEN = BASE + REFRESH_TOKEN_SUFFIX;

        // 로그아웃
        public static final String LOGOUT_SUFFIX = "/logout";
        public static final String LOGOUT = BASE + LOGOUT_SUFFIX;
    }

    public static final class Datasource {
        public static final String BASE = "/datasources";

        // 데이터소스 연결 확인(에이전트 서버)
        public static final String TEST_SUFFIX = "/test";
        public static final String TEST = BASE + TEST_SUFFIX;

        // 특정 데이터소스 조회
        public static final String DETAIL_SUFFIX = "/{datasourceId}";
        public static final String DETAIL = BASE + DETAIL_SUFFIX;
    }

    public static final class Schema {
        public static final String BASE = "/schemas";

        // 스키마 스캔(에이전트 서버)
        public static final String SCAN_SUFFIX = "/scan";
        public static final String SCAN = BASE + SCAN_SUFFIX;

        // 스키마 등록(에이전트 서버)
        public static final String REGISTER_SUFFIX = "/register";
        public static final String REGISTER = BASE + REGISTER_SUFFIX;
    }

    public static final class Query {
        public static final String BASE = "/queries";

        // 쿼리 실행(에이전트 서버)
        public static final String EXECUTE_SUFFIX = "/execute";
        public static final String EXECUTE = BASE + EXECUTE_SUFFIX;
    }

    public static final class Job {
        public static final String BASE = "/jobs";

        // 특정 작업
        public static final String DETAIL_SUFFIX = "/{jobId}";

        // 특정 작업 결과
        public static final String DETAIL_RESULT_SUFFIX = "/{jobId}/result";
        public static final String DETAIL_RESULT = BASE + DETAIL_RESULT_SUFFIX;
    }

    public static final class Quota {
        public static final String BASE = "/quota";

        // 내 조직 사용량 조회
        public static final String ORG_ME_SUFFIX = "/organizations/me";
        public static final String ORG_ME = BASE + ORG_ME_SUFFIX;

        // 사용량 기록
        public static final String USAGE_SUFFIX = "/usage";
        public static final String USAGE = BASE + USAGE_SUFFIX;

        // 모든 조직 사용량 조회
        public static final String ORGS_SUFFIX = "/organizations";
        public static final String ORGS = BASE + ORGS_SUFFIX;

        // 특정 조직 사용량 조회
        public static final String ORG_DETAIL_SUFFIX = "/organizations/{orgId}";
        public static final String ORG_DETAIL = BASE + ORG_DETAIL_SUFFIX;
    }
}
