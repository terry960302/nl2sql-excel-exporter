import { HEADERS, CONTENT_TYPES, AUTH_HEADER_PREFIX } from "@/lib/api/constants/headers";
import { API_ENDPOINTS } from "@/lib/api/constants/endpoints";
import apiClient from "@/lib/api/client";

export interface NaturalLanguageQueryRequest {
  orgId: string;
  agentId: string;
  datasourceId: string;
  naturalText: string;
}

export interface NaturalLanguageQueryResponse {
  filename: string;
  downloadUrl: string;
  sql: string;
  rowCount: number;
  elapsedTime: number;
}

export const queryClient = {
  async executeQuery(accessToken: string, data: NaturalLanguageQueryRequest) {
    const res = await apiClient.post<NaturalLanguageQueryResponse>(
      API_ENDPOINTS.QUERIES,
      data,
      {
        headers: {
          [HEADERS.AUTHORIZATION]: `${AUTH_HEADER_PREFIX} ${accessToken}`,
          [HEADERS.CONTENT_TYPE]: CONTENT_TYPES.APPLICATION_JSON,
        },
      }
    );
    return res.data;
  },
};
