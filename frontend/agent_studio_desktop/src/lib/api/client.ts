import axios from "axios";
import { HEADERS, CONTENT_TYPES } from "@/lib/api/constants/headers";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    [HEADERS.CONTENT_TYPE]: CONTENT_TYPES.APPLICATION_JSON,
  },
});

export default apiClient;
