import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import {
  NewDatasourceForm,
  DatasourceFormData,
} from "@/components/datasource/NewDatasourceForm";
import { DatasourceSkeleton } from "@/components/datasource/DatasourceSkeleton";
import { schemaClient, Datasource } from "@/lib/api/schemaClient";
import { useAuth } from "@/hooks/useAuth";

const Container = styled.div`
  padding: 2rem;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
`;

const Title = styled.h1`
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
`;

const StyledCard = styled(Card)`
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }
`;

const CardHeaderWrapper = styled(CardHeader)`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 1.5rem;
`;

const CardContentWrapper = styled(CardContent)`
  padding: 0 1.5rem 1.5rem;
`;

const CardMeta = styled.div`
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.875rem;
  margin-top: 1rem;
`;

const EmptyState = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const EmptyStateTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
`;

const EmptyStateDescription = styled.p`
  color: #666;
  margin-bottom: 1.5rem;
`;

export const Datasources: React.FC = () => {
  const { accessToken } = useAuth();
  const [datasources, setDatasources] = useState<Datasource[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchDatasources = async () => {
      try {
        setIsLoading(true);
        const data = await schemaClient.getDatasources(accessToken!);
        setDatasources(data);
      } catch (error) {
        console.error("데이터 소스 목록을 불러오는데 실패했습니다:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDatasources();
  }, []);

  const handleCreateNew = () => {
    setIsModalOpen(true);
  };

  const handleTestConnection = async (_: DatasourceFormData) => {
    try {
      const success = await schemaClient.testConnection(accessToken!);
      if (success) {
        alert("연결 테스트가 성공했습니다.");
      } else {
        alert("연결 테스트가 실패했습니다.");
      }
    } catch (error) {
      console.error("연결 테스트 실패:", error);
      alert("연결 테스트 중 오류가 발생했습니다.");
    }
  };

  const handleSubmit = async (data: DatasourceFormData) => {
    try {
      await schemaClient.createDatasource(accessToken!, data.id, data);
      setIsModalOpen(false);
      // 목록 새로고침
      const updatedList = await schemaClient.getDatasources(accessToken!);
      setDatasources(updatedList);
    } catch (error) {
      console.error("데이터소스 생성 실패:", error);
      alert("데이터소스 생성 중 오류가 발생했습니다.");
    }
  };

  const handleDatasourceClick = (id: string) => {
    navigate(`/datasources/${id}`);
  };

  return (
    <Container>
      <Header>
        <Title>데이터 소스</Title>
        <Button onClick={handleCreateNew}>새 데이터 소스</Button>
      </Header>

      {isLoading ? (
        <DatasourceSkeleton />
      ) : datasources.length === 0 ? (
        <EmptyState>
          <EmptyStateTitle>데이터 소스가 없습니다</EmptyStateTitle>
          <EmptyStateDescription>
            새 데이터 소스를 추가하여 시작하세요.
          </EmptyStateDescription>
          <Button onClick={handleCreateNew}>첫 데이터 소스 추가하기</Button>
        </EmptyState>
      ) : (
        <Grid>
          {datasources.map((datasource) => (
            <StyledCard
              key={datasource.id}
              onClick={() => handleDatasourceClick(datasource.id)}
            >
              <CardHeaderWrapper>
                <CardTitle>{datasource.name}</CardTitle>
                <Badge variant="secondary">{datasource.dbType}</Badge>
              </CardHeaderWrapper>
              <CardContentWrapper>
                <CardDescription>{datasource.name}</CardDescription>
                <CardMeta>
                  <span>{datasource.engineType}</span>
                </CardMeta>
              </CardContentWrapper>
            </StyledCard>
          ))}
        </Grid>
      )}

      <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>데이터소스 추가</DialogTitle>
            <DialogDescription>
              데이터소스 연결을 위한 상세정보를 입력해주세요.
            </DialogDescription>
          </DialogHeader>
          <NewDatasourceForm
            onSubmit={handleSubmit}
            onTestConnection={handleTestConnection}
          />
        </DialogContent>
      </Dialog>
    </Container>
  );
};
