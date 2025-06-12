import {
  HEADERS,
  CONTENT_TYPES,
  AUTH_HEADER_PREFIX,
} from "@/lib/api/constants/headers";
import apiClient from "@/lib/api/client";
import { API_ENDPOINTS } from "@/lib/api/constants/endpoints";

export const schemaClient = {
  async getDatasources(accessToken: string): Promise<Datasource[]> {
    const response = await apiClient.get<Datasource[]>(
      `${API_ENDPOINTS.DATASOURCES}`,
      {
        headers: {
          [HEADERS.AUTHORIZATION]: `${AUTH_HEADER_PREFIX} ${accessToken}`,
        },
      }
    );
    return response.data;
  },

  async testConnection(accessToken: string): Promise<Datasource> {
    const response = await apiClient.post(`${API_ENDPOINTS.DATASOURCES}`, {
      headers: {
        [HEADERS.AUTHORIZATION]: `${AUTH_HEADER_PREFIX} ${accessToken}`,
      },
    });
    return response.data;
  },

  async createDatasource(
    accessToken: string,
    datasourceId: string,
    data: CreateDatasourceRequest
  ): Promise<Datasource> {
    const response = await apiClient.post(
      `${API_ENDPOINTS.DATASOURCES}/${datasourceId}`,
      data,
      {
        headers: {
          [HEADERS.AUTHORIZATION]: `${AUTH_HEADER_PREFIX} ${accessToken}`,
          [HEADERS.CONTENT_TYPE]: CONTENT_TYPES.APPLICATION_JSON,
        },
      }
    );
    return response.data;
  },

  async getDatasource(
    accessToken: string,
    datasourceId: string
  ): Promise<Datasource> {
    const response = await apiClient.get(
      `${API_ENDPOINTS.DATASOURCES}/${datasourceId}`,
      {
        headers: {
          [HEADERS.AUTHORIZATION]: `${AUTH_HEADER_PREFIX} ${accessToken}`,
        },
      }
    );
    return response.data;
  },
};

// Types
export interface Datasource {
  id: string;
  name: string | null;
  dbType: string | null;
  engineType: string | null;
  isEnabled: "ENABLED" | "DISABLED";
  createdAt: string;
  updatedAt: string;
}

export interface CreateDatasourceRequest {
  name: string;
  dbType: string;
  engineType: string;
}

export interface Column {
  id: string;
  columnName: string;
  dataType: string;
  nullable: boolean;
  primaryKey: boolean;
}

export interface TableSchema {
  id: string;
  tableName: string;
  columns: Column[];
}

export interface Schema {
  id: string;
  name: string;
  rawJson: string;
}

export interface CreateSchemaRequest {
  datasourceId: string;
  name: string;
  schemas: TableSchema[];
  rawSchema: string;
}

export interface UpdateDatasourceRequest {
  name: string;
  dbType: string;
  engineType: string;
}
