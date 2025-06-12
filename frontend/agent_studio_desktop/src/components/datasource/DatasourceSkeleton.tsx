import React from "react";
import styled from "styled-components";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
`;

const StyledCard = styled(Card)`
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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

const TitleSkeleton = styled(Skeleton)`
  width: 60%;
  height: 24px;
`;

const BadgeSkeleton = styled(Skeleton)`
  width: 100px;
  height: 24px;
`;

const DescriptionSkeleton = styled(Skeleton)`
  width: 100%;
  height: 16px;
  margin-bottom: 8px;
`;

const MetaSkeleton = styled(Skeleton)`
  width: 40%;
  height: 14px;
`;

export const DatasourceSkeleton: React.FC = () => {
  return (
    <Grid>
      {[1, 2, 3].map((index) => (
        <StyledCard key={index}>
          <CardHeaderWrapper>
            <TitleSkeleton />
            <BadgeSkeleton />
          </CardHeaderWrapper>
          <CardContentWrapper>
            <DescriptionSkeleton />
            <DescriptionSkeleton />
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                marginTop: "1rem",
              }}
            >
              <MetaSkeleton />
              <MetaSkeleton />
            </div>
          </CardContentWrapper>
        </StyledCard>
      ))}
    </Grid>
  );
};
