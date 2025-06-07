import styled from "@emotion/styled";
import BannerImage from "@atoms/BannerImage";

const Section = styled.section`
  width: 100%;
  padding: 4rem 0;
  background-color: #ffffff;
`;

const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
`;

const BannerSection = () => {
  return (
    <Section>
      <Container>
        <BannerImage />
      </Container>
    </Section>
  );
};

export default BannerSection;
