import React from "react";
import styled from "@emotion/styled";

interface TestimonialCardProps {
  content: string;
  role: string;
  location: string;
  isActive?: boolean;
  onClick?: () => void;
}

const Card = styled.div<{ isActive?: boolean }>`
  background: ${({ isActive }) => (isActive ? "#2e4f21" : "#ffffff")};
  border: 1px solid #2e4f21;
  border-radius: 8px;
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: ${({ isActive }) => (isActive ? "#2e4f21" : "#f8f8f8")};
  }
`;

const Content = styled.p<{ isActive?: boolean }>`
  font-size: 16px;
  font-weight: 400;
  color: ${({ isActive }) => (isActive ? "#ffffff" : "#2e4f21")};
  letter-spacing: -0.72px;
  line-height: 1.5;
  margin-bottom: 1rem;
`;

const Role = styled.p<{ isActive?: boolean }>`
  font-size: 14px;
  font-weight: 500;
  color: ${({ isActive }) => (isActive ? "#ffffff" : "#2e4f21")};
  letter-spacing: -0.63px;
  margin: 0;
`;

const Location = styled.p<{ isActive?: boolean }>`
  font-size: 14px;
  font-weight: 400;
  color: ${({ isActive }) => (isActive ? "#ffffff" : "#2e4f21")};
  letter-spacing: -0.63px;
  margin: 0;
`;

const TestimonialCard: React.FC<TestimonialCardProps> = ({
  content,
  role,
  location,
  isActive = false,
  onClick,
}) => {
  return (
    <Card isActive={isActive} onClick={onClick}>
      <Content isActive={isActive}>{content}</Content>
      <Role isActive={isActive}>{role}</Role>
      <Location isActive={isActive}>{location}</Location>
    </Card>
  );
};

export default TestimonialCard;
