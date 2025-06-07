import styled from "@emotion/styled";
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

const ConnectButton = styled.button`
  background-color: #2e4f21;
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 1.125rem;
  font-weight: 600;
  padding: 1rem 2rem;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background-color: #1e3f11;
    transform: translateY(-2px);
  }
`;

const CTASection = () => {
  const handleContactClick = () => {
    analytics.trackCTAClick("CONTACT_INQUIRY");
    // 여기에 문의하기 페이지로 이동하는 로직 추가
  };

  return (
    <Section>
      <Container>
        <ContentWrapper>
          <Title>
            누구나 쉽게, 데이터에 답을 묻다.
            <br />
            오늘부터 NL2SQL를 함께 시작해보세요!
          </Title>
          <ConnectButton onClick={handleContactClick}>문의하기</ConnectButton>
        </ContentWrapper>
      </Container>
    </Section>
  );
};

export default CTASection;
