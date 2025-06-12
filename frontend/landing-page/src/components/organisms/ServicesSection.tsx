import styled from "@emotion/styled";
import Button from "@atoms/Button";
import bubbleTalkDb from "@assets/icons/bubble_talk_with_db.png";
import security from "@assets/icons/security.png";
import excel from "@assets/icons/excel.png";
import { breakpoints } from "@constants/breakpoints";

const Section = styled.section`
  width: 100%;
  padding: 4rem 0;
  background-color: #ffffff;
  scroll-margin-top: 80px;
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

const HeaderWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
  text-align: center;
  margin-bottom: 4rem;
  @media (max-width: ${breakpoints.mobile}) {
    margin-bottom: 2rem;
  }
`;

const Title = styled.h2`
  font-size: 1rem;
  font-weight: 500;
  color: #005f91;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 0.875rem;
  }
`;

const Subtitle = styled.h3`
  font-size: 2.5rem;
  font-weight: 700;
  color: #000000;
  line-height: 1.2;
  letter-spacing: -0.02em;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 1.75rem;
  }
`;

const Description = styled.p`
  font-size: 1.125rem;
  line-height: 1.5;
  color: #4a4a4a;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 1rem;
  }
`;

const ServicesContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6rem;
  @media (max-width: ${breakpoints.mobile}) {
    gap: 3rem;
  }
`;

const ServiceCard = styled.div<{ isReversed?: boolean }>`
  display: flex;
  gap: 2rem;
  align-items: flex-start;
  flex-direction: ${({ isReversed }) => (isReversed ? "row-reverse" : "row")};
  background-color: #eaf6ff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  max-width: calc(100% - 4rem);
  margin-left: ${({ isReversed }) => (isReversed ? "10rem" : "0")};
  margin-right: ${({ isReversed }) => (isReversed ? "0" : "10rem")};
  @media (max-width: ${breakpoints.mobile}) {
    flex-direction: column;
    margin: 0;
    max-width: 100%;
  }
`;

const ServiceImage = styled.div`
  flex: 0 0 300px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;

  img {
    width: 100%;
    height: auto;
    object-fit: cover;
  }
  @media (max-width: ${breakpoints.mobile}) {
    flex: none;
  }
`;

const ServiceContent = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 1rem;
  @media (max-width: ${breakpoints.mobile}) {
    padding: 0;
  }
`;

const ServiceTitle = styled.h4`
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: -0.02em;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 1.25rem;
  }
`;

const ServiceDescription = styled.p`
  font-size: 1.125rem;
  line-height: 1.5;
  color: #4a4a4a;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 1rem;
  }
`;

const TagsContainer = styled.div`
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  @media (max-width: ${breakpoints.mobile}) {
    justify-content: center;
  }
`;

const Tag = styled.span`
  padding: 0.5rem 1rem;
  background-color: #f8f8f8;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 500;
  color: #4a4a4a;
  letter-spacing: -0.54px;
  @media (max-width: ${breakpoints.mobile}) {
    font-size: 0.75rem;
  }
`;

const services = [
  {
    title: "자연어 → SQL 자동 변환",
    description:
      '누구나 질문하듯 말하면, 정확한 쿼리를 생성해줍니다. 자연어로 "이번 달 신규 가입자 중 구매 전환율 알려줘"라고 입력하면, NL2SQL이 실제 데이터베이스 구조를 기반으로 SQL을 자동 생성합니다. SQL을 몰라도 원하는 데이터를 직접 요청할 수 있습니다.',
    image: bubbleTalkDb,
    tags: ["자연어 처리", "SQL 생성", "데이터 요청"],
  },
  {
    title: "설치형 에이전트 기반 보안 구조",
    description:
      "사내 데이터는 사내에서만 처리됩니다. 외부에서 고객사 DB에 접근하지 않습니다. NL2SQL Studio(설치형 프로그램)가 로컬에서 SQL을 실행하고 결과만 SaaS 서버와 주고받아 보안 걱정 없이 사용 가능합니다.",
    image: security,
    tags: ["보안", "설치형", "로컬 처리"],
  },
  {
    title: "셀 병합까지 처리된 Excel 자동 생성",
    description:
      "정리된 엑셀, 보고서에 그대로 사용하세요. 조인된 데이터도 구조에 맞게 셀 병합하여, 읽기 쉬운 Excel 파일로 자동 정리됩니다. 보고서 작업을 위한 추가 편집 없이 바로 사용할 수 있습니다.",
    image: excel,
    tags: ["Excel", "자동 정리", "보고서"],
  },
];

const ServicesSection = () => {
  return (
    <Section id="features">
      <Container>
        <HeaderWrapper>
          <Title>Features</Title>
          <Subtitle>
            NL2SQL의 기능,
            <br />
            여러분의 업무시간을 줄여줍니다.
          </Subtitle>
          <Description>
            자연어로 데이터를 요청하고, SQL은 NL2SQL이 대신합니다.
          </Description>
          <Button>데모 예약하기</Button>
        </HeaderWrapper>
        <ServicesContainer>
          {services.map((service, index) => (
            <ServiceCard key={index} isReversed={index % 2 === 1}>
              <ServiceImage>
                <img src={service.image} alt={service.title} />
              </ServiceImage>
              <ServiceContent>
                <ServiceTitle>{service.title}</ServiceTitle>
                <ServiceDescription>{service.description}</ServiceDescription>
                <TagsContainer>
                  {service.tags.map((tag, tagIndex) => (
                    <Tag key={tagIndex}>{tag}</Tag>
                  ))}
                </TagsContainer>
              </ServiceContent>
            </ServiceCard>
          ))}
        </ServicesContainer>
      </Container>
    </Section>
  );
};

export default ServicesSection;
