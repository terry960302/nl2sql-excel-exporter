export const API_BASE_URL = "http://localhost:8080";

export const API_VERSIONS = {
  AUTH: "v1",
  DATASOURCE: "v1",
  QUERY: "v1",
  USAGE: "v1",
} as const;

export const AUTH_ENDPOINT_PATHS = {
  LOGIN: "/login",
  SIGNUP: "/signup",
  LOGOUT: "/logout",
  ME: "/me",
  TOKEN_REFRESH: "/token/refresh",
} as const;

export const DATASOURCE_ENDPOINT_PATHS = {
  LIST: "",
  DETAIL: "/:id",
  UPDATE: "/:id/update",
  DELETE: "/:id/delete",
} as const;

export const API_ENDPOINTS = {
  AUTH: `/api/${API_VERSIONS.AUTH}/auth`,
  DATASOURCES: `/api/${API_VERSIONS.DATASOURCE}/datasources`,
  SCHEMAS: `/api/${API_VERSIONS.DATASOURCE}/schemas`,
  JOBS: `/api/${API_VERSIONS.DATASOURCE}/jobs`,
  QUERIES: `/api/${API_VERSIONS.QUERY}/queries`,
};

// export const createAuthEndpoint = (path: keyof typeof AUTH_ENDPOINT_PATHS) =>
//   `${API_ENDPOINTS.AUTH}${AUTH_ENDPOINT_PATHS[path]}`;

// export const createDatasourceEndpoint = (
//   path: keyof typeof DATASOURCE_ENDPOINT_PATHS,
//   datasourceId?: string
// ) => {
//   const basePath = `${API_ENDPOINTS.DATASOURCES}`;
//   const endpointPath = DATASOURCE_ENDPOINT_PATHS[path];

//   if (datasourceId) {
//     return `${basePath}/${datasourceId}/${endpointPath}`;
//   }

//   return `${basePath}/${endpointPath}`;
// };
