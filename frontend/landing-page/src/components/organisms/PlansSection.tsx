import styled from "styled-components";
import { analytics } from "@lib/analytics";

const Section = styled.section`
  padding: 4rem 0;
  text-align: center;
  scroll-margin-top: 80px;
`;

const SectionTitle = styled.h2`
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 1rem;
`;

const SectionDescription = styled.p`
  font-size: 1.25rem;
  color: #4a4a4a;
  margin-bottom: 2rem;
`;

const PlanCard = styled.div`
  flex: 1;
  min-width: 300px;
  max-width: 350px;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  transition: all 0.3s ease;

  &:hover {
    background-color: #f8f8f8;
    transform: translateY(-4px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
`;

const PlanName = styled.h4`
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
  transition: color 0.3s ease;
`;

const PlanPrice = styled.div`
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
  transition: color 0.3s ease;
`;

const PlanPeriod = styled.p`
  font-size: 0.875rem;
  color: #4a4a4a;
  transition: color 0.3s ease;
`;

const FeatureItem = styled.li`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #4a4a4a;
  font-size: 0.875rem;
  transition: color 0.3s ease;

  &::before {
    content: "✓";
    color: #007acc;
    transition: color 0.3s ease;
  }
`;

const PlansContainer = styled.div`
  display: flex;
  gap: 2rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 3rem;
`;

const Button = styled.button`
  background-color: #007acc;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 1rem 2rem;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: #4a753f;
  }
`;

const plans = [
  {
    name: "Basic",
    price: "₩49,000",
    period: "월",
    features: [
      "기본 SQL 쿼리 생성",
      "일일 100회 쿼리 제한",
      "이메일 지원",
      "기본 문서화",
    ],
  },
  {
    name: "Pro",
    price: "₩138,000",
    period: "월",
    features: [
      "고급 SQL 쿼리 생성",
      "무제한 쿼리",
      "우선 지원",
      "고급 문서화",
      "API 액세스",
    ],
  },
  {
    name: "Enterprise",
    price: "문의",
    period: "맞춤형",
    features: [
      "맞춤형 SQL 쿼리 생성",
      "무제한 쿼리",
      "전담 지원",
      "맞춤형 문서화",
      "API 액세스",
      "온보딩 지원",
    ],
  },
];

const PlansSection = () => {
  const handleStartClick = (planName: string) => {
    analytics.trackCTAClick("PLAN_START", { plan_name: planName });
    // 여기에 시작하기 페이지로 이동하는 로직 추가
  };

  return (
    <Section id="plans">
      <SectionTitle>요금제</SectionTitle>
      <SectionDescription>
        비즈니스 규모에 맞는 최적의 요금제를 선택하세요
      </SectionDescription>
      <PlansContainer>
        {plans.map((plan) => (
          <PlanCard key={plan.name}>
            <PlanName>{plan.name}</PlanName>
            <PlanPrice>{plan.price}</PlanPrice>
            <PlanPeriod>/{plan.period}</PlanPeriod>
            <ul>
              {plan.features.map((feature) => (
                <FeatureItem key={feature}>{feature}</FeatureItem>
              ))}
            </ul>
            <Button onClick={() => handleStartClick(plan.name)}>
              시작하기
            </Button>
          </PlanCard>
        ))}
      </PlansContainer>
    </Section>
  );
};

export default PlansSection;
