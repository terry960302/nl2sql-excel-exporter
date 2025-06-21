import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  schemaClient,
  Datasource,
  ScanSchemaResponse,
  RegisterSchemaRequest,
} from "@/lib/api/schemaClient";
import { useAuth } from "@/hooks/useAuth";
import { useAuthStore } from "@/stores/authStore";

const Container = styled.div`
  padding: 2rem;
`;

export const DatasourceDetail: React.FC = () => {
  const { datasourceId } = useParams<{ datasourceId: string }>();
  const { accessToken } = useAuth();
  const { userId, userInfo } = useAuthStore();
  const [datasource, setDatasource] = useState<Datasource | null>(null);
  const [scanResult, setScanResult] = useState<ScanSchemaResponse | null>(null);
  const [schemaName, setSchemaName] = useState("");

  useEffect(() => {
    if (!datasourceId) return;
    const fetch = async () => {
      try {
        const ds = await schemaClient.getDatasource(accessToken!, datasourceId);
        setDatasource(ds);
        setSchemaName(ds.name ?? "");
      } catch (e) {
        console.error("데이터소스 조회 실패:", e);
      }
    };
    fetch();
  }, [datasourceId, accessToken]);

  const handleScan = async () => {
    if (!datasourceId) return;
    try {
      const result = await schemaClient.scanSchema(accessToken!, datasourceId);
      setScanResult(result);
    } catch (e) {
      console.error("스키마 스캔 실패:", e);
      alert("스키마 스캔 중 오류가 발생했습니다.");
    }
  };

  const handleRegister = async () => {
    if (!scanResult || !userInfo) return;
    const req: RegisterSchemaRequest = {
      orgId: userInfo.organization.id,
      userId: userId!,
      agentId: (import.meta.env.VITE_AGENT_ID as string) || "agent",
      datasourceId: scanResult.datasourceId,
      name: schemaName || "schema",
      schemas: scanResult.schemas,
      rawSchema: scanResult.rawSchema,
    };
    try {
      await schemaClient.registerSchema(accessToken!, req);
      alert("스키마가 등록되었습니다.");
    } catch (e) {
      console.error("스키마 등록 실패:", e);
      alert("스키마 등록 중 오류가 발생했습니다.");
    }
  };

  return (
    <Container>
      {datasource && (
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>{datasource.name}</CardTitle>
          </CardHeader>
          <CardContent>
            <p>DB Type: {datasource.dbType}</p>
            <p>Engine: {datasource.engineType}</p>
          </CardContent>
        </Card>
      )}
      <div className="flex gap-2 mb-4">
        <Button onClick={handleScan}>스키마 스캔</Button>
        {scanResult && (
          <>
            <Input
              placeholder="스키마 이름"
              value={schemaName}
              onChange={(e) => setSchemaName(e.target.value)}
              className="w-48"
            />
            <Button onClick={handleRegister}>스키마 업로드</Button>
          </>
        )}
      </div>
      {scanResult && (
        <div className="space-y-4">
          {scanResult.schemas.map((table) => (
            <Card key={table.id}>
              <CardHeader>
                <CardTitle>{table.tableName}</CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="list-disc pl-4">
                  {table.columns.map((col) => (
                    <li key={col.id}>{`${col.columnName} (${col.dataType})`}</li>
                  ))}
                </ul>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </Container>
  );
};
