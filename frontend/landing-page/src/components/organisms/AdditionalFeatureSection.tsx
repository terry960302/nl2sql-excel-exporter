import styled from "@emotion/styled";
import FeatureImage from "@atoms/FeatureImage";
import { breakpoints } from "@constants/breakpoints";

const Section = styled.section`
  width: 100%;
  padding: 4rem 0;
  background-color: #ffffff;
  @media (max-width: ${breakpoints.mobile}) {
    padding: 2rem 0;
  }
`;

const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  @media (max-width: ${breakpoints.mobile}) {
    padding: 0 1rem;
  }
`;

const ContentWrapper = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
  @media (max-width: ${breakpoints.mobile}) {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
`;

const TextContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  background-color: #f8f8f8;
  padding: 3rem;
  border-radius: 1rem;
`;

const Title = styled.h2`
  font-size: 48px;
  font-weight: 400;
  color: #000000;
  letter-spacing: -2.16px;
  line-height: 1;
  margin: 0;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 32px;
  }
`;

const Description = styled.p`
  font-size: 18px;
  font-weight: 400;
  color: #4a4a4a;
  letter-spacing: -0.81px;
  line-height: 1.5;
  margin: 0;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 16px;
  }
`;

const LearnMoreButton = styled.button`
  background-color: #ffffff;
  border: none;
  border-radius: 51.5746px;
  padding: 1rem 2rem;
  font-size: 12px;
  font-weight: 500;
  color: #4a4a4a;
  letter-spacing: -0.54px;
  cursor: pointer;
  width: fit-content;
  transition: all 0.3s ease;

  &:hover {
    background-color: #f5f5f5;
  }
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 14px;
  }
`;

const AdditionalFeatureSection = () => {
  return (
    <Section>
      <Container>
        <ContentWrapper>
          <TextContent>
            <Title>단순 조회가 아니에요</Title>
            <Description>
              NL2SQL Studio는 엑셀 파일로 자동 셀병합까지 하여 쉽고 편하게
              데이터를 조회할 수 있습니다.
            </Description>
            <LearnMoreButton>더 알아보기</LearnMoreButton>
          </TextContent>
          <FeatureImage />
        </ContentWrapper>
      </Container>
    </Section>
  );
};

export default AdditionalFeatureSection;
