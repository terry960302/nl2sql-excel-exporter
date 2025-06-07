import styled from "@emotion/styled";
import heroImage from "@assets/icons/ask_bubble_database.jpg";

const Section = styled.section`
  width: 100%;
  padding: 4rem 0;
  background-color: #f5f5f5;
`;

const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
`;

const ContentWrapper = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
`;

const TextContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

const Title = styled.h1`
  font-size: 60px;
  font-weight: 400;
  color: #2e4f21;
  letter-spacing: -3px;
  line-height: 1.1;
  margin: 0;
`;

const Description = styled.p`
  font-size: 18px;
  font-weight: 400;
  color: #2e4f21;
  letter-spacing: -0.81px;
  line-height: 1.5;
  margin: 0;
`;

const ImageContainer = styled.div`
  width: 90%;
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin: 0 auto;
`;

const Image = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
`;

const HeroSection = () => {
  return (
    <Section>
      <Container>
        <ContentWrapper>
          <TextContent>
            <Title>
              데이터 요청, <br />더 이상 눈치 보지 마세요
            </Title>
            <Description>
              "이 데이터 뽑아줄 수 있나요?" <br /> 이제는 개발팀에 미안해하지
              않아도 됩니다. <br />
              <strong>NL2SQL</strong>은 자연어로 질문하면, SQL도 몰라도 엑셀로
              결과를 직접 받아볼 수 있는 실무자 전용 도구입니다.
            </Description>
          </TextContent>
          <ImageContainer>
            <Image
              src={heroImage}
              alt="Database visualization"
              loading="lazy"
            />
          </ImageContainer>
        </ContentWrapper>
      </Container>
    </Section>
  );
};

export default HeroSection;
