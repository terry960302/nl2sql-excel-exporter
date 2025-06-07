import styled from "@emotion/styled";
import DemoCarousel from "@organisms/DemoSectionCarousel";
// import DemoVideo from "./DemoSectionVideo";
import queryScreen from "@assets/screens/query_screen.png";
import datasourceScreen from "@assets/screens/datasource_screen.png";
import quotaScreen from "@assets/screens/quota_screen.png";
import { analytics } from "@lib/analytics";

const Section = styled.section`
  width: 100%;
  padding: 4rem 0;
  background-color: #ffffff;
  scroll-margin-top: 80px;
`;

const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
`;

const ContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  align-items: center;
`;

const Title = styled.h2`
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a1a1a;
  text-align: center;
  margin-bottom: 1rem;
`;

const Description = styled.p`
  font-size: 1.25rem;
  color: #4a4a4a;
  text-align: center;
  margin-bottom: 2rem;
  max-width: 800px;
`;

const DemoButton = styled.button`
  background-color: #2e4f21;
  color: white;
  padding: 1rem 2rem;
  border: none;
  border-radius: 8px;
  font-size: 1.125rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: #1e3f11;
    transform: translateY(-2px);
  }
`;

const DemoSection = () => {
  const handleDemoClick = () => {
    analytics.trackCTAClick("DEMO_RESERVATION");
    // 여기에 데모 예약 페이지로 이동하는 로직 추가
  };

  return (
    <Section id="demo">
      <Container>
        <ContentWrapper>
          <Title>NL2SQL 데모 살펴보기</Title>
          <Description>
            이미지를 좌우로 넘기며 주요 기능 작동 과정을 직접 확인해보세요.
          </Description>
          {/* <DemoVideo videoUrl="/demo/demo.mp4" /> */}
          {/* 이미지 캐러셀 예시 */}
          <DemoCarousel images={[queryScreen, datasourceScreen, quotaScreen]} />
          <DemoButton onClick={handleDemoClick}>데모 예약하기</DemoButton>
        </ContentWrapper>
      </Container>
    </Section>
  );
};

export default DemoSection;
