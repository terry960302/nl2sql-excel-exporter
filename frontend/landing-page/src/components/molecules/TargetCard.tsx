import React from "react";
import styled from "@emotion/styled";

export interface TargetCardProps {
  icon: React.ReactNode;
  title: string;
  subtitle: string;
  description: string;
}

const Card = styled.div`
  background: #fff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }
`;

const IconWrapper = styled.div`
  margin-bottom: 1rem;
`;

const Title = styled.h3`
  font-size: 1.5rem;
  font-weight: 600;
  color: #2e4f21;
  margin-bottom: 0.5rem;
`;

const Subtitle = styled.h4`
  font-size: 1.1rem;
  font-weight: 500;
  color: #666;
  margin-bottom: 1rem;
`;

const Description = styled.p`
  font-size: 1rem;
  color: #666;
  line-height: 1.6;
`;

const TargetCard: React.FC<TargetCardProps> = ({
  icon,
  title,
  subtitle,
  description,
}) => {
  return (
    <Card>
      <IconWrapper>{icon}</IconWrapper>
      <Title>{title}</Title>
      <Subtitle>{subtitle}</Subtitle>
      <Description>{description}</Description>
    </Card>
  );
};

export default TargetCard;
