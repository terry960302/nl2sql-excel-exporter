export const HEADERS = {
  CONTENT_TYPE: "Content-Type",
  AUTHORIZATION: "Authorization",
  X_REFRESH_TOKEN: "X-Refresh-Token",
  ORG_ID: "X-Organization-Id",
  USER_ID: "X-User-Id",
  AGENT_ID: "X-Agent-Id",
} as const;

export const CONTENT_TYPES = {
  APPLICATION_JSON: "application/json",
} as const;

export const AUTH_HEADER_PREFIX = "Bearer";
