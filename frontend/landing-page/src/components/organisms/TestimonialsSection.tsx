import styled from "@emotion/styled";
import TestimonialCard from "@molecules/TestimonialCard";
import { useState } from "react";
import marketerAvatar from "@assets/avatar/marketer_avatar.jpg";
import pmAvatar from "@assets/avatar/pm_avatar.jpg";
import designerAvatar from "@assets/avatar/designer_avatar.jpg";

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
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

const Title = styled.h2`
  font-size: 48px;
  font-weight: 700;
  color: #000000;
  letter-spacing: -4.2px;
  line-height: 1;
  text-align: center;
  margin-bottom: 4rem;
`;

const ContentWrapper = styled.div`
  display: flex;
  gap: 2rem;
  max-width: 1000px;
  margin: 0 auto;
  width: 100%;
`;

const CardsContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 300px;
`;

const TestimonialContent = styled.div`
  flex: 1;
  border-left: 1px solid #2e4f21;
  padding-left: 2rem;
`;

const Quote = styled.p`
  font-size: 30px;
  font-weight: 400;
  color: #4a4a4a;
  letter-spacing: -1.35px;
  line-height: 1.4;
  margin-bottom: 2rem;
`;

const AuthorInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const AuthorImage = styled.img`
  width: 46px;
  height: 46px;
  border-radius: 16px;
  object-fit: cover;
`;

const AuthorName = styled.p`
  font-size: 16px;
  font-weight: 400;
  color: #4a4a4a;
  letter-spacing: -0.72px;
`;

const testimonials = [
  {
    content:
      "데이터 요청할 때마다 눈치 보였는데, 이제는 혼자서도 다 할 수 있어요",
    role: "브랜드 마케터",
    location: "중견 F&B 기업",
    avatar: marketerAvatar,
  },
  {
    content: "매주 반복되던 데이터 요청 작업이 10분으로 줄었습니다",
    role: "서비스 기획자",
    location: "스타트업",
    avatar: pmAvatar,
  },
  {
    content: "전환율 보고서 엑셀로 바로 나오는 거 보고 진짜 감동했어요",
    role: "캠페인 운영 매니저",
    location: "패션 커머스",
    avatar: designerAvatar,
  },
];

const TestimonialsSection = () => {
  const [activeIndex, setActiveIndex] = useState(0);

  return (
    <Section id="testimonials">
      <Container>
        <Title>인터뷰로 검증된 가치</Title>
        <ContentWrapper>
          <CardsContainer>
            {testimonials.map((testimonial, index) => (
              <TestimonialCard
                key={index}
                content={testimonial.content}
                role={testimonial.role}
                location={testimonial.location}
                isActive={index === activeIndex}
                onClick={() => setActiveIndex(index)}
              />
            ))}
          </CardsContainer>
          <TestimonialContent>
            <Quote>"{testimonials[activeIndex].content}"</Quote>
            <AuthorInfo>
              <AuthorImage
                src={testimonials[activeIndex].avatar}
                alt={testimonials[activeIndex].role}
              />
              <AuthorName>
                {testimonials[activeIndex].role},{" "}
                {testimonials[activeIndex].location}
              </AuthorName>
            </AuthorInfo>
          </TestimonialContent>
        </ContentWrapper>
      </Container>
    </Section>
  );
};

export default TestimonialsSection;
